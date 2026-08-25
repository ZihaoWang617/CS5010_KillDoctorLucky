# Kill Doctor Lucky - Milestone 4 (Graphical User Interface)

## Author
Zihao Wang - Northeastern University Vancouver

## Project Overview
This is an implementation of the "Kill Doctor Lucky" board game for CS5010 at Northeastern University. The project now includes both a **graphical user interface (GUI)** and the original text-based interface, implementing a complete MVC (Model-View-Controller) architecture for both play modes.

---

## Milestone 4 Features (NEW - GUI Implementation)

### **Graphical User Interface**
- **Welcome Screen**: Displays game information, controls, and credits the creator
- **Main Game Window**: 
  - Visual representation of the mansion with rooms displayed using actual coordinates from world file
  - Real-time display of players (blue circles with initials), Doctor Lucky (red D), and pet (orange P)
  - Scrollable view for large world maps (e.g., mansion.txt)
  - Room grid layout matches the world specification exactly
- **Information Panel**: 
  - Current player and turn counter
  - Doctor Lucky's health and location
  - Pet location
  - Current player's inventory (items carried)
  - Last action result display
- **Menu Bar**: 
  - File → New Game (load new world specification)
  - File → Restart Game (restart with current world)
  - File → Exit (quit application)
  - Help → About (show welcome screen)

### **User Interactions**
- **Mouse Controls**:
  - Click on rooms to move your player to adjacent rooms
  - Click on player icons to view detailed player information (name, type, location, inventory)
  - Invalid moves (non-adjacent rooms) are prevented with warning messages
- **Keyboard Shortcuts**:
  - **P** - Pick up item from current room (prompts for selection if multiple items)
  - **L** - Look around (displays current room info, adjacent rooms, and occupants)
  - **A** - Attempt murder on Doctor Lucky (prompts for weapon selection)
  - **M** - Move the pet to another room (prompts for room selection)
  - **E** - End turn without taking an action
  - **H** - Show keyboard help dialog

### **MVC Architecture (Milestone 4 Focus)**
- **Model**: 
  - `Game` implements `ReadOnlyGameModel` interface for controlled access
  - `GameState` class provides immutable snapshots for the View
  - All game logic remains in Model, fully decoupled from View and Controller
  - Supports both text and GUI controllers without modification
  
- **View**: 
  - `GameView` interface defines all view operations
  - `SwingGameView` implements the interface using Java Swing
  - `GamePanel` - Renders the world map with rooms, players, and target
  - `InfoPanel` - Displays game information and player status
  - View has no knowledge of Model implementation details
  
- **Controller**: 
  - `Features` interface defines controller capabilities for View callbacks
  - `GraphicalController` implements Features for GUI interactions
  - `TextController` continues to support text-based gameplay
  - Controllers translate user input into Model operations and update View

### **Dual Mode Support**
- **GUI Mode**: Full graphical interface with mouse and keyboard controls
- **Text Mode**: Original command-line interface preserved from Milestones 2-3
- Both modes share the same Model implementation
- Demonstrates proper MVC separation and reusability

---

## Milestone 3 Features
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

---

## How to Run the Game

### **GUI Mode (Milestone 4 - Recommended):**

#### Using the JAR file:
```bash
java -jar res/KillDoctorLucky.jar
```

#### From Eclipse:
1. Right-click `Main.java` (in `killdoctorlucky.controller` package)
2. Run As → Java Application

#### From source code:
```bash
# Compile
javac -d bin src/killdoctorlucky/**/*.java

# Run
java -cp bin killdoctorlucky.controller.Main
```

**Gameplay Steps:**
1. Launch the application (GUI window appears)
2. Click **File → New Game**
3. Select a world file (e.g., `res/demo-world.txt` or `res/mansion.txt`)
4. Enter maximum number of turns (e.g., 20 for demo-world, 30 for mansion)
5. Add 3-7 players (choose Human or Computer for each)
6. Select starting rooms for each player
7. Click "Start game now" when ready
8. Play using mouse clicks or keyboard shortcuts!

---

### **Text Mode (Milestone 2-3):**

