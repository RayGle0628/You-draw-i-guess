package messaging;

import java.io.Serializable;

/**
 * Messages are used to send information between clients and data that is more complex than single data types. The
 * COmmand enum defined the type of message and data is a series of strings to go with the command.
 * For example A message could be LOGIN command with the username and password as data.
 * Commands are defined in the Command Enum
 */
public class Message implements Serializable {

    Command command;
    String[] data;

    public Message() {
        command = null;
        data = null;
    }

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
