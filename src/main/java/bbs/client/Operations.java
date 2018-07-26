package bbs.client;

import bbs.core.Message;
import bbs.core.UserClient;
import bbs.core.Utils;

import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.time.ZonedDateTime;
import java.util.List;

public class Operations {

  private List<BBSClient> restClients;
  private int selected;

  public Operations(List<BBSClient> clients) {
    this.restClients = clients;
    this.selected = 0;
  }

  private void nextClient() {
    this.selected = ((this.selected + 1) % this.restClients.size());
  }

  public UserClient userInfo (String userId) throws NoSuchAlgorithmException {
    BBSClient restClient = this.restClients.get(this.selected);
    nextClient();

    return restClient.getUser(userId);
  }

  public void register (UserClient user) throws NoSuchAlgorithmException {
    BBSClient restClient = this.restClients.get(this.selected);
    nextClient();

    handleResponse(restClient.createUser(user));
  }

  public void post (UserClient user, String messageBody) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    BBSClient restClient = this.restClients.get(this.selected);
    nextClient();

    Message message = new Message();
    message.setBody(messageBody);
    message.setUserId(user.getStringId());
    message.setCreationDate(ZonedDateTime.now());
    user.sign(message);
    handleResponse(restClient.createMessage(user.getStringId(), message));
  }

  public List<Message> read (String userId, int number) throws NoSuchAlgorithmException {
    BBSClient restClient = this.restClients.get(this.selected);
    nextClient();

    return restClient.getMessages(userId, number);
  }

  private void handleResponse (Response response) {
    System.out.println("== Reponse ==");
    System.out.println("Status: " + response.getStatus());
    System.out.println("Body: " + response.readEntity(String.class));
  }
}
