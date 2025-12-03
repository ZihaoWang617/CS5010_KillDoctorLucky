package killdoctorlucky.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import killdoctorlucky.controller.commands.AddComputerPlayerCommand;
import killdoctorlucky.controller.commands.AddHumanPlayerCommand;
import killdoctorlucky.controller.commands.AttemptMurderCommand;
import killdoctorlucky.controller.commands.CreateMapCommand;
import killdoctorlucky.controller.commands.DisplayPlayerCommand;
import killdoctorlucky.controller.commands.DisplaySpaceCommand;
import killdoctorlucky.controller.commands.LookAroundCommand;
import killdoctorlucky.controller.commands.MoveCommand;
import killdoctorlucky.controller.commands.MovePetCommand;
import killdoctorlucky.controller.commands.PickUpItemCommand;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.GameStatus;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.occupants.ComputerPlayer;
import killdoctorlucky.model.occupants.Player;
import killdoctorlucky.util.RandomGenerator;

/**
 * Text-based controller: reads commands from a {@link Readable}, writes to an
 * {@link Appendable}, and executes commands against the {@link Game} model.
 */
public class TextController {

  private final Readable input;
  private final Appendable output;
  private final Game game;
  private final int maxTurns;
  private boolean gameStarted = false;

  /**
   * Creates a controller.
   *
   * @param in       input source (e.g., System.in wrapped as Reader), non-null
   * @param out      output target (e.g., System.out), non-null
   * @param model    game model, non-null
   * @param maxTurnsLimit maximum number of turns to play (&gt;= 1)
   * @throws IllegalArgumentException if any argument is null or maxTurns < 1
   */
  public TextController(Readable in, Appendable out, Game model, int maxTurnsLimit) {
    if (in == null || out == null || model == null) {
      throw new IllegalArgumentException("in/out/model must be non-null");
    }
    if (maxTurnsLimit < 1) {
      throw new IllegalArgumentException("Max turns must be at least 1");
    }
    this.input = in;
    this.output = out;
    this.game = model;
    this.maxTurns = maxTurnsLimit;
  }

