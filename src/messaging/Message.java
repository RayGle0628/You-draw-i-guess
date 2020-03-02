package messaging;

import java.io.Serializable;

//TODO
/**
 * Messages are used to send information between clients and data that is more complex than single data types. The
 * Command enum defined the type of message and data is a series of strings to go with the command.
 * For example A message could be LOGIN command with the username and password as data.
 * Commands are defined in the Command Enum
 */
public class Message implements Serializable {
    Command command;

    public Message(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
