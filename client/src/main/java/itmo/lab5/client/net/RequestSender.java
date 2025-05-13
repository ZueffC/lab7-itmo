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

    private RequestSender() {}

    public static RequestSender getInstance() {
        RequestSender instance = RequestSender.instance;
        if (instance == null) {
            synchronized (RequestSender.class) {
                instance = RequestSender.instance;
                if (instance == null) {
                    RequestSender.instance = instance = new RequestSender();
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

        try (SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress(host, port))) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(packet);
            }

            byte[] data = baos.toByteArray();
            ByteBuffer writeBuffer = ByteBuffer.wrap(data);
            clientChannel.write(writeBuffer);
            
            ByteBuffer readBuffer = ByteBuffer.allocate(65536);
            int bytesRead = clientChannel.read(readBuffer);

            if (bytesRead > 0) {
                readBuffer.flip();
                byte[] responseBytes = new byte[readBuffer.remaining()];
                readBuffer.get(responseBytes);
                return new String(responseBytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "No response from server";
    }
}
