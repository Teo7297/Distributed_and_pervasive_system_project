package node.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import node.grpc.services.NodeServicesImpl;
import java.io.IOException;
import java.net.BindException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ServerGRPC implements Runnable {
    private int port;
    private Server server;
    private final node.Node node;

    public ServerGRPC(node.Node node) {
        this.node = node;
        this.port = node.getPort();
    }

    @Override
    public void run() {
        boolean running = false;
        server = null;

        while(!running) {
            try {
                server = ServerBuilder.forPort(port).addService(new NodeServicesImpl(this.node)).build().start();
                System.out.println("INFO: GRPC Server started succesfully");
                running = true;

            } catch (IOException e) {
                port = ThreadLocalRandom.current().nextInt(1025, 65535);
            }
        }

        node.setPort(port);

        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            System.err.println("NODE GRPC SERVER ERROR");
            System.err.println("A problem occurred while waiting for the server termination...");

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
