package killdoctorlucky;

import java.io.IOException;
import java.util.List;

/**
 * Command that moves the current player to an adjacent room by name.
 */
public class MoveCommand implements Command {

  private final String targetRoom;

  /**
   * get the name of target room.
   * @param roomName the name of the room to move to (non-null/non-blank)
   */
  public MoveCommand(String roomName) {
    this.targetRoom = roomName;
  }

  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output must be non-null");
    }

    try {
      Player current = game.getCurrentPlayer();
      Room currentRoom = current.getCurrentRoom();
      Room target = game.getBoard().getRoom(targetRoom);

      if (target == null) {
        out.append("Room '").append(targetRoom).append("' does not exist.\n");
        return;
      }

      // Check adjacency
      List<Room> adjacent = game.getBoard().getAdjacentRooms(currentRoom);
      if (!adjacent.contains(target)) {
        out.append("Cannot move to ").append(targetRoom)
           .append(" (not adjacent to current room).\n");
        return;
      }

      boolean moved = current.moveToRoom(target);
      if (moved) {
        out.append(current.getName())
           .append(" moved to ")
           .append(targetRoom)
            .append(".\n");
      } else {
        out.append("Failed to move to ").append(targetRoom).append(".\n");
      }

    } catch (IOException e) {
      throw new IllegalStateException("Error writing move result", e);
    }
  }

  @Override
  public String getDescription() {
    return "Moves the current player to a neighboring room.";
  }
}