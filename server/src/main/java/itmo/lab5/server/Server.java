package itmo.lab5.server;

import itmo.lab5.server.io.Reader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import itmo.lab5.shared.DataPacket;
import itmo.lab5.shared.models.Flat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Server {
    public static void main(String[] args) throws FileNotFoundException {
        int port = 8080;
        System.out.println("Starting TCP server on port " + port);
        HashMap<Integer, Flat> collection = null;
        
        try {
            collection = Reader.parseCSV(new File("data.csv"));
        } catch(Exception e) {
            System.out.println("There's a problem while trying to read file: " 
                    + e.getMessage());
            return;
        }
        
        
        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
                Socket socket = ss.accept();
                requestHandler(socket, collection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void requestHandler(Socket socket, HashMap<Integer, Flat> collection) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        
        DataPacket packet = (DataPacket) ois.readObject();
        var result = CommandManager.getAppropriateCommand(packet, collection);

        oos.writeUTF(result);
        oos.flush();
    }
}