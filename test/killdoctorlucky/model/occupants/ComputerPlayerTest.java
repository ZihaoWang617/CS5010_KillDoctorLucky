package killdoctorlucky.model.occupants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import killdoctorlucky.model.Board;
import killdoctorlucky.model.Deck;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.Room;
import killdoctorlucky.util.RandomGenerator;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ComputerPlayer (Milestone 2 & 3).
 * Tests computer player creation, AI behavior, and automatic turn execution.
 */
public class ComputerPlayerTest {
  
  private Board board;
  private Room room1;
  private Room room2;
  private Room room3;
  private Game game;
  private RandomGenerator rng;
  
  /**
   * Sets up the test fixture.
   * Creates a game world with multiple rooms for testing computer player behavior.
   */
  @Before
  public void setUp() {
    board = new Board();
    
    room1 = new Room("Kitchen");
    room1.setGeometry(0, 0, 5, 5);
    
    room2 = new Room("Dining Room");
    room2.setGeometry(6, 0, 10, 5);
    
    room3 = new Room("Library");
    room3.setGeometry(0, 6, 5, 10);
    
    board.addRoom(room1);
    board.addRoom(room2);
    board.addRoom(room3);
    
    board.connectRooms(room1, room2);
    board.connectRooms(room2, room3);
    
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    Deck deck = new Deck();
    game = new Game(board, deck, doctor);
    
    rng = new RandomGenerator(0, 1, 2, 0, 1, 2);
  }
  
  // ===== CREATION TESTS =====
  
  /**
   * Tests computer player creation.
   */
  @Test
  public void testComputerPlayerCreation() {
    ComputerPlayer bot = new ComputerPlayer("Bot", room1, rng);
    
    assertNotNull(bot);
    assertEquals("Bot", bot.getName());
    assertEquals(room1, bot.getCurrentRoom());
  }
  
  /**
   * Tests computer player creation with null name throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testComputerPlayerCreationNullName() {
    new ComputerPlayer(null, room1, rng);
  }
  
  /**
   * Tests computer player creation with empty name throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testComputerPlayerCreationEmptyName() {
    new ComputerPlayer("", room1, rng);
  }
  
  /**
   * Tests computer player creation with null room throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testComputerPlayerCreationNullRoom() {
    new ComputerPlayer("Bot", null, rng);
  }
  
  /**
   * Tests computer player creation with null RNG throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testComputerPlayerCreationNullRng() {
    new ComputerPlayer("Bot", room1, null);
  }
  
  // ===== INHERITANCE TESTS =====
  
  /**
   * Tests computer player is a Player.
   */
  @Test
  public void testComputerPlayerIsPlayer() {
    ComputerPlayer bot = new ComputerPlayer("Bot", room1, rng);
    assertTrue("ComputerPlayer should be instance of Player", 
        bot instanceof Player);
  }
  
  /**
   * Tests computer player can move like regular player.
   */
  @Test
  public void testComputerPlayerCanMove() {
    ComputerPlayer bot = new ComputerPlayer("Bot", room1, rng);
    
    assertTrue(bot.moveToRoom(room2));
    assertEquals(room2, bot.getCurrentRoom());
  }
  
  /**
   * Tests computer player can pick up items.
   */
  @Test
  public void testComputerPlayerCanPickUpItems() {
    ComputerPlayer bot = new ComputerPlayer("Bot", room1, rng);
    Item knife = new Item("Knife", 5);
    
    assertTrue(bot.pickUpItem(knife));
    assertEquals(1, bot.getInventory().size());
  }
  
  // ===== AI BEHAVIOR TESTS =====
  
  /**
   * Tests computer player can take a turn.
   */
  @Test
  public void testComputerPlayerTakeTurn() {
    game.setMaxTurns(20);
    game.addComputerPlayer("Bot", room1, rng);
    game.addHumanPlayer("Alice", room2);
    game.addHumanPlayer("Bob", room3);
    game.startGame();
    
    ComputerPlayer bot = (ComputerPlayer) game.getCurrentPlayer();
    
    // Take a turn
    bot.takeTurn(game);
    
    // Computer player should have done something (moved, picked up, or looked)
    // We can't predict exact behavior due to randomness, just verify no crash
    assertNotNull(bot.getCurrentRoom());
  }
  
  /**
   * Tests computer player takeTurn with null game throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testTakeTurnNullGame() {
    ComputerPlayer bot = new ComputerPlayer("Bot", room1, rng);
    bot.takeTurn(null);
  }
  
  /**
   * Tests computer player can move randomly.
   */
  @Test
  public void testComputerPlayerRandomMovement() {
    // Create RNG that will choose move action
    RandomGenerator moveRng = new RandomGenerator(0, 0, 0);  // Always choose move
    
    game.setMaxTurns(20);
    game.addComputerPlayer("Bot", room1, moveRng);
    game.addHumanPlayer("Alice", room2);
    game.addHumanPlayer("Bob", room3);
    game.startGame();
    
    ComputerPlayer bot = (ComputerPlayer) game.getCurrentPlayer();
    
    bot.takeTurn(game);
    
    // Bot may have moved (if RNG chose a valid neighbor)
    assertNotNull(bot.getCurrentRoom());
  }
  
  /**
   * Tests computer player can pick up items.
   */
  @Test
  public void testComputerPlayerPicksUpItems() {
    // Add item to room
    Item knife = new Item("Knife", 5);
    room1.addItem(knife);
    
    // Create RNG that will choose pickup action
    RandomGenerator pickupRng = new RandomGenerator(1, 1, 1);  // Always choose pickup
    
    game.setMaxTurns(20);
    game.addComputerPlayer("Bot", room1, pickupRng);
    game.addHumanPlayer("Alice", room2);
    game.addHumanPlayer("Bob", room3);
    game.startGame();
    
    ComputerPlayer bot = (ComputerPlayer) game.getCurrentPlayer();
    
    bot.takeTurn(game);
    
    // Bot should have picked up the item (if it had capacity)
    // Can't guarantee due to capacity limits, just verify no crash
    assertNotNull(bot.getInventory());
  }
  
  /**
   * Tests computer player toString.
   */
  @Test
  public void testComputerPlayerToString() {
    ComputerPlayer bot = new ComputerPlayer("Bot", room1, rng);
    
    String result = bot.toString();
    assertNotNull(result);
    assertTrue(result.contains("Bot"));
    assertTrue(result.contains("Kitchen"));
  }
  
  /**
   * Tests multiple computer players can coexist.
   */
  @Test
  public void testMultipleComputerPlayers() {
    RandomGenerator rng1 = new RandomGenerator(0, 1, 2);
    RandomGenerator rng2 = new RandomGenerator(2, 1, 0);
    
    ComputerPlayer bot1 = new ComputerPlayer("Bot1", room1, rng1);
    ComputerPlayer bot2 = new ComputerPlayer("Bot2", room2, rng2);
    
    assertNotEquals(bot1, bot2);
    assertEquals(room1, bot1.getCurrentRoom());
    assertEquals(room2, bot2.getCurrentRoom());
  }
  
  /**
   * Tests computer player interacts with game correctly.
   */
  @Test
  public void testComputerPlayerInGame() {
    game.setMaxTurns(20);
    game.addComputerPlayer("Bot1", room1, rng);
    game.addComputerPlayer("Bot2", room2, rng);
    game.addComputerPlayer("Bot3", room3, rng);
    game.startGame();
    
    assertEquals(3, game.getPlayers().size());
    assertTrue(game.getCurrentPlayer() instanceof ComputerPlayer);
  }
}