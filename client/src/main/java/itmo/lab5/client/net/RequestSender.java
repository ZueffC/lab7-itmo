package itmo.lab5.client.net;

import itmo.lab5.shared.DataPacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class RequestSender {
    private static volatile RequestSender instance;
    private static String host;
    private static int port;

    private static final int MAX_RETRIES = 5;       
    private static final int RETRY_DELAY_MS = 2000;

    private RequestSender() {}

    public static RequestSender getInstance() {
        if (instance == null) {
            synchronized (RequestSender.class) {
                if (instance == null) {
                    instance = new RequestSender();
                }
            }
        }
        return instance;
    }

    public static void init(String host, int port) {
        RequestSender.host = host;
        RequestSender.port = port;
    }

    public String sendRequest(DataPacket packet) {
        if (host == null || port == 0) {
            throw new IllegalStateException("RequestHandler is not initialized. Call init() first.");
        }

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try (SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress(host, port))) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(packet);
                }

                ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
                clientChannel.write(buffer);
                
                ByteBuffer responseBuffer = ByteBuffer.allocate(1024 * 1024);
                int bytesRead = clientChannel.read(responseBuffer);
                if (bytesRead > 0) {
                    responseBuffer.flip();
                    byte[] responseBytes = new byte[bytesRead];
                    responseBuffer.get(responseBytes);
                    return new String(responseBytes);
                } else {
                    System.err.println("Empty response from server on attempt " + attempt);
                }

            } catch (IOException e) {
                System.err.println("Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        System.out.println("Retrying in " + (RETRY_DELAY_MS / 1000) + " seconds...");
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return "Error during retry: " + ie.getMessage();
                    }
                } else {
                    return "Failed to reach server after " + MAX_RETRIES + " attempts.";
                }
            }
        }

        return "Request failed.";
    }
}
