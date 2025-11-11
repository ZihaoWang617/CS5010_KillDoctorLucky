package killdoctorlucky.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import killdoctorlucky.model.cards.Playable;
import org.junit.Before;
import org.junit.Test;



/**
 * Test class for Deck functionality.
 * Tests deck creation, card drawing, and deck state management.
 */
public class DeckTest {
  
  private Deck deck;
  
  /**
   * Sets up the test fixture.
   * Creates a new deck for testing.
   */
  @Before
  public void setUp() {
    deck = new Deck();
  }
  
  /**
   * Tests deck creation.
   */
  @Test
  public void testDeckCreation() {
    assertNotNull(deck);
  }
  
  /**
   * Tests newly created deck is not empty.
   */
  @Test
  public void testNewDeckNotEmpty() {
    assertFalse("New deck should not be empty", deck.isEmpty());
  }
  
  /**
   * Tests drawing a card from deck.
   */
  @Test
  public void testDrawCard() {
    Playable card = deck.drawCard();
    assertNotNull("Should be able to draw a card", card);
  }
  
  /**
   * Tests drawing multiple cards.
   */
  @Test
  public void testDrawMultipleCards() {
    Playable card1 = deck.drawCard();
    Playable card2 = deck.drawCard();
    Playable card3 = deck.drawCard();
    
    assertNotNull(card1);
    assertNotNull(card2);
    assertNotNull(card3);
  }
  
  /**
   * Tests deck becomes empty after drawing all cards.
   */
  @Test
  public void testDeckBecomesEmpty() {
    // Draw all cards until deck is empty
    while (!deck.isEmpty()) {
      Playable card = deck.drawCard();
      assertNotNull(card);
    }
    
    assertTrue("Deck should be empty after drawing all cards", deck.isEmpty());
  }
  
  /**
   * Tests drawing from empty deck returns null.
   */
  @Test
  public void testDrawFromEmptyDeck() {
    // Draw all cards
    while (!deck.isEmpty()) {
      deck.drawCard();
    }
    
    // Try to draw from empty deck
    Playable card = deck.drawCard();
    assertNull("Drawing from empty deck should return null", card);
  }
  
  /**
   * Tests deck state after creation.
   */
  @Test
  public void testDeckInitialState() {
    Deck newDeck = new Deck();
    assertFalse("New deck should have cards", newDeck.isEmpty());
  }
}