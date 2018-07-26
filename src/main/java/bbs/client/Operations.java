package bbs.client;

import bbs.core.*;

import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.SyncFailedException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Operations {

  private List<BBSClient> restClients;
  private int acks;

  public Operations(List<BBSClient> clients) {
    this.restClients = clients;
    this.acks = 0;
  }

  // TODO make all these requests async
  public UserClient userInfo (String userId) throws NoSuchAlgorithmException, SyncFailedException {
    List<UserClient> responses = new ArrayList<UserClient>();
    for(BBSClient restClient : this.restClients) {
      responses.add(restClient.getUser(userId));
    }
    return handleReadUserResponse(responses);
  }

  public void register (UserClient user) throws NoSuchAlgorithmException, SyncFailedException {
    this.acks = 0;
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

  public List<Message> read (String userId, int number) throws NoSuchAlgorithmException, SyncFailedException {
    List<List<Message>> responses = new ArrayList<List<Message>>();
    for(BBSClient restClient : this.restClients) {
      responses.add(restClient.getMessages(userId, number));
    }
    return handleReadMessageResponse(responses);
  }

  private void handleWriteResponse (Response response) {
    if (response.getStatus() < 400) {
      acks++;
    }
    System.out.println("== Reponse ==");
    System.out.println("Status: " + response.getStatus());
    System.out.println("Body: " + response.readEntity(String.class));
  }

  private UserClient handleReadUserResponse (List<UserClient> responses) throws SyncFailedException {
    if (responses.size() > (this.restClients.size() / 2)) {
      ZonedDateTime now = ZonedDateTime.now();
      UserClient finalResponse = null;
      int diff = 0;
      for (UserClient response : responses) {
        if(ChronoUnit.SECONDS.between(response.getTimestamp(), now) > diff) {
          finalResponse = response;
        }
      }
      return finalResponse;
    }
    throw new SyncFailedException("No consensus from servers");
  }

  private List<Message> handleReadMessageResponse (List<List<Message>> responses) throws SyncFailedException {
    if (responses.size() > (this.restClients.size() / 2)) {
      ZonedDateTime now = ZonedDateTime.now();
      List<Message> finalResponse = null;
      int diff = 0;
      for (List<Message> response : responses) {
        if(ChronoUnit.SECONDS.between(response.get(0).getTimestamp(), now) > diff) {
          finalResponse = response;
        }
      }
      return finalResponse;
    }
    throw new SyncFailedException("No consensus from servers");
  }

  private boolean haveConsensus () {
    return this.acks > (this.restClients.size() / 2);
  }
}
