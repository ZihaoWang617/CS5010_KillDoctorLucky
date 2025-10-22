package killdoctorlucky;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test class for Game functionality.
 * Tests game initialization, player management, and turn execution.
 */
public class GameTest {
  private Game game;
  private Board board;
  private Deck deck;
  private DoctorLucky doctor;
  private Room kitchen;
  private Room dining;
  
  /**
   * Sets up the test fixture before each test.
   * Creates a simple game environment for testing.
   */
  @Before
  public void setUp() {
    board = new Board();
    kitchen = new Room("Kitchen", true);
    dining = new Room("Dining Room", true);
    board.addRoom(kitchen);
    board.addRoom(dining);
    board.connectRooms(kitchen, dining);
    
    doctor = new DoctorLucky(kitchen);
    deck = new Deck();
    game = new Game(board, deck, doctor);
  }

  /**
   * Tests valid game initialization.
   */
  @Test
  public void testValidGameInitialization() {
    Assert.assertNotNull(game);
    Assert.assertEquals(GameStatus.SETUP, game.getStatus());
    Assert.assertEquals(0, game.getTurnCount());
  }

  /**
   * Tests adding human player to game.
   */
  @Test
  public void testAddHumanPlayer() {
    game.addHumanPlayer("Alice", kitchen);
    Assert.assertEquals(1, game.getPlayers().size());
    Assert.assertEquals("Alice", game.getPlayers().get(0).getName());
  }

  /**
   * Tests adding computer player to game.
   */
  @Test
  public void testAddComputerPlayer() {
    RandomGenerator rng = new RandomGenerator(0, 1, 2);
    game.addComputerPlayer("HAL", kitchen, rng);
    Assert.assertEquals(1, game.getPlayers().size());
    Assert.assertTrue(game.getPlayers().get(0) instanceof ComputerPlayer);
  }

  /**
   * Tests starting game changes status.
   */
  @Test
  public void testStartGame() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", dining);
    game.addHumanPlayer("Charlie", kitchen);
    
    Assert.assertEquals(GameStatus.SETUP, game.getStatus());
    game.startGame();
    Assert.assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
  }

  /**
   * Tests cannot start game twice.
   */
  @Test(expected = IllegalStateException.class)
  public void testStartGameTwice() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", dining);
    game.addHumanPlayer("Charlie", kitchen);
    game.startGame();
    game.startGame();
  }

  /**
   * Tests getting current player after start.
   */
  @Test
  public void testGetCurrentPlayerAfterStart() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", dining);
    game.addHumanPlayer("Charlie", kitchen);
    game.startGame();
    
    Player current = game.getCurrentPlayer();
    Assert.assertNotNull(current);
    Assert.assertEquals("Alice", current.getName());
  }

  /**
   * Tests playing turn before start throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testPlayTurnBeforeStart() {
    game.playTurn();
  }

  /**
   * Tests playing turn advances to next player.
   */
  @Test
  public void testPlayTurn() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", dining);
    game.addHumanPlayer("Charlie", kitchen);
    game.startGame();
    
    Player first = game.getCurrentPlayer();
    game.playTurn();
    Player second = game.getCurrentPlayer();
    
    Assert.assertNotEquals(first, second);
  }

  /**
   * Tests game is not over initially.
   */
  @Test
  public void testIsGameOverInitially() {
    Assert.assertFalse(game.isGameOver());
    
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", dining);
    game.addHumanPlayer("Charlie", kitchen);
    game.startGame();
    Assert.assertFalse(game.isGameOver());
  }

  /**
   * Tests no winner before game over.
   */
  @Test
  public void testGetWinnerBeforeGameOver() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", dining);
    game.addHumanPlayer("Charlie", kitchen);
    game.startGame();
    
    Assert.assertNull(game.getWinner());
  }

  /**
   * Tests pickup from room functionality.
   */
  @Test
  public void testPickupFromRoom() {
    Item knife = new Item("Knife", 3);
    kitchen.addItem(knife);
    
    game.addHumanPlayer("Alice", kitchen);
    game.startGame();
    
    Player alice = game.getCurrentPlayer();
    boolean picked = game.pickupFromRoom(alice, "Knife");
    
    Assert.assertTrue(picked);
    Assert.assertEquals(1, alice.getInventory().size());
  }

  /**
   * Tests pickup non-existent item.
   */
  @Test
  public void testPickupNonExistentItem() {
    game.addHumanPlayer("Alice", kitchen);
    game.startGame();
    
    Player alice = game.getCurrentPlayer();
    boolean picked = game.pickupFromRoom(alice, "NonExistent");
    
    Assert.assertFalse(picked);
  }

  /**
   * Tests describe look around functionality.
   */
  @Test
  public void testDescribeLookAround() {
    game.addHumanPlayer("Alice", kitchen);
    game.startGame();
    
    String description = game.describeLookAround(game.getCurrentPlayer());
    Assert.assertNotNull(description);
    Assert.assertTrue(description.contains("You are in:"));
    Assert.assertTrue(description.contains("Kitchen"));
  }
}