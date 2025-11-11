package killdoctorlucky.model;

import java.util.ArrayList;
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
 */
public class Game {

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

  /* ==== Setup & lifecycle ==== */

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
  }

  /**
   * Executes one player's turn. The controller should invoke player actions via Commands.
   * After the player's actions are processed, Doctor Lucky and Pet automatically move.
   */
  public void playTurn() {
    if (status != GameStatus.IN_PROGRESS) {
      throw new IllegalStateException("Game has not started");
    }
    // Controller is expected to have executed the chosen Command(s) for the current player.

    // Doctor Lucky moves after each player's turn per M2 requirement.
    doctorLucky.moveNext();
    
    // Pet wanders to next location (Milestone 3)
    if (pet != null) {
      pet.wanderNext();
    }

    // Advance to next player / turn.
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    turnCount++;
    
    // Check if max turns reached
    if (maxTurns > 0 && turnCount >= maxTurns) {
      status = GameStatus.FINISHED;
    }
  }

  /** 
   * Gets the current player.
   * @return the current player (whose turn it is). 
   */
  public Player getCurrentPlayer() {
    if (players.isEmpty()) {
      throw new IllegalStateException("No players have been added");
    }
    return players.get(currentPlayerIndex);
  }

  /** 
   * Gets game status.
   * @return current game status. 
   */
  public GameStatus getStatus() {
    return status;
  }

  /** 
   * Gets number of turns elapsed.
   * @return number of turns elapsed. 
   */
  public int getTurnCount() {
    return turnCount;
  }

  /** 
   * Gets all the players in the join order.
   * @return all players in join order.
   */
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  /**
   * Gets the game board.
   * @return the board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Gets Doctor Lucky.
   * @return Doctor Lucky
   */
  public DoctorLucky getDoctorLucky() {
    return doctorLucky;
  }

  /* ==== Milestone 2: Adding players during setup ==== */

  /**
   * Adds a human-controlled player to the game (turn order = join order).
   *
   * @param name  player name (non-null/non-blank)
   * @param start starting room (non-null)
   */
  public void addHumanPlayer(String name, Room start) {
    ensureSetupPhase();
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null/blank");
    }
    if (start == null) {
      throw new IllegalArgumentException("Starting room cannot be null");
    }
    if (players.size() >= MAX_PLAYERS) {
      throw new IllegalStateException("Cannot exceed max players: " + MAX_PLAYERS);
    }
    players.add(new Player(name.trim(), start));
  }

  /**
   * Adds a computer-controlled player to the game.
   *
   * @param name  player name (non-null/non-blank)
   * @param start starting room (non-null)
   * @param rng   random generator used by the computer player (non-null)
   */
  public void addComputerPlayer(String name, Room start, RandomGenerator rng) {
    ensureSetupPhase();
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null/blank");
    }
    if (start == null || rng == null) {
      throw new IllegalArgumentException("Start room and RNG must be non-null");
    }
    if (players.size() >= MAX_PLAYERS) {
      throw new IllegalStateException("Cannot exceed max players: " + MAX_PLAYERS);
    }
    players.add(new ComputerPlayer(name.trim(), start, rng));
  }

  private void ensureSetupPhase() {
    if (status != GameStatus.SETUP) {
      throw new IllegalStateException("Players can only be added during SETUP");
    }
  }

  /**
   * Tries to move an item by name from the current room into the player's inventory.
   * If the player lacks capacity, the item is restored to the room.
   *
   * @param player   the player attempting to pick up
   * @param itemName the name of the item (case-sensitive)
   * @return true if the item was picked up
   */
  public boolean pickupFromRoom(Player player, String itemName) {
    if (player == null) {
      throw new IllegalArgumentException("player cannot be null");
    }
    if (itemName == null || itemName.trim().isEmpty()) {
      throw new IllegalArgumentException("itemName must not be null/blank");
    }
    Room here = player.getCurrentRoom();
    Item taken = here.removeItem(itemName);
    if (taken == null) {
      return false; // not present
    }
    boolean added = player.pickUpItem(taken);
    if (!added) {
      // restore to room if capacity prevents pickup
      here.addItem(taken);
      return false;
    }
    return true;
  }

  /**
   * Produces a textual description for what the given player can observe:
   * current room, adjacent rooms, items, visible players, Doctor Lucky, and pet.
   *
   * @param player the querying player
   * @return multi-line string description
   */
  public String describeLookAround(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("player cannot be null");
    }
    Room here = player.getCurrentRoom();
    StringBuilder sb = new StringBuilder();

    // Where am I?
    sb.append("You are in: ").append(here.getName()).append('\n');

    // Adjacent rooms (considering pet blocking)
    List<Room> neighbors = board.getAdjacentRooms(here);
    List<String> visibleNeighbors = new ArrayList<>();
    for (Room neighbor : neighbors) {
      // Check if pet blocks this room
      if (pet != null && pet.getCurrentRoom().equals(neighbor)) {
        // Pet makes this room invisible
        continue;
      }
      visibleNeighbors.add(neighbor.getName());
    }

    sb.append("Adjacent rooms (").append(visibleNeighbors.size()).append("): ");
    for (int i = 0; i < visibleNeighbors.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(visibleNeighbors.get(i));
    }
    sb.append('\n');

    // Items in current room
    List<Item> items = here.getItems();
    sb.append("Items here (").append(items.size()).append("): ");
    for (int i = 0; i < items.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(items.get(i).getName());
    }
    sb.append('\n');

    // Visible players (and Doctor Lucky)
    List<String> visible = new ArrayList<>();
    for (Player p : players) {
      if (p == player) {
        continue;
      }
      if (p.canBeSeenBy(player, board)) {
        visible.add(p.getName());
      }
    }
    if (doctorLucky != null && doctorLucky.canBeSeenBy(player, board)) {
      visible.add("Doctor Lucky");
    }

    sb.append("Visible players (").append(visible.size()).append("): ");
    for (int i = 0; i < visible.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(visible.get(i));
    }
    sb.append('\n');

    // Show pet location if visible
    if (pet != null) {
      if (pet.getCurrentRoom().equals(here)) {
        sb.append("The pet ").append(pet.getName()).append(" is here with you.\n");
      }
    }

    return sb.toString();
  }

  /**
   * Checks if the game is over.
   * @return true if the game is over. 
   */
  public boolean isGameOver() {
    return status == GameStatus.FINISHED;
  }

  // ===== MILESTONE 3: PET MANAGEMENT =====

  /**
   * Sets the pet for this game and initializes its wandering path.
   * This should be called during game setup after all rooms are loaded.
   * 
   * @param gamePet the pet to add to the game
   * @throws IllegalArgumentException if pet is null
   */
  public void setPet(Pet gamePet) {
    if (gamePet == null) {
      throw new IllegalArgumentException("Pet cannot be null");
    }
    this.pet = gamePet;
    // Initialize the pet's DFS wandering path
    this.pet.initializeDfsPath(board);
  }

  /**
   * Gets the pet in this game.
   * 
   * @return the pet, or null if no pet has been set
   */
  public Pet getPet() {
    return pet;
  }

  /**
   * Moves the pet to the specified target room.
   * This represents a player's turn action.
   * 
   * @param targetRoom the room to move the pet to
   * @throws IllegalArgumentException if targetRoom is null
   * @throws IllegalStateException if no pet exists in the game
   */
  public void movePet(Room targetRoom) {
    if (targetRoom == null) {
      throw new IllegalArgumentException("Target room cannot be null");
    }
    if (pet == null) {
      throw new IllegalStateException("No pet in the game");
    }

    pet.moveToRoom(targetRoom);
  }

  /**
   * Sets the maximum number of turns allowed in the game.
   * 
   * @param max the maximum turns (must be positive)
   * @throws IllegalArgumentException if max <= 0
   */
  public void setMaxTurns(int max) {
    if (max <= 0) {
      throw new IllegalArgumentException("Max turns must be positive");
    }
    this.maxTurns = max;
  }

  /**
   * Gets the maximum number of turns allowed.
   * 
   * @return the maximum turns
   */
  public int getMaxTurns() {
    return maxTurns;
  }

  // ===== MILESTONE 3: MURDER ATTEMPT LOGIC =====

  /**
   * Attempts to murder Doctor Lucky using the specified item.
   * The murder can succeed or fail based on witnesses and game rules.
   * 
   * @param player the player attempting the murder
   * @param itemName the name of the item to use (can be empty for "poke in eye")
   * @return the result of the murder attempt
   * @throws IllegalArgumentException if player is null
   * @throws IllegalStateException if game is not in progress
   */
  public MurderResult attemptMurder(Player player, String itemName) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (status != GameStatus.IN_PROGRESS) {
      throw new IllegalStateException("Game is not in progress");
    }

    // Check if player is in same room as Doctor Lucky
    if (!player.getCurrentRoom().equals(doctorLucky.getCurrentRoom())) {
      throw new IllegalArgumentException("Player must be in same room as Doctor Lucky");
    }

    // Check if other players can see this attempt
    if (isPlayerVisible(player)) {
      // Witnessed by another player - attack fails automatically
      return MurderResult.FAILED_WITNESS_PRESENT;
    }

    // Determine damage
    int damage = 0;
    Item weaponUsed = null;

    if (itemName == null || itemName.trim().isEmpty()) {
      // Poke in the eye
      damage = player.getPokeInEyeDamage();
    } else {
      // Try to find the item in player's inventory
      weaponUsed = findItemInInventory(player, itemName.trim());
      if (weaponUsed == null) {
        throw new IllegalArgumentException("Player does not have item: " + itemName);
      }
      damage = weaponUsed.getDamage();
    }

    // Apply damage to Doctor Lucky
    doctorLucky.takeDamage(damage);

    // Remove the weapon from game (becomes evidence)
    if (weaponUsed != null) {
      player.dropItem(weaponUsed.getName());
      // Item is removed from game (evidence)
    }

    // Check if Doctor Lucky is dead
    if (!doctorLucky.isAlive()) {
      status = GameStatus.FINISHED;
      winner = player;
      return MurderResult.SUCCESS;
    }

    // Doctor Lucky survived
    return MurderResult.FAILED_INSUFFICIENT_WEAPON;
  }

  /**
   * Checks if a player can be seen by any other player during a murder attempt.
   * Takes into account pet location blocking visibility.
   * 
   * @param player the player to check visibility for
   * @return true if any other player can see this player
   * @throws IllegalArgumentException if player is null
   */
  public boolean isPlayerVisible(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    Room playerRoom = player.getCurrentRoom();

    // Check if pet is in the same room (blocks visibility from neighbors)
    boolean petInRoom = (pet != null && pet.getCurrentRoom().equals(playerRoom));

    // Check each other player
    for (Player otherPlayer : players) {
      if (otherPlayer.equals(player)) {
        continue; // Skip self
      }

      Room otherRoom = otherPlayer.getCurrentRoom();

      // Same room - always visible
      if (otherRoom.equals(playerRoom)) {
        return true;
      }

      // Different room - check if they're neighbors
      if (board.getAdjacentRooms(otherRoom).contains(playerRoom)) {
        // They're neighbors, but check if pet blocks the view
        if (!petInRoom) {
          // No pet blocking - other player can see
          return true;
        }
      }
    }

    // No one can see the player
    return false;
  }

  /**
   * Gets the winner of the game if there is one.
   * 
   * @return the player who won, or null if no winner yet or game not finished
   */
  public Player getWinner() {
    if (status != GameStatus.FINISHED) {
      return null;
    }
    return winner;
  }

  /**
   * Ends the game, setting status to FINISHED.
   */
  public void endGame() {
    this.status = GameStatus.FINISHED;
  }

  /**
   * Helper method to find an item in player's inventory by name.
   * 
   * @param player the player
   * @param itemName the item name to find
   * @return the item, or null if not found
   */
  private Item findItemInInventory(Player player, String itemName) {
    for (Item item : player.getInventory()) {
      if (item.getName().equalsIgnoreCase(itemName)) {
        return item;
      }
    }
    return null;
  }

  /** Deals the opening hand to each player. */
  private void dealInitialCards() {
    for (int i = 0; i < STARTING_HAND_SIZE; i++) {
      for (Player p : players) {
        if (!deck.isEmpty() && p.canDrawCard()) {
          Playable c = deck.drawCard();
          if (c != null) {
            p.addCard(c);
          }
        }
      }
    }
  }
}