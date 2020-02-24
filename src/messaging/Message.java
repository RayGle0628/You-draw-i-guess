package messaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Messages are used to send information between clients and data that is more complex than single data types. The
 * COmmand enum defined the type of message and data is a series of strings to go with the command.
 * For example A message could be LOGIN command with the username and password as data.
 * Commands are defined in the Command Enum
 */
public class Message implements Serializable {

    Command command;
    String[] data;
    ArrayList<String> roomNames;

    public Boolean getBool() {
        return bool;
    }

    Boolean bool;

    public Message() {
        command = null;
        data = null;
    }

    public Message(Command command, String... data) {
        this.command = command;
        this.data = data;
    }
    public Message(Command command, ArrayList<String> dataAL) {
        this.command = command;
        roomNames = dataAL;

    }
    public Message(Command command, Boolean bool) {
        this.command = command;
        this.bool=bool;

    }
    public Command getCommand() {
        return command;
    }

    public String[] getData() {
        return data;
    }

    public ArrayList<String> getRoomNames() {
        return roomNames;
    }

    @Override
    public String toString() {
        return "This message is a "+command;
    }
}
