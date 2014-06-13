package me.flamin.lilypadOnlinePlayers;

import java.util.HashMap;
import java.util.Map;

public enum Actions {
    LEGACY_ADD(0),            // 0  | name | server | world | visibility
    LEGACY_REMOVE(1),         // 1  | name
    LEGACY_MOVEWORLD(2),      // 2  | name | world
    LEGACY_VANISH(3),         // 3  | name
    LEGACY_SHOW(4),           // 4  | name
    RESEND(5),                // 5
    ADD(6),                   // 6  | name | uuid | world | visibility
    REMOVE(7),                // 7  | name | uuid
    MOVEWORLD(8),             // 8  | name | uuid | world
    VANISH(9),                // 9  | name | uuid
    SHOW(10);                 // 10 | name | uuid

    final int id;
    private static final Map<Integer, Actions> cachedValues = new HashMap<Integer, me.flamin.lilypadOnlinePlayers.Actions>();

    static {
        for(Actions a : Actions.values())
            cachedValues.put(a.getID(), a);
    }

    private Actions(int i) {
        id = i;
    }

    public String getIDString() { return Integer.toString(id); }

    int getID() {
        return id;
    }

    public static Actions get(int ID) {
        return cachedValues.get(ID);
    }
}