  /** Runs the interactive loop until quit / EOF / reaching maxTurns. */
  public void run() {
    println("========================================");
    println("Welcome to Kill Doctor Lucky!");
    println("========================================");
    println("Type 'help' for available commands.");
    println("");

    boolean running = true;
    int turns = 0;
    Scanner sc = new Scanner(input);

    while (running) {
      // Display current game status
      if (gameStarted && game.getStatus() == GameStatus.IN_PROGRESS) {
        println("");
        println("========================================");
        println("Turn " + (turns + 1) + " of " + maxTurns);
        println("========================================");
        
        Player currentPlayer = game.getCurrentPlayer();
        println("Current player: " + currentPlayer.getName());
        
        Room currentRoom = currentPlayer.getCurrentRoom();
        println("You are in: " + currentRoom.getName());
        
        // Show adjacent rooms for navigation ONLY
        List<Room> adjacentRooms = game.getBoard().getAdjacentRooms(currentRoom);
        if (!adjacentRooms.isEmpty()) {
          print("Adjacent rooms: ");
          for (int i = 0; i < adjacentRooms.size(); i++) {
            if (i > 0) {
              print(", ");
            }
            print(adjacentRooms.get(i).getName());
          }
          println("");
        }
        
        // Show player's inventory
        List<Item> inventory = currentPlayer.getInventory();
        if (!inventory.isEmpty()) {
          print("Your items: ");
          for (int i = 0; i < inventory.size(); i++) {
            if (i > 0) {
              print(", ");
            }
            print(inventory.get(i).getName() + " (" + inventory.get(i).getDamage() + " dmg)");
          }
          println(" [" + inventory.size() + "/" + currentPlayer.getMaxCarry() + "]");
        } else {
          println("Your items: (none) [0/" + currentPlayer.getMaxCarry() + "]");
        }
        
        println("");
        println("Doctor Lucky is in: " + game.getDoctorLucky().getCurrentRoom().getName());
        println("Doctor Lucky health: " + game.getDoctorLucky().getHealth() 
            + "/" + game.getDoctorLucky().getMaxHealth());
        
        // Show pet location if pet exists
        if (game.getPet() != null) {
          println("Pet '" + game.getPet().getName() + "' is in: " 
              + game.getPet().getCurrentRoom().getName());
        }
        
        println("");
        
        // If current player is computer, automatically execute its turn
        if (currentPlayer instanceof ComputerPlayer) {
          println("Computer player " + currentPlayer.getName() + " is taking their turn...");
          ((ComputerPlayer) currentPlayer).takeTurn(game);
          
          // Check if game ended due to murder (before calling playTurn)
          if (game.isGameOver()) {
            // Game ended due to successful murder by computer player
            Player winner = game.getWinner();
            if (winner != null) {
              println("");
              println("========================================");
              println("GAME OVER!");
              println("Computer player " + winner.getName() + " has won the game!");
              println("========================================");
            }
            running = false;
            break;
          }
          
          // Game continues - advance turn
          game.playTurn();
          turns++;
          
          // Check if game ended due to max turns
          if (checkGameEnd(turns)) {
            running = false;
            break;
          }
          
          continue;
        }
      }
      
      print("> ");
      if (!sc.hasNext()) {
        break; 
      }

      String token = sc.next().toLowerCase(Locale.ROOT);

      try {
        // Setup commands
        if ("add-human".equals(token)) {
          if (gameStarted) {
            println("Cannot add players after game has started.");
            continue;
          }
          String name = requireNext(sc, "player name");
          String room = sc.nextLine().trim(); 
          if (room.isEmpty()) {
            println("Please specify a room name.");
            continue;
          }
          new AddHumanPlayerCommand(name, room).execute(game, output);

        } else if ("add-computer".equals(token)) {
          if (gameStarted) {
            println("Cannot add players after game has started.");
            continue;
          }
          String name = requireNext(sc, "player name");
          String room = sc.nextLine().trim();
          if (room.isEmpty()) {
            println("Please specify a room name.");
            continue;
          }
          RandomGenerator gen = new RandomGenerator(1, 0, 2, 1, 0, 2, 0, 1, 2);
          new AddComputerPlayerCommand(name, room, gen).execute(game, output);

        } else if ("start".equals(token)) {
          if (gameStarted) {
            println("Game has already started.");
            continue;
          }
          if (game.getPlayers().isEmpty()) {
            println("Please add at least one player before starting.");
            continue;
          }
          game.startGame();
          gameStarted = true;
          println("Game started with " + game.getPlayers().size() + " players.");
          println("Maximum turns: " + maxTurns);
          
        // Play commands
        } else if ("look".equals(token)) {
          if (!ensureGameInProgress()) {
            continue;
          }
          new LookAroundCommand().execute(game, output);

          game.playTurn();
          turns++;
          
          if (checkGameEnd(turns)) {
            running = false;
            break;
          }
          
        } else if ("move".equals(token)) {
          if (!ensureGameInProgress()) {
            continue;
          }
          String room = sc.nextLine().trim();
          if (room.isEmpty()) {
            println("Please specify a room name.");
            continue;
          }
          new MoveCommand(room).execute(game, output);
          
          game.playTurn();
          turns++;
          
          if (checkGameEnd(turns)) {
            running = false;
            break;
          }
          
        } else if ("pickup".equals(token)) {
          if (!ensureGameInProgress()) {
            continue;
          }
          String itemName = sc.nextLine().trim();
          if (itemName.isEmpty()) {
            println("Please specify an item name.");
            continue;
          }
          // Remove quotes if present
          if (itemName.startsWith("\"") && itemName.endsWith("\"")) {
            itemName = itemName.substring(1, itemName.length() - 1);
          }
          new PickUpItemCommand(itemName).execute(game, output);
          

          game.playTurn();
          turns++;
          
          if (checkGameEnd(turns)) {
            running = false;
            break;
          }
          
        } else if ("movepet".equals(token)) {
          if (!ensureGameInProgress()) {
            continue;
          }
          String roomName = sc.nextLine().trim();
          if (roomName.isEmpty()) {
            println("Please specify a room name.");
            continue;
          }
          new MovePetCommand(roomName).execute(game, output);
          

          game.playTurn();
          turns++;
          
          if (checkGameEnd(turns)) {
            running = false;
            break;
          }
          
        } else if ("attack".equals(token)) {
          if (!ensureGameInProgress()) {
            continue;
          }
          // Read the rest of the line as the item name
          String weaponName = sc.hasNextLine() ? sc.nextLine().trim() : "";
          new AttemptMurderCommand(weaponName.isEmpty() ? null : weaponName).execute(game, output);
          
          // If murder succeeds, game ends automatically in the command
          // Otherwise, end turn
          if (!game.isGameOver()) {
            game.playTurn();
            turns++;
            
            if (checkGameEnd(turns)) {
              running = false;
              break;
            }
          } else {
            // Game ended due to successful murder
            running = false;
            break;
          }
          
        } else if ("endturn".equals(token)) {
          if (!ensureGameInProgress()) {
            continue;
          }
          
          println("Skipping turn...");
          game.playTurn();
          turns++;
          
          if (checkGameEnd(turns)) {
            running = false;
            break;
          }
          
        // Information commands - DO NOT end turn
        } else if ("info".equals(token)) {
          String spaceName = sc.nextLine().trim();
          if (spaceName.isEmpty()) {
            println("Please specify a space name.");
            continue;
          }
          new DisplaySpaceCommand(spaceName).execute(game, output);
          
        } else if ("player".equals(token)) {
          String playerName = sc.nextLine().trim();
          if (playerName.isEmpty()) {
            println("Please specify a player name.");
            continue;
          }
          new DisplayPlayerCommand(playerName).execute(game, output);
          
        } else if ("map".equals(token)) {
          String filename = requireNext(sc, "output filename");
          new CreateMapCommand(filename).execute(game, output);
          
        } else if ("spaces".equals(token)) {
          println("Available spaces:");
          for (Room room : game.getBoard().getAllRooms()) {
            println("  - " + room.getName());
          }
          
        } else if ("players".equals(token)) {
          if (game.getPlayers().isEmpty()) {
            println("No players added yet.");
          } else {
            println("Players in the game:");
            for (Player p : game.getPlayers()) {
              String type = (p instanceof ComputerPlayer) ? " (Computer)" : " (Human)";
              println("  - " + p.getName() + type + " in " + p.getCurrentRoom().getName());
            }
          }
          
        } else if ("status".equals(token)) {
          println("Game Status: " + game.getStatus());
          if (gameStarted) {
            println("Turn: " + (turns + 1) + " / " + maxTurns);
            println("Current Player: " + game.getCurrentPlayer().getName());
            println("Doctor Lucky health: " + game.getDoctorLucky().getHealth() 
                + "/" + game.getDoctorLucky().getMaxHealth());
          }
          
        // General commands
        } else if ("help".equals(token)) {
          printHelp();

        } else if ("quit".equals(token)) {
          println("Thanks for playing! Goodbye.");
          break;

        } else {
          println("Unknown command: '" + token + "'. Type 'help' for available commands.");
        }

      } catch (IllegalArgumentException | IllegalStateException e) {
        println("Error: " + e.getMessage());
      }
    }
  }
  
