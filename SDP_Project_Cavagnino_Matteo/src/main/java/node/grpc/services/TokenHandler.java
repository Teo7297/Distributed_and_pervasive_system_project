package node.grpc.services;

import com.objects.Objects;
import node.Node;
import node.grpc.client.TokenClient;
import com.objects.Objects.*;
import java.util.ArrayList;
import java.util.List;
import node.simulators.Measurement;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TokenHandler implements Runnable{
    private final Token token;
    private final Node node;

    private final boolean test = false;

    public TokenHandler(Token token, Node node){
        this.token = token;
        this.node = node;
    }

    @Override
    public void run() {
        List<String> peersInvolved = new ArrayList<>(token.getParticipantsList());
        //TOKEN NEEDEd
        if(peersInvolved.contains(node.getId()) && node.getBuffer().isReady()){
            List<String> receivedList = new ArrayList<>(token.getMeasurementsList());
            Objects.Token newToken = null;
            System.out.println("INFO: Adding mean in the token ...");
            //NEED TO EXIT
            if(node.isExitFlag()){
                //NOT LAST
                if(peersInvolved.size() > 1) {
                    peersInvolved.remove(node.getId());
                    node.leaveNetwork();
                    newToken = Objects.Token.newBuilder()
                            .addAllParticipants(peersInvolved)
                            .addAllMeasurements(receivedList)
                            .addMeasurements(node.getBuffer().getMean())
                            .build();
                    //send modified token to target
                    TokenClient.sendToken(newToken, node.getTarget(), node);
                    //LAST
                }else{
                    node.leaveNetwork();
                    List<String> toInsert = new ArrayList<>(node.getIdList());
                    toInsert.remove(node.getId());
                    newToken = Objects.Token.newBuilder()
                            .addAllParticipants(toInsert)
                            .build();
                    TokenClient.sendToken(newToken, node.getTarget(), node);
                    receivedList.add(node.getBuffer().getMean());
                    Measurement toPublish = computeMean(receivedList);
                    node.sendMessageToGateway(Node.PUBLISH_MEASUREMENT_PATH, node.toBean(), toPublish);
                    System.out.println("INFO: Before exiting one last value has been sent to the gateway, published value : { " + toPublish + " }");
                    //send modified token to target
                }
                if(test) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);

                //DON'T NEED TO EXIT
            }else{
                //NOT LAST
                if(peersInvolved.size() > 1) {
                    peersInvolved.remove(node.getId());
                    newToken = Objects.Token.newBuilder()
                            .addAllParticipants(peersInvolved)
                            .addAllMeasurements(receivedList)
                            .addMeasurements(node.getBuffer().getMean())
                            .build();
                    //LAST
                }else{
                    newToken = Objects.Token.newBuilder()
                            .addAllParticipants(node.getIdList())
                            .build();
                    receivedList.add(node.getBuffer().getMean());
                    Measurement toPublish = computeMean(receivedList);
                    node.sendMessageToGateway(Node.PUBLISH_MEASUREMENT_PATH, node.toBean(), toPublish);
                    System.out.println("INFO: Value has been sent to the gateway, published value : { " + toPublish + " }");
                }
            }
            //send modified token to target
            TokenClient.sendToken(newToken, node.getTarget(), node);

            //TOKEN NOT NEEDED
        }else{
            //NEED TO EXIT
            if(node.isExitFlag()){
                Objects.Token newToken = null;
                List<String> receivedList = new ArrayList<>(token.getMeasurementsList());
                node.leaveNetwork();
                //Se maggiore di 1 mi tolgo (se ci sono anchio) e inoltro
                if(peersInvolved.size() > 1) {
                    peersInvolved.remove(node.getId());
                    newToken = Objects.Token.newBuilder()
                            .addAllParticipants(peersInvolved)
                            .addAllMeasurements(receivedList)
                            .build();
                    TokenClient.sendToken(newToken, node.getTarget(), node);

                //se l'ultimo ero io pubblico la media degli altri
                }else if(peersInvolved.contains(node.getId())){
                    //this was the last missing, need to calculate and send the mean anyways...
                    if(receivedList.size() > 0) {
                        Measurement toPublish = computeMean(receivedList);
                        node.sendMessageToGateway(Node.PUBLISH_MEASUREMENT_PATH, node.toBean(), toPublish);
                        System.out.println("INFO: Before exiting one last value has been sent to the gateway, published value : { " + toPublish + " }");
                    }
                    List<String> toInsert = new ArrayList<>(node.getIdList());
                    toInsert.remove(node.getId());
                    newToken = Objects.Token.newBuilder()
                            .addAllParticipants(toInsert)
                            .build();
                    TokenClient.sendToken(newToken, node.getTarget(), node);
                }else{
                    TokenClient.sendToken(token, node.getTarget(), node);
                }


                if(test) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);

                //DON'T NEED TO EXIT
            }else{
                TokenClient.sendToken(token, node.getTarget(), node);
            }
        }

    }

    private static Measurement computeMean(List<String> ms){
        double sum = 0;
        long ts = 0;
        String type = null;
        String id = null;
        for (String m : ms){
            JSONObject obj = null;
            try {
                obj = new JSONObject(m);
            } catch (JSONException e) {
                System.err.println("LEADER NODE ERROR - Found a wrongly formatted measurement inside the token");
                e.printStackTrace();
            }
            try {
                sum += Double.parseDouble(obj.get("value").toString());
                ts += Long.parseLong(obj.get("timestamp").toString());

            } catch (JSONException e) {
                System.err.println("LEADER NODE ERROR - Cannot extract value from measurement");
                e.printStackTrace();
            }
        }
        return new Measurement("Network", "PM-10", sum/ms.size(), ts/ms.size());  //avg of values and timestamps
    }
}
