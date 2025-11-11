package killdoctorlucky;

import java.io.IOException;
import java.util.List;

/**
 * Command to display detailed information about a specific space in the world.
 */
public class DisplaySpaceCommand implements Command {
  
  private final String spaceName;
  
  /**
   * Creates a command to display space information.
   * 
   * @param roomName the name of the space to display information about
   * @throws IllegalArgumentException if roomName is null or empty
   */
  public DisplaySpaceCommand(String roomName) {
    if (roomName == null || roomName.trim().isEmpty()) {
      throw new IllegalArgumentException("Space name cannot be null or empty");
    }
    this.spaceName = roomName.trim();
  }
  
  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output must be non-null");
    }
    
    try {
      Room room = game.getBoard().getRoom(spaceName);
      
      if (room == null) {
        out.append("Space '").append(spaceName).append("' does not exist.\n");
        out.append("Use 'spaces' command to see list of available spaces.\n");
        return;
      }
      
      // Build detailed room information
      out.append("\n=== Space Information ===\n");
      out.append("Name: ").append(room.getName()).append("\n");
      
      // Adjacent rooms
      List<Room> neighbors = room.getConnections();
      out.append("Adjacent spaces (").append(String.valueOf(neighbors.size())).append("): ");
      if (neighbors.isEmpty()) {
        out.append("None");
      } else {
        for (int i = 0; i < neighbors.size(); i++) {
          if (i > 0) {
            out.append(", ");
          }
          out.append(neighbors.get(i).getName());
        }
      }
      out.append("\n");
      
      // Items in the room
      List<Item> items = room.getItems();
      out.append("Items (").append(String.valueOf(items.size())).append("): ");
      if (items.isEmpty()) {
        out.append("None");
      } else {
        out.append("\n");
        for (Item item : items) {
          out.append("  - ").append(item.getName())
             .append(" (damage: ").append(String.valueOf(item.getDamage()))
             .append(")\n");
        }
      }
      if (items.isEmpty()) {
        out.append("\n");
      }
      
      // Occupants in the room
      List<Occupant> occupants = room.getOccupants();
      out.append("Occupants (").append(String.valueOf(occupants.size())).append("): ");
      if (occupants.isEmpty()) {
        out.append("None");
      } else {
        out.append("\n");
        for (Occupant occupant : occupants) {
          if (occupant instanceof DoctorLucky) {
            out.append("  - Doctor Lucky (Target)\n");
          } else if (occupant instanceof Player) {
            Player player = (Player) occupant;
            String type = (player instanceof ComputerPlayer) ? " (Computer)" : " (Human)";
            out.append("  - ").append(player.getName()).append(type).append("\n");
          }
        }
      }
      if (occupants.isEmpty()) {
        out.append("\n");
      }
      
      // Spaces visible from here (simplified - showing connected rooms)
      out.append("Visible spaces from here: ");
      if (neighbors.isEmpty()) {
        out.append("None");
      } else {
        for (int i = 0; i < neighbors.size(); i++) {
          if (i > 0) {
            out.append(", ");
          }
          out.append(neighbors.get(i).getName());
        }
      }
      out.append("\n");
      
    } catch (IOException e) {
      throw new IllegalStateException("Error writing space information", e);
    }
  }
  
  @Override
  public String getDescription() {
    return "Displays detailed information about a specific space";
  }
}