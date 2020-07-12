package node;

import com.objects.Objects;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import node.simulators.BufferImpl;
import node.simulators.Measurement;
import node.simulators.PM10Simulator;
import node.grpc.client.Broadcaster;
import node.grpc.client.TokenClient;
import node.grpc.server.ServerGRPC;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Node {
    private static final String ADD_NODE_PATH = "http://localhost:1337/nodes/add";
    private static final String REMOVE_NODE_PATH = "http://localhost:1337/nodes/remove";
    public static final String PUBLISH_MEASUREMENT_PATH = "http://localhost:1337/measurements/publish";
    private static final long TIMER_CAP = 10000000L;

    private final BufferImpl buffer;
    private final List<beans.Node> network;
    private final List<String> idList;
    private int port;
    private String id;
    private String addr;
    private ServerGRPC serverGRPC;
    private beans.Node target;
    private volatile boolean exitFlag;
    private Timer timer;

    public Node(String id, int port) {
        this.id = id;
        this.port = port;
        try {
            this.addr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("NODE INITIALIZATION ERROR - Provided host is not valid");
            e.printStackTrace();
        }
        this.buffer = new BufferImpl();
        this.network = new ArrayList<>();
        this.idList = new ArrayList<>();
        this.exitFlag = false;

    }

    public static void main(String[] args) {
        //Node node = new Node("" + new Random().nextInt(10000), ThreadLocalRandom.current().nextInt(1025, 65535));           //random parameters
        //Node node = new Node(args[0], Integer.parseInt(args[1]));                                                           //args parameters
        Node node = askParameters();                                                                                          //get parameters from input

        node.setup();
    }

    private static Node askParameters() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("To initialize node, please insert ID and PORT separated by a space (e.g. 11 8081)");
        String input = null;
        Node newNode = null;

        while (input == null) {
            input = scanner.nextLine();
            String[] pair = input.split(" ");
            try {
                String i = "" + Integer.parseInt(pair[0]);
                int p = Integer.parseInt(pair[1]);
                if (p < 1025 || p > 65535) {
                    throw new NumberFormatException();
                }
                newNode = new Node(i, p);
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                input = null;
                System.err.println("ERROR: Please insert a valid pair of numbers");
            }
        }
        return newNode;
    }

    public void setup() {

        System.out.println("INFO: Starting Node ...");
        new PM10Simulator(this.getBuffer()).start();    //Start simulator thread
        System.out.println("INFO: Started sensor");

        System.out.println("INFO: Starting GRPC server ...");
        this.startGRPCServer();                          //Starts a thread with grpc server

        //give a bit of time to the server thread to start, needed if lots of nodes start on the same machine or double port values are inserted
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Contacting gateway ...");
        this.sendMessageToGateway(ADD_NODE_PATH, this.toBean());    //tells gateway that the node is active, the response fills the network list (ordered by id)

        this.broadcastMessage("join");         //broadcast a hello message to the network

        System.err.println("PRESS ENTER TO KILL THIS NODE");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        this.setExitFlag(true);

    }

    //timer needed only for not controlled crashes (not happening in this version, hence, timer won't go off )
    public void restartTimer(){
        if(this.timer != null) {
            this.timer.cancel();
        }
        this.timer = new Timer("token timeout");
        this.timer.schedule(new TimerTask(){
            @Override
            public void run(){
                if(isHighest()){
                    spawnToken();
                }
            }
        }, TIMER_CAP);
    }

    public void leaveNetwork() {
        this.broadcastMessage("leave");
        this.sendMessageToGateway(REMOVE_NODE_PATH, this.toBean());
        this.serverGRPC.shutServer();
        System.err.println("INFO: Node removed, killing ...");
    }

    private void broadcastMessage(String msg){
        List<Thread> threads = makeBroadcastThreads(msg);
        for(Thread t : threads){
            t.start();
        }
    }

    //sync needed to cycle network
    private List<Thread> makeBroadcastThreads(String msg){
        List<Thread> threads = new ArrayList<>();
        for(beans.Node n : this.getNetworkCopy()){
            if(!n.getId().equals(this.id))
                threads.add(new Thread(new Broadcaster(this, n, msg, this.toBean().toJSONString())));
        }
        return threads;
    }

    private void startGRPCServer(){
        this.serverGRPC = new ServerGRPC(this);
        new Thread(this.serverGRPC).start();
    }

    public void sendMessageToGateway(String path, beans.Node sender, Measurement... params){

        ObjectMapper map = new ObjectMapper();
        Client client = Client.create();
        WebResource resource = client.resource(path);
        ClientResponse response = null;

        switch (path) {
            case ADD_NODE_PATH:
                String nodesListJSON = "";
                try {
                    response = resource.accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, sender.toJSONObject());
                }catch (com.sun.jersey.api.client.ClientHandlerException e){
                    System.err.println("INFO: Server is not available, closing...");
                    System.exit(0);
                }
                while (response.getStatus() != 200) {
                    this.setId("" + new Random().nextInt(10000));
                    sender = this.toBean();
                    response = resource.accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, sender.toJSONObject());
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntityInputStream()))) {
                    nodesListJSON = br.readLine();
                    Wrapper w = map.readValue(nodesListJSON, Wrapper.class);
                    List<beans.Node> nodeList = w.getNode();

                    for (beans.Node n : nodeList) {
                        addNodeToNetwork(n);
                    }
                } catch (org.codehaus.jackson.map.JsonMappingException e) {
                    beans.Node n = null;
                    try {
                        //triggered if this is the first node in the network
                        n = map.readValue(removeDirtyChars(nodesListJSON), beans.Node.class);
                        this.setTarget(this.toBean());

                        //starts own timer and spawns first token
                        this.restartTimer();
                        this.spawnToken();

                    } catch (IOException ioException) {
                        System.err.println("NODE ERROR - Received wrongly formatted (single item) JSON String from REST server");
                        ioException.printStackTrace();
                    }
                    assert n != null;
                    addNodeToNetwork(n);
                } catch (JsonParseException e) {
                    System.err.println("NODE ERROR - An error occurred while parsing the JSON REST server response");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("NODE ERROR - Received a wrongly formatted JSON String from the REST server");
                    e.printStackTrace();
                }
                System.out.println("INFO: Started " + this);
                break;

            case REMOVE_NODE_PATH :
                System.out.println("INFO: Communicating node exit to gateway ...");
                try {
                    response = resource.accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, sender.toJSONObject());
                }catch (com.sun.jersey.api.client.ClientHandlerException e){
                    System.err.println("INFO: Server is not available, closing...");
                    System.exit(0);
                }
                if(response.getStatus() != 200){
                    System.err.println("SERVER ERROR - Error code: " + response.getStatus());
                }else{
                    System.out.println("INFO: Node removed from gateway!");
                }
                break;

            case PUBLISH_MEASUREMENT_PATH:
                System.out.println("INFO: Publishing new data ...");
                try {
                    response = resource.accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, new JSONObject(new beans.Measurement(params[0].getId(), params[0].getType(), params[0].getValue(), params[0].getTimestamp()).toJSONString()));
                } catch (JSONException e) {
                    System.err.println("NODE ERROR - Error occurred while generating a JSONObject from Measurement");
                    e.printStackTrace();
                } catch (com.sun.jersey.api.client.ClientHandlerException e){
                    System.err.println("INFO: Server is not available, closing...");
                    System.exit(0);
                }
                assert response != null;
                if(response.getStatus() != 200){
                    System.err.println("SERVER ERROR - Error code: " + response.getStatus());
                }
                break;

            default:
                System.err.println("NODE REST ERROR - Path is not valid");
        }
    }

    public void spawnToken(){
        TokenClient.sendToken(Objects.Token.newBuilder().addParticipants(this.id).build(), this.getTarget());
        System.err.println("INFO: New Token spawned!!");
    }

    public synchronized boolean isHighest(){
        for(beans.Node n : this.getNetworkCopy()){
            if(Integer.parseInt(this.getId()) < Integer.parseInt(n.getId())){
                return false;
            }
        }
        return true;
    }


    /**
     * method to remove extra characters from the first node insertion (the server will return an object instead of an array)
     */
    private static String removeDirtyChars(String a){
        return a.replace("{\"node\":", "").replace(a.substring(a.length()-1), "") + "}";
    }

    public beans.Node toBean(){
        return new beans.Node(this.id, this.port);
    }

    public synchronized void addNodeToNetwork(beans.Node n){
        boolean inserted = false;
        int position = -1;
        int thisId = Integer.parseInt(this.getId());
        int nId = Integer.parseInt(n.getId());

        for(int i = 0; i < this.network.size(); i++){
            if(Integer.parseInt(network.get(i).getId()) > nId){
                network.add(i, n);
                inserted = true;
                position = i;
                break;
            }
        }
        if(!inserted){
            network.add(n);
            position = network.size()-1;
        }
        //add id to the id list (used for token communication)
        idList.add(n.getId());

        System.out.println("INFO : [ " + nId + " ] joined the network ..."+network );

        //if this is the node being added (triggered only in joining phase)
        if(nId == thisId) {
            this.setTarget();

            //if another node is being added
        }else if((position == 0 && Integer.parseInt(network.get(network.size()-1).getId()) == thisId) || ((position > 0) && Integer.parseInt(network.get(position-1).getId()) == thisId)){
            this.target = n;
            System.out.println("INFO: Node [ " + this.getId() + " ] target set to " + this.target.getId());
        }
    }

    //this is triggered only if other nodes leave
    public synchronized void removeNodeFromNetwork(beans.Node n){
        System.out.println("INFO: Removing [ " + n.getId() + " ] from the network ...");
        this.network.removeIf(bn -> bn.getId().equals(n.getId()));
        this.idList.remove(n.getId());
        System.out.println("INFO: [ " + n.getId() + " ] removed from the network");
        if(n.getId().equals(this.target.getId())){
            this.setTarget();
        }
    }

    public synchronized void setTarget(){
        List<beans.Node> ntwCopy = this.getNetworkCopy();
        for(int i = 0; i < ntwCopy.size(); i++){
            if(ntwCopy.get(i).getId().equals(this.id)){
                this.target = ntwCopy.get((i+1)%ntwCopy.size());
                System.out.println("INFO: Node [ " + this.id + " ] target set to " + this.target.getId());
                break;
            }
        }
    }

    public synchronized List<beans.Node> getNetworkCopy(){
        return new ArrayList<>(network);
    }

    //##################################################################
    //classes
    private static class Wrapper {
        private List<beans.Node> node;

        public List<beans.Node> getNode() {
            return node;
        }
        public void setNode(List<beans.Node> node) {
            this.node = node;
        }
    }

    //##################################################################
    //Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int n){
        this.port = n;
    }

    public String getAddr() {
        return addr;
    }

    public synchronized BufferImpl getBuffer() {
        return buffer;
    }

    public synchronized beans.Node getTarget(){
        return this.target;
    }

    public synchronized void setTarget(beans.Node target){
        this.target = target;
    }

    public synchronized List<String> getIdList() {
        return idList;
    }

    public boolean isExitFlag() {
        return exitFlag;
    }

    public void setExitFlag(boolean exitFlag) {
        this.exitFlag = exitFlag;
    }

    public String toString(){
        return this.getId() + " " + this.getAddr() + ":" + this.getPort();
    }


}
