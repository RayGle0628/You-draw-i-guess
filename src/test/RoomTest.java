package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import server.DatabaseManager;
import server.Room;

public class RoomTest {
	
	Room room = new Room("room1",null);
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
		assertEquals(true, room.parseGuess("Dog"));
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
