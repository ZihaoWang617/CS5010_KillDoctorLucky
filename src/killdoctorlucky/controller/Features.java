package killdoctorlucky.controller;

/**
 * Features interface representing all the operations that the controller
 * can perform. This interface is passed to the View, allowing the View
 * to callback into the Controller when user actions occur.
 * 
 * <p>This design follows the MVC pattern by keeping the View decoupled
 * from the Controller implementation. The View only knows about this
 * interface, not the concrete controller class.</p>
 */
public interface Features {
  
  /**
   * Starts a new game with a new world specification file.
   * This will prompt the user for a world file and max turns.
   */
  void startNewGame();
  
  /**
   * Starts a new game with the current world specification.
   * Resets the game state but keeps the same world.
   */
  void restartGame();
  
  /**
   * Adds a human player to the game.
   * 
   * @param playerName name of the player
   * @param startingRoom name of the room where player starts
   * @throws IllegalArgumentException if playerName or startingRoom is invalid
   */
  void addHumanPlayer(String playerName, String startingRoom);
  
  /**
   * Adds a computer player to the game.
   * 
   * @param playerName name of the computer player
   * @param startingRoom name of the room where player starts
   * @throws IllegalArgumentException if playerName or startingRoom is invalid
   */
  void addComputerPlayer(String playerName, String startingRoom);
  
  /**
   * Moves the current player to the specified room.
   * The room must be adjacent to the player's current room.
   * 
   * @param roomName name of the target room
   * @throws IllegalArgumentException if roomName is invalid
   */
  void movePlayer(String roomName);
  
  /**
   * Current player picks up an item from their current room.
   * 
   * @param itemName name of the item to pick up
   * @throws IllegalArgumentException if itemName is invalid
   */
  void pickUpItem(String itemName);
  
  /**
   * Current player looks around to see adjacent rooms and their contents.
   */
  void lookAround();
  
  /**
   * Current player attempts to murder Doctor Lucky.
   * 
   * @param itemName name of the weapon item to use, or null for "poke in eye"
   */
  void attemptMurder(String itemName);
  
  /**
   * Moves the pet to the specified room.
   * 
   * @param roomName name of the target room
   * @throws IllegalArgumentException if roomName is invalid
   */
  void movePet(String roomName);
  
  /**
   * Current player skips their turn without taking any action.
   */
  void endTurn();
  
  /**
   * Handles a mouse click on a room in the graphical world view.
   * This is typically used for moving the player.
   * 
   * @param roomName name of the clicked room
   */
  void handleRoomClick(String roomName);
  
  /**
   * Exits the application.
   */
  void exitGame();
}