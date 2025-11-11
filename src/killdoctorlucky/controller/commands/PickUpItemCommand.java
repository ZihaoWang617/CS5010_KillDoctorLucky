package killdoctorlucky.controller.commands;

import java.io.IOException;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.occupants.Player;

/** Command that lets the current player pick up an item from the current room. */
public class PickUpItemCommand implements Command {

  private final String item; 

  /**
   * command to pick up the item.
   * @param itemName the name of the item to pick up (non-null/non-blank)
   */
  public PickUpItemCommand(String itemName) {
    this.item = itemName;
  }

  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output must be non-null");
    }
    try {
      Player p = game.getCurrentPlayer();
      boolean ok = game.pickupFromRoom(p, item);
      if (ok) {
        out.append(p.getName()).append(" picked up ").append(item).append(".\n");
      } else {
        out.append("Pick up failed: item not present or capacity full.\n");
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error writing pickup result", e);
    }
  }

  @Override
  public String getDescription() {
    return "Picks up an item from the current room.";
  }
}