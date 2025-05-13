package itmo.lab5.client.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import itmo.lab5.shared.CommandType;
import itmo.lab5.shared.DataPacket;
import itmo.lab5.shared.models.Flat;

import java.net.*;
import java.util.concurrent.TimeUnit;

public class RequestSender {
    private static RequestSender instance;
    private final String host;
    private final int port;
    private static final int MAX_RETRIES = 5; // Максимальное число попыток
    private static final long RETRY_DELAY_MS = 2000; // Задержка между попытками в миллисекундах

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
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 5000); // timeout на подключение
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                DataPacket packet = new DataPacket(type, id, flat);
                oos.writeObject(packet);

                String response = ois.readUTF();
                return response;

            } catch (IOException e) {
                attempt++;
                System.err.println("Connection failed (attempt " + attempt + "/" + MAX_RETRIES + "). Retrying in "
                        + (RETRY_DELAY_MS / 1000) + " seconds...");
                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "Connection interrupted.";
                }
            }
        }

        return "Server is unreachable after " + MAX_RETRIES + " attempts. Please try again later.";
    }
}
