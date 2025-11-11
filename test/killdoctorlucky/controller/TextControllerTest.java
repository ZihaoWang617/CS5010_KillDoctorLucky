package killdoctorlucky.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import killdoctorlucky.model.Board;
import killdoctorlucky.model.Deck;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.GameStatus;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.model.occupants.Pet;
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
    room1 = new Room("Library");
    room1.setGeometry(0, 0, 5, 5);
    
    room2 = new Room("Kitchen");
    room2.setGeometry(6, 0, 10, 5);
    
    room3 = new Room("Bedroom");
    room3.setGeometry(0, 6, 5, 10);
    
    board.addRoom(room1);
    board.addRoom(room2);
    board.addRoom(room3);
    
    board.connectRooms(room1, room2);
    board.connectRooms(room2, room3);
    
    // Add some items to rooms
    room1.addItem(new Item("Knife", 5));
    room2.addItem(new Item("Rope", 3));
    
    // Create game with pet
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    doctor.setMovementSequence(java.util.Arrays.asList(room1, room2, room3));
    Deck deck = new Deck();
    game = new Game(board, deck, doctor);
    
    // Add pet for Milestone 3
    Pet pet = new Pet("Fortune the Cat", room1);
    game.setPet(pet);
    game.setMaxTurns(20);
  }

  /**
   * Tests adding a human player through controller.
   */
  @Test
  public void testAddHumanPlayerCommand() {
    String input = "add-human Alice Library\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Added human player: Alice"));
    assertEquals(1, game.getPlayers().size());
  }

  /**
   * Tests adding a computer player through controller.
   */
  @Test
  public void testAddComputerPlayerCommand() {
    String input = "add-computer Bot1 Kitchen\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Added computer player: Bot1"));
    assertEquals(1, game.getPlayers().size());
  }

  /**
   * Tests starting the game with minimum players.
   */
  @Test
  public void testStartGameCommand() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Game started"));
    assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
  }

  /**
   * Tests look around command.
   */
  @Test
  public void testLookAroundCommand() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nlook\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("You are in:"));
    assertTrue(out.contains("Adjacent rooms"));
  }

  /**
   * Tests move command to adjacent room.
   */
  @Test
  public void testMoveCommand() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nmove Kitchen\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("moved to Kitchen"));
  }

  /**
   * Tests pickup command.
   */
  @Test
  public void testPickupCommand() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\npickup Knife\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("picked up Knife"));
  }

  /**
   * Tests display space command.
   */
  @Test
  public void testDisplaySpaceCommand() {
    String input = "info Library\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("Space Information"));
    assertTrue(out.contains("Library"));
  }

  /**
   * Tests display player command.
   */
  @Test
  public void testDisplayPlayerCommand() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "player Alice\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("Player Information"));
    assertTrue(out.contains("Alice"));
  }

  /**
   * Tests help command displays available commands.
   */
  @Test
  public void testHelpCommand() {
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

  /**
   * Tests end turn command advances turn.
   */
  @Test
  public void testEndTurnCommand() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nendturn\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertEquals(1, game.getTurnCount());
  }

  /**
   * Tests game ends when max turns reached.
   */
  @Test
  public void testMaxTurnsReached() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nendturn\nendturn\nendturn\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 3);
    controller.run();
    
    assertTrue(output.toString().contains("Maximum number of turns reached"));
  }

  /**
   * Tests cannot add players after game starts.
   */
  @Test
  public void testCannotAddPlayersAfterStart() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nadd-human Dave Kitchen\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Cannot add players after game has started"));
    assertEquals(3, game.getPlayers().size());
  }

  /**
   * Tests moving to non-adjacent room fails.
   */
  @Test
  public void testInvalidMoveToNonAdjacentRoom() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nmove Bedroom\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("not adjacent") 
        || output.toString().contains("Error"));
  }

  /**
   * Tests multiple players taking turns.
   */
  @Test
  public void testMultiplePlayers() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nendturn\nendturn\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertEquals(2, game.getTurnCount());
  }

  /**
   * Tests spaces command lists all rooms.
   */
  @Test
  public void testSpacesCommand() {
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

  /**
   * Tests players command lists all players.
   */
  @Test
  public void testPlayersCommand() {
    String input = "add-human Alice Library\n"
                 + "add-computer Bot1 Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "players\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("Players in the game"));
    assertTrue(out.contains("Alice"));
    assertTrue(out.contains("Bot1"));
  }

  /**
   * Tests null input throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullInput() {
    new TextController(null, new StringBuilder(), game, 10);
  }

  /**
   * Tests null output throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullOutput() {
    new TextController(new StringReader(""), null, game, 10);
  }

  /**
   * Tests null game throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullGame() {
    new TextController(new StringReader(""), new StringBuilder(), null, 10);
  }

  /**
   * Tests invalid max turns throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMaxTurns() {
    new TextController(new StringReader(""), new StringBuilder(), game, 0);
  }

  /**
   * Tests unknown command handling.
   */
  @Test
  public void testUnknownCommand() {
    String input = "invalidcommand\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Unknown command"));
  }

  /**
   * Tests computer player takes automatic turn.
   */
  @Test
  public void testComputerPlayerTakesAutomaticTurn() {
    String input = "add-computer Bot1 Library\n"
                 + "add-computer Bot2 Kitchen\n"
                 + "add-computer Bot3 Bedroom\n"
                 + "start\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Computer player"));
    assertTrue(output.toString().contains("taking their turn"));
  }
  
  // ===== MILESTONE 3: PET COMMAND TESTS =====
  
  /**
   * Tests move pet command.
   */
  @Test
  public void testMovePetCommand() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nmovepet Kitchen\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Moved the pet"));
    assertEquals(room2, game.getPet().getCurrentRoom());
  }
  
  /**
   * Tests move pet to invalid room.
   */
  @Test
  public void testMovePetInvalidRoom() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nmovepet InvalidRoom\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("does not exist"));
  }
  
  /**
   * Tests attack command with weapon.
   */
  @Test
  public void testAttackCommandWithWeapon() {
    // Create game where Alice can successfully attack
    Room isolatedRoom = new Room("IsolatedRoom");
    isolatedRoom.setGeometry(20, 20, 25, 25);
    board.addRoom(isolatedRoom);
    
    String input = "add-human Alice Library\n"
                 + "add-human Bob IsolatedRoom\n"
                 + "add-human Charlie IsolatedRoom\n"
                 + "start\npickup Knife\nattack Knife\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String result = output.toString();
    assertTrue(result.contains("attempt"));
  }
  
  /**
   * Tests attack command with poke (no weapon).
   */
  @Test
  public void testAttackCommandPoke() {
    Room isolatedRoom = new Room("IsolatedRoom");
    isolatedRoom.setGeometry(20, 20, 25, 25);
    board.addRoom(isolatedRoom);
    
    String input = "add-human Alice Library\n"
                 + "add-human Bob IsolatedRoom\n"
                 + "add-human Charlie IsolatedRoom\n"
                 + "start\nattack\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("poke") 
        || output.toString().contains("attempt"));
  }
  
  /**
   * Tests status command shows game information.
   */
  @Test
  public void testStatusCommand() {
    String input = "add-human Alice Library\n"
                 + "add-human Bob Kitchen\n"
                 + "add-human Charlie Bedroom\n"
                 + "start\nstatus\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("Game Status"));
    assertTrue(out.contains("Turn"));
    assertTrue(out.contains("Doctor Lucky health"));
  }

  
  /**
   * Tests welcome message is displayed.
   */
  @Test
  public void testWelcomeMessage() {
    String input = "quit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    assertTrue(output.toString().contains("Welcome to Kill Doctor Lucky"));
  }
  
  /**
   * Tests help command displays all commands.
   */
  @Test
  public void testHelpCommandComplete() {
    String input = "help\nquit\n";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    
    TextController controller = new TextController(reader, output, game, 10);
    controller.run();
    
    String out = output.toString();
    assertTrue(out.contains("movepet"));
    assertTrue(out.contains("attack"));
    assertTrue(out.contains("look"));
    assertTrue(out.contains("move"));
  }
}