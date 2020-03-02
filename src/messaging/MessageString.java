package messaging;

public class MessageString extends Message {
    String[] data;

    public MessageString(Command command, String... data) {
        super(command);
        this.command = command;
        this.data = data;
    }

    public String[] getData() {
        return data;
    }
}
