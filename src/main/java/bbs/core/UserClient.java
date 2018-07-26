package bbs.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ThreadLocalRandom;

public class UserClient extends User {

  private PrivateKey privKey;

  @JsonIgnore
  public PrivateKey getPrivKey() {
    return privKey;
  }

  @JsonIgnore
  public void setPrivKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
    KeyFactory kf = KeyFactory.getInstance("RSA");
    this.privKey = (RSAPrivateKey) kf.generatePublic(Utils.loadPrivateKeyFromPEMString(key));
  }

  @JsonIgnore
  public void setPrivKey(PrivateKey key) {
    this.privKey = key;
  }

  @Override
  @JsonIgnore
  public boolean isValid() {
    try {
      // The attributes must be set
      if (this.getStringId() == null || this.getPubKey() == null || this.privKey == null) {
        return false;
      }
      // Id should be based on the sha256 digest of the key
      // Compare digests
      if (!MessageDigest.isEqual(this.getByteId(), Utils.getKeyDigest(this.getPubKey()))) {
        return false;
      }
      // Validate key pair
      // Create a challenge
      byte[] challenge = new byte[10000];
      ThreadLocalRandom.current().nextBytes(challenge);

      // Sign using the private key
      Signature sig = Signature.getInstance("SHA256withRSA");
      sig.initSign(this.privKey);
      sig.update(challenge);
      byte[] signature = sig.sign();

      // Verify signature using the public key
      sig.initVerify(this.getPubKey());
      sig.update(challenge);

      return sig.verify(signature);

      // Catch everything and return false otherwise
    } catch (Exception e) {
      return false;
    }
  }

  public void sign(Message message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    Signature signature = Signature.getInstance("SHA256withRSA");
    signature.initSign(this.privKey, new SecureRandom());
    signature.update(message.getContentToSign());
    message.setSignature(signature.sign());
  }
}
