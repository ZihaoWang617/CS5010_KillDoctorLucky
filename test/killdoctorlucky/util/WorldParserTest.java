package killdoctorlucky.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import killdoctorlucky.model.Room;
import org.junit.Test;


/**
 * Test class for WorldParser.
 * Tests parsing of world specification files including Milestone 3 features.
 */
public class WorldParserTest {

  /**
   * Tests parsing a valid world specification.
   */
  @Test
  public void testParseValidWorld() throws IOException {
    String worldSpec = "36 30 Test Mansion\n"
        + "50 Doctor Lucky\n"
        + "Fortune the Cat\n"  // Pet name
        + "2\n"
        + "0 0 5 5 Kitchen\n"
        + "6 0 10 5 Library\n"
        + "1\n"
        + "0 3 Knife";

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    assertNotNull(data);
    assertNotNull(data.board);
    assertEquals(2, data.roomsInOrder.size());
    assertEquals(50, data.targetHealth);
    assertEquals("Fortune the Cat", data.petName);
  }

  /**
   * Tests parsing world with no items.
   */
  @Test
  public void testParseWorldWithNoItems() throws IOException {
    String worldSpec = "10 10 Small World\n"
        + "50 Doctor Lucky\n"
        + "Fluffy\n"
        + "1\n"
        + "0 0 5 5 Room1\n"
        + "0";  // No items

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    assertNotNull(data);
    assertEquals(1, data.roomsInOrder.size());
    assertEquals("Fluffy", data.petName);
  }

  /**
   * Tests parsing world without pet.
   */
  @Test
  public void testParseWorldWithoutPet() throws IOException {
    String worldSpec = "10 10 Small World\n"
        + "50 Doctor Lucky\n"
        + "\n"  // Empty line for pet (no pet)
        + "1\n"
        + "0 0 5 5 Room1\n"
        + "0";

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    assertNotNull(data);
    assertEquals(50, data.targetHealth);
    assertNull("Pet name should be null when not specified", data.petName);
  }

  /**
   * Tests parsing null input throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testParseNullInput() throws IOException {
    WorldParser.parseWorld(null);
  }

  /**
   * Tests parsing empty file throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testParseEmptyFile() throws IOException {
    WorldParser.parseWorld(new StringReader(""));
  }

  /**
   * Tests parsing with invalid world line throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidWorldLine() throws IOException {
    String worldSpec = "InvalidLine\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "1\n"
        + "0 0 5 5 Room\n"
        + "0";

    WorldParser.parseWorld(new StringReader(worldSpec));
  }

  /**
   * Tests parsing with missing target line throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testParseMissingTargetLine() throws IOException {
    String worldSpec = "10 10 World\n";

    WorldParser.parseWorld(new StringReader(worldSpec));
  }

  /**
   * Tests parsing with missing room count throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testParseMissingRoomCount() throws IOException {
    String worldSpec = "10 10 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n";

    WorldParser.parseWorld(new StringReader(worldSpec));
  }

  /**
   * Tests parsing with missing room data throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testParseMissingRoomData() throws IOException {
    String worldSpec = "10 10 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "2\n"  // Says 2 rooms
        + "0 0 5 5 Room1\n";  // Only 1 room provided

    WorldParser.parseWorld(new StringReader(worldSpec));
  }

  /**
   * Tests parsing with invalid room format throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidRoomFormat() throws IOException {
    String worldSpec = "10 10 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "1\n"
        + "0 0 5 InvalidRoom\n"  // Missing one coordinate
        + "0";

    WorldParser.parseWorld(new StringReader(worldSpec));
  }

  /**
   * Tests parsing with invalid item room index throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testItemInvalidRoomIndex() throws IOException {
    String worldSpec = "10 10 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "1\n"
        + "0 0 5 5 Room1\n"
        + "1\n"
        + "10 3 Knife";  // Room index 10 doesn't exist

    WorldParser.parseWorld(new StringReader(worldSpec));
  }

  /**
   * Tests multiple adjacent rooms are connected.
   */
  @Test
  public void testMultipleAdjacentRooms() throws IOException {
    String worldSpec = "20 20 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "3\n"
        + "0 0 5 5 Room1\n"
        + "6 0 10 5 Room2\n"  // Shares vertical edge with Room1
        + "0 6 5 10 Room3\n"  // Shares horizontal edge with Room1
        + "0";

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    assertEquals(3, data.roomsInOrder.size());
    
    Room r1 = data.roomsInOrder.get(0);
    Room r2 = data.roomsInOrder.get(1);
    Room r3 = data.roomsInOrder.get(2);

    // Room1 and Room2 should be connected (share vertical edge)
    assertTrue(r1.getConnections().contains(r2));
    
    // Room1 and Room3 should be connected (share horizontal edge)
    assertTrue(r1.getConnections().contains(r3));
    
    // Room2 and Room3 should NOT be connected
    assertFalse(r2.getConnections().contains(r3));
  }

