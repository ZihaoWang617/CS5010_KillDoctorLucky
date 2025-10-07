package killdoctorlucky;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Represents the main game logic and state management for Kill Doctor Lucky.
 * The Game class coordinates all game components including players, the board,
 * Doctor Lucky, and the card deck to provide complete gameplay functionality.
 */
public class Game {
  private static final int MIN_PLAYERS = 3;
  private static final int MAX_PLAYERS = 7;
  private static final int STARTING_HAND_SIZE = 6;

  private final List<Player> players;
  private final Board board;
  private final DoctorLucky doctorLucky;
  private final Deck deck;
  private int currentPlayerIndex;
  private int turnCount;
  private GameStatus gameStatus;

  /**
   * Creates a new Kill Doctor Lucky game with the specified players. The game
   * initializes the board, places Doctor Lucky, creates the deck, and deals
   * starting cards to all players.
   * 
   * @param playerNames list of player names for the game
   * @throws IllegalArgumentException if playerNames is null, contains invalid
   *                                  names, or has fewer than MIN_PLAYERS or more
   *                                  than MAX_PLAYERS
   */
  public Game(List<String> playerNames) {
    if (playerNames == null) {
      throw new IllegalArgumentException("Player names list cannot be null");
    }
    if (playerNames.size() < MIN_PLAYERS) {
      throw new IllegalArgumentException("Need at least " + MIN_PLAYERS + " players");
    }
    if (playerNames.size() > MAX_PLAYERS) {
      throw new IllegalArgumentException("Cannot have more than " + MAX_PLAYERS + " players");
    }

    this.board = new Board();
    this.deck = new Deck();
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.turnCount = 0;
    this.gameStatus = GameStatus.SETUP;

    // Initialize board and rooms (simplified - would normally load from file)
    initializeBoard();

    // Create Doctor Lucky in starting room
    Room startingRoom = board.getAllRooms().iterator().next();
    this.doctorLucky = new DoctorLucky(startingRoom);

    // Create players and place them in different starting rooms
    initializePlayers(playerNames);

    // Deal initial cards
    dealInitialCards();
  }

  /**
   * Starts the game, transitioning from setup to active play. After calling this
   * method, players can begin taking turns.
   */
  public void startGame() {
    if (gameStatus != GameStatus.SETUP) {
      throw new IllegalStateException("Game can only be started from SETUP state");
    }
    this.gameStatus = GameStatus.IN_PROGRESS;
  }

  /**
   * Executes a complete turn for the current player. This includes movement, card
   * playing opportunities, and turn advancement.
   */
  public void playTurn() {
    if (gameStatus != GameStatus.IN_PROGRESS) {
      throw new IllegalStateException("Cannot play turn when game is not in progress");
    }

    Player currentPlayer = getCurrentPlayer();

    // Allow player to draw a card if in a visible room
    if (currentPlayer.canDrawCard() && !deck.isEmpty()) {
      Playable drawnCard = deck.drawCard();
      if (drawnCard != null) {
        currentPlayer.addCard(drawnCard);
      }
    }

    // Move Doctor Lucky at the end of each turn
    doctorLucky.moveNext();

    // Advance to next player
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    if (currentPlayerIndex == 0) {
      turnCount++;
    }

    // Check win condition
    if (checkWinCondition()) {
      gameStatus = GameStatus.FINISHED;
    }
  }

  /**
   * Gets the player whose turn it currently is.
   * 
   * @return the current player
   * @throws IllegalStateException if game is not in progress
   */
  public Player getCurrentPlayer() {
    if (gameStatus == GameStatus.SETUP) {
      throw new IllegalStateException("No current player during setup");
    }
    return players.get(currentPlayerIndex);
  }

  /**
   * Checks if the game has ended.
   * 
   * @return true if the game is over, false if still in progress
   */
  public boolean isGameOver() {
    return gameStatus == GameStatus.FINISHED;
  }

