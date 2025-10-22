package killdoctorlucky;

import java.io.IOException;

/**
 * Command to add a human player to the game during setup.
 */
public class AddHumanPlayerCommand implements Command {

  private final String player;
  private final String startRoom;

  /**
   * command to add the human player.
   * @param playerName name of the player to add (non-null, non-blank)
   * @param startRoomName starting room for this player (non-null, must exist)
   * @throws IllegalArgumentException if playerName or startRoomName is null or blank
   */
  public AddHumanPlayerCommand(String playerName, String startRoomName) {
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or blank");
    }
    if (startRoomName == null || startRoomName.trim().isEmpty()) {
      throw new IllegalArgumentException("Start room cannot be null or blank");
    }
    this.player = playerName.trim();
    this.startRoom = startRoomName.trim();
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

      game.addHumanPlayer(player, room);
      out.append("Added human player: ").append(player)
         .append(" starting in ").append(startRoom).append(".\n");

    } catch (IOException e) {
      throw new IllegalStateException("Error writing player addition", e);
    }
  }

  @Override
  public String getDescription() {
    return "Adds a human player to the game.";
  }
}