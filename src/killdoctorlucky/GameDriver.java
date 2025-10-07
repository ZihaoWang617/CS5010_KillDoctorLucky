package killdoctorlucky;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Main driver class for Kill Doctor Lucky game. Demonstrates model
 * functionality using command-line arguments.
 */
public class GameDriver {
  /**
   * The main entry point of the Kill Doctor Lucky game driver.
   * @param args Command-line arguments, expecting a world file path.
   */
  public static void main(String[] args) {
    // Check if world file was provided
    if (args.length < 1) {
      System.err.println("Usage: java killdoctorlucky.GameDriver <world-file.txt>");
      System.err.println("Example: java killdoctorlucky.GameDriver mansion.txt");
      System.exit(1);
    }

    String worldFile = args[0];

    try {
      System.out.println("=".repeat(60));
      System.out.println("Kill Doctor Lucky - World Demonstration");
      System.out.println("=".repeat(60));
      System.out.println();

      // 1. Load world from file using Readable
      System.out.println("Step 1: Loading world from file: " + worldFile);
      WorldParser.WorldData worldData = WorldParser.parseWorld(new FileReader(worldFile));
      Board board = worldData.board;
      System.out.println("✓ World loaded successfully!");
      System.out.println("✓ Total rooms: " + board.getRoomCount());
      System.out.println();

      // 2. Demonstrate room neighbors
      demonstrateNeighbors(board);

      // 3. Demonstrate room information
      demonstrateRoomInfo(board);

      // 4. Demonstrate target character movement
      demonstrateTargetMovement(worldData);

      System.out.println("=".repeat(60));
      System.out.println("Demonstration completed successfully!");
      System.out.println("=".repeat(60));

    } catch (IOException e) {
      System.err.println("ERROR: Could not read world file: " + e.getMessage());
      System.exit(1);
    } catch (IllegalArgumentException e) {
      System.err.println("ERROR: Invalid world specification: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Demonstrates finding neighbors of rooms.
   */
  private static void demonstrateNeighbors(Board board) {
    System.out.println("-".repeat(60));
    System.out.println("Step 2: Demonstrating Room Neighbors");
    System.out.println("-".repeat(60));

    int count = 0;
    for (Room room : board.getAllRooms()) {
      System.out.println("Room: " + room.getName());
      System.out.println("  Neighbors (" + room.getConnections().size() + "):");
      for (Room neighbor : room.getConnections()) {
        System.out.println("    - " + neighbor.getName());
      }
      System.out.println();

      count++;
      if (count >= 5) {
        System.out.println("  ... (showing first 5 rooms only)");
        break;
      }
    }
    System.out.println();
  }

  /**
   * Demonstrates displaying information about specific rooms.
   */
  private static void demonstrateRoomInfo(Board board) {
    System.out.println("-".repeat(60));
    System.out.println("Step 3: Demonstrating Room Information");
    System.out.println("-".repeat(60));

    // Get first room
    Room firstRoom = board.getAllRooms().iterator().next();

    System.out.println("Detailed information for room: " + firstRoom.getName());
    System.out.println("  - Is named room: " + firstRoom.isNamedRoom());
    System.out.println("  - Number of occupants: " + firstRoom.getOccupants().size());
    System.out.println("  - Number of connections: " + firstRoom.getConnections().size());
    System.out.println("  - Player count: " + firstRoom.getPlayerCount());
    System.out.println("  - Items in this room:");
    if (firstRoom.getItems().isEmpty()) {
      System.out.println("      (no items)");
    } else {
      for (Item item : firstRoom.getItems()) {
        System.out.println("      * " + item.getName() + " (damage: " + item.getDamage() + ")");
      }
    }

    // 显示从这个房间可以看到的其他房间
    System.out.println("  - Rooms visible from here:");
    if (firstRoom.getConnections().isEmpty()) {
      System.out.println("      (no visible rooms)");
    } else {
      for (Room neighbor : firstRoom.getConnections()) {
        System.out.println("      * " + neighbor.getName());
      }
    }

    System.out.println();
  }

  /**
   * Demonstrates Doctor Lucky moving through the world.
   */
  private static void demonstrateTargetMovement(WorldParser.WorldData worldData) {
    System.out.println("-".repeat(60));
    System.out.println("Step 4: Demonstrating Target Character Movement");
    System.out.println("-".repeat(60));
    System.out.println();

    List<Room> roomsInOrder = worldData.roomsInOrder;

    // Create Doctor Lucky in first room
    Room startRoom = roomsInOrder.get(0);
    DoctorLucky doctor = new DoctorLucky(startRoom);

    // Set movement sequence
    doctor.setMovementSequence(roomsInOrder);

    System.out.println("Doctor Lucky starts in: " + doctor.getCurrentRoom().getName());
    System.out.println();

    // Simulate 10 turns of movement
    System.out.println("Simulating Doctor Lucky's movement:");
    for (int i = 1; i <= 10; i++) {
      doctor.moveNext();
      System.out
          .println("  Turn " + i + ": Doctor Lucky is now in " + doctor.getCurrentRoom().getName());
    }
    System.out.println();
  }
}