package messaging;

/**
 * MessagePath is an extension of the Message class. MessagePath is used in a specific scenario when it is required
 * to send information about a path being drawn between a client and server. Like Message it contains a Command
 * header, but instead of strings, an ArrayList of coordinates is instead sent which contains the information about
 * the location of a drawn path alongside its size and colour.
 */
public class MessagePath extends Message {
    private Path path;

    public MessagePath(Command command, Path path) {
        super(command);
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
