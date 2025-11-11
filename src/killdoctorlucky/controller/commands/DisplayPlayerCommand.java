package killdoctorlucky.controller.commands;

import java.io.IOException;
import java.util.List;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.cards.Playable;
import killdoctorlucky.model.occupants.ComputerPlayer;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.model.occupants.Occupant;
import killdoctorlucky.model.occupants.Player;

/**
 * Command to display detailed information about a specific player.
 */
public class DisplayPlayerCommand implements Command {
  
  private final String playerName;
  
  /**
   * Creates a command to display player information.
   * 
   * @param name the name of the player to display information about
   * @throws IllegalArgumentException if name is null or empty
   */
  public DisplayPlayerCommand(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty");
    }
    this.playerName = name.trim();
  }
  
  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output must be non-null");
    }
    
    try {
      // Find the player
      Player targetPlayer = null;
      for (Player p : game.getPlayers()) {
        if (p.getName().equalsIgnoreCase(playerName)) {
          targetPlayer = p;
          break;
        }
      }
      
      if (targetPlayer == null) {
        out.append("Player '").append(playerName).append("' not found.\n");
        out.append("Use 'players' command to see list of players.\n");
        return;
      }
      
      // Build detailed player information
      out.append("\n=== Player Information ===\n");
      out.append("Name: ").append(targetPlayer.getName()).append("\n");
      
      // Player type
      String type = (targetPlayer instanceof ComputerPlayer) ? "Computer" : "Human";
      out.append("Type: ").append(type).append("\n");
      
      // Current location
      Room currentRoom = targetPlayer.getCurrentRoom();
      out.append("Current location: ").append(currentRoom.getName()).append("\n");
      
      // Inventory
      List<Item> inventory = targetPlayer.getInventory();
      out.append("Carrying (").append(String.valueOf(inventory.size()))
         .append("/").append(String.valueOf(targetPlayer.getMaxCarry())).append("): ");
      if (inventory.isEmpty()) {
        out.append("Nothing\n");
      } else {
        out.append("\n");
        int totalDamage = 0;
        for (Item item : inventory) {
          out.append("  - ").append(item.getName())
             .append(" (damage: ").append(String.valueOf(item.getDamage()))
             .append(")\n");
          totalDamage += item.getDamage();
        }
        out.append("  Total damage potential: ").append(String.valueOf(totalDamage))
           .append("\n");
      }
      
      // Cards in hand
      List<Playable> hand = targetPlayer.getHand();
      out.append("Cards in hand: ").append(String.valueOf(hand.size())).append("\n");
      
      // Other occupants in the same room
      List<Occupant> roomOccupants = currentRoom.getOccupants();
      out.append("Sharing room with: ");
      boolean foundOthers = false;
      for (Occupant occ : roomOccupants) {
        if (occ != targetPlayer) {
          if (foundOthers) {
            out.append(", ");
          }
          if (occ instanceof DoctorLucky) {
            out.append("Doctor Lucky");
          } else if (occ instanceof Player) {
            out.append(((Player) occ).getName());
          }
          foundOthers = true;
        }
      }
      if (!foundOthers) {
        out.append("Nobody (alone)");
      }
      out.append("\n");
      
      // Available moves
      List<Room> adjacentRooms = game.getBoard().getAdjacentRooms(currentRoom);
      out.append("Can move to: ");
      if (adjacentRooms.isEmpty()) {
        out.append("Nowhere");
      } else {
        for (int i = 0; i < adjacentRooms.size(); i++) {
          if (i > 0) {
            out.append(", ");
          }
          out.append(adjacentRooms.get(i).getName());
        }
      }
      out.append("\n");
      
      // Items available to pick up
      List<Item> roomItems = currentRoom.getItems();
      if (!roomItems.isEmpty() && targetPlayer.canCarryMore()) {
        out.append("Can pick up: ");
        for (int i = 0; i < roomItems.size(); i++) {
          if (i > 0) {
            out.append(", ");
          }
          out.append(roomItems.get(i).getName());
        }
        out.append("\n");
      } else if (!targetPlayer.canCarryMore()) {
        out.append("Cannot pick up items: Inventory full\n");
      }
      
    } catch (IOException e) {
      throw new IllegalStateException("Error writing player information", e);
    }
  }
  
  @Override
  public String getDescription() {
    return "Displays detailed information about a specific player";
  }
}