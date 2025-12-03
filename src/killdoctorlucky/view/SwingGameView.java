package killdoctorlucky.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import killdoctorlucky.controller.Features;
import killdoctorlucky.model.GameState;

/**
 * Swing-based implementation of the GameView interface.
 * This class creates and manages the main game window with all GUI components.
 * 
 * <p>The window consists of:
 * - Menu bar (top) for game control
 * - GamePanel (center) for displaying the world
 * - InfoPanel (right) for displaying game information</p>
 */
public class SwingGameView extends JFrame implements GameView {
  
  private static final long serialVersionUID = 1L;
  
  private Features features;
  private GamePanel gamePanel;
  private InfoPanel infoPanel;
  private JMenuBar menuBar;
  
  /**
   * Constructs a new SwingGameView with default settings.
   * Initializes all GUI components and sets up the window.
   */
  public SwingGameView() {
    super("Kill Doctor Lucky");
    
    // Set up the main frame
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    setMinimumSize(new Dimension(800, 600));
    
    // Initialize components
    initializeComponents();
    
    // Add keyboard listener
    addKeyboardListener();
    
    // Pack and center the window
    pack();
    setLocationRelativeTo(null);
  }
  
  /**
   * Initializes all GUI components.
   */
  private void initializeComponents() {
    // Create menu bar
    initializeMenuBar();
    
    // Create game panel (center - shows the world)
    gamePanel = new GamePanel();
    JScrollPane scrollPane = new JScrollPane(gamePanel);
    scrollPane.setPreferredSize(new Dimension(600, 500));
    add(scrollPane, BorderLayout.CENTER);
    
    // Create info panel (right side - shows game info)
    infoPanel = new InfoPanel();
    infoPanel.setPreferredSize(new Dimension(250, 500));
    add(infoPanel, BorderLayout.EAST);
  }
  
  /**
   * Initializes the menu bar with all menus and items.
   */
  private void initializeMenuBar() {
    menuBar = new JMenuBar();
    
    // File menu
    JMenu fileMenu = new JMenu("File");
    
    JMenuItem newGameItem = new JMenuItem("New Game");
    newGameItem.addActionListener(e -> {
      if (features != null) {
        features.startNewGame();
      }
    });
    fileMenu.add(newGameItem);
    
    JMenuItem restartItem = new JMenuItem("Restart Game");
    restartItem.addActionListener(e -> {
      if (features != null) {
        features.restartGame();
      }
    });
    fileMenu.add(restartItem);
    
    fileMenu.addSeparator();
    
    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(e -> {
      if (features != null) {
        features.exitGame();
      }
    });
    fileMenu.add(exitItem);
    
    menuBar.add(fileMenu);
    
    // Help menu
    JMenu helpMenu = new JMenu("Help");
    
    JMenuItem aboutItem = new JMenuItem("About");
    aboutItem.addActionListener(e -> showWelcomeScreen());
    helpMenu.add(aboutItem);
    
    menuBar.add(helpMenu);
    
    setJMenuBar(menuBar);
  }
  
