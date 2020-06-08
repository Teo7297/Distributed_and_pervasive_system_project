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

        if(peersInvolved.contains(node.getId()) && node.getBuffer().isReady()){
            List<String> receivedList = new ArrayList<>(token.getMeasurementsList());
            Objects.Token newToken;

            //"remove" this id from the list
            if(peersInvolved.size() > 1) {
                peersInvolved.remove(node.getId());

                if(node.isExitFlag()){
                    node.leaveNetwork();
                    node.getIdList().remove(node.getId());
                    newToken = Objects.Token.newBuilder()
                            .addAllParticipants(peersInvolved)
                            .addAllMeasurements(receivedList)
                            .addMeasurements(node.getBuffer().getMean())
                            .build();
                    TokenClient.sendToken(newToken, node.getTarget());
                    if(test) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.exit(0);
                }else{
                newToken = Objects.Token.newBuilder()
                        .addAllParticipants(peersInvolved)
                        .addAllMeasurements(receivedList)
                        .addMeasurements(node.getBuffer().getMean())
                        .build();
                System.out.println("INFO: Adding mean in the token ...");
                }
            }else if (!node.isExitFlag()){
                //This is the last node needed, send to gateway
                receivedList.add(node.getBuffer().getMean());
                Measurement toPublish = getMean(receivedList);
                node.sendMessageToGateway(Node.PUBLISH_MEASUREMENT_PATH, node.toBean(), toPublish);
                newToken = Objects.Token.newBuilder().addAllParticipants(node.getIdList()).build();
                System.out.println("INFO: Token sent to gateway, published value : { " + toPublish + " }");

            }else{
                node.leaveNetwork();
                node.getIdList().remove(node.getId());
                newToken = Objects.Token.newBuilder().addAllParticipants(node.getIdList()).build();
                TokenClient.sendToken(newToken, node.getTarget());
                if(test) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }

            //send modified token
            TokenClient.sendToken(newToken, node.getTarget());

        }else{
            //nothing to add, sends the received token as it is
            TokenClient.sendToken(token, node.getTarget());
        }
    }

    private static Measurement getMean(List<String> ms){
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
