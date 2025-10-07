package killdoctorlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Player functionality. Tests player creation, movement, card
 * management, and interface implementations.
 */
public class PlayerTest {
  private Player alice;
  private Player bob;
  private Room kitchen;
  private Room diningRoom;
  private Room library;
  private WeaponCard knife;
  private MoveCard quickStep;
  private Board board;

  /**
   * Sets up the test fixture before each test.
   * Initializes Room objects (kitchen, diningRoom, library) with connections between them,
   * creates Player instances (alice, bob), WeaponCard and MoveCard objects,
   * and sets up a Board with added rooms and connections.
   */
  
  @Before
  public void setUp() {
    kitchen = new Room("Kitchen", true);
    diningRoom = new Room("Dining Room", true);
    library = new Room("Library", true);

    // Connect rooms
    kitchen.addConnection(diningRoom);
    diningRoom.addConnection(library);

    alice = new Player("Alice", kitchen);
    bob = new Player("Bob", diningRoom);

    knife = new WeaponCard("Knife", 5);
    quickStep = new MoveCard("Quick Step", 1);

    board = new Board();
    board.addRoom(kitchen);
    board.addRoom(diningRoom);
    board.addRoom(library);
    board.connectRooms(kitchen, diningRoom);
    board.connectRooms(diningRoom, library);
  }

  @Test
  public void testValidPlayerCreation() {
    Player charlie = new Player("Charlie", library);
    assertEquals("Charlie", charlie.getName());
    assertEquals(library, charlie.getCurrentRoom());
    assertTrue(library.isOccupiedBy(charlie));
  }

  @Test
  public void testInvalidPlayerCreation() {
    assertThrows(IllegalArgumentException.class, () -> new Player(null, kitchen));
    assertThrows(IllegalArgumentException.class, () -> new Player("", kitchen));
    assertThrows(IllegalArgumentException.class, () -> new Player("Bob", null));
  }

  @Test
  public void testValidMovement() {
    assertEquals(kitchen, alice.getCurrentRoom());
    assertTrue(alice.moveToRoom(diningRoom));
    assertEquals(diningRoom, alice.getCurrentRoom());
    assertTrue(diningRoom.isOccupiedBy(alice));
    assertFalse(kitchen.isOccupiedBy(alice));
  }

  @Test
  public void testInvalidMovement() {
    // Try to move to unconnected room
    assertFalse(alice.moveToRoom(library));
    assertEquals(kitchen, alice.getCurrentRoom());
  }

  @Test
  public void testMoveToNullRoom() {
    assertThrows(IllegalArgumentException.class, () -> alice.moveToRoom(null));
  }

  @Test
  public void testAddCard() {
    List<Playable> hand = alice.getHand();
    int initialSize = hand.size();

    alice.addCard(knife);
    assertEquals(initialSize + 1, alice.getHand().size());
    assertTrue(alice.getHand().contains(knife));
  }

  @Test
  public void testAddNullCard() {
    assertThrows(IllegalArgumentException.class, () -> alice.addCard(null));
  }

  @Test
  public void testRemoveCard() {
    alice.addCard(knife);
    assertTrue(alice.getHand().contains(knife));

    assertTrue(alice.removeCard(knife));
    assertFalse(alice.getHand().contains(knife));
  }

  @Test
  public void testRemoveNonExistentCard() {
    assertFalse(alice.removeCard(knife));
  }

  @Test
  public void testRemoveNullCard() {
    assertThrows(IllegalArgumentException.class, () -> alice.removeCard(null));
  }

  @Test
  public void testPlayCard() {
    alice.addCard(knife);
    assertTrue(alice.getHand().contains(knife));

    alice.playCard(knife);
    assertFalse(alice.getHand().contains(knife));
  }

  @Test
  public void testPlayCardNotInHand() {
    assertThrows(IllegalArgumentException.class, () -> alice.playCard(knife));
  }

  @Test
  public void testPlayNullCard() {
    assertThrows(IllegalArgumentException.class, () -> alice.playCard(null));
  }

  @Test
  public void testHasWeapons() {
    assertFalse(alice.hasWeapons());

    alice.addCard(quickStep);
    assertFalse(alice.hasWeapons());

    alice.addCard(knife);
    assertTrue(alice.hasWeapons());
  }

  @Test
  public void testCanDrawCard() {
    // Based on game rules, players can always draw cards
    assertTrue(alice.canDrawCard());
  }

  @Test
  public void testCanBeSeenBySameRoom() {
    // Move bob to same room as alice
    bob.moveToRoom(kitchen);
    assertTrue(alice.canBeSeenBy(bob, board));
  }

  @Test
  public void testCanBeSeenByNullOccupant() {
    assertThrows(IllegalArgumentException.class, () -> alice.canBeSeenBy(null, board));
  }

  @Test
  public void testCanBeSeenByNullBoard() {
    assertThrows(IllegalArgumentException.class, () -> alice.canBeSeenBy(bob, null));
  }

  @Test
  public void testGetHandDefensiveCopy() {
    alice.addCard(knife);
    List<Playable> hand1 = alice.getHand();
    List<Playable> hand2 = alice.getHand();

    // Should be different list instances but same contents
    assertNotSame(hand1, hand2);
    assertEquals(hand1, hand2);

    // Modifying returned list shouldn't affect player's actual hand
    hand1.clear();
    assertTrue(alice.getHand().contains(knife));
  }

  @Test
  public void testToString() {
    String result = alice.toString();
    assertTrue(result.contains("Alice"));
    assertTrue(result.contains("Kitchen"));
  }

  @Test
  public void testEquals() {
    Player anotherAlice = new Player("Alice", diningRoom);
    assertEquals(alice, anotherAlice);
    assertNotEquals(alice, bob);
  }
}