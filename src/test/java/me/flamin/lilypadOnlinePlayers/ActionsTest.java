package me.flamin.lilypadOnlinePlayers;

import org.junit.Test;

import static org.junit.Assert.*;

public class ActionsTest {
    @Test
    public void test_getID() {
        assertEquals(Actions.ADD.getID(), 0);
        assertEquals(Actions.REMOVE.getID(), 1);
        assertEquals(Actions.MOVEWORLD.getID(), 2);
        assertEquals(Actions.VANISH.getID(), 3);
        assertEquals(Actions.SHOW.getID(), 4);
        assertEquals(Actions.RESEND.getID(), 5);
    }

    @Test
    public void test_get() {
        assertEquals(Actions.ADD, Actions.get(0));
        assertEquals(Actions.REMOVE, Actions.get(1));
        assertEquals(Actions.MOVEWORLD, Actions.get(2));
        assertEquals(Actions.VANISH, Actions.get(3));
        assertEquals(Actions.SHOW, Actions.get(4));
        assertEquals(Actions.RESEND, Actions.get(5));
    }
}