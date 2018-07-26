package bbs.core;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class UserStateManager {
  private static ConcurrentHashMap<String, UserServer> users;

  private UserStateManager() {
  }

  public static void create() {
    users = new ConcurrentHashMap<String, UserServer>();
  }

  public static void add(UserServer user) throws IllegalArgumentException {
    // Check if the user is valid
    System.out.println(user);
    if (!user.isValid()) {
      throw new IllegalArgumentException();
    }
    // Insert only if it doesn't exist
    users.putIfAbsent(user.getStringId(), user);
  }

  public static UserServer find(String id) throws NoSuchElementException {
    System.out.println(id);
    UserServer user = users.get(id);
    if (user == null) {
      throw new NoSuchElementException();
    }
    return user;
  }
}
