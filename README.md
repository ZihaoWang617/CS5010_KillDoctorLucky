# Kill Doctor Lucky - Milestone 3

## Author
Zihao Wang - Northeastern University Vancouver

## Project Overview
This is an implementation of the "Kill Doctor Lucky" board game for CS5010 at Northeastern University. The project implements a text-based interactive game where players move through a mansion attempting to eliminate Doctor Lucky while his pet patrols the mansion blocking visibility.

## Features Implemented

### Milestone 3 Features (NEW)
- **Pet System**: Target character's pet that affects visibility
  - Pet blocks neighboring rooms from seeing into the room it occupies
  - Player can move the pet to any room as a turn action
  - **Extra Credit**: Pet automatically wanders following Depth-First Search (DFS) traversal
- **Murder Mechanics**: Players can attempt to kill Doctor Lucky
  - Attack with items (damage based on item properties)
  - "Poke in the eye" attack (1 damage) when no items available
  - Witness detection system (attack fails if seen by other players)
  - Items used in murder attempts are removed from the game
- **Health System**: Doctor Lucky has health points and can take damage
- **Win Conditions**: 
  - Player wins by successfully killing Doctor Lucky
  - Doctor Lucky escapes if maximum turns is reached
- **Enhanced Computer Player AI**: Computer players automatically attempt murder when conditions are favorable, using the highest damage weapon available

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

### Milestone 1 Features
- World specification file parsing
- Room creation with coordinates and connections
- Item placement in rooms
- Doctor Lucky movement sequence
- Sight line system for custom visibility rules

## How to Run the Game

### Using the JAR file (Recommended):
```bash
java -jar res/KillDoctorLucky.jar <world-file> <max-turns>
```

**Example**:
```bash
java -jar res/KillDoctorLucky.jar res/demo-world.txt 20
```

**Parameters**:
- `<world-file>`: Path to the world specification file (e.g., `mansion.txt` or `res/demo-world.txt`)
- `<max-turns>`: Maximum number of turns allowed (must be a positive integer)

### From Source Code:
```bash
# Compile the source code
javac -d bin -cp lib/* src/killdoctorlucky/**/*.java

# Run the game
java -cp bin killdoctorlucky.controller.GameDriver res/demo-world.txt 20
```

---

## Example Runs

All example runs are located in the `res/` directory and demonstrate all required Milestone 3 features:

### 1. example-run-1-human-wins.txt
**Demonstrates**:
- Pet blocking visibility from neighboring rooms
  - Turn 11: Bob's adjacent rooms changes to (0) when pet enters Start Room
