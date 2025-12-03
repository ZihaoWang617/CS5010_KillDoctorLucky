package killdoctorlucky.model.occupants;

import java.util.List;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.Room;
import killdoctorlucky.util.RandomGenerator;

/**
 * A computer-controlled player that automatically chooses an action on its turn.
 * Uses our own killdoctorlucky.RandomGenerator for predictable testing.
 */
public class ComputerPlayer extends Player {

  private final RandomGenerator rng;

  /**
   * Constructs a computer player.
   *
   * @param name  player name (non-null/non-blank)
   * @param start starting room (non-null)
   * @param randomGen   random generator (non-null)
   */
  public ComputerPlayer(String name, Room start, RandomGenerator randomGen) {
    super(name, start);
    if (randomGen == null) {
      throw new IllegalArgumentException("Random generator cannot be null");
    }
    this.rng = randomGen;
  }

  /**
   * AI behavior: Attempts murder if possible (alone with Doctor Lucky and no witnesses),
   * otherwise randomly chooses one of {move, pickup, look}.
   * When attempting murder, always uses the highest damage weapon available.
   *
   * @param game the current game instance controlling the world
   */
  public void takeTurn(Game game) {
    if (game == null) {
      throw new IllegalArgumentException("Game cannot be null");
    }
    
    // Check if we can attempt murder
    if (canAttemptMurder(game)) {
      attemptMurder(game);
    } else {
      // Otherwise, randomly choose an action
      int choice = rng.nextInt(3); // 0=move, 1=pickup, 2=look
      switch (choice) {
        case 0:
          moveRandomly(game);
          break;
        case 1:
          pickUpRandomItem(game);
          break;
        default:
          lookAround(game);
          break;
      }
    }
  }

  /**
   * Checks if the computer player can attempt murder.
   * Can attempt if in same room as Doctor Lucky and not visible to other players.
   *
   * @param game the game instance
   * @return true if can attempt murder
   */
  private boolean canAttemptMurder(Game game) {
    // Must be in same room as Doctor Lucky
    if (!getCurrentRoom().equals(game.getDoctorLucky().getCurrentRoom())) {
      return false;
    }
    
    // Must not be visible to other players
    return !game.isPlayerVisible(this);
  }

  /**
   * Attempts to murder Doctor Lucky using the best weapon available.
   * If no weapons, uses poke in the eye.
   *
   * @param game the game instance
   */
  private void attemptMurder(Game game) {

    Item bestWeapon = getBestWeaponItem();
    
    String weaponName = (bestWeapon != null) ? bestWeapon.getName() : null;
    
    game.attemptMurder(this, weaponName);
  }

  /** Randomly move to a neighboring room. */
  private void moveRandomly(Game game) {
    Room current = getCurrentRoom();
    List<Room> neighbors = game.getBoard().getAdjacentRooms(current);
    if (!neighbors.isEmpty()) {
      Room next = neighbors.get(rng.nextInt(neighbors.size()));
      moveToRoom(next); 
    }
  }

  /** Try to pick up a random item from the current room. */
  private void pickUpRandomItem(Game game) {
    List<Item> items = getCurrentRoom().getItems();
    if (!items.isEmpty()) {
      Item item = items.get(rng.nextInt(items.size()));
      game.pickupFromRoom(this, item.getName());
    }
  }

  /** Just trigger the model's look-around text. */
  private void lookAround(Game game) {
    game.describeLookAround(this);
  }
}