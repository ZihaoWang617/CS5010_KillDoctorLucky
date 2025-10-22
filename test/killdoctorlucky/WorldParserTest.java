package killdoctorlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;

/**
 * Test class for WorldParser functionality.
 * Tests parsing world specification files and world data creation.
 */
public class WorldParserTest {
  
  /**
   * Tests parsing a valid world specification with two rooms.
   */
  @Test
  public void testParseValidWorld() {
    String worldSpec = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "2\n"
        + "0 0 5 5 Kitchen\n"
        + "6 0 10 5 Library\n"
        + "1\n"
        + "0 3 Knife";
    
    try {
      WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
      
      assertNotNull(data);
      assertNotNull(data.board);
      assertEquals(2, data.board.getRoomCount());
      assertEquals(2, data.roomsInOrder.size());
    } catch (IOException e) {
      fail("Should not throw IOException: " + e.getMessage());
    }
  }
  
  /**
   * Tests parsing with null input throws exception.
   */
  @Test
  public void testParseNullInput() {
    try {
      WorldParser.parseWorld(null);
      fail("Expected IllegalArgumentException for null input");
    } catch (IllegalArgumentException e) {
      // Expected
    } catch (IOException e) {
      fail("Should throw IllegalArgumentException, not IOException");
    }
  }
  
  /**
   * Tests parsing empty file throws exception.
   */
  @Test
  public void testParseEmptyFile() {
    try {
      WorldParser.parseWorld(new StringReader(""));
      fail("Expected IllegalArgumentException for empty file");
    } catch (IllegalArgumentException e) {
      // Expected
    } catch (IOException e) {
      fail("Should throw IllegalArgumentException, not IOException");
    }
  }
  
  /**
   * Tests parsing file with invalid world line format.
   */
  @Test
  public void testParseInvalidWorldLine() {
    String invalid = "invalid data\n";
    try {
      WorldParser.parseWorld(new StringReader(invalid));
      fail("Expected IllegalArgumentException for invalid format");
    } catch (IllegalArgumentException e) {
      // Expected
    } catch (IOException e) {
      fail("Should throw IllegalArgumentException, not IOException");
    }
  }
  
  /**
   * Tests parsing file with missing target character line.
   */
  @Test
  public void testParseMissingTargetLine() {
    String invalid = "36 30 TestMansion\n";
    try {
      WorldParser.parseWorld(new StringReader(invalid));
      fail("Expected IllegalArgumentException for missing target line");
    } catch (IllegalArgumentException e) {
      // Expected
    } catch (IOException e) {
      fail("Should throw IllegalArgumentException, not IOException");
    }
  }
  
  /**
   * Tests parsing file with missing room count.
   */
  @Test
  public void testParseMissingRoomCount() {
    String invalid = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n";
    try {
      WorldParser.parseWorld(new StringReader(invalid));
      fail("Expected IllegalArgumentException for missing room count");
    } catch (IllegalArgumentException e) {
      // Expected
    } catch (IOException e) {
      fail("Should throw IllegalArgumentException, not IOException");
    }
  }
  
  /**
   * Tests parsing file with missing room data.
   */
  @Test
  public void testParseMissingRoomData() {
    String invalid = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "2\n"
        + "0 0 5 5 Kitchen\n";  // Missing second room
    try {
      WorldParser.parseWorld(new StringReader(invalid));
      fail("Expected IllegalArgumentException for missing room data");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Missing room data"));
    } catch (IOException e) {
      fail("Should throw IllegalArgumentException, not IOException");
    }
  }
  
  /**
   * Tests parsing file with invalid room format.
   */
  @Test
  public void testParseInvalidRoomFormat() {
    String invalid = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "1\n"
        + "0 0 5 Kitchen\n";  // Missing coordinate
    try {
      WorldParser.parseWorld(new StringReader(invalid));
      fail("Expected IllegalArgumentException for invalid room format");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Invalid room format"));
    } catch (IOException e) {
      fail("Should throw IllegalArgumentException, not IOException");
    }
  }
  
  /**
   * Tests rooms are correctly connected when they share a wall.
   */
  @Test
  public void testRoomConnectionsVerticalEdge() {
    String worldSpec = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "2\n"
        + "0 0 5 5 Kitchen\n"
        + "0 6 5 10 Library\n"  // Adjacent to Kitchen (shares vertical edge)
        + "0\n";
    
    try {
      WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
      Room kitchen = data.board.getRoom("Kitchen");
      Room library = data.board.getRoom("Library");
      
      assertNotNull(kitchen);
      assertNotNull(library);
      assertTrue(kitchen.getConnections().contains(library));
      assertTrue(library.getConnections().contains(kitchen));
    } catch (IOException e) {
      fail("Should not throw IOException: " + e.getMessage());
    }
  }
  
  /**
   * Tests rooms are correctly connected when they share a horizontal edge.
   */
  @Test
  public void testRoomConnectionsHorizontalEdge() {
    String worldSpec = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "2\n"
        + "0 0 5 5 Kitchen\n"
        + "6 0 10 5 Library\n"  // Adjacent to Kitchen (shares horizontal edge)
        + "0\n";
    
    try {
      WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
      Room kitchen = data.board.getRoom("Kitchen");
      Room library = data.board.getRoom("Library");
      
      assertNotNull(kitchen);
      assertNotNull(library);
      assertTrue(kitchen.getConnections().contains(library));
      assertTrue(library.getConnections().contains(kitchen));
    } catch (IOException e) {
      fail("Should not throw IOException: " + e.getMessage());
    }
  }
  