#### Using the JAR file:
```bash
java -jar res/KillDoctorLucky.jar <world-file> <max-turns>
```

**Example**:
```bash
java -jar res/KillDoctorLucky.jar res/demo-world.txt 20
```

#### From Eclipse:
1. Right-click `GameDriver.java`
2. Run Configurations...
3. Program arguments: `res/demo-world.txt 20`
4. Run

#### From source code:
```bash
# Compile
javac -d bin src/killdoctorlucky/**/*.java

# Run
java -cp bin killdoctorlucky.controller.GameDriver res/demo-world.txt 20
```

**Parameters**:
- `<world-file>`: Path to the world specification file
- `<max-turns>`: Maximum number of turns allowed (must be a positive integer)

---

## GUI Controls Reference (Milestone 4)

### **Keyboard Shortcuts:**
| Key | Action | Ends Turn? |
|-----|--------|------------|
| **P** | Pick up item from current room | Yes |
| **L** | Look around (view adjacent rooms and occupants) | Yes |
| **A** | Attempt to murder Doctor Lucky | Yes |
| **M** | Move the pet to another room | Yes |
| **E** | End turn without taking an action | Yes |
| **H** | Show keyboard help dialog | No |

### **Mouse Controls:**
- **Click on a room** - Move your player to that room (must be adjacent)
- **Click on a player icon** - View detailed player information

### **Menu Bar:**
- **File → New Game** - Load a new world specification file
- **File → Restart Game** - Restart game with current world
- **File → Exit** - Close the application
- **Help → About** - Display welcome screen with game information

### **Visual Legend:**
- 🔴 **Red D** - Doctor Lucky
- 🔵 **Blue circle with letter** - Players (letter = first initial of name)
- 🟠 **Orange P** - Pet

---

## Text Mode Commands Reference

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
│   │   ├── Game.java (Implements ReadOnlyGameModel - Milestone 4)
│   │   ├── GameState.java (Immutable state snapshot - NEW Milestone 4)
│   │   ├── Board.java
│   │   ├── Room.java
│   │   ├── Item.java
│   │   ├── Deck.java
│   │   ├── GameStatus.java (Enum)
│   │   ├── MurderResult.java (Enum)
│   │   ├── occupants/
│   │   │   ├── Occupant.java (Interface)
│   │   │   ├── Player.java
│   │   │   ├── ComputerPlayer.java
│   │   │   ├── DoctorLucky.java
│   │   │   └── Pet.java
│   │   ├── cards/
│   │   │   ├── Playable.java (Interface)
│   │   │   ├── Card.java
│   │   │   ├── WeaponCard.java
│   │   │   ├── MoveCard.java
│   │   │   ├── RoomCard.java
│   │   │   └── FailureCard.java
│   │   └── interfaces/
│   │       ├── Movable.java
│   │       └── ReadOnlyGameModel.java (NEW - Milestone 4)
│   ├── view/ (NEW - Milestone 4)
│   │   ├── GameView.java (Interface)
│   │   ├── SwingGameView.java (Main GUI window)
│   │   ├── GamePanel.java (World map display)
│   │   └── InfoPanel.java (Game information display)
│   ├── controller/
│   │   ├── Features.java (Interface - NEW Milestone 4)
│   │   ├── GraphicalController.java (GUI controller - NEW Milestone 4)
│   │   ├── TextController.java (Text mode controller)
│   │   ├── GameDriver.java (Text mode entry point)
│   │   ├── Main.java (GUI mode entry point - NEW Milestone 4)
│   │   └── commands/
│   │       ├── Command.java (Interface)
│   │       ├── AddHumanPlayerCommand.java
│   │       ├── AddComputerPlayerCommand.java
│   │       ├── MoveCommand.java
│   │       ├── PickUpItemCommand.java
│   │       ├── LookAroundCommand.java
│   │       ├── MovePetCommand.java
│   │       ├── AttemptMurderCommand.java
│   │       ├── DisplayPlayerCommand.java
│   │       ├── DisplaySpaceCommand.java
│   │       └── CreateMapCommand.java
│   └── util/
│       ├── RandomGenerator.java
│       └── WorldParser.java
│
├── test/killdoctorlucky/
│   ├── model/
│   │   ├── GameTest.java
│   │   ├── BoardTest.java
│   │   ├── RoomTest.java
│   │   ├── ItemTest.java
│   │   ├── DeckTest.java
│   │   └── occupants/
│   │       ├── PlayerTest.java
│   │       ├── ComputerPlayerTest.java
│   │       ├── DoctorLuckyTest.java
│   │       └── PetTest.java
│   └── controller/
│       ├── TextControllerTest.java
│       ├── GraphicalControllerTest.java (NEW - Milestone 4)
│       └── commands/
│           ├── CommandTest.java
│           ├── MovePetCommandTest.java
│           └── AttemptMurderCommandTest.java
│
└── res/
    ├── KillDoctorLucky.jar (Runnable JAR - supports both GUI and text modes)
    ├── demo-world.txt (Demo world - 5 rooms, for quick testing)
    ├── mansion.txt (Full mansion - 20 rooms)
    ├── example-run-1-human-wins.txt (Text mode example)
    ├── example-run-2-computer-wins.txt (Text mode example)
    ├── example-run-3-doctor-escapes.txt (Text mode example)
    └── UML-Milestone4.pdf (Complete design document)
