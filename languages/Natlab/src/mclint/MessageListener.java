package mclint;

/**
 * A callback for those interested in a McLint message.
 */
public interface MessageListener {
  /**
   * Called when a message is reported by an analysis. The typical use of this method
   * is to trigger a program transformation that will address the issue causing the
   * message.
   * @return whether the message was addressed; if <tt>true</tt>, it won't be included
   * in the report.
   */
  boolean messageReported(Message message);
}
