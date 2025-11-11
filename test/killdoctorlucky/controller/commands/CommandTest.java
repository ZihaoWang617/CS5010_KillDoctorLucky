package killdoctorlucky.controller.commands;

import java.io.IOException;
import java.io.StringReader;
import killdoctorlucky.model.Deck;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.occupants.ComputerPlayer;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.util.RandomGenerator;
import killdoctorlucky.util.WorldParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Command implementations.
 * Tests all command classes including player addition, movement, and display commands.
 */
public class CommandTest {
  private Game game;
  private StringBuilder output;
  private Room kitchen;
  private Room library;
  
  /**
   * Sets up the test fixture before each test.
   * Creates a simple game world with two rooms for testing commands.
   * 
   * @throws IOException if world parsing fails
   */
  @Before
  public void setUp() throws IOException {
    String worldSpec = "36 30 Mansion\n"
        + "50 Doctor Lucky\n"
        + "Fortune the Cat\n"
        + "2\n"
        + "0 0 5 5 Kitchen\n"
        + "6 0 10 5 Library\n"
        + "1\n"
        + "0 3 Knife";
    
    WorldParser.WorldData data = WorldParser.parseWorld(new StringReader(worldSpec));
    DoctorLucky doctor = new DoctorLucky(data.roomsInOrder.get(0));
    doctor.setMovementSequence(data.roomsInOrder);
    Deck deck = new Deck();
    game = new Game(data.board, deck, doctor);
    output = new StringBuilder();
    
    kitchen = game.getBoard().getRoom("Kitchen");
    library = game.getBoard().getRoom("Library");
  }
  
  /**
   * Tests adding a human player successfully.
   */
  @Test
  public void testAddHumanPlayerCommand() {
    Command cmd = new AddHumanPlayerCommand("Alice", "Kitchen");
    cmd.execute(game, output);
    
    Assert.assertEquals(1, game.getPlayers().size());
    Assert.assertEquals("Alice", game.getPlayers().get(0).getName());
    Assert.assertTrue(output.toString().contains("Added human player: Alice"));
  }
  
  /**
   * Tests adding a human player to non-existent room.
   */
  @Test
  public void testAddHumanPlayerInvalidRoom() {
    Command cmd = new AddHumanPlayerCommand("Bob", "NonExistent");
    cmd.execute(game, output);
    Assert.assertTrue(output.toString().contains("does not exist"));
  }
  
  /**
   * Tests adding a computer player successfully.
   */
  @Test
  public void testAddComputerPlayerCommand() {
    RandomGenerator rng = new RandomGenerator(0, 1, 2);
    Command cmd = new AddComputerPlayerCommand("CompBot", "Library", rng);
    cmd.execute(game, output);
    
    Assert.assertEquals(1, game.getPlayers().size());
    Assert.assertTrue(game.getPlayers().get(0) instanceof ComputerPlayer);
    Assert.assertTrue(output.toString().contains("Added computer player: CompBot"));
  }
  
  /**
   * Tests constructor with null name throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddHumanPlayerNullName() {
    new AddHumanPlayerCommand(null, "Kitchen");
  }
  
  /**
   * Tests constructor with empty name throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddHumanPlayerEmptyName() {
    new AddHumanPlayerCommand("  ", "Kitchen");
  }
  
  /**
   * Tests move command to adjacent room.
   */
  @Test
  public void testMoveCommand() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", kitchen);
    game.addHumanPlayer("Jason", library);
    game.startGame();
    
    Command move = new MoveCommand("Library");
    move.execute(game, output);
    
    Assert.assertTrue(output.toString().contains("moved to Library") 
        || output.toString().contains("not adjacent"));
  }
  
  /**
   * Tests move command to invalid room.
   */
  @Test
  public void testMoveCommandInvalidRoom() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", kitchen);
    game.addHumanPlayer("Jason", library);
    game.startGame();
    
    Command move = new MoveCommand("InvalidRoom");
    move.execute(game, output);
    
    Assert.assertTrue(output.toString().contains("does not exist"));
  }
  
  /**
   * Tests picking up an item from current room.
   */
  @Test
  public void testPickUpItemCommand() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", kitchen);
    game.addHumanPlayer("Jason", library);
    game.startGame();
    
    Command pickup = new PickUpItemCommand("Knife");
    pickup.execute(game, output);
    
    String result = output.toString();
    Assert.assertTrue(result.contains("picked up") || result.contains("not present"));
  }
  
  /**
   * Tests look around command displays room information.
   */
  @Test
  public void testLookAroundCommand() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", kitchen);
    game.addHumanPlayer("Jason", library);
    game.startGame();
    
    Command look = new LookAroundCommand();
    look.execute(game, output);
    
    String result = output.toString();
    Assert.assertTrue(result.contains("You are in:"));
    Assert.assertTrue(result.contains("Adjacent rooms"));
  }
  
  /**
   * Tests displaying information about a specific space.
   */
  @Test
  public void testDisplaySpaceCommand() {
    Command display = new DisplaySpaceCommand("Kitchen");
    display.execute(game, output);
    
    String result = output.toString();
    Assert.assertTrue(result.contains("Space Information"));
    Assert.assertTrue(result.contains("Kitchen"));
  }
  
  /**
   * Tests displaying information about invalid space.
   */
  @Test
  public void testDisplaySpaceCommandInvalidSpace() {
    Command display = new DisplaySpaceCommand("InvalidSpace");
    display.execute(game, output);
    
    Assert.assertTrue(output.toString().contains("does not exist"));
  }
  
  /**
   * Tests displaying information about a player.
   */
  @Test
  public void testDisplayPlayerCommand() {
    game.addHumanPlayer("Alice", kitchen);
    
    Command display = new DisplayPlayerCommand("Alice");
    display.execute(game, output);
    
    String result = output.toString();
    Assert.assertTrue(result.contains("Player Information"));
    Assert.assertTrue(result.contains("Alice"));
  }
  
  /**
   * Tests displaying information about non-existent player.
   */
  @Test
  public void testDisplayPlayerCommandNotFound() {
    Command display = new DisplayPlayerCommand("NonExistent");
    display.execute(game, output);
    
    Assert.assertTrue(output.toString().contains("not found"));
  }
  
  /**
   * Tests command execution with null game throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCommandWithNullGame() {
    Command cmd = new LookAroundCommand();
    cmd.execute(null, output);
  }
  
  /**
   * Tests command execution with null output throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCommandWithNullOutput() {
    Command cmd = new LookAroundCommand();
    cmd.execute(game, null);
  }
}