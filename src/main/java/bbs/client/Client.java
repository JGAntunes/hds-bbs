package bbs.client;

import bbs.core.Message;
import bbs.core.UserClient;
import bbs.core.Utils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class Client {

  private final String uri = "http://localhost:8080/bbs";

  public static void main(String[] args) {
    String privateKeyPath = args[0];
    String publicKeyPath = args[1];
    String[] hosts = args[2].split(",");
    List<BBSClient> bbsClients = new ArrayList<>();
    UserClient me;

    System.out.println("Reading private key from: " + privateKeyPath);
    System.out.println("Reading public key from: " + publicKeyPath);
    for (String host : hosts) {
      System.out.println("Connecting to BBS API host: " + publicKeyPath);
      bbsClients.add(new BBSClient(host));
    }
    Operations operations = new Operations(bbsClients);

    System.out.println("Reading client config");
    try {
      // Create me
      KeyFactory kf = KeyFactory.getInstance("RSA");
      RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(Utils.readPublicKeyFromFile(publicKeyPath));
      RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(Utils.readPrivateKeyFromFile(privateKeyPath));

      me = new UserClient();
      me.setPubKey(pubKey);
      me.setPrivKey(privKey);
      me.setId(DatatypeConverter.printHexBinary(Utils.getKeyDigest(pubKey)));

      System.out.println("Config processing done");

      System.out.println(help());
      while (true) {
        System.out.print("> ");
        String[] input = System.console().readLine().split(" ", 2);

        switch (input[0]) {
          case "q":
            System.out.println("Exit!");
            System.exit(0);
          case "h":
            System.out.println(help());
            break;
          case  "register":
            operations.register(me);
            break;
          case "post":
            operations.post(me, input[1]);
            break;
          case "read":
            String[] extraParams = input[1].split(" ");
            List<Message> messages = operations.read(extraParams[0], new Integer(extraParams[1]));
            for(Message m : messages) {
              System.out.println(m);
            }
            break;
          case "user-info":
            System.out.println(operations.userInfo(input[1]));
            break;
        }
      }
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
  }

  public static String help() {
    return "BBS Commands:\n" +
        "- q: Quit\n" +
        "- h: This message\n" +
        "- register: Register this new user\n" +
        "- post <message>: Post a message to the board\n" +
        "- read <userId> <count>: Read <count> messages from the user <userId>\n";

  }
}
