package node.grpc.client;

import com.objects.NodeServicesGrpc;
import com.objects.Objects;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

public class Broadcaster implements Runnable{
    private final beans.Node target;
    private final String msg;
    private final String nodeJSON;
    private final node.Node sender;

    public Broadcaster(node.Node sender, beans.Node target, String msg, String nodeJSON){
        this.target = target;
        this.msg = msg;
        this.nodeJSON = nodeJSON;
        this.sender = sender;
    }
    @Override
    public void run() {

        //create the channel
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(target.getIpaddr() + ":" +target.getPort()).usePlaintext(true).build();

        //create the stub
        NodeServicesGrpc.NodeServicesStub stub = NodeServicesGrpc.newStub(channel);

        //send message
        stub.sendMessage(Objects.Message.newBuilder().setMessage(msg + "-" + nodeJSON).build(), new StreamObserver<Objects.Message>() {

            public void onNext(Objects.Message message) {
                String[] ackMsg = message.getMessage().split("-");

                //useful in future to get different types of acks
                if ("ok".equals(ackMsg[0])) {
                } else {
                    System.err.println("NODE CLIENT ERROR - Received a wrongly formatted Ack message\nMessage |=> " + ackMsg[0] + " " + ackMsg[1]);
                }
            }

            public void onError(Throwable throwable) {
                //the target left.. delete it from the list
                sender.removeNodeFromNetwork(target);
            }

            public void onCompleted() {
                channel.shutdownNow();
            }
        });
    }
}