  /**
   * Tests non-adjacent rooms are not connected.
   */
  @Test
  public void testNonAdjacentRoomsNotConnected() {
    String worldSpec = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "2\n"
        + "0 0 5 5 Kitchen\n"
        + "10 10 15 15 Library\n"  // Not adjacent to Kitchen
        + "0\n";
    
    try {
      WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
      Room kitchen = data.board.getRoom("Kitchen");
      Room library = data.board.getRoom("Library");
      
      assertNotNull(kitchen);
      assertNotNull(library);
      assertEquals(0, kitchen.getConnections().size());
      assertEquals(0, library.getConnections().size());
    } catch (IOException e) {
      fail("Should not throw IOException: " + e.getMessage());
    }
  }
  
  /**
   * Tests items are placed in correct rooms.
   */
  @Test
  public void testItemPlacement() {
    String worldSpec = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "1\n"
        + "0 0 5 5 Kitchen\n"
        + "2\n"
        + "0 3 Knife\n"
        + "0 5 Rope";
    
    try {
      WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
      Room kitchen = data.roomsInOrder.get(0);
      
      assertEquals(2, kitchen.getItems().size());
      assertEquals("Knife", kitchen.getItems().get(0).getName());
      assertEquals(3, kitchen.getItems().get(0).getDamage());
      assertEquals("Rope", kitchen.getItems().get(1).getName());
      assertEquals(5, kitchen.getItems().get(1).getDamage());
    } catch (IOException e) {
      fail("Should not throw IOException: " + e.getMessage());
    }
  }
  
  /**
   * Tests parsing world with no items.
   */
  @Test
  public void testParseWorldWithNoItems() {
    String worldSpec = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "1\n"
        + "0 0 5 5 Kitchen\n"
        + "0\n";  // Zero items
    
    try {
      WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
      
      assertNotNull(data);
      assertEquals(1, data.board.getRoomCount());
      Room kitchen = data.roomsInOrder.get(0);
      assertEquals(0, kitchen.getItems().size());
    } catch (IOException e) {
      fail("Should not throw IOException: " + e.getMessage());
    }
  }
  
  /**
   * Tests parsing file with invalid item format.
   */
  @Test
  public void testParseInvalidItemFormat() {
    String invalid = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "1\n"
        + "0 0 5 5 Kitchen\n"
        + "1\n"
        + "0 Knife\n";  // Missing damage value
    try {
      WorldParser.parseWorld(new StringReader(invalid));
      fail("Expected IllegalArgumentException for invalid item format");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Invalid item format"));
    } catch (IOException e) {
      fail("Should throw IllegalArgumentException, not IOException");
    }
  }
  
  /**
   * Tests items are not added to invalid room indices.
   */
  @Test
  public void testItemInvalidRoomIndex() {
    String worldSpec = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "1\n"
        + "0 0 5 5 Kitchen\n"
        + "1\n"
        + "5 3 Knife";  // Room index 5 doesn't exist
    
    try {
      WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
      Room kitchen = data.roomsInOrder.get(0);
      
      // Item should not be added to any room
      assertEquals(0, kitchen.getItems().size());
    } catch (IOException e) {
      fail("Should not throw IOException: " + e.getMessage());
    }
  }
  
  /**
   * Tests WorldData contains correct board and room order.
   */
  @Test
  public void testWorldDataStructure() {
    String worldSpec = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "3\n"
        + "0 0 5 5 Kitchen\n"
        + "6 0 10 5 Library\n"
        + "11 0 15 5 Bedroom\n"
        + "0\n";
    
    try {
      WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
      
      assertNotNull(data.board);
      assertNotNull(data.roomsInOrder);
      assertEquals(3, data.board.getRoomCount());
      assertEquals(3, data.roomsInOrder.size());
      
      // Verify room order matches specification order
      assertEquals("Kitchen", data.roomsInOrder.get(0).getName());
      assertEquals("Library", data.roomsInOrder.get(1).getName());
      assertEquals("Bedroom", data.roomsInOrder.get(2).getName());
    } catch (IOException e) {
      fail("Should not throw IOException: " + e.getMessage());
    }
  }
  
  /**
   * Tests multiple adjacent rooms are all connected.
   */
  @Test
  public void testMultipleAdjacentRooms() {
    String worldSpec = "36 30 TestMansion\n"
        + "50 Doctor Lucky\n"
        + "3\n"
        + "0 0 5 5 Kitchen\n"
        + "6 0 10 5 Library\n"
        + "11 0 15 5 Bedroom\n"
        + "0\n";
    
    try {
      WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
      Room kitchen = data.board.getRoom("Kitchen");
      Room library = data.board.getRoom("Library");
      Room bedroom = data.board.getRoom("Bedroom");
      
      // Kitchen should be connected to Library
      assertTrue(kitchen.getConnections().contains(library));
      
      // Library should be connected to both Kitchen and Bedroom
      assertTrue(library.getConnections().contains(kitchen));
      assertTrue(library.getConnections().contains(bedroom));
      
      // Bedroom should be connected to Library
      assertTrue(bedroom.getConnections().contains(library));
      
      // Kitchen and Bedroom should NOT be directly connected
      assertEquals(1, kitchen.getConnections().size());
      assertEquals(1, bedroom.getConnections().size());
      assertEquals(2, library.getConnections().size());
    } catch (IOException e) {
      fail("Should not throw IOException: " + e.getMessage());
    }
  }
}