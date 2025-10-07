package killdoctorlucky;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for Room functionality.
 * Tests room creation, occupant management, and connections.
 */
public class RoomTest {
    private Room kitchen;
    private Room diningRoom;
    private Player alice;
    private Player bob;
    
  @Before
  public void setUp() {
    kitchen = new Room("Kitchen", true);
    diningRoom = new Room("Dining Room", true);
    alice = new Player("Alice", kitchen);
    bob = new Player("Bob", diningRoom);
  }
    
  @Test
  public void testValidRoomCreation() {
    Room library = new Room("Library", true);
    assertEquals("Library", library.getName());
    assertTrue(library.isNamedRoom());
  }
    
  @Test
  public void testInvalidRoomCreation() {
    assertThrows(IllegalArgumentException.class, () -> new Room(null, true));
    assertThrows(IllegalArgumentException.class, () -> new Room("", true));
    assertThrows(IllegalArgumentException.class, () -> new Room("   ", true));
    }
    
  @Test
  public void testAddOccupant() {
    List<Occupant> occupants = kitchen.getOccupants();
    assertTrue(occupants.contains(alice));
    assertEquals(1, occupants.size());   
    DoctorLucky doctor = new DoctorLucky(diningRoom);
    kitchen.addOccupant(doctor);
    occupants = kitchen.getOccupants();
    assertTrue(occupants.contains(doctor));
    assertEquals(2, occupants.size());
    }
    
  @Test
  public void testAddNullOccupant() {
    assertThrows(IllegalArgumentException.class, () -> kitchen.addOccupant(null));
    }
    
  @Test
  public void testRemoveOccupant() {
    assertTrue(kitchen.getOccupants().contains(alice));
    kitchen.removeOccupant(alice);
    assertFalse(kitchen.getOccupants().contains(alice));
    assertEquals(0, kitchen.getOccupants().size());
    }
    
  @Test
  public void testRemoveNullOccupant() {
    assertThrows(IllegalArgumentException.class, () -> kitchen.removeOccupant(null));
    }
    
  @Test
  public void testIsOccupiedBy() {
    assertTrue(kitchen.isOccupiedBy(alice));
    assertFalse(kitchen.isOccupiedBy(bob));
    }
    
  @Test
  public void testIsOccupiedByNull() {
    assertThrows(IllegalArgumentException.class, () -> kitchen.isOccupiedBy(null));
    }
    
  @Test
  public void testGetPlayerCount() {
    assertEquals(1, kitchen.getPlayerCount());
    assertEquals(1, diningRoom.getPlayerCount());
        
    DoctorLucky doctor = new DoctorLucky(kitchen);
    kitchen.addOccupant(doctor);
    assertEquals(1, kitchen.getPlayerCount());
    }
    
  @Test
  public void testRoomConnections() {
    kitchen.addConnection(diningRoom);
    List<Room> connections = kitchen.getConnections();
    assertTrue(connections.contains(diningRoom));
    assertEquals(1, connections.size());
    }
    
  @Test
  public void testAddNullConnection() {
    assertThrows(IllegalArgumentException.class, () -> kitchen.addConnection(null));
    }
    
  @Test
  public void testAddSelfConnection() {
    assertThrows(IllegalArgumentException.class, () -> kitchen.addConnection(kitchen));
    }
    
  @Test
  public void testToString() {
    String result = kitchen.toString();
    assertTrue(result.contains("Kitchen"));
    assertTrue(result.contains("occupants=1"));
    }
    
  @Test
  public void testEquals() {
    Room anotherKitchen = new Room("Kitchen", true);
    assertEquals(kitchen, anotherKitchen);
    assertNotEquals(kitchen, diningRoom);
    }
}