  /**
   * Adds keyboard listener for game controls.
   */
  private void addKeyboardListener() {
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (features == null) {
          return;
        }
        
        switch (e.getKeyCode()) {
          case KeyEvent.VK_P:
            // Pick up item
            features.pickUpItem(null);
            break;
          case KeyEvent.VK_L:
            // Look around
            features.lookAround();
            break;
          case KeyEvent.VK_A:
            // Attempt murder
            features.attemptMurder(null);
            break;
          case KeyEvent.VK_M:
            // Move pet
            features.movePet(null);
            break;
          case KeyEvent.VK_E:
            // End turn (skip turn)
            features.endTurn();
            break;
          case KeyEvent.VK_H:
            // Show help
            showKeyboardHelp();
            break;
          default:
            break;
        }
      }
    });
    
    // Make sure the frame can receive key events
    setFocusable(true);
    requestFocusInWindow();
  }
  
  /**
   * Shows keyboard help dialog.
   */
  private void showKeyboardHelp() {
    String helpText = "Keyboard Controls:\n\n"
        + "P - Pick up item\n"
        + "L - Look around\n"
        + "A - Attempt murder\n"
        + "M - Move pet\n"
        + "E - End turn (skip)\n"
        + "H - Show this help\n\n"
        + "You can also click on rooms to move your player.";
    
    JOptionPane.showMessageDialog(this, helpText, "Keyboard Help",
        JOptionPane.INFORMATION_MESSAGE);
  }
  
  @Override
  public void setFeatures(Features featuresCallback) {
    if (featuresCallback == null) {
      throw new IllegalArgumentException("Features cannot be null");
    }
    this.features = featuresCallback;
    
    // Pass features to panels that need them
    if (gamePanel != null) {
      gamePanel.setFeatures(featuresCallback);
    }
  }
  
  @Override
  public void setBoard(killdoctorlucky.model.Board board) {
    if (gamePanel != null) {
      gamePanel.setBoard(board);
    }
  }
  
  @Override
  public void refresh(GameState state) {
    if (state == null) {
      throw new IllegalArgumentException("GameState cannot be null");
    }
    
    // Update game panel
    if (gamePanel != null) {
      gamePanel.updateGameState(state);
    }
    
    // Update info panel
    if (infoPanel != null) {
      infoPanel.updateGameInfo(state);
    }
    
    // Repaint everything
    repaint();
  }
  
  @Override
  public void makeVisible() {
    setVisible(true);
  }
  
  @Override
  public void showWelcomeScreen() {
    String message = "Kill Doctor Lucky\n\n"
        + "Created by: Zihao Wang\n"
        + "Course: CS5010 - Northeastern University\n\n"
        + "Game Rules:\n"
        + "- Move through the mansion to find Doctor Lucky\n"
        + "- Pick up items to use as weapons\n"
        + "- Attempt to murder Doctor Lucky when alone\n"
        + "- Avoid witnesses to succeed!\n\n"
        + "Keyboard Controls:\n"
        + "- P: Pick up item\n"
        + "- L: Look around\n"
        + "- A: Attempt murder\n"
        + "- M: Move pet\n"
        + "- E: End turn (skip)\n"
        + "- H: Show help\n\n"
        + "Mouse Controls:\n"
        + "- Click on rooms to move";
    
    JOptionPane.showMessageDialog(this, message, "About Kill Doctor Lucky",
        JOptionPane.INFORMATION_MESSAGE);
  }
  
  @Override
  public void showMessage(String message, String title, MessageType messageType) {
    int optionPaneType;
    switch (messageType) {
      case ERROR:
        optionPaneType = JOptionPane.ERROR_MESSAGE;
        break;
      case WARNING:
        optionPaneType = JOptionPane.WARNING_MESSAGE;
        break;
      case SUCCESS:
        optionPaneType = JOptionPane.INFORMATION_MESSAGE;
        break;
      case INFO:
      default:
        optionPaneType = JOptionPane.INFORMATION_MESSAGE;
        break;
    }
    
    JOptionPane.showMessageDialog(this, message, title, optionPaneType);
  }
  
  @Override
  public String getPlayerName(String prompt) {
    String name = JOptionPane.showInputDialog(this, prompt, "Enter Player Name",
        JOptionPane.QUESTION_MESSAGE);
    
    if (name != null) {
      name = name.trim();
      if (name.isEmpty()) {
        return null;
      }
    }
    
    return name;
  }
  
  @Override
  public String selectStartingRoom(String[] availableRooms) {
    if (availableRooms == null || availableRooms.length == 0) {
      return null;
    }
    
    String selection = (String) JOptionPane.showInputDialog(
        this,
        "Select a starting room:",
        "Starting Room",
        JOptionPane.QUESTION_MESSAGE,
        null,
        availableRooms,
        availableRooms[0]
    );
    
    return selection;
  }
  
  @Override
  public String selectItem(String[] availableItems) {
    if (availableItems == null || availableItems.length == 0) {
      showMessage("No items available in this room.", "No Items", MessageType.INFO);
      return null;
    }
    
    String selection = (String) JOptionPane.showInputDialog(
        this,
        "Select an item to pick up:",
        "Pick Up Item",
        JOptionPane.QUESTION_MESSAGE,
        null,
        availableItems,
        availableItems[0]
    );
    
    return selection;
  }
  
  @Override
  public String selectRoomForPet(String[] availableRooms) {
    if (availableRooms == null || availableRooms.length == 0) {
      return null;
    }
    
    String selection = (String) JOptionPane.showInputDialog(
        this,
        "Select a room to move the pet to:",
        "Move Pet",
        JOptionPane.QUESTION_MESSAGE,
        null,
        availableRooms,
        availableRooms[0]
    );
    
    return selection;
  }
  
  @Override
  public String selectWeapon(String[] availableWeapons) {
    // Add "Poke in eye" option
    String[] options;
    if (availableWeapons != null && availableWeapons.length > 0) {
      options = new String[availableWeapons.length + 1];
      System.arraycopy(availableWeapons, 0, options, 0, availableWeapons.length);
      options[availableWeapons.length] = "Poke in eye (1 damage)";
    } else {
      options = new String[] { "Poke in eye (1 damage)" };
    }
    
    String selection = (String) JOptionPane.showInputDialog(
        this,
        "Select a weapon for the murder attempt:",
        "Attempt Murder",
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]
    );
    
    // Return "poke" if user selected poke in eye
    if (selection != null && selection.startsWith("Poke in eye")) {
      return "poke";
    }
    
    return selection;
  }
  
  @Override
  public void resetView() {
    // Clear both panels
    if (gamePanel != null) {
      gamePanel.reset();
    }
    if (infoPanel != null) {
      infoPanel.reset();
    }
    repaint();
  }
}