package node.grpc.services;

import com.objects.Objects.*;
import com.objects.NodeServicesGrpc.*;
import io.grpc.stub.StreamObserver;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


public class NodeServicesImpl extends NodeServicesImplBase {
    private final node.Node node;
    private final boolean test = false;
    private final boolean test2 = false;

    public NodeServicesImpl(node.Node node){
        this.node = node;
    }

    @Override
    public void sendMessage(Message message, StreamObserver<Message> responseObserver) {
        beans.Node newNode = null;
        ObjectMapper mapper = new ObjectMapper();
        String[] msgContent = message.getMessage().split("-");

        //analyze received message
        try {
            newNode = mapper.readValue(msgContent[1], beans.Node.class);
        } catch (IOException e) {
            System.err.println("NODE SERVICE ERROR - Received a wrongly formatted JSON Node object");
            e.printStackTrace();
        }
        switch (msgContent[0]){
            case "join":    //add node to ntw
                assert newNode != null;
                node.addNodeToNetwork(newNode);
                break;
            case "leave":   //remove node from ntw
                assert newNode != null;
                node.removeNodeFromNetwork(newNode);
                break;
        }
        //send ack response
        if (node.isExitFlag()){
            responseObserver.onNext(Message.newBuilder().setMessage("ko-node is leaving").build());
        }else {
            responseObserver.onNext(Message.newBuilder().setMessage("ok").build());
        }
        //closes
        responseObserver.onCompleted();
    }

    @Override
    public void sendToken(Token token, StreamObserver<Message> responseObserver){
        //acks the token received
        responseObserver.onNext(Message.newBuilder().setMessage("ok").build());
        responseObserver.onCompleted();

        //node.restartTimer();
        if(test2){
            System.out.println("Participants" + token.getParticipantsList());
        }
        if(test) {
            try {
                System.out.println("TEST INFO: Received token");
                for(String s : token.getParticipantsList()){
                    System.out.println("Partecipant to current token : id = [ " + s + " ]");
                }
                System.out.println("This is the current state of the token:");
                for(String s : token.getMeasurementsList()){
                    System.out.println(s);
                }
                Thread.sleep(5000);
                System.out.println("TEST INFO: Sending token");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        new Thread(new TokenHandler(token, node)).start();

        if(test){
            System.out.println("token sent to next node : " + node.getTarget());
        }
    }
}