  /**
   * Checks if the game has ended and displays appropriate messages.
   * @param currentTurns current turn count
   * @return true if game should end
   */
  private boolean checkGameEnd(int currentTurns) {
    if (currentTurns >= maxTurns) {
      println("");
      println("========================================");
      println("Maximum number of turns reached: " + maxTurns);
      println("Doctor Lucky has escaped!");
      println("Game Over - No winner!");
      println("========================================");
      return true;
    } else if (game.isGameOver()) {
      Player winner = game.getWinner();
      if (winner != null) {
        println("");
        println("========================================");
        println("GAME OVER!");
        println("Congratulations! " + winner.getName() + " has won the game!");
        println("========================================");
      } else {
        println("");
        println("========================================");
        println("Game Over! No winner.");
        println("========================================");
      }
      return true;
    }
    return false;
  }
  
  private String requireNext(Scanner sc, String what) {
    if (!sc.hasNext()) {
      throw new IllegalStateException("Missing " + what);
    }
    return sc.next();
  }
  
  private boolean ensureGameInProgress() {
    if (!gameStarted) {
      println("Please start the game first using 'start' command.");
      return false;
    }
    if (game.getStatus() != GameStatus.IN_PROGRESS) {
      println("Game is not in progress.");
      return false;
    }
    return true;
  }

  private void printHelp() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n=== AVAILABLE COMMANDS ===\n");
    sb.append("\nSETUP PHASE:\n");
    sb.append("  add-human <name> <room>     - Add a human player\n");
    sb.append("  add-computer <name> <room>  - Add a computer player\n");
    sb.append("  start                       - Start the game\n");
    sb.append("\nMAIN ACTIONS (ends turn automatically):\n");
    sb.append("  look                        - Look around (see items, players, details)\n");
    sb.append("  move <roomName>             - Move to an adjacent room\n");
    sb.append("  pickup <itemName>           - Pick up an item from current room\n");
    sb.append("  movepet <roomName>          - Move the pet to a specified room\n");
    sb.append("  attack [itemName]           - Attempt to murder Doctor Lucky\n");
    sb.append("                                (leave itemName empty to poke in eye)\n");
    sb.append("  endturn                     - Skip turn (do nothing)\n");
    sb.append("\nINFORMATION (does not end turn):\n");
    sb.append("  info <spaceName>            - Display information about a space\n");
    sb.append("  player <playerName>         - Display information about a player\n");
    sb.append("  map <filename>              - Generate world map as PNG\n");
    sb.append("  spaces                      - List all spaces in the world\n");
    sb.append("  players                     - List all players in the game\n");
    sb.append("  status                      - Show current game status\n");  
    sb.append("\nGENERAL:\n");
    sb.append("  help                        - Show this help message\n");
    sb.append("  quit                        - Exit the game\n");
    sb.append("\n");
    println(sb.toString());
  }

  private void print(String s) {
    try {
      output.append(s);
    } catch (IOException e) {
      throw new IllegalStateException("Error writing to output", e);
    }
  }

  private void println(String s) {
    print(s + "\n");
  }
}