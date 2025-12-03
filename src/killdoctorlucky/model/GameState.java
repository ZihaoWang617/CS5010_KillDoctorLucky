package killdoctorlucky.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable snapshot of the game state at a specific point in time.
 * This class is designed to be passed to the View layer to display
 * game information without exposing mutable game objects.
 * 
 * <p>All collections returned by this class are unmodifiable.</p>
 */
public final class GameState {
  
  private final String currentPlayerName;
  private final int turnNumber;
  private final int maxTurns;
  private final String doctorLocation;
  private final String petLocation;
  private final String lastActionResult;
  private final boolean gameOver;
  private final String winner;
  private final List<PlayerInfo> playerInfos;
  private final int doctorHealth;
  private final int doctorMaxHealth;
  
  /**
   * Constructs an immutable game state snapshot.
   * 
   * @param playerName name of the current player
   * @param turn current turn number
   * @param maxTurnsAllowed maximum turns allowed
   * @param doctorRoom room where Doctor Lucky is located
   * @param petRoom room where the pet is located
   * @param actionResult description of the last action's result
   * @param isGameOver whether the game has ended
   * @param winnerName name of the winner (null if no winner)
   * @param infos list of player information
   * @param currentHealth current health of Doctor Lucky
   * @param maximumHealth maximum health of Doctor Lucky
   */
  public GameState(String playerName, int turn, int maxTurnsAllowed,
                   String doctorRoom, String petRoom,
                   String actionResult, boolean isGameOver, String winnerName,
                   List<PlayerInfo> infos, int currentHealth, int maximumHealth) {
    // 参数名和字段名不同,避免 "hides a field" 警告
    this.currentPlayerName = playerName;
    this.turnNumber = turn;
    this.maxTurns = maxTurnsAllowed;
    this.doctorLocation = doctorRoom;
    this.petLocation = petRoom;
    this.lastActionResult = actionResult;
    this.gameOver = isGameOver;
    this.winner = winnerName;
    this.playerInfos = Collections.unmodifiableList(new ArrayList<>(infos));
    this.doctorHealth = currentHealth;
    this.doctorMaxHealth = maximumHealth;
  }
  
  /**
   * Gets the name of the current player.
   * 
   * @return current player's name
   */
  public String getCurrentPlayerName() {
    return currentPlayerName;
  }
  
  /**
   * Gets the current turn number.
   * 
   * @return turn number
   */
  public int getTurnNumber() {
    return turnNumber;
  }
  
  /**
   * Gets the maximum turns allowed.
   * 
   * @return max turns
   */
  public int getMaxTurns() {
    return maxTurns;
  }
  
  /**
   * Gets the room where Doctor Lucky is currently located.
   * 
   * @return doctor's location
   */
  public String getDoctorLocation() {
    return doctorLocation;
  }
  
  /**
   * Gets the room where the pet is currently located.
   * 
   * @return pet's location
   */
  public String getPetLocation() {
    return petLocation;
  }
  
  /**
   * Gets the result message of the last action performed.
   * 
   * @return last action result
   */
  public String getLastActionResult() {
    return lastActionResult;
  }
  
  /**
   * Checks if the game is over.
   * 
   * @return true if game has ended
   */
  public boolean isGameOver() {
    return gameOver;
  }
  
  /**
   * Gets the winner's name, if any.
   * 
   * @return winner's name, or null if no winner
   */
  public String getWinner() {
    return winner;
  }
  
  /**
   * Gets an unmodifiable list of all player information.
   * 
   * @return list of player info
   */
  public List<PlayerInfo> getPlayerInfos() {
    return playerInfos;
  }
  
  /**
   * Gets Doctor Lucky's current health.
   * 
   * @return current health
   */
  public int getDoctorHealth() {
    return doctorHealth;
  }
  
  /**
   * Gets Doctor Lucky's maximum health.
   * 
   * @return max health
   */
  public int getDoctorMaxHealth() {
    return doctorMaxHealth;
  }
  
  /**
   * Immutable player information for display purposes.
   */
  public static final class PlayerInfo {
    private final String name;
    private final String currentRoom;
    private final List<String> items;
    private final boolean isComputer;
    
    /**
     * Constructs player information.
     * 
     * @param playerName player's name
     * @param roomName room where player is located
     * @param itemList list of item names the player carries
     * @param isComputerPlayer whether this is a computer player
     */
    public PlayerInfo(String playerName, String roomName, List<String> itemList, 
                      boolean isComputerPlayer) {
      // 参数名和字段名不同,避免 "hides a field" 警告
      this.name = playerName;
      this.currentRoom = roomName;
      this.items = Collections.unmodifiableList(new ArrayList<>(itemList));
      this.isComputer = isComputerPlayer;
    }
    
    public String getName() {
      return name;
    }
    
    public String getCurrentRoom() {
      return currentRoom;
    }
    
    public List<String> getItems() {
      return items;
    }
    
    public boolean isComputer() {
      return isComputer;
    }
  }
}