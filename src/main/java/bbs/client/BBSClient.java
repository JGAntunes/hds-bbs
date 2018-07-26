package bbs.client;

import bbs.core.Message;
import bbs.core.UserClient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class BBSClient {
  private String uri;
  private Client restClient;

  public BBSClient(String uri) {
    this.restClient = ClientBuilder.newClient();
    this.uri = uri;
  }

  public UserClient getUser(String id) {
    return this.restClient
        .target(this.uri)
        .path("/users/" + id)
        .request(MediaType.APPLICATION_JSON)
        .get(UserClient.class);
  }

  public Response createUser(UserClient user) {
    return this.restClient
        .target(this.uri)
        .path("/users")
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(user, MediaType.APPLICATION_JSON));
  }

  public List<Message> getMessages(String id, int number) {
    return this.restClient
        .target(this.uri)
        .path("/messages/" + id)
        .queryParam("count", number)
        .request(MediaType.APPLICATION_JSON)
        .get(new GenericType<List<Message>>() {});
  }

  public Response createMessage(String id, Message message) {
    return this.restClient
        .target(this.uri)
        .path("/messages/" + id)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(message, MediaType.APPLICATION_JSON));
  }
}