```

---

## Design Patterns and Architecture

### Model-View-Controller (MVC) Architecture

#### **Model Layer:**
- **Core Classes**: `Game`, `Board`, `Room`, `Player`, `DoctorLucky`, `Pet`, `Item`
- **Responsibilities**: 
  - Manages all game state, rules, and business logic
  - Handles murder mechanics, health system, visibility, and win conditions
  - Completely decoupled from View and Controller
- **Key Interface**: `ReadOnlyGameModel` 
  - Provides read-only access to game state for View layer
  - Prevents View from modifying Model directly
- **Immutable State**: `GameState` class
  - Snapshots of game state passed to View
  - Contains current player, turn count, locations, health, and action results

#### **View Layer (Milestone 4):**
- **Interface**: `GameView` defines all view operations
- **Implementation**: `SwingGameView` (Java Swing-based GUI)
  - `GamePanel` - Renders world map using room coordinates
  - `InfoPanel` - Displays game information and player status
- **Responsibilities**:
  - Display game state to user
  - Capture user input (mouse clicks, keyboard)
  - No game logic - only presentation
- **View Types**:
  - GUI View: `SwingGameView` for graphical interface
  - Text View: Console output via `Appendable` for text mode

#### **Controller Layer:**
- **Interface**: `Features` defines controller capabilities
- **Implementations**:
  - `GraphicalController` - Handles GUI interactions (Milestone 4)
  - `TextController` - Handles text-based commands (Milestone 2-3)
- **Responsibilities**:
  - Translate user actions into Model operations
  - Update View with new game state
  - Manage game flow and turn progression
  - Handle computer player automatic actions

### Command Pattern
- All player actions encapsulated as `Command` objects
- Decouples request from execution
- Enables easy addition of new commands
- Used in both text and GUI modes
- Commands: Move, PickUpItem, LookAround, MovePet, AttemptMurder, etc.

### Other Design Patterns
- **Strategy Pattern**: Different player types (Human vs Computer) with polymorphic behavior
- **Facade Pattern**: `Game` class simplifies complex subsystem interactions
- **Observer Pattern (Implicit)**: View refreshes through `refresh(GameState)` callbacks

### Key Design Decisions
- **Strict MVC Separation**: Model, View, Controller can be tested in isolation
- **Interface-Based Design**: Controllers and Views depend on interfaces, not concrete classes
- **Immutable State Transfer**: `GameState` prevents View from corrupting Model
- **Passive View**: View only displays data and forwards user input to Controller
- **Testable Randomness**: `RandomGenerator` wrapper enables predictable testing
- **Package Organization**: Clear separation by architectural layer and responsibility

---

## Game Rules

### Objective
Be the first player to successfully kill Doctor Lucky!

### How to Win
1. Be in the same room as Doctor Lucky
2. Ensure no other players can see you (no witnesses)
3. Attack with a weapon item or poke in the eye
4. Deal enough damage to reduce Doctor Lucky's health to 0

### Murder Attempt Rules
- **Witnesses**: If another player can see you (same room or adjacent room without pet blocking), the attack automatically fails
- **Pet Blocking**: A room with the pet cannot be seen by neighboring rooms
- **Damage**: 
  - Weapons deal their specified damage value
  - Poke in the eye deals 1 damage
- **Evidence**: Items used in murder attempts are removed from the game
- **Computer Players**: Automatically attempt murder when alone with Doctor Lucky, always using their highest damage weapon

### Game Ending
- **Victory**: A player successfully reduces Doctor Lucky's health to 0
- **Escape**: Maximum turns reached and Doctor Lucky survives (no winner declared)

### Automatic Movement (Each Turn)
- **Doctor Lucky**: Moves through rooms in the order they appear in the world file
- **Pet**: Wanders following Depth-First Search (DFS) pattern through all rooms

---

## World File Format
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

**Room Specification**: `<row1> <col1> <row2> <col2> <room name>`
- Coordinates are inclusive corners defining the room's rectangle

**Item Specification**: `<room index> <damage> <item name>`
- Room index refers to room order (0-indexed)

**Example** (`demo-world.txt`):
```
20 15 Demo Mansion
15 Doctor Lucky
Shadow the Cat
5
0 0 4 4 Start Room
5 0 9 4 Middle Room
...
```

---

## Testing

Comprehensive JUnit 4 test coverage for all components:

### Model Tests
- `GameTest.java` - Game flow, murder mechanics, pet management, visibility, win conditions, ReadOnlyGameModel interface
- `BoardTest.java` - Room connections, visibility calculations, pet blocking
- `RoomTest.java` - Room creation, occupants, items, geometry
- `PlayerTest.java` - Movement, inventory management, murder-related methods
- `DoctorLuckyTest.java` - Health system, damage, movement, death conditions
- `PetTest.java` - Pet creation, movement, DFS traversal, visibility blocking
- `ItemTest.java` - Item properties and room assignment
- `DeckTest.java` - Card deck operations, shuffle, draw, discard

### Controller Tests
- `TextControllerTest.java` - Text mode command parsing, game flow, turn limits
- `GraphicalControllerTest.java` - **NEW Milestone 4**: GUI controller with mock View
- `CommandTest.java` - All command implementations
- `MovePetCommandTest.java` - Pet movement command
- `AttemptMurderCommandTest.java` - Murder attempt command

### Utility Tests
- `WorldParserTest.java` - World file parsing including pet and health information
- `RandomGeneratorTest.java` - Predictable and true random behavior

**Note**: View components (SwingGameView, GamePanel, InfoPanel) are not tested as per Milestone 4 requirements (GUI components do not require unit tests).

---

## Example Runs (Text Mode)

All example runs are located in the `res/` directory and demonstrate Milestone 3 features in text mode:

### 1. example-run-1-human-wins.txt
**Demonstrates**: Human player victory, pet movement, visibility blocking, murder mechanics

### 2. example-run-2-computer-wins.txt
**Demonstrates**: Computer player AI, automatic murder attempt, strategic weapon selection

### 3. example-run-3-doctor-escapes.txt
**Demonstrates**: Maximum turn limit, Pet DFS pattern over multiple cycles, game timeout

**Pet DFS Pattern**: Start Room → Middle Room → End Room → Far Room 2 → Far Room 1 → (repeat)

---

## Known Limitations

### Features Implemented in Model but Not in GUI:
- **Card System**: Deck and hand management (WeaponCard, MoveCard, RoomCard, FailureCard) are fully implemented in the Model and functional in text mode, but not yet integrated into the GUI interface. Players do not draw or play cards in GUI mode.

### Design Decisions:
- **Pet Visibility**: The pet is displayed on the GUI map for enhanced gameplay feedback, though Milestone 4 requirements specify showing only the target character and players. This is an intentional enhancement.
- **Dual Play Modes**: Both GUI and text modes are fully supported, demonstrating proper MVC architecture where multiple Controllers and Views can work with the same Model.

---

## Dependencies
- Java 11 or higher
- JUnit 4 (for testing)
- Java Swing (included in JDK)

---

## Design Documents
- `res/UML-Milestone4.pdf` - Complete UML class diagram showing MVC architecture, interfaces, and relationships
- Design includes Model refactoring for ReadOnlyGameModel, View interface design, and Controller design with Features interface

---

## Credits
- **Author**: Zihao Wang
- **Course**: CS5010 Programming Design Paradigms
- **Institution**: Northeastern University Vancouver
- **Semester**: Fall 2025

---

## Version History
- **Milestone 4**: Implemented graphical user interface with complete MVC architecture, dual mode support
- **Milestone 3**: Added pet system, murder mechanics, health system, and game win conditions
- **Milestone 2**: Added interactive gameplay, player management, turn-based system, and commands
- **Milestone 1**: Initial world model, parsing, and basic game structure

## Author
Zihao Wang - Northeastern University Vancouver

## Project Overview
This is an implementation of the "Kill Doctor Lucky" board game for CS5010 at Northeastern University. The project now includes both a **graphical user interface (GUI)** and the original text-based interface, implementing a complete MVC (Model-View-Controller) architecture.

---

## Milestone 4 Features (NEW - GUI Implementation)

### **Graphical User Interface**
- **Welcome Screen**: Displays game information, controls, and credits
- **Main Game Window**: 
  - Visual representation of the mansion with rooms displayed in a grid layout
  - Real-time display of players, Doctor Lucky, and the pet
  - Scrollable view for large maps
- **Information Panel**: Shows current player, turn count, Doctor Lucky's health, player inventory, and last action
- **Menu Bar**: File menu for starting new games, restarting, and exiting

### **User Interactions**
- **Mouse Controls**:
  - Click on rooms to move your player
  - Click on player icons to view detailed player information
- **Keyboard Shortcuts**:
  - **P** - Pick up item from current room
  - **L** - Look around (view surroundings)
  - **A** - Attempt murder on Doctor Lucky
  - **M** - Move the pet to another room
  - **E** - End turn (skip your turn)
  - **H** - Show keyboard help

### **MVC Architecture**
- **Model**: `Game` implements `ReadOnlyGameModel` interface
  - Provides read-only access to game state
  - `GameState` class provides immutable snapshots for the View
- **View**: `GameView` interface with `SwingGameView` implementation
  - `GamePanel` - Displays the world map
  - `InfoPanel` - Displays game information
- **Controller**: `Features` interface with `GraphicalController` implementation
  - Handles user input from GUI
  - Updates Model and refreshes View

---

## Milestone 3 Features
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
- **Enhanced Computer Player AI**: Computer players automatically attempt murder when conditions are favorable

### Milestone 2 Features
- Player Management (human and computer players)
- Interactive text-based gameplay
- Movement, item pickup, look around commands
- Turn-based system with automated computer players
- Game information display
- Map generation (PNG export)
- Turn limits

### Milestone 1 Features
- World specification file parsing
- Room creation with coordinates and connections
- Item placement in rooms
- Doctor Lucky movement sequence
- Sight line system for custom visibility rules

---

## How to Run the Game

### **GUI Mode (Milestone 4 - Recommended):**

#### Using the JAR file:
```bash
java -jar res/KillDoctorLucky.jar
```

#### From source:
```bash
# Compile
javac -d bin src/killdoctorlucky/**/*.java

