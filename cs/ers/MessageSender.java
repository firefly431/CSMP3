package cs.ers;

/**
 * Interface for OOP
 */
public interface MessageSender {
    /**
     * Called by client to send a message to the server
     * without violating encapsulation
     */
    public void send(String data);
}
