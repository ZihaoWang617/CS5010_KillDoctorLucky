package killdoctorlucky.model;

import java.util.List;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.model.occupants.Pet;
import killdoctorlucky.model.occupants.Player;

/**
 * Read-only interface for the game model. This interface provides methods
 * for querying game state without allowing modifications. It's designed to
 * be used by the View layer to display game information while maintaining
 * strict MVC separation.
 */
public interface ReadOnlyGameModel {
  
  /**
   * Gets the current player whose turn it is.
   * 
   * @return the current player, or null if game hasn't started
   */
  Player getCurrentPlayer();
  
  /**
   * Gets an unmodifiable list of all players in the game.
   * 
   * @return list of all players
   */
  List<Player> getPlayers();
  
  /**
   * Gets the game board containing all rooms and connections.
   * 
   * @return the game board
   */
  Board getBoard();
  
  /**
   * Gets the current turn count.
   * 
   * @return current turn number (starts at 1)
   */
  int getTurnCount();
  
  /**
   * Gets the maximum number of turns allowed.
   * 
   * @return maximum turns before game ends
   */
  int getMaxTurns();
  
  /**
   * Checks if the game is over (either by murder or max turns).
   * 
   * @return true if game has ended, false otherwise
   */
  boolean isGameOver();
  
  /**
   * Gets the current game status.
   * 
   * @return the game status (SETUP, IN_PROGRESS, or FINISHED)
   */
  GameStatus getStatus();
  
  /**
   * Gets Doctor Lucky, the target character.
   * 
   * @return the Doctor Lucky instance
   */
  DoctorLucky getDoctorLucky();
  
  /**
   * Gets the pet that wanders through the mansion.
   * 
   * @return the pet instance
   */
  Pet getPet();
  
  /**
   * Gets the winner of the game, if any.
   * 
   * @return the winning player, or null if no winner yet
   */
  Player getWinner();
  
  /**
   * Creates an immutable snapshot of the current game state.
   * This is used to pass information to the View without exposing
   * mutable game objects.
   * 
   * @return an immutable GameState object
   */
  GameState getGameState();
}