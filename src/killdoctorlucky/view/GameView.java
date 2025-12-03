package killdoctorlucky.view;

import killdoctorlucky.controller.Features;
import killdoctorlucky.model.GameState;

/**
 * Interface for the graphical view of the Kill Doctor Lucky game.
 * This interface defines all operations that the View must support,
 * allowing the Controller to update the display and the View to
 * callback to the Controller when user actions occur.
 * 
 * <p>This design follows the MVC pattern by keeping the View decoupled
 * from both the Model and the Controller implementation.</p>
 */
public interface GameView {
  
  /**
   * Sets the features (controller callbacks) for this view.
   * The view will call methods on this Features object when
   * user actions occur (button clicks, menu selections, etc.).
   * 
   * @param features the controller that implements Features interface
   * @throws IllegalArgumentException if features is null
   */
  void setFeatures(Features features);
  
  /**
   * Sets the game board for rendering the world map.
   * This allows the view to access room coordinates for drawing.
   * 
   * @param board the game board with room coordinates
   */
  void setBoard(killdoctorlucky.model.Board board);
  
  /**
   * Refreshes the view to display the current game state.
   * This method is called by the controller after any game state change.
   * 
   * @param state the current immutable game state to display
   * @throws IllegalArgumentException if state is null
   */
  void refresh(GameState state);
  
  /**
   * Makes the view visible to the user.
   * This should be called after the view is fully constructed.
   */
  void makeVisible();
  
  /**
   * Displays the welcome/about screen with game information.
   * This screen should credit the creator and explain basic game rules.
   */
  void showWelcomeScreen();
  
  /**
   * Displays a message to the user (success, error, or info).
   * 
   * @param message the message to display
   * @param title the title for the message dialog
   * @param messageType the type of message (INFO, ERROR, WARNING, SUCCESS)
   */
  void showMessage(String message, String title, MessageType messageType);
  
  /**
   * Prompts the user to enter their name.
   * 
   * @param prompt the prompt message
   * @return the player name entered, or null if cancelled
   */
  String getPlayerName(String prompt);
  
  /**
   * Prompts the user to select a starting room.
   * 
   * @param availableRooms list of room names to choose from
   * @return the selected room name, or null if cancelled
   */
  String selectStartingRoom(String[] availableRooms);
  
  /**
   * Prompts the user to select an item from the current room.
   * 
   * @param availableItems list of item names to choose from
   * @return the selected item name, or null if cancelled
   */
  String selectItem(String[] availableItems);
  
  /**
   * Prompts the user to select a room for moving the pet.
   * 
   * @param availableRooms list of all room names
   * @return the selected room name, or null if cancelled
   */
  String selectRoomForPet(String[] availableRooms);
  
  /**
   * Prompts the user to select a weapon for murder attempt.
   * Includes "Poke in eye" as an option.
   * 
   * @param availableWeapons list of weapon names
   * @return the selected weapon name, "poke" for poke in eye, or null if cancelled
   */
  String selectWeapon(String[] availableWeapons);
  
  /**
   * Resets the view to initial state for a new game.
   */
  void resetView();
  
  /**
   * Enum for message types to display.
   */
  enum MessageType {
    /** Informational message. */
    INFO,
    /** Error message. */
    ERROR,
    /** Warning message. */
    WARNING,
    /** Success message. */
    SUCCESS
  }
}