# Run
java -cp bin killdoctorlucky.controller.Main
```

**Steps:**
1. Launch the application
2. Click **File → New Game**
3. Select a world file (e.g., `res/demo-world.txt` or `res/mansion.txt`)
4. Enter maximum number of turns (e.g., 20)
5. Add 3-7 players (human or computer)
6. Play using mouse clicks or keyboard shortcuts!

---

### **Text Mode (Milestone 2-3):**

#### Using the JAR file:
```bash
java -jar res/KillDoctorLucky.jar <world-file> <max-turns>
```

**Example**:
```bash
java -jar res/KillDoctorLucky.jar res/demo-world.txt 20
```

#### From source:
```bash
java -cp bin killdoctorlucky.controller.GameDriver res/demo-world.txt 20
```

**Parameters**:
- `<world-file>`: Path to the world specification file
- `<max-turns>`: Maximum number of turns allowed (must be a positive integer)

---

## GUI Controls Reference

### **Keyboard Shortcuts:**
| Key | Action |
|-----|--------|
| **P** | Pick up item from current room |
| **L** | Look around (view adjacent rooms and occupants) |
| **A** | Attempt to murder Doctor Lucky |
| **M** | Move the pet to another room |
| **E** | End turn without taking an action |
| **H** | Show keyboard help |

### **Mouse Controls:**
- **Click on a room** - Move your player to that room (if adjacent)
- **Click on a player icon** - View detailed player information

### **Menu Bar:**
- **File → New Game** - Load a new world specification
- **File → Restart Game** - Restart with current world
- **File → Exit** - Close the application
- **Help → About** - Show welcome screen with controls

---

## Text Mode Commands Reference

Type `help` in the game to see all available commands.

### Main Actions (ends turn automatically):
- `look` - Look around current room
- `move <roomName>` - Move to an adjacent room
- `pickup <itemName>` - Pick up an item from current room
- `movepet <roomName>` - Move the pet to any specified room
- `attack [itemName]` - Attempt to murder Doctor Lucky
- `endturn` - Skip your turn

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
│   │   ├── Game.java (Implements ReadOnlyGameModel)
│   │   ├── GameState.java (Immutable state snapshot for View)
│   │   ├── Board.java
│   │   ├── Room.java
│   │   ├── Item.java
│   │   ├── Deck.java
│   │   ├── GameStatus.java (Enum)
│   │   ├── MurderResult.java (Enum)
│   │   ├── occupants/
│   │   │   ├── Occupant.java (Interface)
│   │   │   ├── Player.java
│   │   │   ├── ComputerPlayer.java
│   │   │   ├── DoctorLucky.java
│   │   │   └── Pet.java
│   │   ├── cards/
│   │   │   ├── Playable.java (Interface)
│   │   │   ├── Card.java
│   │   │   ├── WeaponCard.java
│   │   │   ├── MoveCard.java
│   │   │   ├── RoomCard.java
│   │   │   └── FailureCard.java
│   │   └── interfaces/
│   │       ├── Movable.java
│   │       └── ReadOnlyGameModel.java (NEW - Milestone 4)
│   ├── view/
│   │   ├── GameView.java (Interface - NEW - Milestone 4)
│   │   ├── SwingGameView.java (NEW - Milestone 4)
│   │   ├── GamePanel.java (NEW - Milestone 4)
│   │   └── InfoPanel.java (NEW - Milestone 4)
│   ├── controller/
│   │   ├── Features.java (Interface - NEW - Milestone 4)
│   │   ├── GraphicalController.java (NEW - Milestone 4)
│   │   ├── TextController.java (Milestone 2-3)
│   │   ├── GameDriver.java (Text mode entry point)
│   │   ├── Main.java (GUI mode entry point - NEW - Milestone 4)
│   │   └── commands/
│   │       ├── Command.java (Interface)
│   │       ├── MoveCommand.java
│   │       ├── PickUpItemCommand.java
│   │       ├── LookAroundCommand.java
│   │       ├── MovePetCommand.java
│   │       ├── AttemptMurderCommand.java
│   │       ├── AddHumanPlayerCommand.java
│   │       ├── AddComputerPlayerCommand.java
│   │       ├── DisplayPlayerCommand.java
│   │       ├── DisplaySpaceCommand.java
│   │       └── CreateMapCommand.java
│   └── util/
│       ├── RandomGenerator.java
│       └── WorldParser.java
│
├── test/killdoctorlucky/
│   ├── model/
│   │   ├── GameTest.java
│   │   ├── BoardTest.java
│   │   ├── RoomTest.java
│   │   ├── ItemTest.java
│   │   ├── DeckTest.java
│   │   └── occupants/
│   │       ├── PlayerTest.java
│   │       ├── ComputerPlayerTest.java
│   │       ├── DoctorLuckyTest.java
│   │       └── PetTest.java
│   └── controller/
│       ├── TextControllerTest.java
│       ├── GraphicalControllerTest.java (NEW - Milestone 4)
│       └── commands/
│           └── (various command tests)
│
└── res/
    ├── KillDoctorLucky.jar (Runnable JAR - GUI mode)
    ├── demo-world.txt (Demo world for testing)
    ├── mansion.txt (Full mansion world)
    ├── example-run-*.txt (Text mode example runs)
    └── UML-Milestone4.pdf (Design document)
```

