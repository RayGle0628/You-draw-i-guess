package messaging;

import java.io.Serializable;
/**
 * Message is a class used for the communication protocol between clients and the server. Messages are made up of two
 * main components. First is the Command enum type. This acts as a header that tells the client/server what to do.
 * Secondly is contains data in the form of strings. This is used to transport additional information required for an
 * operation specified in the message.
 */

public class Message implements Serializable {
    Command command;
    String[] data;

    public Message(Command command, String... data) {
        this.command = command;
        this.data = data;
    }

    public Command getCommand() {
        return command;
    }

    public String[] getData() {
        return data;
    }
}
