package killdoctorlucky;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DoctorLucky functionality.
 * Tests movement, position tracking, and player interactions.
 */
public class DoctorLuckyTest {
  private Room kitchen;
  private Room dining;
  private Room living;
  private DoctorLucky doctor;
  private Player alice;
  
  /**
   * Sets up the test fixture before each test.
   * Initializes Room objects and creates a DoctorLucky instance.
   */
  @Before
  public void setUp() {
    kitchen = new Room("Kitchen", true);
    dining = new Room("Dining Room", true);
    living = new Room("Living Room", true);

    kitchen.addConnection(dining);
    dining.addConnection(kitchen);
    dining.addConnection(living);
    living.addConnection(dining);

    doctor = new DoctorLucky(kitchen);
  }

  /**
   * Tests DoctorLucky creation in a room.
   */
  @Test
  public void testDoctorCreation() {
    Assert.assertEquals(kitchen, doctor.getCurrentRoom());
    Assert.assertTrue(kitchen.getOccupants().contains(doctor));
  }

  /**
   * Tests sequential movement through rooms.
   */
  @Test
  public void testSequentialMovement() {
    List<Room> sequence = new ArrayList<>();
    sequence.add(kitchen);
    sequence.add(dining);
    sequence.add(living);
    doctor.setMovementSequence(sequence);

    Assert.assertEquals(kitchen, doctor.getCurrentRoom());

    doctor.moveNext();
    Assert.assertEquals(dining, doctor.getCurrentRoom());

    doctor.moveNext();
    Assert.assertEquals(living, doctor.getCurrentRoom());

    doctor.moveNext();
    Assert.assertEquals(kitchen, doctor.getCurrentRoom());
  }

  /**
   * Tests isAloneWith returns true when only player and doctor in room.
   */
  @Test
  public void testIsAloneWithTrue() {
    alice = new Player("Alice", kitchen);
    Assert.assertTrue(doctor.isAloneWith(alice));
  }

  /**
   * Tests isAloneWith returns false with multiple players.
   */
  @Test
  public void testIsAloneWithFalse() {
    alice = new Player("Alice", kitchen);
    Player bob = new Player("Bob", kitchen);
    Assert.assertFalse(doctor.isAloneWith(alice));
    Assert.assertFalse(doctor.isAloneWith(bob));
  }

  /**
   * Tests isAloneWith when in different rooms.
   */
  @Test
  public void testIsAloneWithDifferentRooms() {
    alice = new Player("Alice", dining);
    Assert.assertFalse(doctor.isAloneWith(alice));
  }
}