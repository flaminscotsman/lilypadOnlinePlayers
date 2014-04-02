package me.flamin.lilypadOnlinePlayers;

import java.util.HashMap;
import java.util.Map;

public enum Actions {
    ADD(0),
    REMOVE(1),
    MOVEWORLD(2),
    VANISH(3),
    SHOW(4),
    RESEND(5);

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