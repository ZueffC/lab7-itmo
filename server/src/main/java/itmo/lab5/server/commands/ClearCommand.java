/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itmo.lab5.server.commands;

import itmo.lab5.shared.models.Flat;
import java.util.HashMap;

/**
 *
 * @author oxff
 */
public class ClearCommand {
        public static String execute(HashMap<Integer, Flat> flats) {
        if (null == flats || flats.isEmpty())
            return "Collection is empty now!";

        flats.clear();

        return "Collection has been cleared! " 
                + "(Items now: " + flats.size() + ")";
    }
}
