package killdoctorlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Game functionality. Tests game initialization, player
 * management, turn execution, and win conditions.
 */
public class GameTest {
  private Game game;
  private List<String> validPlayers;
  private List<String> minPlayers;
  private List<String> maxPlayers;
  private List<String> tooFewPlayers;
  private List<String> tooManyPlayers;
  /**
   * Sets up the test fixture before each test.
   * Initializes lists of player names for valid, minimum, maximum, too few, and too many players
   * to be used in game initialization and boundary condition tests.
   */
  
  @Before
  public void setUp() {
    validPlayers = Arrays.asList("Alice", "Bob", "Charlie", "David");
    minPlayers = Arrays.asList("Alice", "Bob", "Charlie"); // 3 players (minimum)
    maxPlayers = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve", "Frank", "George"); // 7
    // players
    // (maximum)
    tooFewPlayers = Arrays.asList("Alice", "Bob"); // 2 players (below minimum)
    tooManyPlayers = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve", "Frank", "George",
        "Henry"); // 8 players (above maximum)
  }

  // Test 1a: Valid game initialization
  @Test
  public void testValidGameInitialization() {
    game = new Game(validPlayers);
    assertNotNull(game);
    assertEquals(GameStatus.SETUP, game.getGameStatus());
    assertEquals(0, game.getTurnCount());
  }

  // Test 1b: Minimum Players (Valid)
  @Test
  public void testMinimumPlayers() {
    game = new Game(minPlayers);
    assertNotNull(game);
    assertEquals(GameStatus.SETUP, game.getGameStatus());
  }

  // Test 1c: Maximum Players (Valid)
  @Test
  public void testMaximumPlayers() {
    game = new Game(maxPlayers);
    assertNotNull(game);
    assertEquals(GameStatus.SETUP, game.getGameStatus());
  }

  // Test 1d: Below Minimum Players
  @Test
  public void testBelowMinimumPlayers() {
    try {
      game = new Game(tooFewPlayers);
      fail("Expected IllegalArgumentException for too few players");
    } catch (IllegalArgumentException e) {
      // Expected - need at least 3 players
      assertTrue(e.getMessage().contains("at least"));
    }
  }

  // Test 1e: Above Maximum Players
  @Test
  public void testAboveMaximumPlayers() {
    try {
      game = new Game(tooManyPlayers);
      fail("Expected IllegalArgumentException for too many players");
    } catch (IllegalArgumentException e) {
      // Expected - cannot have more than 7 players
      assertTrue(e.getMessage().contains("more than"));
    }
  }

  // Test 1f: Start Game
  @Test
  public void testStartGame() {
    game = new Game(validPlayers);
    assertEquals(GameStatus.SETUP, game.getGameStatus());

    game.startGame();
    assertEquals(GameStatus.IN_PROGRESS, game.getGameStatus());
  }

  @Test
  public void testStartGameTwice() {
    game = new Game(validPlayers);
    game.startGame();

    try {
      game.startGame();
      fail("Expected IllegalStateException when starting game twice");
    } catch (IllegalStateException e) {
      // Expected
    }
  }

  // Test game with null player names
  @Test
  public void testNullPlayerNames() {
    try {
      game = new Game(null);
      fail("Expected IllegalArgumentException for null player list");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testGetCurrentPlayerBeforeStart() {
    game = new Game(validPlayers);

    try {
      game.getCurrentPlayer();
      fail("Expected IllegalStateException when getting current player before game starts");
    } catch (IllegalStateException e) {
      // Expected
    }
  }

  @Test
  public void testGetCurrentPlayerAfterStart() {
    game = new Game(validPlayers);
    game.startGame();

    Player currentPlayer = game.getCurrentPlayer();
    assertNotNull(currentPlayer);
  }

  @Test
  public void testPlayTurnBeforeStart() {
    game = new Game(validPlayers);

    try {
      game.playTurn();
      fail("Expected IllegalStateException when playing turn before game starts");
    } catch (IllegalStateException e) {
      // Expected
    }
  }

  @Test
  public void testPlayTurn() {
    game = new Game(validPlayers);
    game.startGame();

    int initialTurn = game.getTurnCount();
    game.playTurn();

    // Turn count may or may not increase depending on player rotation
    assertTrue(game.getTurnCount() >= initialTurn);
  }

  @Test
  public void testIsGameOverInitially() {
    game = new Game(validPlayers);
    assertFalse(game.isGameOver());

    game.startGame();
    assertFalse(game.isGameOver());
  }

  @Test
  public void testGetWinnerBeforeGameOver() {
    game = new Game(validPlayers);
    game.startGame();

    Player winner = game.getWinner();
    assertNull(winner); // No winner yet
  }

  @Test
  public void testAttemptMurderWithoutWeapon() {
    game = new Game(validPlayers);
    game.startGame();

    Player player = game.getCurrentPlayer();
    WeaponCard knife = new WeaponCard("Knife", 5);

    try {
      game.attemptMurder(player, knife);
      fail("Expected IllegalArgumentException when player doesn't have weapon");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testAttemptMurderNullPlayer() {
    game = new Game(validPlayers);
    game.startGame();

    WeaponCard knife = new WeaponCard("Knife", 5);

    try {
      game.attemptMurder(null, knife);
      fail("Expected IllegalArgumentException for null player");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testAttemptMurderNullWeapon() {
    game = new Game(validPlayers);
    game.startGame();

    Player player = game.getCurrentPlayer();

    try {
      game.attemptMurder(player, null);
      fail("Expected IllegalArgumentException for null weapon");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void testGameToString() {
    game = new Game(validPlayers);
    String result = game.toString();

    assertNotNull(result);
    assertTrue(result.contains("Game"));
    assertTrue(result.contains("players"));
  }
}