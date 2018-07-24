package bbs.resources;

import bbs.core.Message;
import bbs.core.MessageStateManager;
import bbs.core.User;
import bbs.core.UserStateManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.NoSuchElementException;

@Path("/messages")
public class MessageResource {
  @POST
  @Path("/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createMessage(@PathParam("userId") String userId, Message message) {
    try{
      User user = UserStateManager.find(userId);
      MessageStateManager.add(user, message);
      return Response.status(201).build();
    } catch (NoSuchElementException e) {
      e.printStackTrace();
      return Response.status(400).build();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return Response.status(400).build();
    }
  }

  @GET
  @Path("/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getMessage(@PathParam("userId") String userId, @QueryParam("count") Integer count) {
    // Set default value for count
    count = count == null ? -1 : count;
    try {
      List<Message> messages = MessageStateManager.find(userId, count);
      return Response.status(200).entity(messages).build();
    } catch (NoSuchElementException e) {
      e.printStackTrace();
      return Response.status(404).build();
    }
  }
}

