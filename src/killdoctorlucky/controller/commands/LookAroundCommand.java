package killdoctorlucky.controller.commands;

import java.io.IOException;
import killdoctorlucky.model.Game;

/**
 * Command to let the current player look around their surroundings.
 */
public class LookAroundCommand implements Command {

  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output must be non-null");
    }

    try {
      String description = game.describeLookAround(game.getCurrentPlayer());
      out.append(description).append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Error writing output", e);
    }
  }
  
  
  @Override
  public String getDescription() {
    return "Displays what the current player can see in their room.";
  }

}
