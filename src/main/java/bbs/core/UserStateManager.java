package bbs.core;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class UserStateManager {
  private static ConcurrentHashMap<String, User> users;

  private UserStateManager() {
  }

  public static void create() {
    users = new ConcurrentHashMap<String, User>();
  }

  public static void add(User user) throws IllegalArgumentException {
    // Check if the user is valid
    System.out.println(user);
    if (!user.isValid()) {
      throw new IllegalArgumentException();
    }
    // Insert only if it doesn't exist
    users.putIfAbsent(user.getStringId(), user);
  }

  public static User find(String id) throws NoSuchElementException {
    System.out.println(id);
    User user = users.get(id);
    if (user == null) {
      throw new NoSuchElementException();
    }
    return user;
  }
}
