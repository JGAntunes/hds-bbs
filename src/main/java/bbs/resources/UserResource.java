package bbs.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/users")
public class UserResource {
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public String createUser() {
   return "Hello";
  }

  @GET @Path("/{userId}")
  @Produces(MediaType.TEXT_PLAIN)
  public String getUser(@PathParam("userId") String userId) {
    return userId;
  }
}
