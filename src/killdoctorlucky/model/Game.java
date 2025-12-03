package killdoctorlucky.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import killdoctorlucky.model.cards.Playable;
import killdoctorlucky.model.occupants.ComputerPlayer;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.model.occupants.Pet;
import killdoctorlucky.model.occupants.Player;
import killdoctorlucky.util.RandomGenerator;

/**
 * Represents the main game logic and state management for Kill Doctor Lucky.
 * The Game class coordinates all game components including players, the board,
 * Doctor Lucky, the pet, and the card deck to provide complete gameplay functionality.
 * 
 * <p>Implements ReadOnlyGameModel to provide controlled read access for the View layer
 * in Milestone 4's MVC architecture.</p>
 */
public class Game implements ReadOnlyGameModel {

  /** Minimum and maximum number of players. */
  public static final int MIN_PLAYERS = 3;
  public static final int MAX_PLAYERS = 7;

  /** Initial number of cards dealt to each player. */
  private static final int STARTING_HAND_SIZE = 6;

  private final List<Player> players = new ArrayList<>();
  private final Board board;
  private final Deck deck;
  private final DoctorLucky doctorLucky;
  
  private Pet pet;
  private int currentPlayerIndex = 0;
  private int turnCount = 0;
  private int maxTurns = 0;
  private GameStatus status = GameStatus.SETUP;
  private Player winner = null;
  
  // Milestone 4: Track last action result for View display
  private String lastActionResult = "";

  /**
   * Creates a game from the given components.
   *
   * @param boardParam the game board (non-null)
   * @param deckParam the draw deck (non-null)
   * @param doctorLuckyParam the target character (non-null)
   * @throws IllegalArgumentException if any argument is null
   */
  public Game(Board boardParam, Deck deckParam, DoctorLucky doctorLuckyParam) {
    if (boardParam == null || deckParam == null || doctorLuckyParam == null) {
      throw new IllegalArgumentException("Board, deck, and Doctor Lucky must be non-null");
    }
    this.board = boardParam;
    this.deck = deckParam;
    this.doctorLucky = doctorLuckyParam;
  }

  /**
   * Sets the pet for this game.
   *
   * @param petParam the pet to set
   * @throws IllegalArgumentException if pet is null
   */
  public void setPet(Pet petParam) {
    if (petParam == null) {
      throw new IllegalArgumentException("Pet cannot be null");
    }
    this.pet = petParam;
    this.pet.initializeDfsPath(board);
  }

  /**
   * Sets the maximum number of turns for the game.
   *
   * @param maxTurnsParam the maximum turns
   * @throws IllegalArgumentException if maxTurns is not positive
   */
  public void setMaxTurns(int maxTurnsParam) {
    if (maxTurnsParam <= 0) {
      throw new IllegalArgumentException("Max turns must be positive");
    }
    this.maxTurns = maxTurnsParam;
  }

  // ========== Player Management ==========

  /**
   * Adds a human player to the game.
   * Can only be called during SETUP phase.
   *
   * @param name the player's name (non-null, non-empty)
   * @param startingRoom the room where the player begins
   * @throws IllegalArgumentException if name or startingRoom is invalid
   * @throws IllegalStateException if game has already started
   */
  public void addHumanPlayer(String name, Room startingRoom) {
    ensureSetupPhase();
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty");
    }
    if (startingRoom == null) {
      throw new IllegalArgumentException("Starting room cannot be null");
    }
    
