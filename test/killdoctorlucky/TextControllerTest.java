package killdoctorlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for TextController.
 * Tests the controller's ability to process commands and manage game flow.
 */
public class TextControllerTest {

  private Board board;
  private Room room1;
  private Room room2;
  private Room room3;
  private Game game;

  /**
   * Sets up the test fixture before each test.
   * Creates a simple game world with three connected rooms (Library, Kitchen, Bedroom)
   * and initializes game components including the board, Doctor Lucky, deck, and game instance.
   * Rooms are connected in a linear path: Library <-> Kitchen <-> Bedroom.
   * Items are placed in rooms: Knife in Library, Rope in Kitchen.
   */
  @Before
  public void setUp() {
    // Create a simple world with 3 connected rooms
    board = new Board();
    room1 = new Room("Library", true);
    room2 = new Room("Kitchen", true);
    room3 = new Room("Bedroom", true);
    
    board.addRoom(room1);
    board.addRoom(room2);
    board.addRoom(room3);
    
    board.connectRooms(room1, room2);
    board.connectRooms(room2, room3);
    
    // Add some items to rooms
    room1.addItem(new Item("Knife", 5));
    room2.addItem(new Item("Rope", 3));
    
    // Create game
    DoctorLucky doctor = new DoctorLucky(room1);
    doctor.setMovementSequence(java.util.Arrays.asList(room1, room2, room3));
    Deck deck = new Deck();
    game = new Game(board, deck, doctor);
  }

  @Test
  public void testAddHumanPlayerCommand() {
    // Test adding a human player through controller
    String input = "add-human Alice Library\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Added human player: Alice"));
    assertEquals(1, game.getPlayers().size());
  }

  @Test
  public void testAddComputerPlayerCommand() {
    // Test adding a computer player through controller
    String input = "add-computer Bot1 Kitchen\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Added computer player: Bot1"));
    assertEquals(1, game.getPlayers().size());
  }

  @Test
  public void testStartGameCommand() {
    // Test starting the game
    String input = "add-human Alice Library\nadd-human Bob Kitchen\nstart\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Game started"));
    assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
  }

  @Test
  public void testLookAroundCommand() {
    // Test look around command
    String input = "add-human Alice Library\nstart\nlook\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("You are in: Library"));
    assertTrue(out.contains("Adjacent rooms"));
  }

  @Test
  public void testMoveCommand() {
    // Test moving player to adjacent room
    String input = "add-human Alice Library\nstart\nmove Kitchen\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("moved to Kitchen"));
    assertEquals(room2, game.getCurrentPlayer().getCurrentRoom());
  }

  @Test
  public void testPickupCommand() {
    // Test picking up an item
    String input = "add-human Alice Library\nstart\npickup Knife\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("picked up Knife"));
    assertEquals(1, game.getCurrentPlayer().getInventory().size());
  }

  @Test
  public void testDisplaySpaceCommand() {
    // Test displaying space information
    String input = "add-human Alice Library\nstart\ninfo Library\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("Space Information"));
    assertTrue(out.contains("Library"));
  }

  @Test
  public void testDisplayPlayerCommand() {
    // Test displaying player information
    String input = "add-human Alice Library\nstart\nplayer Alice\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("Player Information"));
    assertTrue(out.contains("Alice"));
  }

  @Test
  public void testHelpCommand() {
    // Test help command displays available commands
    String input = "help\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("AVAILABLE COMMANDS"));
    assertTrue(out.contains("add-human"));
    assertTrue(out.contains("move"));
  }

  @Test
  public void testEndTurnCommand() {
    // Test ending a turn
    String input = "add-human Alice Library\nstart\nendturn\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertEquals(1, game.getTurnCount());
  }

  @Test
  public void testMaxTurnsReached() {
    // Test game ends when max turns reached
    String input = "add-human Alice Library\nstart\nendturn\nendturn\nendturn\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 3);
    controller.run();
    
    assertTrue(output.toString().contains("Maximum number of turns reached"));
  }

  @Test
  public void testCannotAddPlayersAfterStart() {
    // Test that players cannot be added after game starts
    String input = "add-human Alice Library\nstart\nadd-human Bob Kitchen\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Cannot add players after game has started"));
    assertEquals(1, game.getPlayers().size());
  }

  @Test
  public void testInvalidMoveToNonAdjacentRoom() {
    // Test moving to non-adjacent room fails
    String input = "add-human Alice Library\nstart\nmove Bedroom\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("not adjacent"));
  }

  @Test
  public void testMultiplePlayers() {
    // Test multiple players taking turns
    String input = "add-human Alice Library\nadd-human Bob Kitchen\nstart\n"
                 + "endturn\nendturn\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertEquals(2, game.getTurnCount());
  }

  @Test
  public void testSpacesCommand() {
    // Test listing all spaces
    String input = "spaces\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("Available spaces"));
    assertTrue(out.contains("Library"));
    assertTrue(out.contains("Kitchen"));
  }

  @Test
  public void testPlayersCommand() {
    // Test listing all players
    String input = "add-human Alice Library\nadd-computer Bot1 Kitchen\nplayers\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("Players in the game"));
    assertTrue(out.contains("Alice"));
    assertTrue(out.contains("Bot1"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInput() {
    // Test that null input throws exception
    new TextController(null, new StringBuilder(), game, 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullOutput() {
    // Test that null output throws exception
    new TextController(new StringReader(""), null, game, 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullGame() {
    // Test that null game throws exception
    new TextController(new StringReader(""), new StringBuilder(), null, 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMaxTurns() {
    // Test that invalid max turns throws exception
    new TextController(new StringReader(""), new StringBuilder(), game, 0);
  }

  @Test
  public void testUnknownCommand() {
    // Test handling of unknown commands
    String input = "invalidcommand\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Unknown command"));
  }

  @Test
  public void testComputerPlayerTakesAutomaticTurn() {
    // Test computer player automatically takes turn
    String input = "add-computer Bot1 Library\nstart\nendturn\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Computer player Bot1 is taking their turn"));
    assertEquals(1, game.getTurnCount());
  }
}