package bbs.resources;

import bbs.core.User;
import bbs.core.UserStateManager;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.NoSuchElementException;

@Path("/users")
public class UserResource {
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(User user) {
    try {
      UserStateManager.add(user);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return Response.status(400).build();
    }
    return Response.status(201).build();
  }

  @GET
  @Path("/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUser(@PathParam("userId") String userId) {
    try {
      return Response.status(200).entity(UserStateManager.find(userId)).build();
    } catch (NoSuchElementException e) {
      return Response.status(404).build();
    }
  }
}
