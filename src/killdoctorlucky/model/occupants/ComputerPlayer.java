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
   * Simple AI: randomly choose one of {move, pickup, look}.
   *
   * @param game the current game instance controlling the world
   */
  public void takeTurn(Game game) {
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

  /** Randomly move to a neighboring room (if any). */
  private void moveRandomly(Game game) {
    Room current = getCurrentRoom();
    List<Room> neighbors = game.getBoard().getAdjacentRooms(current);
    if (!neighbors.isEmpty()) {
      Room next = neighbors.get(rng.nextInt(neighbors.size()));
      moveToRoom(next); 
    }
  }

  /** Try to pick up a random item from the current room (if capacity allows). */
  private void pickUpRandomItem(Game game) {
    List<Item> items = getCurrentRoom().getItems();
    if (!items.isEmpty()) {
      Item item = items.get(rng.nextInt(items.size()));
      game.pickupFromRoom(this, item.getName());
    }
  }

  /** Just trigger the model's look-around text (controller会决定如何展示). */
  private void lookAround(Game game) {
    game.describeLookAround(this);
  }
}