package itmo.lab5.server.commands;

import java.sql.SQLException;

import itmo.lab5.server.DatabaseManager;
import itmo.lab5.shared.DataPacket;

public class SignInCommand {
    public static String execute(DataPacket packet, DatabaseManager dbManager) {
        var nick = packet.getNick();
        var password = packet.getPassword();
        var errorClause = "Can't authorize such user!";

        try {
            var status = dbManager.userExists(nick, password);
            if(status)
                return "You've been authorized!";
        } catch (SQLException e) {
            return errorClause;
        }

        return errorClause;
    }   
}
