package killdoctorlucky.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import killdoctorlucky.model.Board;
import killdoctorlucky.model.Deck;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.GameStatus;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.MurderResult;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.occupants.ComputerPlayer;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.model.occupants.Pet;
import killdoctorlucky.model.occupants.Player;
import killdoctorlucky.util.RandomGenerator;
import killdoctorlucky.util.WorldParser;
import killdoctorlucky.view.GameView;

/**
 * Controller for the graphical user interface.
 * Mirrors TextController logic but with GUI interactions.
 */
public class GraphicalController implements Features {
  
  private Game model;
  private final GameView view;
  private String currentWorldFile;
  private int maxTurns;
  
  /**
   * Constructs a GraphicalController with the specified view.
   * 
   * @param gameView the game view
   * @throws IllegalArgumentException if gameView is null
   */
  public GraphicalController(GameView gameView) {
    if (gameView == null) {
      throw new IllegalArgumentException("View cannot be null");
    }
    this.view = gameView;
    this.maxTurns = 20;
    gameView.setFeatures(this);
  }
  
  /**
   * Starts the controller and displays the welcome screen.
   */
  public void start() {
    view.showWelcomeScreen();
    view.makeVisible();
  }
  
  @Override
  public void startNewGame() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
    fileChooser.setCurrentDirectory(new File("res"));
    
