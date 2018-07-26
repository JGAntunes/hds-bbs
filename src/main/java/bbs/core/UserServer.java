package bbs.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class UserServer extends User {

  @Override
  @JsonIgnore
  public boolean isValid() {
    try {
      // The attributes must be set
      if (this.getStringId() == null || this.getPubKey() == null) {
        return false;
      }
      // Id should be based on the sha256 digest of the key
      // Compare digests
      return MessageDigest.isEqual(this.getByteId(), Utils.getKeyDigest(this.getPubKey()));
      // Catch everything and return false otherwise
    } catch (Exception e) {
      return false;
    }
  }

}

