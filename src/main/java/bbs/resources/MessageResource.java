package bbs.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/messages")
public class MessageResource {
    @POST @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String createMessage() {
        return "Hello";
    }

    @GET @Path("/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage(@PathParam("userId") String userId) {
        return userId;
    }
}