    int result = fileChooser.showOpenDialog(null);
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }
    
    File selectedFile = fileChooser.getSelectedFile();
    currentWorldFile = selectedFile.getAbsolutePath();
    
    String maxTurnsStr = JOptionPane.showInputDialog(null,
        "Enter maximum number of turns:", "Max Turns", JOptionPane.QUESTION_MESSAGE);
    
    if (maxTurnsStr == null) {
      return;
    }
    
    try {
      maxTurns = Integer.parseInt(maxTurnsStr.trim());
      if (maxTurns <= 0) {
        view.showMessage("Max turns must be positive!", "Invalid Input",
            GameView.MessageType.ERROR);
        return;
      }
    } catch (NumberFormatException e) {
      view.showMessage("Invalid number format!", "Error", GameView.MessageType.ERROR);
      return;
    }
    
    loadWorld(currentWorldFile, maxTurns);
  }
  
  @Override
  public void restartGame() {
    if (currentWorldFile == null) {
      view.showMessage("No world loaded. Please start a new game.",
          "No World", GameView.MessageType.WARNING);
      return;
    }
    loadWorld(currentWorldFile, maxTurns);
  }
  
  /**
   * Loads a world from the specified file and creates the game.
   * 
   * @param filePath path to the world specification file
   * @param turns maximum number of turns for the game
   */
  private void loadWorld(String filePath, int turns) {
    try {
      FileReader reader = new FileReader(filePath);
      WorldParser.WorldData worldData = WorldParser.parseWorld(reader);
      
      Board board = worldData.board;
      List<Room> rooms = worldData.roomsInOrder;
      
      DoctorLucky doctor = new DoctorLucky(rooms.get(0), worldData.targetHealth);
      doctor.setMovementSequence(rooms);
      
      Pet pet = new Pet(worldData.petName, rooms.get(0));
      
      Deck deck = new Deck();
      
      model = new Game(board, deck, doctor);
      model.setPet(pet);
      model.setMaxTurns(turns);
      
      view.resetView();
      view.setBoard(board);
      
      setupPlayers();
      
    } catch (IOException e) {
      view.showMessage("Failed to load world: " + e.getMessage(),
          "Load Error", GameView.MessageType.ERROR);
    } catch (IllegalArgumentException | IllegalStateException e) {
      view.showMessage("Error: " + e.getMessage(),
          "Error", GameView.MessageType.ERROR);
    }
  }
  
  /**
   * Sets up players for the game by prompting user to add players.
   */
  private void setupPlayers() {
    view.showMessage("Add 3-7 players to begin.", "Setup", GameView.MessageType.INFO);
    
    // Keep adding players until we have at least MIN_PLAYERS
    while (true) {
      // Check if we have minimum players and ask if done
      if (model.getPlayers().size() >= Game.MIN_PLAYERS) {
        int done = JOptionPane.showConfirmDialog(null,
            "You have " + model.getPlayers().size() + " players. Start game now?",
            "Start Game?",
            JOptionPane.YES_NO_OPTION);
        if (done == JOptionPane.YES_OPTION) {
          break; // User wants to start
        }
        // User wants to add more
        if (model.getPlayers().size() >= Game.MAX_PLAYERS) {
          view.showMessage("Maximum " + Game.MAX_PLAYERS + " players reached!",
              "Max Players", GameView.MessageType.INFO);
          break;
        }
      }
      
      // Ask what type of player to add
      int playerType = JOptionPane.showOptionDialog(null,
          "Add a player (current: " + model.getPlayers().size() + "):",
          "Add Player",
          JOptionPane.YES_NO_CANCEL_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,
          new String[] { "Human", "Computer", "Done" },
          "Human");
      
      // User clicked Done or closed dialog
      if (playerType == 2 || playerType == JOptionPane.CLOSED_OPTION) {
        if (model.getPlayers().size() < Game.MIN_PLAYERS) {
          view.showMessage("Need at least " + Game.MIN_PLAYERS + " players!",
              "Not Enough Players", GameView.MessageType.WARNING);
          continue;
        }
        break;
      }
      
      // Get player name
      String name = view.getPlayerName("Enter player name:");
      if (name == null || name.trim().isEmpty()) {
        continue;
      }
      
      // Get starting room
      String[] roomNames = getRoomNames();
      String startRoom = view.selectStartingRoom(roomNames);
      if (startRoom == null) {
        continue;
      }
      
      // Add player
      try {
        if (playerType == 0) {
          addHumanPlayer(name, startRoom);
        } else {
          addComputerPlayer(name, startRoom);
        }
        view.showMessage("Added: " + name, "Player Added", GameView.MessageType.SUCCESS);
      } catch (IllegalArgumentException | IllegalStateException e) {
        view.showMessage("Failed: " + e.getMessage(), "Error",
            GameView.MessageType.ERROR);
      }
    }
    
    // Start the game
    try {
      model.startGame();
      refreshView();
      checkAndExecuteComputerTurn();
    } catch (IllegalStateException e) {
      view.showMessage("Failed to start: " + e.getMessage(),
          "Error", GameView.MessageType.ERROR);
    }
  }
  
  @Override
  public void addHumanPlayer(String playerName, String startingRoom) {
    if (model == null) {
      view.showMessage("No game loaded!", "Error", GameView.MessageType.ERROR);
      return;
    }
    
    Room room = model.getBoard().getRoom(startingRoom);
    if (room == null) {
      view.showMessage("Room not found!", "Error", GameView.MessageType.ERROR);
      return;
    }
    model.addHumanPlayer(playerName, room);
    refreshView();
  }
  
  @Override
  public void addComputerPlayer(String playerName, String startingRoom) {
    if (model == null) {
      view.showMessage("No game loaded!", "Error", GameView.MessageType.ERROR);
      return;
    }
    
    Room room = model.getBoard().getRoom(startingRoom);
    if (room == null) {
      view.showMessage("Room not found!", "Error", GameView.MessageType.ERROR);
      return;
    }
    RandomGenerator rng = new RandomGenerator();
    model.addComputerPlayer(playerName, room, rng);
    refreshView();
  }
  
  @Override
  public void movePlayer(String roomName) {
    if (!checkGameInProgress()) {
      return;
    }
    
    Player currentPlayer = model.getCurrentPlayer();
    Room targetRoom = model.getBoard().getRoom(roomName);
    
    if (targetRoom == null) {
      view.showMessage("Room not found!", "Error", GameView.MessageType.ERROR);
      return;
    }
    
    if (!model.getBoard().isValidMove(currentPlayer.getCurrentRoom(), targetRoom)) {
      view.showMessage("Cannot move to " + roomName + " - not adjacent!",
          "Invalid Move", GameView.MessageType.WARNING);
      return;
    }
    
    currentPlayer.moveToRoom(targetRoom);
    endTurnAndContinue();
  }
  
  @Override
  public void pickUpItem(String itemName) {
    if (!checkGameInProgress()) {
      return;
    }
    
    Player currentPlayer = model.getCurrentPlayer();
    
    if (itemName == null) {
      List<Item> items = currentPlayer.getCurrentRoom().getItems();
      if (items.isEmpty()) {
        view.showMessage("No items in this room!", "No Items",
            GameView.MessageType.INFO);
        return;
      }
      
      String[] itemNames = new String[items.size()];
      for (int i = 0; i < items.size(); i++) {
        itemNames[i] = items.get(i).getName();
      }
      itemName = view.selectItem(itemNames);
      if (itemName == null) {
        return;
      }
    }
    
    boolean success = model.pickupFromRoom(currentPlayer, itemName);
    if (success) {
      view.showMessage("Picked up: " + itemName, "Success",
          GameView.MessageType.SUCCESS);
      endTurnAndContinue();
    } else {
      view.showMessage("Failed to pick up!", "Error", GameView.MessageType.ERROR);
    }
  }
  
  @Override
  public void lookAround() {
    if (!checkGameInProgress()) {
      return;
    }
    
    Player currentPlayer = model.getCurrentPlayer();
    String description = model.describeLookAround(currentPlayer);
    view.showMessage(description, "Look Around", GameView.MessageType.INFO);
    
    // Look around ends turn in GUI mode (like text mode)
    endTurnAndContinue();
  }
  
  @Override
  public void attemptMurder(String itemName) {
    if (!checkGameInProgress()) {
      return;
    }
    
    Player currentPlayer = model.getCurrentPlayer();
    
    if (itemName == null) {
      List<Item> weapons = currentPlayer.getInventory();
      String[] weaponNames = new String[weapons.size()];
      for (int i = 0; i < weapons.size(); i++) {
        weaponNames[i] = weapons.get(i).getName();
      }
      
      itemName = view.selectWeapon(weaponNames);
      if (itemName == null) {
        return;
      }
      if ("poke".equals(itemName)) {
        itemName = null;
      }
    }
    
    MurderResult result = model.attemptMurder(currentPlayer, itemName);
    
    String message;
    GameView.MessageType messageType;
    
    switch (result) {
      case SUCCESS:
        if (model.getDoctorLucky().isAlive()) {
          message = "Hit! Damage dealt.\nHealth: " + model.getDoctorLucky().getHealth();
          messageType = GameView.MessageType.SUCCESS;
        } else {
          message = "SUCCESS! " + currentPlayer.getName() + " killed Doctor Lucky!";
          messageType = GameView.MessageType.SUCCESS;
        }
        break;
      case FAILED_WITNESS_PRESENT:
        message = "Failed - witnesses present!";
        messageType = GameView.MessageType.WARNING;
        break;
      default:
        message = "Failed!";
        messageType = GameView.MessageType.WARNING;
        break;
    }
    
    view.showMessage(message, "Murder Attempt", messageType);
    
    if (!model.isGameOver()) {
      endTurnAndContinue();
    } else {
      refreshView();
    }
  }
  
  @Override
  public void movePet(String roomName) {
    if (!checkGameInProgress()) {
      return;
    }
    
    if (roomName == null) {
      String[] roomNames = getRoomNames();
      roomName = view.selectRoomForPet(roomNames);
      if (roomName == null) {
        return;
      }
    }
    
    Room targetRoom = model.getBoard().getRoom(roomName);
    if (targetRoom == null) {
      view.showMessage("Room not found!", "Error", GameView.MessageType.ERROR);
      return;
    }
    
    model.movePet(targetRoom);
    view.showMessage("Moved pet to " + roomName, "Success",
        GameView.MessageType.SUCCESS);
    endTurnAndContinue();
  }
  
  @Override
  public void endTurn() {
    if (!checkGameInProgress()) {
      return;
    }
    
    // Simply skip turn and advance
    view.showMessage(model.getCurrentPlayer().getName() + " skipped their turn.",
        "Turn Skipped", GameView.MessageType.INFO);
    endTurnAndContinue();
  }
  
  @Override
  public void handleRoomClick(String roomName) {
    if (!checkGameInProgress()) {
      return;
    }
    movePlayer(roomName);
  }
  
  @Override
  public void exitGame() {
    int confirm = JOptionPane.showConfirmDialog(null,
        "Exit game?", "Confirm", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
      System.exit(0);
    }
  }
  
  /**
   * Ends current turn and checks for computer player turns.
   * This is the KEY method that makes Pet move!
   */
  private void endTurnAndContinue() {
    if (model == null || model.getStatus() != GameStatus.IN_PROGRESS) {
      return;
    }
    
    // Call playTurn - this moves Doctor Lucky and Pet!
    model.playTurn();
    
    if (model.isGameOver()) {
      String message = model.getWinner() != null
          ? "Game Over!\nWinner: " + model.getWinner().getName()
          : "Game Over!\nDoctor Lucky escaped!";
      view.showMessage(message, "Game Over", GameView.MessageType.INFO);
      refreshView();
      return;
    }
    
    refreshView();
    
    // Check if next player is computer - if so, auto-execute
    checkAndExecuteComputerTurn();
  }
  
  /**
   * Checks if current player is computer and executes their turn.
   * Recursively handles multiple computer players in a row.
   */
  private void checkAndExecuteComputerTurn() {
    Player current = model.getCurrentPlayer();
    
    if (current instanceof ComputerPlayer) {
      // Use SwingUtilities.invokeLater to avoid blocking
      SwingUtilities.invokeLater(() -> executeComputerTurn((ComputerPlayer) current));
    }
  }
  
  /**
   * Executes a computer player's turn and shows what happened.
   */
  private void executeComputerTurn(ComputerPlayer computer) {
    String name = computer.getName();
    Room startRoom = computer.getCurrentRoom();
    int startInventorySize = computer.getInventory().size();
    
    // Computer takes turn
    computer.takeTurn(model);
    
    // Figure out what computer did
    Room endRoom = computer.getCurrentRoom();
    int endInventorySize = computer.getInventory().size();
    
    StringBuilder msg = new StringBuilder();
    msg.append("Computer: ").append(name).append("\n\n");
    
    if (model.isGameOver() && model.getWinner() == computer) {
      msg.append("KILLED DOCTOR LUCKY!\n");
      msg.append(name).append(" WINS!");
    } else if (!startRoom.equals(endRoom)) {
      msg.append("Moved: ").append(startRoom.getName())
          .append(" → ").append(endRoom.getName());
    } else if (endInventorySize > startInventorySize) {
      msg.append("Picked up an item");
    } else {
      msg.append("Looked around");
    }
    
    view.showMessage(msg.toString(), "Computer Turn", GameView.MessageType.INFO);
    
    if (model.isGameOver()) {
      refreshView();
      return;
    }
    
    // End computer's turn
    endTurnAndContinue();
  }
  
  /**
   * Refreshes the view with the current game state.
   */
  private void refreshView() {
    if (model != null) {
      view.refresh(model.getGameState());
    }
  }
  
  /**
   * Checks if a game is currently in progress.
   * 
   * @return true if game is in progress, false otherwise
   */
  private boolean checkGameInProgress() {
    if (model == null) {
      view.showMessage("No game! Start new game.", "No Game",
          GameView.MessageType.WARNING);
      return false;
    }
    if (model.getStatus() != GameStatus.IN_PROGRESS) {
      view.showMessage("Game not in progress!", "Error",
          GameView.MessageType.WARNING);
      return false;
    }
    return true;
  }
  
  /**
   * Gets an array of all room names in the current game.
   * 
   * @return array of room names, empty array if no game loaded
   */
  private String[] getRoomNames() {
    if (model == null) {
      return new String[0];
    }
    List<String> names = new ArrayList<>();
    for (Room room : model.getBoard().getAllRooms()) {
      names.add(room.getName());
    }
    return names.toArray(new String[0]);
  }
}