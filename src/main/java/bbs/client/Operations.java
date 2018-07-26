package bbs.client;

import bbs.core.Message;
import bbs.core.UserClient;
import bbs.core.Utils;

import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.SyncFailedException;
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
  private int acks;

  public Operations(List<BBSClient> clients) {
    this.restClients = clients;
    this.acks = 0;
  }

  // TODO make all these requests async
  public UserClient userInfo (String userId) throws NoSuchAlgorithmException {
    List<UserClient> responses = new ArrayList<UserClient>();
    for(BBSClient restClient : this.restClients) {
      responses.add(restClient.getUser(userId));
    }
    return responses.get(0);
  }

  public void register (UserClient user) throws NoSuchAlgorithmException, SyncFailedException {
    user.generateTimestamp();
    user.setCreationDate(ZonedDateTime.now());
    for(BBSClient restClient : this.restClients) {
      handleWriteResponse(restClient.createUser(user));
    }
    if (!haveConsensus()) {
      throw new SyncFailedException("No consensus from servers");
    }
  }

  public void post (UserClient user, String messageBody) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, SyncFailedException {
    this.acks = 0;
    for(BBSClient restClient : this.restClients) {
      Message message = new Message();
      message.generateTimestamp();
      message.setBody(messageBody);
      message.setUserId(user.getStringId());
      message.setCreationDate(ZonedDateTime.now());
      user.sign(message);
      handleWriteResponse(restClient.createMessage(user.getStringId(), message));
    }
    if (!haveConsensus()) {
      throw new SyncFailedException("No consensus from servers");
    }
  }

  public List<Message> read (String userId, int number) throws NoSuchAlgorithmException {
    List<List<Message>> responses = new ArrayList<List<Message>>();
    for(BBSClient restClient : this.restClients) {
      responses.add(restClient.getMessages(userId, number));
    }
    return responses.get(0);
  }

  private void handleWriteResponse (Response response) {
    if (response.getStatus() < 400) {
      acks++;
    }
    System.out.println("== Reponse ==");
    System.out.println("Status: " + response.getStatus());
    System.out.println("Body: " + response.readEntity(String.class));
  }

  private boolean haveConsensus () {
    return this.acks > (this.restClients.size() / 2);
  }
}
