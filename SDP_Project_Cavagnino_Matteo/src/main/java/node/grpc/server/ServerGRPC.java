package node.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import node.grpc.services.NodeServicesImpl;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ServerGRPC implements Runnable {
    private final int port;
    private Server server;
    private final node.Node node;

    public ServerGRPC(node.Node node) {
        this.node = node;
        this.port = node.getPort();
    }

    @Override
    public void run() {
        server = ServerBuilder.forPort(port).addService(new NodeServicesImpl(this.node)).build();

        try {
            server.start();
            System.out.println("INFO: GRPC Server started succesfully");
            server.awaitTermination();

        } catch (IOException | InterruptedException e) {
            System.err.println("NODE GRPC SERVER ERROR");
            if (e.getClass().equals(IOException.class)) {
                System.err.println("A problem occurred while starting the server...");
            } else {
                System.err.println("A problem occurred while waiting for the server termination...");
            }
            e.printStackTrace();
        }
    }

    public void shutServer(){
        try {
            this.server.shutdown().awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
