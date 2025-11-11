package killdoctorlucky.model.occupants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import killdoctorlucky.model.Board;
import killdoctorlucky.model.Room;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Pet.
 */
public class PetTest {
  
  private Board board;
  private Room room1;
  private Room room2;
  private Room room3;
  private Pet pet;
  
  /**
   * Sets up the test fixture.
   * Creates a board with 4 connected rooms and a pet for testing.
   */
  @Before
  public void setUp() {
    board = new Board();
    
    room1 = new Room("Kitchen");
    room1.setGeometry(0, 0, 5, 5);
    
    room2 = new Room("Dining Room");
    room2.setGeometry(6, 0, 10, 5);
    
    room3 = new Room("Library");
    room3.setGeometry(0, 6, 5, 10);
    
    board.addRoom(room1);
    board.addRoom(room2);
    board.addRoom(room3);
    
    board.connectRooms(room1, room2);
    board.connectRooms(room1, room3);
    
    pet = new Pet("Fortune the Cat", room1);
  }
  
  @Test
  public void testPetCreation() {
    assertEquals("Fortune the Cat", pet.getName());
    assertEquals(room1, pet.getCurrentRoom());
    assertTrue(room1.getOccupants().contains(pet));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testPetCreationNullName() {
    new Pet(null, room1);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testPetCreationEmptyName() {
    new Pet("", room1);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testPetCreationNullRoom() {
    new Pet("Fluffy", null);
  }
  
  @Test
  public void testMoveToRoom() {
    assertTrue(pet.moveToRoom(room2));
    assertEquals(room2, pet.getCurrentRoom());
    assertFalse(room1.getOccupants().contains(pet));
    assertTrue(room2.getOccupants().contains(pet));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToNullRoom() {
    pet.moveToRoom(null);
  }
  
  @Test
  public void testCanBeSeenBySameRoom() {
    Player player = new Player("Alice", room1);
    assertTrue(pet.canBeSeenBy(player, board));
  }
  
  @Test
  public void testInitializeDfsPath() {
    pet.initializeDfsPath(board);
    
    // After initialization, pet should still be in starting room
    assertEquals(room1, pet.getCurrentRoom());
  }
  
  @Test
  public void testWanderNext() {
    pet.initializeDfsPath(board);
    
    Room startRoom = pet.getCurrentRoom();
    pet.wanderNext();
    
    // Pet should have moved to a different room
    assertNotEquals(startRoom, pet.getCurrentRoom());
  }
  
  @Test
  public void testWanderNextCycles() {
    pet.initializeDfsPath(board);
    
    // Record the path
    Room[] path = new Room[10];
    for (int i = 0; i < 10; i++) {
      pet.wanderNext();
      path[i] = pet.getCurrentRoom();
    }
    
    // The path should eventually cycle
    // At least one room should appear multiple times
    boolean foundCycle = false;
    for (int i = 0; i < path.length - 1; i++) {
      for (int j = i + 1; j < path.length; j++) {
        if (path[i].equals(path[j])) {
          foundCycle = true;
          break;
        }
      }
    }
    assertTrue("Pet should cycle through rooms", foundCycle);
  }
  
  @Test
  public void testToString() {
    String result = pet.toString();
    assertTrue(result.contains("Fortune the Cat"));
    assertTrue(result.contains("Kitchen"));
  }
}