  /**
   * Gets the winner of the game.
   * 
   * @return the winning player, or null if game is not over or has no winner
   */
  public Player getWinner() {
    if (gameStatus != GameStatus.FINISHED) {
      return null;
    }
    // Winner determination logic would be implemented here
    // For now, return null as we need more complex game state tracking
    return null;
  }

  /**
   * Attempts a murder of Doctor Lucky by the specified player with a weapon. The
   * success depends on whether the player is alone with Doctor Lucky, the
   * weapon's attack value, and any failure cards played by other players.
   * 
   * @param player the player attempting the murder
   * @param weapon the weapon card being used
   * @return the result of the murder attempt
   * @throws IllegalArgumentException if player or weapon is null
   * @throws IllegalStateException    if game is not in progress
   */
  public MurderResult attemptMurder(Player player, WeaponCard weapon) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (weapon == null) {
      throw new IllegalArgumentException("Weapon cannot be null");
    }
    if (gameStatus != GameStatus.IN_PROGRESS) {
      throw new IllegalStateException("Cannot attempt murder when game is not in progress");
    }

    // Check if player has the weapon
    if (!player.getHand().contains(weapon)) {
      throw new IllegalArgumentException("Player does not have the specified weapon");
    }

    // Check if player is alone with Doctor Lucky
    if (!doctorLucky.isAloneWith(player)) {
      return MurderResult.FAILED_WITNESS_PRESENT;
    }

    // Check weapon strength (simplified - in full game would allow other players to
    // play failure cards)
    if (weapon.getAttackValue() <= 0) {
      return MurderResult.FAILED_INSUFFICIENT_WEAPON;
    }

    // Remove weapon from player's hand
    player.removeCard(weapon);
    deck.discardCard(weapon);

    // Murder succeeds
    gameStatus = GameStatus.FINISHED;
    return MurderResult.SUCCESS;
  }

  /**
   * Deals the initial hand of cards to each player. Each player receives
   * STARTING_HAND_SIZE cards from the deck.
   */
  private void dealInitialCards() {
    for (Player player : players) {
      for (int i = 0; i < STARTING_HAND_SIZE; i++) {
        Playable card = deck.drawCard();
        if (card != null) {
          player.addCard(card);
        }
      }
    }
  }

  /**
   * Checks if any win condition has been met.
   * 
   * @return true if someone has won, false otherwise
   */
  private boolean checkWinCondition() {
    // Win condition is primarily checked through successful murder attempts
    // Additional conditions could be added here (time limits, special cards, etc.)
    return gameStatus == GameStatus.FINISHED;
  }

  /**
   * Initializes the game board with rooms and connections. In a full
   * implementation, this would load from the mansion.txt file.
   */
  private void initializeBoard() {
    // Simplified board initialization
    // In full implementation, would parse mansion.txt file
    Room kitchen = new Room("Kitchen", true);
    Room library = new Room("Library", true);
    Room diningHall = new Room("Dining Hall", true);

    board.addRoom(kitchen);
    board.addRoom(library);
    board.addRoom(diningHall);

    board.connectRooms(kitchen, diningHall);
    board.connectRooms(diningHall, library);
  }

  /**
   * Creates player objects and places them in starting positions.
   * 
   * @param playerNames list of names for the players
   */
  private void initializePlayers(List<String> playerNames) {
    Collection<Room> rooms = board.getAllRooms();
    List<Room> roomList = new ArrayList<>(rooms);

    for (int i = 0; i < playerNames.size(); i++) {
      String name = playerNames.get(i);
      if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Player name cannot be null or empty");
      }

      // Place players in different rooms
      Room startingRoom = roomList.get(i % roomList.size());
      Player player = new Player(name.trim(), startingRoom);
      players.add(player);
    }
  }

  /**
   * Gets the current turn number.
   * 
   * @return the number of complete rounds played
   */
  public int getTurnCount() {
    return turnCount;
  }

  /**
   * Gets the current game status.
   * 
   * @return the current status of the game
   */
  public GameStatus getGameStatus() {
    return gameStatus;
  }

  @Override
  public String toString() {
    return String.format("Game{players=%d, turn=%d, status=%s}", players.size(), turnCount,
        gameStatus);
  }
}