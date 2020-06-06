package node.grpc.services;

import com.objects.Objects.*;
import com.objects.NodeServicesGrpc.*;
import io.grpc.stub.StreamObserver;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


public class NodeServicesImpl extends NodeServicesImplBase {
    private final node.Node node;

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
        responseObserver.onNext(Message.newBuilder().setMessage("ok").build());
        //closes
        responseObserver.onCompleted();
    }

    @Override
    public void sendToken(Token token, StreamObserver<Message> responseObserver){

        node.restartTimer();

        new Thread(new TokenHandler(token, node)).start();

        responseObserver.onNext(Message.newBuilder().setMessage("ok").build());

        responseObserver.onCompleted();
    }
}
