package killdoctorlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import org.junit.Before;
import org.junit.Test;


/**
 * Test class for Board functionality. Tests board initialization, room
 * management, connections, and visibility.
 */
public class BoardTest {
  private Board board;
  private Room kitchen;
  private Room dining;
  private Room library;
  private Room bedroom;

  /**
   * Sets up the test fixture before each test.
   * Initializes a new Board instance and creates Room objects (kitchen, dining, library)
   * to be used in the tests, adding them to the board.
   */
  @Before
  public void setUp() {
    board = new Board();
    kitchen = new Room("Kitchen", true);
    dining = new Room("Dining Room", true);
    library = new Room("Library", true);
    bedroom = new Room("Bedroom", true);

    board.addRoom(kitchen);
    board.addRoom(dining);
    board.addRoom(library);
  }

  @Test
  public void testBoardCreation() {
    Board newBoard = new Board();
    assertNotNull(newBoard);
    assertEquals(0, newBoard.getRoomCount());
  }

  @Test
  public void testAddRoom() {
    assertEquals(3, board.getRoomCount());

    board.addRoom(bedroom);
    assertEquals(4, board.getRoomCount());
  }

  @Test
  public void testAddDuplicateRoom() {
    Room anotherKitchen = new Room("Kitchen", true);

    try {
      board.addRoom(anotherKitchen);
      fail("Expected IllegalArgumentException for duplicate room");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testAddNullRoom() {
    try {
      board.addRoom(null);
      fail("Expected IllegalArgumentException for null room");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testGetValidRoom() {
    Room retrieved = board.getRoom("Kitchen");
    assertNotNull(retrieved);
    assertEquals("Kitchen", retrieved.getName());
    assertEquals(kitchen, retrieved);
  }

  @Test
  public void testGetInvalidRoom() {
    Room retrieved = board.getRoom("NonExistent Room");
    assertNull(retrieved);
  }

  @Test
  public void testGetRoomNullName() {
    try {
      board.getRoom(null);
      fail("Expected IllegalArgumentException for null name");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testGetRoomEmptyName() {
    try {
      board.getRoom("");
      fail("Expected IllegalArgumentException for empty name");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testConnectRoomsBidirectional() {
    board.connectRooms(kitchen, dining);

    // Check kitchen can reach dining
    List<Room> kitchenConnections = kitchen.getConnections();
    assertTrue(kitchenConnections.contains(dining));

    // Check dining can reach kitchen (bidirectional)
    List<Room> diningConnections = dining.getConnections();
    assertTrue(diningConnections.contains(kitchen));
  }

  @Test
  public void testConnectMultipleRooms() {
    board.connectRooms(kitchen, dining);
    board.connectRooms(dining, library);

    assertEquals(1, kitchen.getConnections().size());
    assertEquals(2, dining.getConnections().size()); // Connected to both kitchen and library
    assertEquals(1, library.getConnections().size());
  }

  @Test
  public void testConnectNullRooms() {
    try {
      board.connectRooms(null, dining);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Expected
    }

    try {
      board.connectRooms(kitchen, null);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testIsValidMoveConnected() {
    board.connectRooms(kitchen, dining);

    assertTrue(board.isValidMove(kitchen, dining));
    assertTrue(board.isValidMove(dining, kitchen));
  }

  @Test
  public void testIsValidMoveNotConnected() {
    board.connectRooms(kitchen, dining);

    assertFalse(board.isValidMove(kitchen, library));
    assertFalse(board.isValidMove(library, kitchen));
  }

  @Test
  public void testGetAdjacentRooms() {
    board.connectRooms(kitchen, dining);
    board.connectRooms(kitchen, library);

    List<Room> adjacent = board.getAdjacentRooms(kitchen);
    assertEquals(2, adjacent.size());
    assertTrue(adjacent.contains(dining));
    assertTrue(adjacent.contains(library));
  }

  @Test
  public void testGetAdjacentRoomsNullRoom() {
    try {
      board.getAdjacentRooms(null);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testCalculateVisibilitySameRoom() {
    Player alice = new Player("Alice", kitchen);
    Player bob = new Player("Bob", kitchen);

    assertTrue(board.calculateVisibility(alice, bob));
  }

  @Test
  public void testCalculateVisibilityConnectedRooms() {
    board.connectRooms(kitchen, dining);

    Player alice = new Player("Alice", kitchen);
    Player bob = new Player("Bob", dining);

    // Connected rooms should be visible to each other
    assertTrue(board.calculateVisibility(alice, bob));
  }

  @Test
  public void testCalculateVisibilityNotConnected() {
    board.connectRooms(kitchen, dining);

    Player alice = new Player("Alice", kitchen);
    Player charlie = new Player("Charlie", library);

    // Not connected, should not be visible
    assertFalse(board.calculateVisibility(alice, charlie));
  }

  @Test
  public void testGetAllRooms() {
    assertEquals(3, board.getAllRooms().size());
    assertTrue(board.getAllRooms().contains(kitchen));
    assertTrue(board.getAllRooms().contains(dining));
    assertTrue(board.getAllRooms().contains(library));
  }

  @Test
  public void testRoomCount() {
    assertEquals(3, board.getRoomCount());

    board.addRoom(bedroom);
    assertEquals(4, board.getRoomCount());
  }
}