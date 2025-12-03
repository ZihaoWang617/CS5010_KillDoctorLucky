package killdoctorlucky.controller;

import javax.swing.SwingUtilities;
import killdoctorlucky.view.GameView;
import killdoctorlucky.view.SwingGameView;

/**
 * Main entry point for the Kill Doctor Lucky game application.
 * This class launches the graphical user interface version of the game.
 * 
 * <p>Usage: java killdoctorlucky.controller.Main</p>
 */
public class Main {
  
  /**
   * Main method to start the application.
   * 
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    // Run the GUI on the Event Dispatch Thread
    SwingUtilities.invokeLater(() -> {
      // Create the view
      GameView view = new SwingGameView();
      
      // Create the controller
      GraphicalController controller = new GraphicalController(view);
      
      // Start the application
      controller.start();
    });
  }
}