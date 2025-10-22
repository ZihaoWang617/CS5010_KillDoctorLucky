# Kill Doctor Lucky - Milestone 2

## Author
Zihao Wang - Northeastern University Vancouver

## Project Overview
This is an implementation of the "Kill Doctor Lucky" board game for CS5010 at Northeastern University. The project implements a text-based interactive game where players move through a mansion attempting to eliminate Doctor Lucky.

## Features Implemented

### Milestone 2 Features
- **Player Management**: Add human-controlled and computer-controlled players
- **Interactive Gameplay**: Text-based command interface for game control
- **Movement System**: Players can move between adjacent rooms
- **Item Management**: Players can pick up and carry items (up to 3 per player)
- **Look Around**: Players can observe their surroundings and see other players
- **Turn-Based System**: Multiple players take turns, including automated computer players
- **Game Information**: Display detailed information about players and spaces
- **Map Generation**: Create and save a graphical representation of the world as PNG
- **Turn Limits**: Game ends automatically after reaching maximum number of turns

### Command Pattern Implementation
All player actions are implemented using the Command design pattern:
- `AddHumanPlayerCommand` - Adds a human player to the game
- `AddComputerPlayerCommand` - Adds a computer player to the game
- `MoveCommand` - Moves player to an adjacent room
- `PickUpItemCommand` - Picks up an item from current room
- `LookAroundCommand` - Shows information about current room and surroundings
- `DisplayPlayerCommand` - Shows detailed player information
- `DisplaySpaceCommand` - Shows detailed space information
- `CreateMapCommand` - Generates world map as PNG image

## How to Run

### Using JAR File (Recommended)
```bash
java -jar res/KillDoctorLucky.jar res/mansion.txt 15
```

**Arguments:**
- `res/mansion.txt` - Path to the world specification file
- `20` - Maximum number of turns allowed (must be a positive integer)

### From Source Code
```bash
# Compile the source code
javac -d bin src/killdoctorlucky/*.java

# Run the game
java -cp bin killdoctorlucky.GameDriver res/mansion.txt 15
```

## Game Commands

### Setup Phase Commands
- `add-human <name> <room>` - Add a human-controlled player
  - Example: `add-human Alice Library`
- `add-computer <name> <room>` - Add a computer-controlled player
  - Example: `add-computer Bot1 Kitchen`
- `start` - Start the game (requires 3-7 players)
- `players` - List all players in the game
- `spaces` - List all available spaces

### Gameplay Commands
- `look` - Look around the current room
- `move <roomName>` - Move to an adjacent room
  - Example: `move Kitchen`
- `pickup <itemName>` - Pick up an item from current room
  - Example: `pickup "Sharp Knife"` (use quotes for multi-word items)
- `endturn` - End your turn and pass to next player

### Information Commands
- `info <spaceName>` - Display detailed information about a space
  - Example: `info Library`
- `player <playerName>` - Display detailed information about a player
  - Example: `player Alice`
- `map <filename>` - Generate and save world map as PNG
  - Example: `map world-map.png`
- `status` - Show current game status (turn number, current player)

### General Commands
- `help` - Display all available commands
- `quit` - Exit the game

## Example Runs

### example-run-1.txt
Demonstrates world loading and model functionality:
- World specification file parsing
- Room connections and neighbor relationships
- Item placement verification
- Doctor Lucky movement sequence through rooms

### example-run-2.txt
Demonstrates complete Milestone 2 gameplay features:
- **Adding players** (lines 11-15): Human players Alice and Bob, computer player Bot1
- **Game start** (line 18): Initializing game with 3 players, 15 turn limit
- **Look around** (lines 20, 42): Observing room contents and visible players
- **Player information** (lines 25, 45): Displaying detailed player status
- **Item pickup** (lines 31, 43): Successfully picking up "Sharp Knife" and "Deadly Poison"
- **Player movement** (lines 33, 47): Moving between adjacent rooms
- **Space information** (line 49): Detailed room information display
- **Map generation** (line 58): Creating world-map.png visualization
- **Computer player** (lines 56, 67, 78, 87, 96): Automatic turn execution
- **Multiple players** (throughout): Alice, Bob, and Bot1 taking turns
- **Game ending** (lines 99-103): Automatic termination at maximum turns