  /**
   * Tests non-adjacent rooms are not connected.
   */
  @Test
  public void testNonAdjacentRoomsNotConnected() throws IOException {
    String worldSpec = "20 20 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "2\n"
        + "0 0 5 5 Room1\n"
        + "10 10 15 15 Room2\n"  // Far apart
        + "0";

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    Room r1 = data.roomsInOrder.get(0);
    Room r2 = data.roomsInOrder.get(1);

    assertFalse("Non-adjacent rooms should not be connected", 
        r1.getConnections().contains(r2));
  }

  /**
   * Tests room connections are bidirectional.
   */
  @Test
  public void testRoomConnectionsVerticalEdge() throws IOException {
    String worldSpec = "20 20 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "2\n"
        + "0 0 5 5 Room1\n"
        + "6 0 10 5 Room2\n"  // Shares vertical edge
        + "0";

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    Room r1 = data.roomsInOrder.get(0);
    Room r2 = data.roomsInOrder.get(1);

    assertTrue(r1.getConnections().contains(r2));
    assertTrue(r2.getConnections().contains(r1));
  }

  /**
   * Tests WorldData structure contains correct information.
   */
  @Test
  public void testWorldDataStructure() throws IOException {
    String worldSpec = "10 10 World\n"
        + "75 Doctor Lucky\n"
        + "Shadow\n"
        + "1\n"
        + "0 0 5 5 Room1\n"
        + "0";

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    assertNotNull(data.board);
    assertNotNull(data.roomsInOrder);
    assertEquals(75, data.targetHealth);
    assertEquals("Shadow", data.petName);
    assertEquals(1, data.roomsInOrder.size());
  }

  /**
   * Tests parsing world with sight lines.
   */
  @Test
  public void testParseWorldWithSightLines() throws IOException {
    String worldSpec = "20 20 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "2\n"
        + "0 0 5 5 Kitchen\n"
        + "10 10 15 15 Library\n"
        + "0\n"
        + "SIGHT\n"
        + "Kitchen : Library\n"
        + "END";

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    assertNotNull(data);
    assertNotNull(data.board.getSightLinesFrom("Kitchen"));
    assertTrue(data.board.getSightLinesFrom("Kitchen").contains("Library"));
  }

  /**
   * Tests parsing world with multiple sight lines.
   */
  @Test
  public void testParseWorldWithMultipleSightLines() throws IOException {
    String worldSpec = "20 20 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "3\n"
        + "0 0 5 5 Room1\n"
        + "6 0 10 5 Room2\n"
        + "0 6 5 10 Room3\n"
        + "0\n"
        + "SIGHT\n"
        + "Room1 : Room2, Room3\n"
        + "Room2 : Room1\n"
        + "END";

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    assertEquals(2, data.board.getSightLinesFrom("Room1").size());
    assertTrue(data.board.getSightLinesFrom("Room1").contains("Room2"));
    assertTrue(data.board.getSightLinesFrom("Room1").contains("Room3"));
  }

  /**
   * Tests parsing world without sight lines block.
   */
  @Test
  public void testParseWorldWithoutSightLines() throws IOException {
    String worldSpec = "10 10 World\n"
        + "50 Doctor Lucky\n"
        + "Pet\n"
        + "1\n"
        + "0 0 5 5 Room1\n"
        + "0";

    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));

    assertNotNull(data);
  }
}