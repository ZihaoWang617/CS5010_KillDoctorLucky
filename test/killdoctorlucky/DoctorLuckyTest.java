package killdoctorlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DoctorLucky functionality.
 */
public class DoctorLuckyTest {
  private Room kitchen;
  private Room dining;
  private Room living;
  private DoctorLucky doctor;
  private Player alice;
  
  /**
   * Sets up the test fixture before each test.
   * Initializes Room objects (kitchen, dining, living) with connections between them
   * and creates a DoctorLucky instance starting in the kitchen.
   */
  
  @Before
  public void setUp() {
    kitchen = new Room("Kitchen", true);
    dining = new Room("Dining Room", true);
    living = new Room("Living Room", true);

    kitchen.addConnection(dining);
    dining.addConnection(living);

    doctor = new DoctorLucky(kitchen);
  }

  @Test
  public void testDoctorCreation() {
    assertEquals(kitchen, doctor.getCurrentRoom());
    assertTrue(kitchen.isOccupiedBy(doctor));
  }

  @Test
  public void testSequentialMovement() {
    // Set movement sequence
    List<Room> sequence = new ArrayList<>();
    sequence.add(kitchen);
    sequence.add(dining);
    sequence.add(living);
    doctor.setMovementSequence(sequence);

    // Test circular movement
    assertEquals(kitchen, doctor.getCurrentRoom());

    doctor.moveNext();
    assertEquals(dining, doctor.getCurrentRoom());

    doctor.moveNext();
    assertEquals(living, doctor.getCurrentRoom());

    doctor.moveNext();
    assertEquals(kitchen, doctor.getCurrentRoom()); // Back to start
  }

  @Test
  public void testIsAloneWithTrue() {
    alice = new Player("Alice", kitchen);

    // Only Alice and Doctor Lucky in kitchen
    assertTrue(doctor.isAloneWith(alice));
  }

  @Test
  public void testIsAloneWithFalse() {
    alice = new Player("Alice", kitchen);
    Player bob = new Player("Bob", kitchen);

    // Multiple players in room
    assertFalse(doctor.isAloneWith(alice));
    assertFalse(doctor.isAloneWith(bob));
  }

  @Test
  public void testIsAloneWithDifferentRooms() {
    alice = new Player("Alice", dining);

    // Doctor in kitchen, Alice in dining
    assertFalse(doctor.isAloneWith(alice));
  }
}