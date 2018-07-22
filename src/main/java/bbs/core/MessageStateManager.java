package bbs.core;

import java.util.concurrent.ConcurrentHashMap;

public class MessageStateManager {
  private static ConcurrentHashMap<String, ConcurrentHashMap<String, Message>> messages;

  public static void create() {
    messages = new ConcurrentHashMap<String, ConcurrentHashMap<String, Message>>();
  }
}
