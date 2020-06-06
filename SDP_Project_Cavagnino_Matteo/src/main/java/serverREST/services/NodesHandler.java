package serverREST.services;

import beans.Node;
import beans.Nodes;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * this class is the REST interface used by nodes to communicate their activity (start/stop)
 */
@Path("nodes")
public class NodesHandler {

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getNodesList(){
        return Response.ok(Nodes.getInstance()).build();
    }


    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    //insert node in nodes list
    //return nodes list/map
    public Response insertNode(Node n){
        Nodes instance;
        if ((instance = Nodes.getInstance().add(n)) != null){
            return Response.ok(instance).build();
        }else{
            return Response.status(Response.Status.NOT_ACCEPTABLE).build(); //returns a not acceptable error if ID is already registered
        }
    }

    @Path("remove")
    @POST
    @Consumes({"application/json", "application/xml"})
    //remove node from map
    public Response removeNode(Node n){
        Nodes.getInstance().remove(n);
        return Response.ok().build();
    }
}

