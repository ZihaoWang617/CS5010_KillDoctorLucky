package killdoctorlucky.controller.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import killdoctorlucky.model.Board;
import killdoctorlucky.model.Deck;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.model.occupants.Player;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for AttemptMurderCommand (Milestone 3).
 * Tests the command to attempt murdering Doctor Lucky.
 */
public class AttemptMurderCommandTest {
  
  private Game game;
  private Board board;
  private Room room1;
  private Room room2;
  private Room room3;
  private DoctorLucky doctor;
  private StringBuilder output;
  
  /**
   * Sets up the test fixture.
   * Creates a game environment for testing murder attempts.
   */
  @Before
  public void setUp() {
    board = new Board();
    
    room1 = new Room("Kitchen");
    room1.setGeometry(0, 0, 5, 5);
    
    room2 = new Room("Dining Room");
    room2.setGeometry(6, 0, 10, 5);
    
    room3 = new Room("Library");
    room3.setGeometry(11, 0, 15, 5);
    
    board.addRoom(room1);
    board.addRoom(room2);
    board.addRoom(room3);
    board.connectRooms(room1, room2);
    
    doctor = new DoctorLucky(room1, 50);
    Deck deck = new Deck();
    game = new Game(board, deck, doctor);
    game.setMaxTurns(20);
    
    output = new StringBuilder();
  }
  
  /**
   * Tests AttemptMurderCommand creation with weapon.
   */
  @Test
  public void testAttemptMurderCommandCreation() {
    AttemptMurderCommand cmd = new AttemptMurderCommand("Knife");
    assertNotNull(cmd);
  }
  
  /**
   * Tests AttemptMurderCommand creation with null (poke).
   */
  @Test
  public void testAttemptMurderCommandCreationPoke() {
    AttemptMurderCommand cmd = new AttemptMurderCommand(null);
    assertNotNull(cmd);
  }
  
  /**
   * Tests executing murder with weapon.
   */
  @Test
  public void testExecuteAttemptMurderWithWeapon() {
    game.addHumanPlayer("Alice", room1);
    game.addHumanPlayer("Bob", room2);
    game.addHumanPlayer("Charlie", room2);
    game.startGame();
    
    Player alice = game.getCurrentPlayer();
    Item knife = new Item("Knife", 5);
    alice.pickUpItem(knife);
    
    AttemptMurderCommand cmd = new AttemptMurderCommand("Knife");
    cmd.execute(game, output);
    
    String result = output.toString();
    assertTrue(result.contains("attempt"));
    assertTrue(result.contains("Knife"));
  }
  
  /**
   * Tests executing murder with poke in eye.
   */
  @Test
  public void testExecuteAttemptMurderWithPoke() {
    game.addHumanPlayer("Alice", room1);
    game.addHumanPlayer("Bob", room2);
    game.addHumanPlayer("Charlie", room2);
    game.startGame();
    
    AttemptMurderCommand cmd = new AttemptMurderCommand(null);
    cmd.execute(game, output);
    
    String result = output.toString();
    assertTrue(result.contains("poke") || result.contains("attempt"));
  }
  
  /**
   * Tests successful murder displays success message.
   */
  @Test
  public void testExecuteSuccessfulMurder() {
    DoctorLucky weakDoctor = new DoctorLucky(room1, 1);
    Deck deck = new Deck();
    Game testGame = new Game(board, deck, weakDoctor);
    testGame.setMaxTurns(20);
    
    testGame.addHumanPlayer("Alice", room1);
    testGame.addHumanPlayer("Bob", room3);
    testGame.addHumanPlayer("Charlie", room3);
    testGame.startGame();
    
    AttemptMurderCommand cmd = new AttemptMurderCommand(null);
    cmd.execute(testGame, output);
    
    String result = output.toString();
    assertTrue(result.contains("SUCCESS") || result.contains("killed") 
        || result.contains("wins"));
  }
  
  /**
   * Tests failed murder due to witness displays failure message.
   */
  @Test
  public void testExecuteFailedMurderWitness() {
    game.addHumanPlayer("Alice", room1);
    game.addHumanPlayer("Bob", room1);  // Witness in same room
    game.addHumanPlayer("Charlie", room1);
    game.startGame();
    
    Player alice = game.getCurrentPlayer();
    Item gun = new Item("Gun", 50);
    alice.pickUpItem(gun);
    
    AttemptMurderCommand cmd = new AttemptMurderCommand("Gun");
    cmd.execute(game, output);
    
    String result = output.toString();
    assertTrue(result.contains("FAILED") || result.contains("witness"));
  }
  
  /**
   * Tests murder when not in same room displays error.
   */
  @Test
  public void testExecuteMurderNotInSameRoom() {
    game.addHumanPlayer("Alice", room2);  // Different room
    game.addHumanPlayer("Bob", room2);
    game.addHumanPlayer("Charlie", room2);
    game.startGame();
    
    AttemptMurderCommand cmd = new AttemptMurderCommand("Knife");
    cmd.execute(game, output);
    
    assertTrue(output.toString().contains("same room") 
        || output.toString().contains("Error"));
  }
  
  /**
   * Tests execute with null game throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testExecuteNullGame() {
    AttemptMurderCommand cmd = new AttemptMurderCommand("Knife");
    cmd.execute(null, output);
  }
  
  /**
   * Tests execute with null output throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testExecuteNullOutput() {
    AttemptMurderCommand cmd = new AttemptMurderCommand("Knife");
    cmd.execute(game, null);
  }
  
  /**
   * Tests get description returns valid string.
   */
  @Test
  public void testGetDescription() {
    AttemptMurderCommand cmd = new AttemptMurderCommand("Knife");
    String desc = cmd.getDescription();
    
    assertNotNull(desc);
    assertFalse(desc.isEmpty());
    assertTrue(desc.toLowerCase().contains("murder") 
        || desc.toLowerCase().contains("attack"));
  }
  
  /**
   * Tests murder attempt with empty weapon name uses poke.
   */
  @Test
  public void testExecuteEmptyWeaponNameUsesPoke() {
    game.addHumanPlayer("Alice", room1);
    game.addHumanPlayer("Bob", room2);
    game.addHumanPlayer("Charlie", room2);
    game.startGame();
    
    AttemptMurderCommand cmd = new AttemptMurderCommand("");
    cmd.execute(game, output);
    
    assertTrue(output.toString().contains("poke") || output.toString().contains("attempt"));
  }
}