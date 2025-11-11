package killdoctorlucky.controller.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import killdoctorlucky.model.Board;
import killdoctorlucky.model.Deck;
import killdoctorlucky.model.Game;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.occupants.DoctorLucky;
import killdoctorlucky.model.occupants.Pet;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for MovePetCommand (Milestone 3).
 * Tests the command to move the pet to a specified room.
 */
public class MovePetCommandTest {
  
  private Game game;
  private Board board;
  private Room room1;
  private Room room2;
  private Room room3;
  private Pet pet;
  private StringBuilder output;
  
  /**
   * Sets up the test fixture.
   * Creates a game with multiple rooms and a pet for testing.
   */
  @Before
  public void setUp() {
    board = new Board();
    
    room1 = new Room("Kitchen");
    room1.setGeometry(0, 0, 5, 5);
    
    room2 = new Room("Dining Room");
    room2.setGeometry(6, 0, 10, 5);
    
    room3 = new Room("Library");
    room3.setGeometry(0, 6, 5, 10);
    
    board.addRoom(room1);
    board.addRoom(room2);
    board.addRoom(room3);
    
    board.connectRooms(room1, room2);
    board.connectRooms(room2, room3);
    
    DoctorLucky doctor = new DoctorLucky(room1, 50);
    Deck deck = new Deck();
    game = new Game(board, deck, doctor);
    
    pet = new Pet("Fortune the Cat", room1);
    game.setPet(pet);
    
    output = new StringBuilder();
  }
  
  /**
   * Tests MovePetCommand creation.
   */
  @Test
  public void testMovePetCommandCreation() {
    MovePetCommand cmd = new MovePetCommand("Library");
    assertNotNull(cmd);
  }
  
  /**
   * Tests constructor with null room throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePetCommandNullRoomName() {
    new MovePetCommand(null);
  }
  
  /**
   * Tests constructor with empty room throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePetCommandEmptyRoomName() {
    new MovePetCommand("");
  }
  
  /**
   * Tests constructor with whitespace room throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePetCommandWhitespaceRoomName() {
    new MovePetCommand("   ");
  }
  
  /**
   * Tests executing move pet to valid room.
   */
  @Test
  public void testExecuteMovePetToValidRoom() {
    assertEquals(room1, pet.getCurrentRoom());
    
    MovePetCommand cmd = new MovePetCommand("Dining Room");
    cmd.execute(game, output);
    
    assertEquals(room2, pet.getCurrentRoom());
    assertTrue(output.toString().contains("Moved the pet"));
    assertTrue(output.toString().contains("Fortune the Cat"));
    assertTrue(output.toString().contains("Dining Room"));
  }
  
  /**
   * Tests executing move pet to another room.
   */
  @Test
  public void testExecuteMovePetToAnotherRoom() {
    MovePetCommand cmd1 = new MovePetCommand("Library");
    cmd1.execute(game, output);
    
    assertEquals(room3, pet.getCurrentRoom());
    
    output.setLength(0);
    
    MovePetCommand cmd2 = new MovePetCommand("Kitchen");
    cmd2.execute(game, output);
    
    assertEquals(room1, pet.getCurrentRoom());
  }
  
  /**
   * Tests executing move pet to invalid room.
   */
  @Test
  public void testExecuteMovePetToInvalidRoom() {
    assertEquals(room1, pet.getCurrentRoom());
    
    MovePetCommand cmd = new MovePetCommand("NonExistentRoom");
    cmd.execute(game, output);
    
    assertEquals(room1, pet.getCurrentRoom());
    assertTrue(output.toString().contains("does not exist"));
  }
  
  /**
   * Tests executing when no pet exists.
   */
  @Test
  public void testExecuteMovePetWhenNoPet() {
    Board testBoard = new Board();
    Room testRoom = new Room("Kitchen");
    testBoard.addRoom(testRoom);
    
    DoctorLucky testDoctor = new DoctorLucky(testRoom, 50);
    Deck testDeck = new Deck();
    Game testGame = new Game(testBoard, testDeck, testDoctor);
    
    MovePetCommand cmd = new MovePetCommand("Kitchen");
    cmd.execute(testGame, output);
    
    assertTrue(output.toString().contains("No pet"));
  }
  
  /**
   * Tests execute with null game throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testExecuteNullGame() {
    MovePetCommand cmd = new MovePetCommand("Library");
    cmd.execute(null, output);
  }
  
  /**
   * Tests execute with null output throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testExecuteNullOutput() {
    MovePetCommand cmd = new MovePetCommand("Library");
    cmd.execute(game, null);
  }
  
  /**
   * Tests get description returns valid string.
   */
  @Test
  public void testGetDescription() {
    MovePetCommand cmd = new MovePetCommand("Library");
    String desc = cmd.getDescription();
    
    assertNotNull(desc);
    assertFalse(desc.isEmpty());
  }
}