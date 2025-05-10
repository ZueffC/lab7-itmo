package itmo.lab5.client.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import itmo.lab5.shared.CommandType;
import itmo.lab5.shared.DataPacket;
import itmo.lab5.shared.models.Flat;

public class RequestSender {
    private static RequestSender instance;

    private final String host;
    private final int port;

    private RequestSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static synchronized RequestSender init(String host, int port) {
        if (instance != null)
            throw new IllegalStateException("RequestSender already initialized");
    
        instance = new RequestSender(host, port);
        return instance;
    }

    public static synchronized RequestSender getInstance() {
        if (instance == null)
            throw new IllegalStateException("RequestSender not initialized. Call init() first.");

        return instance;
    }

    public String sendRequest(CommandType type, Integer id, Flat flat) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            DataPacket packet = new DataPacket(type, id, flat);
            oos.writeObject(packet);

            String response = ois.readUTF();
            return response;

        } catch (IOException e) {
            System.err.println("Error sending request: " + e.getMessage());
            return "ERROR";
        }
    }
}