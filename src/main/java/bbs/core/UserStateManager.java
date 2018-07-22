package bbs.core;

import java.util.concurrent.ConcurrentHashMap;

public class UserStateManager {
  private static ConcurrentHashMap<String, User> users;

  public static void create() {
    users = new ConcurrentHashMap<String, User>();
  }
}
