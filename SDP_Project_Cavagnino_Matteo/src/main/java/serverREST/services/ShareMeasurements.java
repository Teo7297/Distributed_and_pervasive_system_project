package serverREST.services;

import serverREST.handleRequests.DataRequestsHandler;
import beans.Measurement;
import beans.Measurements;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * this class is the REST interface used by nodes to post the measurements they computed
 * and by analysts to obtain the stored measurements
 */

@Path("measurements")
public class ShareMeasurements {

    @Path("publish")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response publishMeasurement(Measurement m){
        Measurements.getInstance().add(m);
        return Response.ok().build();
    }

    @Path("get/nodenumber")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getNumber(){
        return Response.ok(DataRequestsHandler.computeResult("nodes-number")).build();
    }
    @Path("get/sd-mean/{n}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getSDMean(@PathParam("n") String n){
        return Response.ok(DataRequestsHandler.computeResult("SD-mean", Integer.parseInt(n))).build();
    }

    @Path("get/last-measurements/{n}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getLastN(@PathParam("n") String n){
        return Response.ok(DataRequestsHandler.computeResult("last-n", Integer.parseInt(n))).build();
    }
}