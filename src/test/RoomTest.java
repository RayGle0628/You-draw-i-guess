package test;

import org.junit.jupiter.api.Test;
import server.Room;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    Room room = new Room("room1", null);
    private ArrayList<String> words;

    @Test
    public void testResetScores() {
        fail("Not yet implemented");
    }

    @Test
    public void testFinalScores() {
        fail("Not yet implemented");
    }

    @Test
    public void testRoom() {
        fail("Not yet implemented");
    }

    @Test
    public void testWordList() {
        room.wordList();
        System.out.println(room.getWords().size());
        assertEquals(50, room.getWords().size());
    }

    @Test
    public void testBeginGame() {
        fail("Not yet implemented");
    }

    @Test
    public void testStartRound() {
        fail("Not yet implemented");
    }

    @Test
    public void testEndRound() {
        fail("Not yet implemented");
    }

    @Test
    public void testClearCanvas() {
        fail("Not yet implemented");
    }

    @Test
    public void testSelectNextDrawer() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetRoomName() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddUser() {
        fail("Not yet implemented");
    }

    @Test
    public void testRemoveUser() {
        fail("Not yet implemented");
    }

    @Test
    public void testDisperseMessage() {
        fail("Not yet implemented");
    }

    @Test
    public void testParseGuess() {
        room.setCurrentWord("Dog");
		assertTrue(room.parseGuess("Dog"));
        assertTrue(room.parseGuess(" Dog "));
        assertTrue(room.parseGuess("Cat Dog Man"));
        assertFalse(room.parseGuess("CatDog"));
        assertFalse(room.parseGuess("Dogman"));
        assertFalse(room.parseGuess("CatDogman"));
    }

    @Test
    public void testDisperseStroke() {
        fail("Not yet implemented");
    }

    @Test
    public void testCurrentUserList() {
        fail("Not yet implemented");
    }

    @Test
    public void testCompareTo() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPopulation() {
        fail("Not yet implemented");
    }
}
