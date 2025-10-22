package killdoctorlucky;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main driver class for Kill Doctor Lucky game.
 * Handles command-line arguments to start the interactive game.
 */
public class GameDriver {
  /**
   * The main entry point of the Kill Doctor Lucky game driver.
   * 
   * @param args Command-line arguments:
   *             args[0] - path to world specification file
   *             args[1] - maximum number of turns allowed
   */
  public static void main(String[] args) {
    // Check for correct number of arguments
    if (args.length < 2) {
      System.err.println("Usage: java killdoctorlucky.GameDriver <world-file> <max-turns>");
      System.err.println("Example: java killdoctorlucky.GameDriver mansion.txt 20");
      System.exit(1);
    }

    String worldFile = args[0];
    int maxTurns;
    
    // Parse max turns argument
    try {
      maxTurns = Integer.parseInt(args[1]);
      if (maxTurns <= 0) {
        System.err.println("ERROR: Max turns must be a positive integer");
        System.exit(1);
      }
    } catch (NumberFormatException e) {
      System.err.println("ERROR: Invalid max turns value: " + args[1]);
      System.err.println("Max turns must be a positive integer");
      System.exit(1);
      return;
    }

    try {
      System.out.println("=".repeat(60));
      System.out.println("Kill Doctor Lucky - Interactive Game");
      System.out.println("=".repeat(60));
      System.out.println();

      // Load world from file
      System.out.println("Loading world from file: " + worldFile);
      WorldParser.WorldData worldData = WorldParser.parseWorld(new FileReader(worldFile));
      System.out.println("✓ World loaded successfully!");
      System.out.println("✓ Total rooms: " + worldData.board.getRoomCount());
      System.out.println("✓ Maximum turns: " + maxTurns);
      System.out.println();

      // Create game components
      DoctorLucky doctor = new DoctorLucky(worldData.roomsInOrder.get(0));
      doctor.setMovementSequence(worldData.roomsInOrder);
      Deck deck = new Deck();
      Game game = new Game(worldData.board, deck, doctor);

      // Create controller with System.in and System.out
      Readable input = new InputStreamReader(System.in);
      Appendable output = System.out;
      
      TextController controller = new TextController(input, output, game, maxTurns);
      
      // Start the interactive game
      controller.run();

    } catch (IOException e) {
      System.err.println("ERROR: Could not read world file: " + e.getMessage());
      System.exit(1);
    } catch (IllegalArgumentException e) {
      System.err.println("ERROR: Invalid world specification: " + e.getMessage());
      System.exit(1);
    }
  }
}