- Human player moving the pet (Turn 7: `movepet Start Room`)
- Human player attempting murder (Turn 16: `attack Mega Gun`)
- Human player winning by killing Doctor Lucky (Turn 16: Alice wins with 15 damage attack)
- Pet wandering in DFS pattern (observe Shadow the Cat's location changing each turn)
- Multiple use of `look` command showing detailed room information

**Key moments**:
- Lines showing pet movement: Each turn displays pet location
- Line showing pet blocking visibility: Turn 11 - Far Room 1 neighbors become (0)
- Line showing successful murder: Turn 16 - "SUCCESS! Doctor Lucky has been killed!"

### 2. example-run-2-computer-wins.txt
**Demonstrates**:
- Computer player automatically attempting murder when alone with Doctor Lucky
- Computer player (Bot3) using best weapon available
- Computer player winning the game (Turn 10: Bot3 kills Doctor Lucky with Rock)
- Pet DFS wandering pattern consistent across multiple turns
- Automatic AI behavior for all three computer players

**Key moments**:
- Line showing computer murder attempt: Turn 10 - Bot3 attempts murder
- Line showing computer victory: Turn 10 - "Computer player Bot3 has won the game!"

### 3. example-run-3-doctor-escapes.txt
**Demonstrates**:
- Game ending when maximum turns is reached (Turn 20)
- Doctor Lucky escaping alive (no winner)
- Complete Pet DFS traversal showing two full cycles through all rooms
- Turn progression and game state display using `status` command
- Pet location tracking across 20 turns

**Key moments**:
- Pet DFS pattern: Turns 1-20 show complete cycles (Start Room → Middle Room → End Room → Far Room 2 → Far Room 1 → repeat)
- Line showing game over: Turn 20 - "Maximum number of turns reached: 20"
- Line showing no winner: "Doctor Lucky has escaped! Game Over - No winner!"

---

## Pet DFS Wandering (Extra Credit Implementation)

The pet follows a Depth-First Search traversal pattern through all rooms in the world, automatically moving with each turn.

**Evidence in Example Runs**: Observe the pet's location in any example run:
```
Turn 1:  Start Room
Turn 2:  Middle Room
Turn 3:  End Room
Turn 4:  Far Room 2
Turn 5:  Far Room 1
Turn 6:  Start Room    (cycle repeats)
Turn 7:  Middle Room
Turn 8:  End Room
...
```

This consistent pattern across all runs demonstrates the DFS implementation. The pet visits all rooms following a depth-first graph traversal and cycles continuously.

**Implementation**: See `Pet.java` - methods `initializeDfsPath()` and `wanderNext()`

---

## Game Commands Reference

Type `help` in the game to see all available commands.

### Main Actions (ends turn automatically):
- `look` - Look around current room (see items, players, visibility)
- `move <roomName>` - Move to an adjacent room
- `pickup <itemName>` - Pick up an item from current room
- `movepet <roomName>` - Move the pet to any specified room
- `attack [itemName]` - Attempt to murder Doctor Lucky
  - Use with item name to attack with weapon
  - Leave empty to "poke in the eye" (1 damage)
- `endturn` - Skip your turn (do nothing)

### Information Commands (does not end turn):
- `status` - Show game status, current turn, Doctor Lucky health
- `players` - List all players and their locations
- `spaces` - List all available rooms in the world
- `info <spaceName>` - Display detailed information about a specific room
- `player <playerName>` - Display detailed information about a specific player
- `map <filename>` - Generate world map visualization as PNG

### General Commands:
- `help` - Display all available commands with descriptions
- `quit` - Exit the game

---

## Project Structure
```
CS5010/
├── src/killdoctorlucky/
│   ├── model/
│   │   ├── Game.java (Core game logic and state)
│   │   ├── Board.java (World map with rooms and connections)
│   │   ├── Room.java (Individual room with items and occupants)
│   │   ├── Item.java (Weapon items with damage values)
│   │   ├── Deck.java (Card deck management)
│   │   ├── GameStatus.java (Enum for game states)
│   │   ├── MurderResult.java (Enum for murder attempt results)
│   │   ├── occupants/
│   │   │   ├── Occupant.java (Interface for room occupants)
│   │   │   ├── Player.java (Human player with inventory)
│   │   │   ├── ComputerPlayer.java (AI player with automatic actions)
│   │   │   ├── DoctorLucky.java (Target character with health)
│   │   │   └── Pet.java (Pet with DFS wandering)
│   │   ├── cards/
│   │   │   ├── Playable.java (Card interface)
│   │   │   ├── Card.java (Abstract card base)
│   │   │   ├── WeaponCard.java
│   │   │   ├── MoveCard.java
│   │   │   ├── RoomCard.java
│   │   │   └── FailureCard.java
│   │   └── interfaces/
│   │       └── Movable.java (Interface for movable entities)
│   ├── controller/
│   │   ├── TextController.java (Main controller with MVC pattern)
│   │   ├── GameDriver.java (Entry point, command-line argument handler)
│   │   └── commands/
│   │       ├── Command.java (Command Pattern interface)
│   │       ├── AddHumanPlayerCommand.java
│   │       ├── AddComputerPlayerCommand.java
│   │       ├── MoveCommand.java
│   │       ├── PickUpItemCommand.java
│   │       ├── LookAroundCommand.java
│   │       ├── MovePetCommand.java (NEW - Milestone 3)
│   │       ├── AttemptMurderCommand.java (NEW - Milestone 3)
│   │       ├── DisplayPlayerCommand.java
│   │       ├── DisplaySpaceCommand.java
│   │       └── CreateMapCommand.java
│   └── util/
│       ├── RandomGenerator.java (Testable random number generation)
│       └── WorldParser.java (World file parser with pet support)
│
├── test/killdoctorlucky/
│   ├── model/
│   │   ├── GameTest.java (Game logic, murder mechanics, pet management)
│   │   ├── BoardTest.java (Room connections, visibility)
│   │   ├── RoomTest.java (Room behavior, occupants, items)
│   │   ├── ItemTest.java (Item properties)
│   │   ├── DeckTest.java (Deck operations)
│   │   └── occupants/
│   │       ├── PlayerTest.java (Player actions, murder-related methods)
│   │       ├── ComputerPlayerTest.java (AI behavior)
│   │       ├── DoctorLuckyTest.java (Health system, damage, death)
│   │       └── PetTest.java (NEW - Pet movement, DFS, visibility)
│   ├── controller/
│   │   ├── TextControllerTest.java (Controller integration)
│   │   └── commands/
│   │       ├── CommandTest.java (All command tests)
│   │       ├── MovePetCommandTest.java (NEW - Pet movement command)
│   │       └── AttemptMurderCommandTest.java (NEW - Murder command)
│   └── util/
│       ├── RandomGeneratorTest.java
│       └── WorldParserTest.java (Pet parsing support)
│
└── res/
    ├── KillDoctorLucky.jar              # Runnable JAR file
    ├── demo-world.txt                   # Demo world (simplified for testing)
    ├── mansion.txt                      # Original mansion world
    ├── example-run-1-human-wins.txt     # Human player victory scenario
    ├── example-run-2-computer-wins.txt  # Computer player victory scenario
    ├── example-run-3-doctor-escapes.txt # Doctor Lucky escapes scenario
    └── UML-Milestone3.pdf               # Complete UML class diagram
```

---

## Design Patterns and Architecture

### Model-View-Controller (MVC) Architecture
- **Model**: `Game`, `Board`, `Room`, `Player`, `DoctorLucky`, `Pet`, `Item`
  - Manages game state, rules, murder mechanics, and business logic
  - Handles health system, visibility, and win conditions
- **Controller**: `TextController`, Command implementations
  - Processes user input and executes game commands
  - Manages turn flow and automatic computer player actions
- **View**: Console text output via `Appendable`
  - Presents game information to players
  - Displays turn information, game state, and results

### Design Patterns Used
- **Command Pattern**: All player actions encapsulated as command objects
  - Decouples request from execution
  - Enables easy addition of new commands (MovePet, Attack)
  - Each command validates input and provides error messages
- **Strategy Pattern**: Different player types (Human vs Computer)
  - Polymorphic behavior for player actions
  - Computer players automatically attempt murder when possible
- **Facade Pattern**: `Game` class simplifies complex subsystem interactions
  - Centralizes game rules and state management
  - Coordinates between Board, Players, Doctor Lucky, and Pet
- **Template Method**: Player action execution flow
  - Base Player class defines common behavior
  - ComputerPlayer extends with AI-specific logic

### Key Design Decisions
- **Decoupled I/O**: Used `Readable` and `Appendable` for testable I/O operations
- **Testable Randomness**: Implemented `RandomGenerator` wrapper for predictable testing
- **Package Organization**: Separated model, controller, and utilities into distinct packages
  - `model.occupants` - All entities that can occupy rooms
  - `model.cards` - Card-related classes
  - `controller.commands` - Command pattern implementations
- **Visibility System**: Pet-aware visibility calculation for murder attempt validation
- **Turn Management**: Actions automatically end turn, clear separation of action vs information commands

---

## World File Format (Milestone 3)
```
<rows> <cols> <world name>
<health> <target character name>
<pet name>
<number of rooms>
<room specifications...>
<number of items>
<item specifications...>
SIGHT
<sight line specifications...>
END
```

**Example** (`demo-world.txt`):
```
20 15 Demo Mansion
15 Doctor Lucky
Shadow the Cat
5
0 0 4 4 Start Room
...
```

---

## Game Rules

### Objective
Be the first player to successfully kill Doctor Lucky!

### How to Win
1. Be in the same room as Doctor Lucky
2. Ensure no other players can see you (no witnesses)
3. Use `attack <itemName>` with a weapon, or `attack` to poke in the eye
4. Deal enough damage to reduce Doctor Lucky's health to 0

### Murder Attempt Rules
- **Witnesses**: If another player can see you (same room or adjacent room without pet), the attack automatically fails
- **Pet Blocking**: A room with the pet cannot be seen by neighboring rooms
- **Damage**: 
  - Weapons deal their specified damage
  - Poke in the eye deals 1 damage
- **Evidence**: Items used in murder attempts are removed from the game
- **Computer Players**: Always attempt murder when alone with Doctor Lucky, using their highest damage weapon

### Game Ending
- **Victory**: A player successfully reduces Doctor Lucky's health to 0
- **Escape**: Maximum turns reached and Doctor Lucky survives (no winner)

---

## Testing

Comprehensive JUnit 4 test coverage for all components:

### Model Tests
- `GameTest.java` - Game flow, murder mechanics, pet management, visibility, win conditions
- `BoardTest.java` - Room connections, visibility, pet blocking
- `RoomTest.java` - Room creation, occupants, items
- `PlayerTest.java` - Movement, inventory, murder-related methods
- `DoctorLuckyTest.java` - Health system, damage, movement, death
- `PetTest.java` - **NEW**: Pet creation, movement, DFS traversal, visibility blocking
- `ItemTest.java` - Item properties and room assignment
- `DeckTest.java` - Card deck operations

### Controller Tests
- `TextControllerTest.java` - Command parsing, game flow, turn limits, new commands
- `CommandTest.java` - All command implementations
- `MovePetCommandTest.java` - **NEW**: Pet movement command
- `AttemptMurderCommandTest.java` - **NEW**: Murder attempt command

### Utility Tests
- `WorldParserTest.java` - World file parsing including pet information
- `RandomGeneratorTest.java` - Predictable and true random behavior

