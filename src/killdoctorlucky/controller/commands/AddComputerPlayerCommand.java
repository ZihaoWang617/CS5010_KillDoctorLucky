package killdoctorlucky.controller.commands;

import java.io.IOException;
import java.util.random.RandomGenerator;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.Room;

/**
 * Command to add a computer-controlled player during setup.
 */
public class AddComputerPlayerCommand implements Command {

  private final String player;
  private final String startRoom;
  private final RandomGenerator rng;

  /**
   * command to add compouter player into game.
   * @param playerName the computer player's name (non-null, non-blank)
   * @param startRoomName the starting room (non-null, must exist)
   * @param randomGenerator random generator used by the computer player (non-null)
   */
  public AddComputerPlayerCommand(String playerName, String startRoomName, 
      RandomGenerator randomGenerator) {
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or blank");
    }
    if (startRoomName == null || randomGenerator == null) {
      throw new IllegalArgumentException("Start room and RNG must be non-null");
    }
    this.player = playerName.trim();
    this.startRoom = startRoomName.trim();
    this.rng = randomGenerator;
  }

  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output must be non-null");
    }

    try {
      Room room = game.getBoard().getRoom(startRoom);
      if (room == null) {
        out.append("Room '").append(startRoom).append("' does not exist.\n");
        return;
      }

      game.addComputerPlayer(player, room, rng);

      out.append("Added computer player: ").append(player)
         .append(" starting in ").append(startRoom).append(".\n");

    } catch (IOException e) {
      throw new IllegalStateException("Error writing computer player addition", e);
    }
  }

  @Override
  public String getDescription() {
    return "Adds a computer-controlled player to the game.";
  }
}