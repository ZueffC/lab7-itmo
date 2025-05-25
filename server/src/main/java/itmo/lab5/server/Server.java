package itmo.lab5.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import itmo.lab5.shared.DataPacket;

public class Server {
    private static final int PORT = 7070;
    private static final ExecutorService readPool = Executors.newFixedThreadPool(4);
    private static final ForkJoinPool processPool = new ForkJoinPool(10);
    private static final ExecutorService writePool = Executors.newCachedThreadPool();

    private static final Map<SocketChannel, ByteBuffer> writeBuffers = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static Collection collection;
    private static DatabaseManager dbManager;

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();

        serverSocket.bind(new InetSocketAddress(PORT));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        logger.info("Server started on port: " + PORT);

        try {
            dbManager = DatabaseManager.getInstance(
                "jdbc:postgresql://127.0.0.1:5432/studs", 
                "s489388", 
                "tonL/3319",
                "s489388"
            );
        } catch (SQLException e) {
            logger.error("Can't connect to DB: " + e.toString());
            System.exit(1);
        }

        try {
            collection = Collection.getInstance(dbManager);
        } catch (SQLException e) {
            logger.error("Can't parse collection!");
            System.exit(1);
        }

        while (true) {
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                try {
                    if (key.isAcceptable())
                        handleAccept(serverSocket, selector);
                    else if (key.isReadable()) {
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ); 
                        readPool.submit(() -> handleRead(key, collection));
                    } else if (key.isWritable())
                        writePool.submit(() -> handleWrite(key));

                } catch (Exception e) {
                    key.cancel();
                    key.channel().close();
                }
            }
        }
    }

    private static void handleAccept(ServerSocketChannel serverSocket, Selector selector) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        logger.debug("New connection from: " + client.getRemoteAddress());
    }

    private static void handleRead(SelectionKey key, Collection collection) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        try {
            int read = client.read(buffer);
            if (read == -1) {
                client.close();
                return;
            }

            buffer.flip();
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
            ObjectInputStream ois = new ObjectInputStream(bais);
            DataPacket request = (DataPacket) ois.readObject();

            processPool.submit(() -> {
                String response = processRequest(request);
                ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());

                synchronized (writeBuffers){
                    writeBuffers.put(client, responseBuffer);
                }

                key.interestOps(SelectionKey.OP_WRITE);
                key.selector().wakeup(); 
            });

        } catch (Exception e) {
            try {
                client.close();
            } catch (IOException ignored) {}
        }
    }

    private static String processRequest(DataPacket request) {
        logger.debug("Got command: " + request.getType());
        return CommandManager.getAppropriateCommand(request, collection, dbManager);
    }

    private static void handleWrite(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer;
        synchronized (writeBuffers) {
            buffer = writeBuffers.get(client);
        }

        try {
            client.write(buffer);
            if (!buffer.hasRemaining()) {
                synchronized (writeBuffers) {
                    writeBuffers.remove(client);
                }
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            try {
                client.close();
            } catch (IOException ignored) {}
        }
    }
}