    Player player = new Player(name, startingRoom);
    players.add(player);
    lastActionResult = "Added human player: " + name;
  }

  /**
   * Adds a computer player to the game.
   * Can only be called during SETUP phase.
   *
   * @param name the player's name (non-null, non-empty)
   * @param startingRoom the room where the player begins
   * @param rng the random number generator for AI decisions
   * @throws IllegalArgumentException if any argument is invalid
   * @throws IllegalStateException if game has already started
   */
  public void addComputerPlayer(String name, Room startingRoom, RandomGenerator rng) {
    ensureSetupPhase();
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty");
    }
    if (startingRoom == null) {
      throw new IllegalArgumentException("Starting room cannot be null");
    }
    if (rng == null) {
      throw new IllegalArgumentException("RandomGenerator cannot be null");
    }
    
    ComputerPlayer computerPlayer = new ComputerPlayer(name, startingRoom, rng);
    players.add(computerPlayer);
    lastActionResult = "Added computer player: " + name;
  }

  // ========== Game Flow ==========

  /**
   * Starts the game: validates player count, deals opening hands, and switches to IN_PROGRESS.
   * Call this after all players have been added.
   */
  public void startGame() {
    if (status != GameStatus.SETUP) {
      throw new IllegalStateException("Game has already been started");
    }
    if (players.size() < MIN_PLAYERS || players.size() > MAX_PLAYERS) {
      throw new IllegalStateException(
          "Player count must be between " + MIN_PLAYERS + " and " + MAX_PLAYERS);
    }
    dealInitialCards();
    status = GameStatus.IN_PROGRESS;
    lastActionResult = "Game started with " + players.size() + " players";
  }

  /**
   * Executes one player's turn. The controller should invoke player actions via Commands.
   * After the player's actions are processed, Doctor Lucky and Pet automatically move.
   */
  public void playTurn() {
    if (status != GameStatus.IN_PROGRESS) {
      throw new IllegalStateException("Game has not started");
    }
    
    // Doctor Lucky moves after each player's turn
    doctorLucky.moveNext();
    
    // Pet wanders in DFS pattern (no parameters needed - uses initialized path)
    if (pet != null) {
      pet.wanderNext();
      lastActionResult += "\nPet moved to: " + pet.getCurrentRoom().getName();
    }
    
    // Move to next player
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    turnCount++;
    
    // Check if max turns reached
    if (turnCount >= maxTurns) {
      status = GameStatus.FINISHED;
      lastActionResult = "Maximum turns reached. Doctor Lucky escapes!";
    }
  }

  /**
   * Ends the game by setting status to FINISHED.
   */
  public void endGame() {
    status = GameStatus.FINISHED;
    if (lastActionResult.isEmpty()) {
      lastActionResult = "Game ended";
    }
  }

  // ========== Game Actions ==========

  /**
   * Attempts to murder Doctor Lucky with the specified item.
   *
   * @param player the player attempting the murder
   * @param itemName the name of the item to use (null for "poke in eye")
   * @return the result of the murder attempt
   * @throws IllegalArgumentException if player is null
   */
  public MurderResult attemptMurder(Player player, String itemName) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    
    // Check if player is in same room as Doctor Lucky
    if (!player.getCurrentRoom().equals(doctorLucky.getCurrentRoom())) {
      lastActionResult = "Doctor Lucky is not in your room!";
      return MurderResult.FAILED_INSUFFICIENT_WEAPON;
    }
    
    // Check for witnesses
    if (isPlayerVisible(player)) {
      lastActionResult = "Murder attempt failed - witnesses present!";
      return MurderResult.FAILED_WITNESS_PRESENT;  // 注意:不是复数
    }
    
    // Calculate damage
    int damage = 1; // Default "poke in eye"
    Item weaponUsed = null;
    
    if (itemName != null && !itemName.trim().isEmpty()) {
      // Find the item in player's inventory
      for (Item item : player.getInventory()) {
        if (item.getName().equalsIgnoreCase(itemName.trim())) {
          damage = item.getDamage();
          weaponUsed = item;
          break;
        }
      }
      
      if (weaponUsed == null) {
        lastActionResult = "You don't have that item!";
        return MurderResult.FAILED_INSUFFICIENT_WEAPON;
      }
    }
    
    // Apply damage
    doctorLucky.takeDamage(damage);
    
    // Remove item from game if used
    if (weaponUsed != null) {
      player.dropItem(weaponUsed.getName());
    }
    
    // Check if Doctor Lucky is dead
    if (!doctorLucky.isAlive()) {
      winner = player;
      status = GameStatus.FINISHED;
      lastActionResult = player.getName() + " successfully killed Doctor Lucky!";
      return MurderResult.SUCCESS;
    }
    
    lastActionResult = "Attack dealt " + damage + " damage. Doctor Lucky has " 
        + doctorLucky.getHealth() + " health remaining.";
    return MurderResult.SUCCESS;
  }

  /**
   * Player picks up an item from their current room.
   *
   * @param player the player picking up the item
   * @param itemName the name of the item
   * @return true if successful, false otherwise
   */
  public boolean pickupFromRoom(Player player, String itemName) {
    if (player == null || itemName == null) {
      return false;
    }
    
    Room currentRoom = player.getCurrentRoom();
    Item item = currentRoom.removeItem(itemName);
    
    if (item == null) {
      lastActionResult = "Item '" + itemName + "' not found in this room";
      return false;
    }
    
    if (!player.canCarryMore()) {
      currentRoom.addItem(item); // Put it back
      lastActionResult = "Cannot carry more items (max " + player.getMaxCarry() + ")";
      return false;
    }
    
    boolean success = player.pickUpItem(item);
    if (success) {
      lastActionResult = player.getName() + " picked up " + itemName;
    } else {
      currentRoom.addItem(item); // Put it back
      lastActionResult = "Failed to pick up " + itemName;
    }
    
    return success;
  }

  /**
   * Moves the pet to the specified room.
   *
   * @param targetRoom the room to move the pet to
   * @throws IllegalArgumentException if targetRoom is null
   */
  public void movePet(Room targetRoom) {
    if (targetRoom == null) {
      throw new IllegalArgumentException("Target room cannot be null");
    }
    if (pet == null) {
      throw new IllegalStateException("No pet in the game");
    }
    
    pet.moveToRoom(targetRoom);
    lastActionResult = "Moved pet to " + targetRoom.getName();
  }

  /**
   * Gets a description of what the player can see from their current room.
   *
   * @param player the player looking around
   * @return a formatted string describing visible rooms, players, and items
   */
  public String describeLookAround(Player player) {
    if (player == null) {
      return "Invalid player";
    }
    
    Room currentRoom = player.getCurrentRoom();
    StringBuilder description = new StringBuilder();
    
    description.append("You are in: ").append(currentRoom.getName()).append("\n\n");
    
    // Items in current room
    description.append("Items here: ");
    if (currentRoom.getItems().isEmpty()) {
      description.append("None");
    } else {
      for (Item item : currentRoom.getItems()) {
        description.append(item.getName()).append(" (").append(item.getDamage())
            .append(" damage), ");
      }
      description.setLength(description.length() - 2); // Remove last comma
    }
    description.append("\n\n");
    
    // Players in current room
    description.append("Players here: ");
    List<Player> playersInRoom = new ArrayList<>();
    for (Player p : players) {
      if (p.getCurrentRoom().equals(currentRoom) && !p.equals(player)) {
        playersInRoom.add(p);
      }
    }
    if (playersInRoom.isEmpty()) {
      description.append("None");
    } else {
      for (Player p : playersInRoom) {
        description.append(p.getName()).append(", ");
      }
      description.setLength(description.length() - 2);
    }
    description.append("\n\n");
    
    // Doctor Lucky location
    if (doctorLucky.getCurrentRoom().equals(currentRoom)) {
      description.append("Doctor Lucky is HERE!\n");
    } else {
      description.append("Doctor Lucky is in: ")
          .append(doctorLucky.getCurrentRoom().getName()).append("\n");
    }
    
    // Pet location
    if (pet != null) {
      if (pet.getCurrentRoom().equals(currentRoom)) {
        description.append("Pet is HERE!\n");
      } else {
        description.append("Pet is in: ").append(pet.getCurrentRoom().getName()).append("\n");
      }
    }
    description.append("\n");
    
    // Adjacent rooms (considering pet blocking)
    description.append("Adjacent rooms:\n");
    List<Room> adjacentRooms = board.getAdjacentRooms(currentRoom);
    if (adjacentRooms.isEmpty()) {
      description.append("  None");
    } else {
      for (Room room : adjacentRooms) {
        // Check if room is visible (not blocked by pet)
        boolean blocked = (pet != null && pet.getCurrentRoom().equals(room));
        if (blocked) {
          description.append("  - ").append(room.getName())
              .append(" (blocked by pet)\n");
        } else {
          description.append("  - ").append(room.getName()).append("\n");
        }
      }
    }
    
    lastActionResult = "Looked around";
    return description.toString();
  }

  /**
   * Checks if a player can be seen by other players (for witness detection).
   *
   * @param player the player to check
   * @return true if player can be seen by at least one other player
   */
  public boolean isPlayerVisible(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    
    for (Player other : players) {
      if (other.equals(player)) {
        continue;
      }
      
      // Check if other player can see this player
      if (player.canSeeOtherPlayer(other, board)) {
        return true;
      }
    }
    
    return false;
  }

  // ========== ReadOnlyGameModel Implementation ==========

  @Override
  public Player getCurrentPlayer() {
    if (status != GameStatus.IN_PROGRESS || players.isEmpty()) {
      return null;
    }
    return players.get(currentPlayerIndex);
  }

  @Override
  public List<Player> getPlayers() {
    return Collections.unmodifiableList(players);
  }

  @Override
  public Board getBoard() {
    return board;
  }

  @Override
  public int getTurnCount() {
    return turnCount;
  }

  @Override
  public int getMaxTurns() {
    return maxTurns;
  }

  @Override
  public boolean isGameOver() {
    return status == GameStatus.FINISHED;
  }

  @Override
  public GameStatus getStatus() {
    return status;
  }

  @Override
  public DoctorLucky getDoctorLucky() {
    return doctorLucky;
  }

  @Override
  public Pet getPet() {
    return pet;
  }

  @Override
  public Player getWinner() {
    return winner;
  }

  @Override
  public GameState getGameState() {
    // Current player name
    String currentPlayerName = "";
    if (getCurrentPlayer() != null) {
      currentPlayerName = getCurrentPlayer().getName();
    }
    
    // Doctor Lucky and Pet locations
    String doctorLocation = doctorLucky.getCurrentRoom().getName();
    String petLocation = pet != null ? pet.getCurrentRoom().getName() : "Unknown";
    
    // Winner name
    String winnerName = null;
    if (winner != null) {
      winnerName = winner.getName();
    }
    
    // Create immutable player information
    List<GameState.PlayerInfo> playerInfos = new ArrayList<>();
    for (Player player : players) {
      List<String> itemNames = new ArrayList<>();
      for (Item item : player.getInventory()) {
        itemNames.add(item.getName());
      }
      
      boolean isComputer = player instanceof ComputerPlayer;
      GameState.PlayerInfo info = new GameState.PlayerInfo(
          player.getName(),
          player.getCurrentRoom().getName(),
          itemNames,
          isComputer
      );
      playerInfos.add(info);
    }
    
    // Create and return GameState
    return new GameState(
        currentPlayerName,
        turnCount,
        maxTurns,
        doctorLocation,
        petLocation,
        lastActionResult,
        isGameOver(),
        winnerName,
        playerInfos,
        doctorLucky.getHealth(),
        doctorLucky.getMaxHealth()
    );
  }

  // ========== Helper Methods ==========

  /**
   * Ensures the game is in SETUP phase.
   *
   * @throws IllegalStateException if not in SETUP phase
   */
  private void ensureSetupPhase() {
    if (status != GameStatus.SETUP) {
      throw new IllegalStateException("Can only be called during setup phase");
    }
  }

  /**
   * Deals initial cards to all players.
   */
  private void dealInitialCards() {
    for (Player player : players) {
      for (int i = 0; i < STARTING_HAND_SIZE; i++) {
        if (!deck.isEmpty()) {
          Playable card = deck.drawCard();
          player.addCard(card);
        }
      }
    }
  }
}