package itmo.lab5.server.commands;

import java.sql.SQLException;

import itmo.lab5.server.DatabaseManager;
import itmo.lab5.shared.DataPacket;

public class SignUpCommand {
    public static String execute(DataPacket packet, DatabaseManager dbManager) {
        var nick = packet.getNick();
        var password = packet.getPassword();

        try {
            var status = dbManager.addUser(nick, password);
            if(status)
                return "User was successfuly created";
            else
                return "Such user already exists!";
        } catch (SQLException e) {
            return "Can't create such user!";
        }
    }   
}
