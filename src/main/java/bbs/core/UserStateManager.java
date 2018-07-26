package bbs.core;

import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class UserStateManager {
  private static ConcurrentHashMap<String, User> users;

  private UserStateManager() {
  }

  public static void create() {
    users = new ConcurrentHashMap<String, User>();
  }

  public static void add(User user) throws IllegalArgumentException, IllegalStateException {
    // Check if the user is valid
    System.out.println(user);
    if (!user.isValid()) {
      throw new IllegalArgumentException();
    }
    // Atomic update
    users.compute(user.getStringId(), (id, prevUser) -> {
      if (prevUser == null) {
        return user;
      }
      // Set new user if timestamp is greater then prev user
      if (ChronoUnit.SECONDS.between(user.getTimestamp(),prevUser.getTimestamp()) < 0){
        return user;
      }
      // No update was perfomed, throwing leaves everything unchanged
      throw new IllegalStateException();
    });
  }

  public static User find(String id) throws NoSuchElementException {
    System.out.println("Searching: " + id);
    User user = users.get(id);
    if (user == null) {
      throw new NoSuchElementException();
    }
    return user;
  }
}
