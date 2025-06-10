package itmo.lab5.server.commands;

import java.sql.SQLException;
import java.util.HashMap;

import itmo.lab5.server.Collection;
import itmo.lab5.server.DatabaseManager;
import itmo.lab5.shared.DataPacket;
import itmo.lab5.shared.models.Flat;

public class CheckAffiliationCommand {
    public static String execute(DataPacket pack, Collection collection, DatabaseManager dbManager) {        
        String nick, password = null;

        try {
            nick = pack.getNick();
            password = pack.getPassword();
            
            if(nick == null || password == null)
                return "No nick or password; false";

            if(!dbManager.userExists(nick, password))
                return "No such user; false";

            HashMap<Integer, Flat> flats = dbManager.getAllFlats();
            Integer reqFlatId = pack.getFlat().getId();

            boolean exists = flats.values().stream()
                .anyMatch(flat -> ((flat.getId() == reqFlatId) && (flat.getOwnerName().equals(nick))));
            
            if(!exists)
                return "No such flat; false";

            return "true";
        } catch (SQLException e) {
            return "Exception; false";
        }
    }
}