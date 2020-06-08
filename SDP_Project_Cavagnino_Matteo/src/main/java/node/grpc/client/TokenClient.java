package node.grpc.client;

import com.objects.NodeServicesGrpc;
import com.objects.Objects.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

public class TokenClient {

    public static void sendToken(Token token, beans.Node target){

        //create a new channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress(target.getIpaddr(), target.getPort()).usePlaintext(true).build();
        //create a new stub
        NodeServicesGrpc.NodeServicesStub stub = NodeServicesGrpc.newStub(channel);
        //send token to next node's server
        stub.sendToken(token, new StreamObserver<Message>() {

            public void onNext(Message message) {
                String[] ackMsg = message.getMessage().split("-");

                if ("ok".equals(ackMsg[0])) {
                } else {
                    System.err.println("NODE CLIENT ERROR - Received a wrongly formatted Ack message\nMessage |=> " + ackMsg[0] + " " + ackMsg[1]);
                }
            }

            public void onError(Throwable throwable) {
                System.err.println("This was the last node remaining ...\nToken deleted!");
            }

            public void onCompleted() {
                channel.shutdown();
            }
        });
    }
}
