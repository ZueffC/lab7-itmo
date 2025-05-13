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
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 65536;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private static void checkWritePermissions(Path path) throws IOException {
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                throw new SecurityException("Path is a directory: " + path);
            }
            if (!Files.isWritable(path)) {
                throw new SecurityException("File is not writable: " + path);
            }
        } else {
            Path parentDir = path.getParent();
            if (parentDir == null || !Files.exists(parentDir)) {
                throw new SecurityException("Parent directory does not exist: " + parentDir);
            }
            if (!Files.isDirectory(parentDir)) {
                throw new SecurityException("Parent path is not a directory: " + parentDir);
            }
            if (!Files.isWritable(parentDir)) {
                throw new SecurityException("Directory is not writable: " + parentDir);
            }
        }
    }
  
    public static Path getDataFileFromEnv(String envVariable) throws IOException {
        String envPath = System.getenv(envVariable);
        final Path path;

        if (envPath == null || envPath.trim().isEmpty())
            throw new IllegalArgumentException(
                    "Environment variable '" + envVariable + "' is not set or empty.");

        try {
            path = Paths.get(envPath);
        } catch (InvalidPathException ex) {
            throw new IllegalArgumentException(
                    "The path provided in environment variable '" +
                           envVariable + "' is invalid: " + ex.getMessage(),
                    ex);
        }                   

        if (!Files.exists(path))
            throw new IllegalArgumentException("The file at path '" + path + "' does not exist.");

        if (!Files.isRegularFile(path))
            throw new IllegalArgumentException("The path '" + path + "' is not a file. Check twice!");

        if (!Files.isReadable(path))
            throw new IllegalArgumentException("The file at path '" + path + "' is not readable. " +
                    "Check file permissions!");

        return path;
    }
    
    public static void main(String[] args) {
        Path dataFilePath = null;
        try (Selector selector = Selector.open();
                ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            dataFilePath = getDataFileFromEnv("LAB5_DATA");
            
            var collection = new HashMap<Integer, Flat>();
            try {
                collection = Reader.parseCSV(dataFilePath.toFile());
            } catch (Exception e) {
                logger.error("Can't parse collection from file!", e);
            }
            
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("Server started on port " + PORT);

            while (true) {
                selector.select();

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable())
                        handleAccept(key, selector);
                    else if (key.isReadable())
                        handleRead(key, collection, dataFilePath);
                }
            }
        } catch (IOException e) {
            logger.error("Exception: ", e);
        }
    }

    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        logger.debug("Accepted connection from " + clientChannel.getRemoteAddress());
    }

    private static void handleRead(SelectionKey key, HashMap<Integer, Flat> collection, Path dataFilePath) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            clientChannel.close();
            logger.debug("Client disconnected");
            return;
        }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            if(obj instanceof DataPacket) {
                DataPacket packet = (DataPacket) obj;
                
                String response = CommandManager.getAppropriateCommand(packet, collection, dataFilePath);
                logger.debug("Recieved command: " + packet.getType());
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
        
        logger.debug("Sending response...");
    }
}
