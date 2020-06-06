package serverREST.server;

import serverREST.services.NodesHandler;
import serverREST.services.ShareMeasurements;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class StartServer {

    private static final String HOST = "localhost";
    private static final int PORT = 1337;

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServerFactory.create("http://"+HOST+":"+PORT+"/", new DefaultResourceConfig(NodesHandler.class, ShareMeasurements.class));
        server.start();

        System.out.println("Server online");
        System.out.println("Server started on: http://"+HOST+":"+PORT+"/");

        System.out.println("Press Enter to stop...");
        System.in.read();

        System.out.println("Stopping server");
        server.stop(0);

        System.out.println("Server stopped");
        System.exit(0);

    }
}
