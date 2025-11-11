package killdoctorlucky.model;

import java.util.ArrayList;
import java.util.List;
import killdoctorlucky.model.cards.Playable;
import killdoctorlucky.model.cards.WeaponCard;
import killdoctorlucky.model.occupants.ComputerPlayer;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.model.occupants.Player;
import killdoctorlucky.util.RandomGenerator;


/**
 * Represents the main game logic and state management for Kill Doctor Lucky.
 * The Game class coordinates all game components including players, the board,
 * Doctor Lucky, and the card deck to provide complete gameplay functionality.
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

  private int currentPlayerIndex = 0;
  private int turnCount = 0;
  private GameStatus status = GameStatus.SETUP;


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
    if (players.size() < MIN_PLAYERS || players.size() > MAX_PLAYERS) {
      throw new IllegalStateException(
          "Player count must be between " + MIN_PLAYERS + " and " + MAX_PLAYERS);
    }
    dealInitialCards();
    status = GameStatus.IN_PROGRESS;
  }

  /**
   * Executes one player's turn. The controller should invoke player actions via Commands.
   * After the player's actions are processed, Doctor Lucky automatically moves.
   */
  public void playTurn() {
    if (status != GameStatus.IN_PROGRESS) {
      throw new IllegalStateException("Game has not started");
    }
    // Controller is expected to have executed the chosen Command(s) for the current player.

    // Doctor Lucky moves after each player's turn per M2 requirement.
    doctorLucky.moveNext();

    // Advance to next player / turn.
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    turnCount++;
  }

  /** 
   * current player.
   * @return the current player (whose turn it is). 
   */
  public Player getCurrentPlayer() {
    if (players.isEmpty()) {
      throw new IllegalStateException("No players have been added");
    }
    return players.get(currentPlayerIndex);
  }

  /** 
   * get game status.
   * @return current game status. 
   */
  public GameStatus getStatus() {
    return status;
  }

  /** 
   * get numer of turns elapsed.
   * @return number of turns elapsed. 
   */
  public int getTurnCount() {
    return turnCount;
  }

  /** 
   * get all the players in the join order.
   * @return all players in join order.
   */
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  public Board getBoard() {
    return board;
  }

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
   *   <li>current room</li>
   *   <li>adjacent rooms</li>
   *   <li>items in the current room</li>
   *   <li>visible players (including Doctor Lucky if visible).
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

    // Adjacent rooms
    List<Room> neighbors = board.getAdjacentRooms(here);
    sb.append("Adjacent rooms (").append(neighbors.size()).append("): ");
    for (int i = 0; i < neighbors.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(neighbors.get(i).getName());
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

    return sb.toString();
  }

  /**
   * check if the game is over.
   *@return true if the game is over. */
  public boolean isGameOver() {
    return status == GameStatus.FINISHED;
  }

  /**
   * Checks the outcome of a murder attempt by the given player using the specified weapon.
   * Determines success or failure based on witnesses, failure cards, and game rules.
   *
   * @param player the player attempting the murder
   * @param weapon the weapon card used for the murder attempt
   * @return the result of the murder attempt, as a {@link MurderResult}
   */
  public MurderResult attemptMurder(Player player, WeaponCard weapon) {
    return MurderResult.FAILED_WITNESS_PRESENT;
  }

  /**
   * Determines the winner of the game if one exists.
   *
   * @return the player who has won the game, or {@code null} if the game is still ongoing
   */
  public Player getWinner() {
    return null; 
  }


  /** Deals the opening hand to each player. */
  private void dealInitialCards() {
    for (int i = 0; i < STARTING_HAND_SIZE; i++) {
      for (Player p : players) {
        if (!deck.isEmpty() && p.canDrawCard()) {
          Playable c = deck.drawCard();
          p.addCard(c);
        }
      }
    }
  }
}