---

## Design Patterns and Architecture

### Model-View-Controller (MVC) Architecture
- **Model**: 
  - `Game` class implements `ReadOnlyGameModel` interface
  - `GameState` provides immutable snapshots to View
  - Manages all game logic, rules, and state
  - No knowledge of View or Controller
  
- **View**: 
  - `GameView` interface defines all view operations
  - `SwingGameView` implements the interface using Java Swing
  - `GamePanel` displays the world map
  - `InfoPanel` displays game information
  - No knowledge of Model implementation
  
- **Controller**: 
  - `Features` interface defines controller capabilities
  - `GraphicalController` implements Features
  - Mediates between Model and View
  - Handles user input and updates both Model and View

### Command Pattern
- All player actions encapsulated as Command objects
- Enables easy addition of new commands
- Used in both text and GUI modes

### Observer Pattern (Implicit)
- View refreshes through `refresh(GameState)` calls
- Controller notifies View of state changes
- Passive View design

---

## Game Rules

### Objective
Be the first player to successfully kill Doctor Lucky!

### How to Win
1. Be in the same room as Doctor Lucky
2. Ensure no other players can see you (no witnesses)
3. Attack with a weapon or poke Doctor Lucky in the eye
4. Deal enough damage to reduce his health to 0

### How to Lose
- Maximum turns reached without killing Doctor Lucky
- Doctor Lucky escapes!

