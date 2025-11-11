package killdoctorlucky.controller.commands;

import java.io.IOException;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.MurderResult;
import killdoctorlucky.model.occupants.Player;

/**
 * Command to attempt to murder Doctor Lucky.
 * This represents a player's turn action.
 */
public class AttemptMurderCommand implements Command {

  private final String itemName;

  /**
   * Creates a command to attempt murder.
   * 
   * @param weaponName the name of the item to use (can be null or empty for "poke in eye")
   */
  public AttemptMurderCommand(String weaponName) {
    this.itemName = weaponName;
  }

  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output must be non-null");
    }

    try {
      Player currentPlayer = game.getCurrentPlayer();
      
      // Check if player is in same room as Doctor Lucky
      if (!currentPlayer.getCurrentRoom().equals(game.getDoctorLucky().getCurrentRoom())) {
        out.append("You must be in the same room as Doctor Lucky to attempt murder!\n");
        return;
      }

      // Display attempt message
      if (itemName == null || itemName.trim().isEmpty()) {
        out.append(currentPlayer.getName()).append(" attempts to poke Doctor Lucky in the eye!\n");
      } else {
        out.append(currentPlayer.getName()).append(" attempts to murder Doctor Lucky with ")
           .append(itemName).append("!\n");
      }

      // Attempt the murder
      MurderResult result = game.attemptMurder(currentPlayer, itemName);

      // Display result
      switch (result) {
        case SUCCESS:
          out.append("\n");
          out.append("========================================\n");
          out.append("SUCCESS! Doctor Lucky has been killed!\n");
          out.append(currentPlayer.getName()).append(" wins the game!\n");
          out.append("========================================\n");
          break;

        case FAILED_WITNESS_PRESENT:
          out.append("Murder attempt FAILED!\n");
          out.append("Another player witnessed the attack!\n");
          out.append("The attack was stopped before any damage could be done.\n");
          break;

        case FAILED_INSUFFICIENT_WEAPON:
          out.append("Murder attempt FAILED!\n");
          out.append("The attack was successful but not lethal.\n");
          out.append("Doctor Lucky survived with ")
             .append(String.valueOf(game.getDoctorLucky().getHealth()))
              .append(" health remaining.\n");
          break;

        case FAILED_BLOCKED_BY_CARDS:
          out.append("Murder attempt FAILED!\n");
          out.append("Other players used cards to block the attack!\n");
          break;

        default:
          out.append("Unknown result.\n");
          break;
      }

    } catch (IllegalArgumentException e) {
      try {
        out.append("Error: ").append(e.getMessage()).append("\n");
      } catch (IOException ioException) {
        throw new IllegalStateException("Error writing error message", ioException);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error writing murder attempt result", e);
    }
  }

  @Override
  public String getDescription() {
    return "Attempts to murder Doctor Lucky";
  }
}