## Project Structure
```
CS5010/
├── src/killdoctorlucky/
│   ├── Command.java (Command Pattern interface)
│   ├── TextController.java (Main controller with MVC pattern)
│   ├── GameDriver.java (Entry point, handles command-line arguments)
│   ├── GameDriverDemo.java (Milestone 1 demonstration)
│   ├── Game.java (Game logic and state management)
│   ├── Board.java (Game board with rooms and connections)
│   ├── Room.java (Individual room representation)
│   ├── Player.java (Human player implementation)
│   ├── ComputerPlayer.java (AI player with random actions)
│   ├── DoctorLucky.java (Target character with movement)
│   ├── Item.java (Weapon items with damage values)
│   ├── RandomGenerator.java (Testable random number generation)
│   ├── WorldParser.java (World file parser)
│   ├── Command implementations:
│   │   ├── AddHumanPlayerCommand.java
│   │   ├── AddComputerPlayerCommand.java
│   │   ├── MoveCommand.java
│   │   ├── PickUpItemCommand.java
│   │   ├── LookAroundCommand.java
│   │   ├── DisplayPlayerCommand.java
│   │   ├── DisplaySpaceCommand.java
│   │   └── CreateMapCommand.java
│   └── Supporting classes (Card types, Deck, Enums, Interfaces)
│
├── test/killdoctorlucky/
│   ├── BoardTest.java
│   ├── CommandTest.java
│   ├── TextControllerTest.java
│   ├── GameTest.java
│   ├── PlayerTest.java
│   ├── DoctorLuckyTest.java
│   ├── ItemTest.java
│   ├── RoomTest.java
│   ├── RandomGeneratorTest.java
│   └── WorldParserTest.java
│
└── res/
    ├── mansion.txt (World specification file)
    ├── example-run-1.txt (Model demonstration)
    ├── example-run-2.txt (Interactive gameplay demonstration)
    ├── world-map.png (Generated game world map)
    ├── KillDoctorLucky.jar (Executable JAR file)
    └── UML Diagram for CS5010.pdf (Complete class diagram)
```

## Design Patterns and Architecture

### Model-View-Controller (MVC) Architecture
- **Model**: `Game`, `Board`, `Room`, `Player`, `DoctorLucky`, `Item`
  - Manages game state, rules, and business logic
- **Controller**: `TextController`, Command implementations
  - Processes user input and executes game commands
- **View**: Console text output via `Appendable`
  - Presents game information to players

### Design Patterns Used
- **Command Pattern**: All player actions encapsulated as command objects
  - Decouples request from execution
  - Enables easy addition of new commands
- **Strategy Pattern**: Different player types (Human vs Computer)
  - Polymorphic behavior for player actions
- **Facade Pattern**: `Game` class simplifies complex subsystem interactions
- **Template Method**: Player action execution flow

### Key Design Decisions
- Used `Readable` and `Appendable` for testable I/O operations
- Implemented `RandomGenerator` wrapper for predictable testing
- Command Pattern allows easy extension of game actions
- MVC separation enables potential GUI addition in future milestones

## Testing

Comprehensive JUnit 4 test coverage for all components:

### Model Tests
- `BoardTest.java` - Room connections, visibility, navigation
- `RoomTest.java` - Room creation, occupants, items
- `PlayerTest.java` - Movement, inventory, cards
- `GameTest.java` - Game flow, turn management, player management
- `DoctorLuckyTest.java` - Movement sequence, visibility
- `ItemTest.java` - Item properties and room assignment

### Controller Tests
- `TextControllerTest.java` - Command parsing, game flow, turn limits
- `CommandTest.java` - All command implementations

### Utility Tests
- `WorldParserTest.java` - World file parsing, room connections
- `RandomGeneratorTest.java` - Predictable and true random behavior

**Run all tests:**
```bash