### Key Mechanics
- **Pet Blocking**: The pet makes its current room invisible to neighboring rooms
- **Witness Detection**: Murder attempts fail if other players can see you
- **Automatic Movement**: Doctor Lucky and the pet move automatically each turn
- **Computer Players**: AI players make strategic decisions automatically

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

---

## Known Limitations

### Features Implemented in Model but Not in GUI:
- **Card System**: Deck and hand management are fully implemented in the Model and work in text mode, but are not yet integrated into the GUI interface. Players do not draw or play cards in GUI mode.

### Design Decisions:
- **Pet Display**: The pet is displayed on the map in GUI mode, though Milestone 4 requirements specify only showing the target character. This is an intentional enhancement for better gameplay visibility.
- **Two Play Modes**: The game supports both GUI and text modes, each using the same underlying Model but with different Controllers and Views.

---

## Dependencies
- Java 11 or higher
- JUnit 4 (for testing)
- Java Swing (included in JDK)

---

## Testing
The project includes comprehensive JUnit tests for:
- All Model classes (Game, Board, Room, Player, DoctorLucky, Pet, etc.)
- Both Controllers (TextController and GraphicalController)
- Command implementations

Run tests in Eclipse: Right-click on `test` folder → Run As → JUnit Test

**Note**: View components (SwingGameView, GamePanel, InfoPanel) are not tested as per Milestone 4 requirements.

---

## Design Documents
- `res/UML-Milestone4.pdf` - Complete UML class diagram showing MVC architecture
- Design documents include Model refactoring, View design, and Controller design

---

## Example Usage

### GUI Mode:
1. Run `java -jar res/KillDoctorLucky.jar`
2. File → New Game
3. Select `res/mansion.txt`
4. Enter max turns: `30`
5. Add players (mix of human and computer)
6. Use mouse and keyboard to play!

### Text Mode:
```bash
java -jar res/KillDoctorLucky.jar res/demo-world.txt 15
```

---

## Credits
- **Author**: Zihao Wang
- **Course**: CS5010 Programming Design Paradigms
- **Institution**: Northeastern University Vancouver
- **Semester**: Fall 2024

---

## Version History
- **Milestone 4**: Added graphical user interface with full MVC architecture
- **Milestone 3**: Added pet system, murder mechanics, and game win conditions
- **Milestone 2**: Added interactive gameplay, player management, and turn-based system
- **Milestone 1**: Initial world parsing and model implementation
