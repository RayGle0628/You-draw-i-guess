package messaging;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Message is a class used for the communication protocol between clients and the server. Messages are made up of two
 * main components. First is the Command enum type. This acts as a header that tells the client/server what to do.
 * Secondly is contains data in the form of strings. This is used to transport additional information required for an
 * operation specified in the message.
 */
public class Message implements Serializable {

    Command command;
    String[] data;

    /**
     * Constructor for the message object.
     *
     * @param command the Command header for the message.
     * @param data    the string data to be sent in the message object.
     */
    public Message(Command command, String... data) {
        this.command = command;
        this.data = data;
    }

    /**
     * Getter for the command header
     *
     * @return the Command header.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * getter for the message data.
     *
     * @return the data.
     */
    public String[] getData() {
        return data;
    }

    /**
     * Represents the message as a human-readable string.
     *
     * @return the message is its string representation.
     */
    @Override
    public String toString() {
        return "Message{" + "command=" + command + ", data=" + Arrays.toString(data) + '}';
    }
}
