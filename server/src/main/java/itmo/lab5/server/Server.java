package itmo.lab5.server;

import itmo.lab5.server.io.Reader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import itmo.lab5.shared.DataPacket;
import itmo.lab5.shared.models.Flat;
import java.io.File;
import java.util.HashMap;

public class Server {
    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 65536;

    public static void main(String[] args) {
        try (Selector selector = Selector.open();
                ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            var collection = new HashMap<Integer, Flat>();
            try {
                collection = Reader.parseCSV(new File("data.csv"));
            } catch (Exception e) {
                System.out.println("Can't parse collection from file!");
            }
            
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started on port " + PORT);

            while (true) {
                selector.select();

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable())
                        handleAccept(key, selector);
                    else if (key.isReadable())
                        handleRead(key, collection);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Accepted connection from " + clientChannel.getRemoteAddress());
    }

    private static void handleRead(SelectionKey key, HashMap<Integer, Flat> collection) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            clientChannel.close();
            System.out.println("Client disconnected");
            return;
        }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            if(obj instanceof DataPacket) {
                DataPacket packet = (DataPacket) obj;
                
                String response = CommandManager.getAppropriateCommand(packet, collection);
                sendResponse(clientChannel, response);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponse(SocketChannel clientChannel, String response) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
        while (buffer.hasRemaining()) {
            clientChannel.write(buffer);
        }
    }
}
