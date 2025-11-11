package killdoctorlucky.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import killdoctorlucky.model.occupants.ComputerPlayer;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.model.occupants.Pet;
import killdoctorlucky.model.occupants.Player;
import killdoctorlucky.util.RandomGenerator;
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
  private Room library;
  
  /**
   * Sets up the test fixture before each test.
   * Creates a simple game environment for testing.
   */
  @Before
  public void setUp() {
    board = new Board();
    kitchen = new Room("Kitchen", true);
    dining = new Room("Dining Room", true);
    library = new Room("Library", true);
    
    board.addRoom(kitchen);
    board.addRoom(dining);
    board.addRoom(library);
    board.connectRooms(kitchen, dining);
    board.connectRooms(dining, library);
    
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
    game.addHumanPlayer("Charlie", kitchen);  // ← 添加第三个玩家
    game.startGame();
    game.startGame();  // Should throw exception
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
    Room testRoom2 = new Room("Dining Room");
    Room testRoom3 = new Room("Library");

    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", testRoom2);
    game.addHumanPlayer("Charlie", testRoom3);
    game.startGame();

    Player alice = game.getCurrentPlayer();
    boolean picked = game.pickupFromRoom(alice, "Knife");

    assertTrue(picked);
    assertEquals(1, alice.getInventory().size());
  }

  /**
   * Tests pickup non-existent item.
   */
  @Test
  public void testPickupNonExistentItem() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", dining);
    game.addHumanPlayer("Charlie", library);
    game.startGame();

    Player alice = game.getCurrentPlayer();
    boolean picked = game.pickupFromRoom(alice, "NonExistent");

    assertFalse(picked);
    assertEquals(0, alice.getInventory().size());
  }

  /**
   * Tests describe look around functionality.
   */
  @Test
  public void testDescribeLookAround() {
    game.addHumanPlayer("Alice", kitchen);
    game.addHumanPlayer("Bob", dining);      // ← 添加
    game.addHumanPlayer("Charlie", dining);  // ← 添加
    game.startGame();

    String description = game.describeLookAround(game.getCurrentPlayer());
    assertNotNull(description);
    assertTrue(description.contains("You are in:"));
    assertTrue(description.contains("Kitchen"));
  }
  
  /**
   * Tests setting and getting the pet.
   */
  @Test
  public void testSetAndGetPet() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    Pet testPet = new Pet("Fortune", testRoom1);
    testGame.setPet(testPet);
    
    assertEquals(testPet, testGame.getPet());
  }
  
  /**
   * Tests setting null pet throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetNullPet() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    testGame.setPet(null);
  }
  
  /**
   * Tests moving the pet to a different room.
   */
  @Test
  public void testMovePet() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Library");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    Pet testPet = new Pet("Fortune", testRoom1);
    testGame.setPet(testPet);
    
    assertEquals(testRoom1, testPet.getCurrentRoom());
    
    testGame.movePet(testRoom2);
    
    assertEquals(testRoom2, testPet.getCurrentRoom());
  }
  
  /**
   * Tests moving pet to null room throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePetNullRoom() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    Pet testPet = new Pet("Fortune", testRoom1);
    testGame.setPet(testPet);
    
    testGame.movePet(null);
  }
  
  /**
   * Tests moving pet when no pet exists throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testMovePetNoPetExists() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Library");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    // No pet set
    testGame.movePet(testRoom2);
  }
  
  /**
   * Tests setting and getting max turns.
   */
  @Test
  public void testSetAndGetMaxTurns() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    testGame.setMaxTurns(20);
    assertEquals(20, testGame.getMaxTurns());
  }
  
  /**
   * Tests setting invalid max turns throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetMaxTurnsZero() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    testGame.setMaxTurns(0);
  }
  
  /**
   * Tests setting negative max turns throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetMaxTurnsNegative() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    testGame.setMaxTurns(-5);
  }
  
  // ===== MILESTONE 3: MURDER ATTEMPT TESTS =====
  
  /**
   * Tests successful murder with weapon.
   */
  @Test
  public void testAttemptMurderSuccessWithWeapon() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Library");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 10);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Add 3 players (minimum required)
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);    // In different room
    testGame.addHumanPlayer("Charlie", testRoom2); // In different room
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    
    // Give player a strong weapon
    Item gun = new Item("Gun", 10);
    testPlayer.pickUpItem(gun);
    
    // Attempt murder (alone in room1, others in room2)
    MurderResult result = testGame.attemptMurder(testPlayer, "Gun");
    
    assertEquals(MurderResult.SUCCESS, result);
    assertFalse(testDoctor.isAlive());
    assertEquals(0, testDoctor.getHealth());
    assertTrue(testGame.isGameOver());
  }
  
  /**
   * Tests successful murder with poke in eye.
   */
  @Test
  public void testAttemptMurderSuccessWithPoke() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Library");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 1);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Add 3 players
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    
    // Poke in eye (no weapon)
    MurderResult result = testGame.attemptMurder(testPlayer, null);
    
    assertEquals(MurderResult.SUCCESS, result);
    assertFalse(testDoctor.isAlive());
    assertTrue(testGame.isGameOver());
  }
  
  /**
   * Tests failed murder due to insufficient weapon.
   */
  @Test
  public void testAttemptMurderFailedInsufficientWeapon() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Library");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Add 3 players
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    
    // Weak weapon
    Item testKnife = new Item("Knife", 5);
    testPlayer.pickUpItem(testKnife);
    
    MurderResult result = testGame.attemptMurder(testPlayer, "Knife");
    
    assertEquals(MurderResult.FAILED_INSUFFICIENT_WEAPON, result);
    assertTrue(testDoctor.isAlive());
    assertEquals(45, testDoctor.getHealth());
    assertFalse(testGame.isGameOver());
  }
  
  /**
   * Tests failed murder due to witness in same room.
   */
  @Test
  public void testAttemptMurderFailedWitnessInSameRoom() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Add two players in same room
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom1);
    testGame.addHumanPlayer("Charlie", testRoom1);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    Item gun = new Item("Gun", 50);
    testPlayer.pickUpItem(gun);
    
    // Bob is in same room - witness!
    MurderResult result = testGame.attemptMurder(testPlayer, "Gun");
    
    assertEquals(MurderResult.FAILED_WITNESS_PRESENT, result);
    assertTrue(testDoctor.isAlive());
    assertEquals(50, testDoctor.getHealth());
  }
  
  /**
   * Tests failed murder due to witness in adjacent room.
   */
  @Test
  public void testAttemptMurderFailedWitnessInAdjacentRoom() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Dining Room");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    testBoard.connectRooms(testRoom1, testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Alice in room with Doctor Lucky, Bob in adjacent room
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    Item gun = new Item("Gun", 50);
    testPlayer.pickUpItem(gun);
    
    // Bob can see from adjacent room - witness!
    MurderResult result = testGame.attemptMurder(testPlayer, "Gun");
    
    assertEquals(MurderResult.FAILED_WITNESS_PRESENT, result);
  }
  
  /**
   * Tests successful murder when pet blocks witness view.
   */
  @Test
  public void testAttemptMurderSuccessPetBlocksWitness() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Dining Room");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    testBoard.connectRooms(testRoom1, testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 10);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Set pet in same room as attacker
    Pet testPet = new Pet("Fortune", testRoom1);
    testGame.setPet(testPet);
    
    // Alice in room with Doctor Lucky and pet, Bob in adjacent room
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    Item gun = new Item("Gun", 10);
    testPlayer.pickUpItem(gun);
    
    // Pet blocks Bob's view - no witness!
    MurderResult result = testGame.attemptMurder(testPlayer, "Gun");
    
    assertEquals(MurderResult.SUCCESS, result);
    assertFalse(testDoctor.isAlive());
  }
  
  /**
   * Tests murder attempt removes weapon from game.
   */
  @Test
  public void testAttemptMurderRemovesWeapon() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Library");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Add 3 players
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    Item testKnife = new Item("Knife", 5);
    testPlayer.pickUpItem(testKnife);
    
    assertEquals(1, testPlayer.getInventory().size());
    
    testGame.attemptMurder(testPlayer, "Knife");
    
    // Weapon should be removed from inventory
    assertEquals(0, testPlayer.getInventory().size());
  }
  
  /**
   * Tests murder attempt when player not in same room throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAttemptMurderNotInSameRoom() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Library");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Add 3 players
    testGame.addHumanPlayer("Alice", testRoom2);  // Different room from Doctor
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    Item gun = new Item("Gun", 50);
    testPlayer.pickUpItem(gun);
    
    testGame.attemptMurder(testPlayer, "Gun");
  }
  
  /**
   * Tests murder attempt with non-existent item throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAttemptMurderWithNonExistentItem() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Library");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Add 3 players
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    
    // Player doesn't have this item
    testGame.attemptMurder(testPlayer, "MagicSword");
  }
  
  /**
   * Tests murder attempt when game not in progress throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testAttemptMurderGameNotInProgress() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    testGame.addHumanPlayer("Alice", testRoom1);
    // Don't start game
    
    Player testPlayer = testGame.getPlayers().get(0);
    testGame.attemptMurder(testPlayer, null);
  }
  
  // ===== MILESTONE 3: VISIBILITY TESTS =====
  
  /**
   * Tests player is visible when in same room as another player.
   */
  @Test
  public void testIsPlayerVisibleSameRoom() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom1);
    testGame.addHumanPlayer("Charlie", testRoom1);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    
    // Other players in same room
    assertTrue(testGame.isPlayerVisible(testPlayer));
  }
  
  /**
   * Tests player is visible from adjacent room.
   */
  @Test
  public void testIsPlayerVisibleAdjacentRoom() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Dining Room");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    testBoard.connectRooms(testRoom1, testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    
    // Bob in adjacent room can see
    assertTrue(testGame.isPlayerVisible(testPlayer));
  }
  
  /**
   * Tests player is not visible when pet blocks the view.
   */
  @Test
  public void testIsPlayerNotVisiblePetBlocks() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Dining Room");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    testBoard.connectRooms(testRoom1, testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Pet in same room as Alice blocks view from neighbors
    Pet testPet = new Pet("Fortune", testRoom1);
    testGame.setPet(testPet);
    
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    
    // Pet blocks Bob's view
    assertFalse(testGame.isPlayerVisible(testPlayer));
  }
  
  @Test
  public void testIsPlayerNotVisibleAlone() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Dining Room");
    Room testRoom3 = new Room("Library");  // Isolated room
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    testBoard.addRoom(testRoom3);
    testBoard.connectRooms(testRoom1, testRoom2);
    // testRoom3 is NOT connected to anything

    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);

    // Alice STARTS in isolated room3
    testGame.addHumanPlayer("Alice", testRoom3);  // ← 直接在隔离房间开始
    testGame.addHumanPlayer("Bob", testRoom1);
    testGame.addHumanPlayer("Charlie", testRoom1);
    testGame.startGame();

    Player testPlayer = testGame.getCurrentPlayer(); // Alice

    // Alice is in isolated room3, no one can see her
    assertFalse("Player in isolated room should not be visible", 
        testGame.isPlayerVisible(testPlayer));
  }
  
  /**
   * Tests isPlayerVisible with null player throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIsPlayerVisibleNullPlayer() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    testGame.isPlayerVisible(null);
  }
  
  // ===== MILESTONE 3: GAME END TESTS =====
  
  /**
   * Tests getting winner after successful murder.
   */
  @Test
  public void testGetWinnerAfterMurder() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    Room testRoom2 = new Room("Library");
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 10);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    // Add 3 players
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom2);
    testGame.addHumanPlayer("Charlie", testRoom2);
    testGame.startGame();
    
    Player testPlayer = testGame.getCurrentPlayer();
    Item gun = new Item("Gun", 10);
    testPlayer.pickUpItem(gun);
    
    testGame.attemptMurder(testPlayer, "Gun");
    
    assertNotNull(testGame.getWinner());
    assertEquals(testPlayer, testGame.getWinner());
  }
  
  /**
   * Tests getting winner when game not finished returns null.
   */
  @Test
  public void testGetWinnerGameNotFinished() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom1);
    testGame.addHumanPlayer("Charlie", testRoom1);
    testGame.startGame();
    
    assertNull(testGame.getWinner());
  }
  
  /**
   * Tests endGame sets status to finished.
   */
  @Test
  public void testEndGame() {
    Board testBoard = new Board();
    Room testRoom1 = new Room("Kitchen");
    testBoard.addRoom(testRoom1);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom1, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    testGame.setMaxTurns(20);
    
    testGame.addHumanPlayer("Alice", testRoom1);
    testGame.addHumanPlayer("Bob", testRoom1);
    testGame.addHumanPlayer("Charlie", testRoom1);
    testGame.startGame();
    
    assertFalse(testGame.isGameOver());
    
    testGame.endGame();
    
    assertTrue(testGame.isGameOver());
  }
}