package bbs.resources;

import bbs.core.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Path("/users")
public class UserResource {
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(User user) {
    return Response.status(201).build();
  }

  @GET
  @Path("/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  public User getUser(@PathParam("userId") String userId) {
    User user = new User();
    try {
      user.setKey("-----BEGIN PUBLIC KEY-----\n" +
          "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxiUrH00bdLboTGwtsYcM\n" +
          "TscHDLyGwEsHHWxEtzxUC/VzFNC4N0SOap14TDtI9kgBYaoLmnti1CZ35KMetUyX\n" +
          "VJp4A4O15sir3e0uxWSyErhPQ9X/2e3AvIGfmhPMiOC6zmMnZfcSjXcAKCaRCDx6\n" +
          "C3MhFaHtC8MCLiBcJO09nBYUK7B1te1MDwYq5YAhoFgjDFlb6GKMSMRT2MsK5VYD\n" +
          "T9srSlq5e94RlF1hTOhNyhLjuWfxuVxK5okmaoQUcDsHOYXmJPU9t4VE+djz946v\n" +
          "M4sVOzq3hxFHCrI/jPyrFNJ0jRFvQk3lgpE9+muDsrbW/3r/XjCswlW7mhmiHGgS\n" +
          "rQIDAQAB\n" +
          "-----END PUBLIC KEY-----\n");
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return user;
  }
}
