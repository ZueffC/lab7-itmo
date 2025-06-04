package itmo.lab5.server.commands;

import itmo.lab5.server.Collection;
import itmo.lab5.server.DatabaseManager;
import itmo.lab5.shared.DataPacket;

public class CheckRights {
    public static String execute(DataPacket pack, DatabaseManager manager, Collection collection) {
        if(!collection.containsFlat(pack.getFlat().getId()))
            return "false";
        
        try {
            if(manager.userExists(pack.getNick(), pack.getPassword()) && pack.getNick() == collection.getFlat(pack.getFlat().getId()).getOwnerName())
                return "true";
        } catch(Exception e) {
            return "false";
        }

        
        return "false";
    }
}
