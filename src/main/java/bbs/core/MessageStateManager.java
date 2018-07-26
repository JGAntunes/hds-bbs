package bbs.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MessageStateManager {
  private static ConcurrentHashMap<String, ConcurrentLinkedDeque<Message>> messages;

  // Singleton
  private MessageStateManager() {}

  public static void create() {
    messages = new ConcurrentHashMap<String, ConcurrentLinkedDeque<Message>>();
  }

  public static void add(User user, Message message) throws IllegalArgumentException {
    System.out.println(message);
    try {
      // Confirm the user exists in our data management system
      UserStateManager.find(user.getStringId());
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException();
    }
    // The user and message are both valid
    if (!user.isValid() || !message.isValid(user)) {
      System.out.println("User: " + user.isValid() + " Message: " + message.isValid(user));
      throw new IllegalArgumentException();
    }
    // Initialise the queue of messages for the user if it doesn't exist
    messages.putIfAbsent(user.getStringId(), new ConcurrentLinkedDeque<Message>());
    // Insert it
    messages.get(user.getStringId()).addFirst(message);
  }

  public static List<Message> find(String id, int maxNum) throws NoSuchElementException {
    ConcurrentLinkedDeque<Message> userMessages = messages.get(id);
    try {
      // Return all the messages
      if (maxNum == -1) {
        // Convert the messages to an array and then to a list
        return new LinkedList<Message>(Arrays.asList(userMessages.toArray(new Message[0])));
      }
      int i = 0;
      LinkedList<Message> result = new LinkedList<Message>();
      // The list is already ordered, just need to insert and return
      for (Iterator<Message> it = userMessages.iterator(); it.hasNext() && i < maxNum; i++) {
        Message message = it.next();
        result.addLast(message);
      }
      return result;
    } catch (NullPointerException e) {
      throw new NoSuchElementException();
    }
  }
}
