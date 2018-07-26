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
import java.util.ArrayList;
import java.util.List;

public class Operations {

  private List<BBSClient> restClients;
  private int selected;

  public Operations(List<BBSClient> clients) {
    this.restClients = clients;
  }

  // TODO make all these requests async
  public UserClient userInfo (String userId) throws NoSuchAlgorithmException {
    List<UserClient> responses = new ArrayList<UserClient>();
    for(BBSClient restClient : this.restClients) {
      responses.add(restClient.getUser(userId));
    }
    return responses.get(0);
  }

  public void register (UserClient user) throws NoSuchAlgorithmException {
    user.generateTimestamp();
    user.setCreationDate(ZonedDateTime.now());
    for(BBSClient restClient : this.restClients) {
      handleResponse(restClient.createUser(user));
    }
  }

  public void post (UserClient user, String messageBody) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    for(BBSClient restClient : this.restClients) {
      Message message = new Message();
      message.generateTimestamp();
      message.setBody(messageBody);
      message.setUserId(user.getStringId());
      message.setCreationDate(ZonedDateTime.now());
      user.sign(message);
      handleResponse(restClient.createMessage(user.getStringId(), message));
    }
  }

  public List<Message> read (String userId, int number) throws NoSuchAlgorithmException {
    List<List<Message>> responses = new ArrayList<List<Message>>();
    for(BBSClient restClient : this.restClients) {
      responses.add(restClient.getMessages(userId, number));
    }
    return responses.get(0);
  }

  private void handleResponse (Response response) {
    System.out.println("== Reponse ==");
    System.out.println("Status: " + response.getStatus());
    System.out.println("Body: " + response.readEntity(String.class));
  }
}
