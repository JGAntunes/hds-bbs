package bbs.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.NoSuchElementException;

public class Utils {

  // Singleton
  private Utils () {}

  public static X509EncodedKeySpec loadPublicKeyFromPEMString (String key) throws NoSuchAlgorithmException {
    // Strip down the key delimiters
    // Remove all the new lines
    String cleanKey = key
        .replace("-----BEGIN PUBLIC KEY-----\n", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replace("\n", "");
    // Base64 decode it
    byte[] decoded = Base64.getDecoder().decode(cleanKey.getBytes(StandardCharsets.UTF_8));
    return new X509EncodedKeySpec(decoded);
  }

  public static PKCS8EncodedKeySpec loadPrivateKeyFromPEMString (String key) throws NoSuchAlgorithmException {
    // Strip down the key delimiters
    // Remove all the new lines
    String cleanKey = key
        .replace("-----BEGIN PRIVATE KEY-----\n", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replace("\n", "");
    // Base64 decode it
    byte[] decoded = Base64.getDecoder().decode(cleanKey.getBytes(StandardCharsets.UTF_8));
    return new PKCS8EncodedKeySpec(decoded);
  }

  public static X509EncodedKeySpec readPublicKeyFromFile (String path) throws IOException, NoSuchAlgorithmException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    String keyString = new String(encoded, StandardCharsets.UTF_8);
    return loadPublicKeyFromPEMString(keyString);
  }

  public static PKCS8EncodedKeySpec readPrivateKeyFromFile (String path) throws IOException, NoSuchAlgorithmException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    String keyString = new String(encoded, StandardCharsets.UTF_8);
    return loadPrivateKeyFromPEMString(keyString);
  }

  public static byte[] getKeyDigest (PublicKey key) throws NoSuchAlgorithmException {
    MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
    return sha256Digest.digest(key.getEncoded());
  }
}
