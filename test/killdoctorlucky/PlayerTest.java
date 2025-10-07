package killdoctorlucky;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for Player functionality.
 * Tests player creation, movement, card management, and interface implementations.
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
    
    @Before
    void setUp() {
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
    }
    
    @Test
    void testValidPlayerCreation() {
        Player charlie = new Player("Charlie", library);
        assertEquals("Charlie", charlie.getName());
        assertEquals(library, charlie.getCurrentRoom());
        assertTrue(library.isOccupiedBy(charlie));
    }
    
    @Test
    void testInvalidPlayerCreation() {
        assertThrows(IllegalArgumentException.class, () -> new Player(null, kitchen));
        assertThrows(IllegalArgumentException.class, () -> new Player("", kitchen));
        assertThrows(IllegalArgumentException.class, () -> new Player("Bob", null));
    }
    
    @Test
    void testValidMovement() {
        assertEquals(kitchen, alice.getCurrentRoom());
        assertTrue(alice.moveToRoom(diningRoom));
        assertEquals(diningRoom, alice.getCurrentRoom());
        assertTrue(diningRoom.isOccupiedBy(alice));
        assertFalse(kitchen.isOccupiedBy(alice));
    }
    
    @Test
    void testInvalidMovement() {
        // Try to move to unconnected room
        assertFalse(alice.moveToRoom(library));
        assertEquals(kitchen, alice.getCurrentRoom());
    }
    
    @Test
    void testMoveToNullRoom() {
        assertThrows(IllegalArgumentException.class, () -> alice.moveToRoom(null));
    }
    
    @Test
    void testAddCard() {
        List<Playable> hand = alice.getHand();
        int initialSize = hand.size();
        
        alice.addCard(knife);
        assertEquals(initialSize + 1, alice.getHand().size());
        assertTrue(alice.getHand().contains(knife));
    }
    
    @Test
    void testAddNullCard() {
        assertThrows(IllegalArgumentException.class, () -> alice.addCard(null));
    }
    
    @Test
    void testRemoveCard() {
        alice.addCard(knife);
        assertTrue(alice.getHand().contains(knife));
        
        assertTrue(alice.removeCard(knife));
        assertFalse(alice.getHand().contains(knife));
    }
    
    @Test
    void testRemoveNonExistentCard() {
        assertFalse(alice.removeCard(knife));
    }
    
    @Test
    void testRemoveNullCard() {
        assertThrows(IllegalArgumentException.class, () -> alice.removeCard(null));
    }
    
    @Test
    void testPlayCard() {
        alice.addCard(knife);
        assertTrue(alice.getHand().contains(knife));
        
        alice.playCard(knife);
        assertFalse(alice.getHand().contains(knife));
    }
    
    @Test
    void testPlayCardNotInHand() {
        assertThrows(IllegalArgumentException.class, () -> alice.playCard(knife));
    }
    
    @Test
    void testPlayNullCard() {
        assertThrows(IllegalArgumentException.class, () -> alice.playCard(null));
    }
    
    @Test
    void testHasWeapons() {
        assertFalse(alice.hasWeapons());
        
        alice.addCard(quickStep);
        assertFalse(alice.hasWeapons());
        
        alice.addCard(knife);
        assertTrue(alice.hasWeapons());
    }
    
    @Test
    void testCanDrawCard() {
        // Based on game rules, players can always draw cards
        assertTrue(alice.canDrawCard());
    }
    
    @Test
    void testCanBeSeenBySameRoom() {
        // Move bob to same room as alice
        bob.moveToRoom(kitchen);
        assertTrue(alice.canBeSeenBy(bob, board));
    }
    
    @Test
    void testCanBeSeenByNullOccupant() {
        assertThrows(IllegalArgumentException.class, () -> alice.canBeSeenBy(null, board));
    }
    
    @Test
    void testCanBeSeenByNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> alice.canBeSeenBy(bob, null));
    }
    
    @Test
    void testGetHandDefensiveCopy() {
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
    void testToString() {
        String result = alice.toString();
        assertTrue(result.contains("Alice"));
        assertTrue(result.contains("Kitchen"));
    }
    
    @Test
    void testEquals() {
        Player anotherAlice = new Player("Alice", diningRoom);
        assertEquals(alice, anotherAlice);
        assertNotEquals(alice, bob);
    }
}