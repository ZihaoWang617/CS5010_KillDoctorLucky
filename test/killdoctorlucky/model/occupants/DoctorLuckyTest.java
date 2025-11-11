package killdoctorlucky.model.occupants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import killdoctorlucky.model.Board;
import killdoctorlucky.model.Room;
import org.junit.Before;
import org.junit.Test;


/**
 * Test class for DoctorLucky.
 * Tests both Milestone 2 and Milestone 3 functionality.
 */
public class DoctorLuckyTest {
  
  private Room room1;
  private Room room2;
  private Room room3;
  private Board board;
  
  /**
   * Set up the text feature.
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
    board.connectRooms(room2, room3);
  }
  
  // ===== MILESTONE 2 TESTS =====
  
  @Test
  public void testDoctorLuckyCreation() {
    DoctorLucky doctor = new DoctorLucky(room1);
    
    assertNotNull(doctor);
    assertEquals(room1, doctor.getCurrentRoom());
    assertTrue(room1.getOccupants().contains(doctor));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testDoctorLuckyCreationNullRoom() {
    new DoctorLucky(null);
  }
  
  @Test
  public void testMoveNext() {
    final DoctorLucky doctor = new DoctorLucky(room1);
    List<Room> sequence = new ArrayList<>();
    sequence.add(room1);
    sequence.add(room2);
    sequence.add(room3);
    doctor.setMovementSequence(sequence);
    
    assertEquals(room1, doctor.getCurrentRoom());
    
    doctor.moveNext();
    assertEquals(room2, doctor.getCurrentRoom());
    assertFalse(room1.getOccupants().contains(doctor));
    assertTrue(room2.getOccupants().contains(doctor));
  }
  
  @Test
  public void testMoveNextCycles() {
    DoctorLucky doctor = new DoctorLucky(room1);
    List<Room> sequence = new ArrayList<>();
    sequence.add(room1);
    sequence.add(room2);
    doctor.setMovementSequence(sequence);
    
    doctor.moveNext(); // room1 -> room2
    assertEquals(room2, doctor.getCurrentRoom());
    
    doctor.moveNext(); // room2 -> room1 (cycle)
    assertEquals(room1, doctor.getCurrentRoom());
  }
  
  @Test
  public void testSetMovementSequence() {
    final DoctorLucky doctor = new DoctorLucky(room1);
    List<Room> sequence = new ArrayList<>();
    sequence.add(room2);
    sequence.add(room3);
    sequence.add(room1);
    
    doctor.setMovementSequence(sequence);
    
    // Doctor Lucky should move through the sequence
    doctor.moveNext();
    assertEquals(room2, doctor.getCurrentRoom());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testSetMovementSequenceNull() {
    DoctorLucky doctor = new DoctorLucky(room1);
    doctor.setMovementSequence(null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testSetMovementSequenceEmpty() {
    DoctorLucky doctor = new DoctorLucky(room1);
    List<Room> emptySequence = new ArrayList<>();
    doctor.setMovementSequence(emptySequence);
  }
  
  @Test
  public void testMoveToRoom() {
    DoctorLucky doctor = new DoctorLucky(room1);
    
    assertTrue(doctor.moveToRoom(room2));
    assertEquals(room2, doctor.getCurrentRoom());
    assertFalse(room1.getOccupants().contains(doctor));
    assertTrue(room2.getOccupants().contains(doctor));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToRoomNull() {
    DoctorLucky doctor = new DoctorLucky(room1);
    doctor.moveToRoom(null);
  }
  
  @Test
  public void testIsAloneWith() {
    DoctorLucky doctor = new DoctorLucky(room1);
    Player player = new Player("Alice", room1);
    
    // Doctor Lucky and Alice are alone in room1
    assertTrue(doctor.isAloneWith(player));
  }
  
  @Test
  public void testIsAloneWithDifferentRoom() {
    DoctorLucky doctor = new DoctorLucky(room1);
    Player player = new Player("Alice", room2);
    
    // They're in different rooms
    assertFalse(doctor.isAloneWith(player));
  }
  
  @Test
  public void testIsAloneWithThirdOccupant() {
    DoctorLucky doctor = new DoctorLucky(room1);
    Player alice = new Player("Alice", room1);
    Player bob = new Player("Bob", room1);
    
    // Three occupants in the room
    assertFalse(doctor.isAloneWith(alice));
    assertFalse(doctor.isAloneWith(bob));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testIsAloneWithNullPlayer() {
    DoctorLucky doctor = new DoctorLucky(room1);
    doctor.isAloneWith(null);
  }
  
  @Test
  public void testCanBeSeenBySameRoom() {
    DoctorLucky doctor = new DoctorLucky(room1);
    Player player = new Player("Alice", room1);
    
    assertTrue(doctor.canBeSeenBy(player, board));
  }
  
  @Test
  public void testCanBeSeenByAdjacentRoom() {
    DoctorLucky doctor = new DoctorLucky(room1);
    Player player = new Player("Alice", room2);
    
    // room1 and room2 are connected
    assertTrue(doctor.canBeSeenBy(player, board));
  }
  
  @Test
  public void testCanBeSeenByNonAdjacentRoom() {
    DoctorLucky doctor = new DoctorLucky(room1);
    Player player = new Player("Alice", room3);
    
    // room1 and room3 are not directly connected
    assertFalse(doctor.canBeSeenBy(player, board));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCanBeSeenByNullOccupant() {
    DoctorLucky doctor = new DoctorLucky(room1);
    doctor.canBeSeenBy(null, board);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCanBeSeenByNullBoard() {
    DoctorLucky doctor = new DoctorLucky(room1);
    Player player = new Player("Alice", room1);
    doctor.canBeSeenBy(player, null);
  }
  
  // ===== MILESTONE 3 TESTS =====
  
  @Test
  public void testDoctorLuckyWithHealth() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    
    assertEquals(50, doctor.getHealth());
    assertEquals(50, doctor.getMaxHealth());
    assertTrue(doctor.isAlive());
  }
  
  @Test
  public void testDoctorLuckyWithCustomHealth() {
    DoctorLucky doctor = new DoctorLucky(room1, 100);
    
    assertEquals(100, doctor.getHealth());
    assertEquals(100, doctor.getMaxHealth());
    assertTrue(doctor.isAlive());
  }
  
  @Test
  public void testDefaultHealthConstructor() {
    DoctorLucky doctor = new DoctorLucky(room1);
    
    assertEquals(50, doctor.getHealth());
    assertEquals(50, doctor.getMaxHealth());
    assertTrue(doctor.isAlive());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testDoctorLuckyZeroHealth() {
    new DoctorLucky(room1, 0);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testDoctorLuckyNegativeHealth() {
    new DoctorLucky(room1, -10);
  }
  
  @Test
  public void testTakeDamage() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    
    doctor.takeDamage(10);
    assertEquals(40, doctor.getHealth());
    assertTrue(doctor.isAlive());
  }
  
  @Test
  public void testTakeDamageMultipleTimes() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    
    doctor.takeDamage(10);
    assertEquals(40, doctor.getHealth());
    
    doctor.takeDamage(15);
    assertEquals(25, doctor.getHealth());
    
    doctor.takeDamage(5);
    assertEquals(20, doctor.getHealth());
    assertTrue(doctor.isAlive());
  }
  
  @Test
  public void testTakeDamageLethal() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    
    doctor.takeDamage(50);
    assertEquals(0, doctor.getHealth());
    assertFalse(doctor.isAlive());
  }
  
  @Test
  public void testTakeDamageExceedsHealth() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    
    doctor.takeDamage(100);
    assertEquals(0, doctor.getHealth());
    assertFalse(doctor.isAlive());
  }
  
  @Test
  public void testTakeDamageZero() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    
    doctor.takeDamage(0);
    assertEquals(50, doctor.getHealth());
    assertTrue(doctor.isAlive());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testTakeDamageNegative() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    doctor.takeDamage(-10);
  }
  
  @Test
  public void testIsAlive() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    
    assertTrue(doctor.isAlive());
    
    doctor.takeDamage(30);
    assertTrue(doctor.isAlive());
    
    doctor.takeDamage(20);
    assertFalse(doctor.isAlive());
  }
  
  @Test
  public void testIsAloneWithPlayerSameRoom() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    Player player = new Player("Alice", room1);
    
    assertTrue(doctor.isAloneWithPlayer(player, board));
  }
  
  @Test
  public void testIsAloneWithPlayerDifferentRoom() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    Player player = new Player("Alice", room2);
    
    assertFalse(doctor.isAloneWithPlayer(player, board));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testIsAloneWithPlayerNullPlayer() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    doctor.isAloneWithPlayer(null, board);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testIsAloneWithPlayerNullBoard() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    Player player = new Player("Alice", room1);
    doctor.isAloneWithPlayer(player, null);
  }
  
  @Test
  public void testToString() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    
    String result = doctor.toString();
    assertNotNull(result);
    assertTrue(result.contains("Kitchen"));
    assertTrue(result.contains("50"));
  }
  
  @Test
  public void testHealthAfterMovement() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    doctor.takeDamage(20);
    assertEquals(30, doctor.getHealth());
    
    // Moving shouldn't affect health
    doctor.moveToRoom(room2);
    assertEquals(30, doctor.getHealth());
    assertEquals(room2, doctor.getCurrentRoom());
  }
  
  @Test
  public void testGetMaxHealthNeverChanges() {
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    
    assertEquals(50, doctor.getMaxHealth());
    
    doctor.takeDamage(30);
    assertEquals(50, doctor.getMaxHealth());
    
    doctor.takeDamage(20);
    assertEquals(50, doctor.getMaxHealth());
  }
}