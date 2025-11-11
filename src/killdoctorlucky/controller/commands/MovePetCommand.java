package killdoctorlucky.controller.commands;

import java.io.IOException;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.Room;

/**
 * Command to move the pet to a specified room.
 * This represents a player's turn action.
 */
public class MovePetCommand implements Command {

  private final String targetRoomName;

  /**
   * Creates a command to move the pet.
   * 
   * @param roomName the name of the room to move the pet to
   * @throws IllegalArgumentException if roomName is null or empty
   */
  public MovePetCommand(String roomName) {
    if (roomName == null || roomName.trim().isEmpty()) {
      throw new IllegalArgumentException("Room name cannot be null or empty");
    }
    this.targetRoomName = roomName.trim();
  }

  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output must be non-null");
    }

    try {
      // Find the target room
      Room targetRoom = game.getBoard().getRoom(targetRoomName);
      
      if (targetRoom == null) {
        out.append("Room '").append(targetRoomName).append("' does not exist.\n");
        return;
      }

      // Check if pet exists
      if (game.getPet() == null) {
        out.append("No pet in the game.\n");
        return;
      }

      // Move the pet
      game.movePet(targetRoom);
      
      out.append("Moved the pet '").append(game.getPet().getName())
         .append("' to ").append(targetRoomName).append(".\n");

    } catch (IOException e) {
      throw new IllegalStateException("Error writing pet movement result", e);
    }
  }

  @Override
  public String getDescription() {
    return "Moves the pet to a specified room";
  }
}