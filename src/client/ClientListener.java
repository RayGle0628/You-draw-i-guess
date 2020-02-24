package client;

import java.io.DataInputStream;
import java.io.ObjectInputStream;

/**
 * ClientListener listens for messages from the server and executes them as required.
 */
public class ClientListener extends Thread {
    private ObjectInputStream input;
    private DataInputStream inputData;
    public ClientListener() {

    }

    @Override
    public void run() {
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public void setInputData(DataInputStream inputData) {
        this.inputData = inputData;
    }
}
