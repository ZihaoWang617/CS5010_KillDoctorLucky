package killdoctorlucky;

import java.util.*;

/**
 * Represents the deck of cards in the Kill Doctor Lucky game.
 * The deck manages two piles: a draw pile for drawing new cards and a discard pile
 * for used cards. When the draw pile is empty, the discard pile is reshuffled.
 */
public class Deck {
    private final Stack<Playable> drawPile;
    private final Stack<Playable> discardPile;
    private final Random random;
    
    /**
     * Creates a new deck and initializes it with all game cards.
     * The deck is automatically shuffled after initialization.
     */
    public Deck() {
        this.drawPile = new Stack<>();
        this.discardPile = new Stack<>();
        this.random = new Random();
        initializeCards();
        shuffle();
    }
    
    /**
     * Draws a card from the top of the draw pile.
     * If the draw pile is empty, the discard pile is reshuffled into the draw pile first.
     * 
     * @return the card from the top of the draw pile, or null if no cards remain
     */
    public Playable drawCard() {
        reshuffleIfNeeded();
        
        if (drawPile.isEmpty()) {
            return null; // No cards left in either pile
        }
        
        return drawPile.pop();
    }
    
    /**
     * Adds a card to the discard pile.
     * Cards are typically discarded after being played by players.
     * 
     * @param card the card to add to the discard pile
     * @throws IllegalArgumentException if card is null
     */
    public void discardCard(Playable card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        discardPile.push(card);
    }
    
    /**
     * Shuffles the draw pile randomly.
     * This method randomizes the order of cards in the draw pile.
     */
    public void shuffle() {
        if (drawPile.isEmpty()) {
            return;
        }
        
        List<Playable> cards = new ArrayList<>(drawPile);
        Collections.shuffle(cards, random);
        drawPile.clear();
        drawPile.addAll(cards);
    }
    
    /**
     * Checks if both the draw pile and discard pile are empty.
     * 
     * @return true if no cards remain in either pile, false otherwise
     */
    public boolean isEmpty() {
        return drawPile.isEmpty() && discardPile.isEmpty();
    }
    
    /**
     * Counts the total number of cards remaining in both piles.
     * 
     * @return the total number of cards in draw pile and discard pile combined
     */
    public int cardsRemaining() {
        return drawPile.size() + discardPile.size();
    }
    
    /**
     * Initializes the deck with all game cards.
     * This creates the standard set of weapon cards, failure cards, move cards,
     * and room cards for the Kill Doctor Lucky game.
     */
    private void initializeCards() {
        // Add weapon cards with various attack values
        drawPile.add(new WeaponCard("Revolver", 8));
        drawPile.add(new WeaponCard("Knife", 6));
        drawPile.add(new WeaponCard("Rope", 7));
        drawPile.add(new WeaponCard("Candlestick", 4));
        drawPile.add(new WeaponCard("Lead Pipe", 5));
        drawPile.add(new WeaponCard("Wrench", 3));
        drawPile.add(new WeaponCard("Poison", 9));
        drawPile.add(new WeaponCard("Dagger", 6));
        
        // Add failure cards with various block values
        drawPile.add(new FailureCard("Bad Luck", 3));
        drawPile.add(new FailureCard("Distraction", 2));
        drawPile.add(new FailureCard("Mishap", 4));
        drawPile.add(new FailureCard("Interference", 3));
        drawPile.add(new FailureCard("Obstacle", 5));
        drawPile.add(new FailureCard("Complication", 2));
        
        // Add movement cards with various distances
        drawPile.add(new MoveCard("Quick Step", 1));
        drawPile.add(new MoveCard("Sprint", 2));
        drawPile.add(new MoveCard("Dash", 1));
        drawPile.add(new MoveCard("Run", 2));
        drawPile.add(new MoveCard("Hurry", 1));
        
        // Add room cards for teleportation
        drawPile.add(new RoomCard("Kitchen Card", "Kitchen"));
        drawPile.add(new RoomCard("Library Card", "Library"));
        drawPile.add(new RoomCard("Dining Hall Card", "Dining Hall"));
        drawPile.add(new RoomCard("Drawing Room Card", "Drawing Room"));
        drawPile.add(new RoomCard("Billiard Room Card", "Billiard Room"));
        drawPile.add(new RoomCard("Trophy Room Card", "Trophy Room"));
    }
    
    /**
     * Reshuffles the discard pile into the draw pile if the draw pile is empty.
     * This method is called automatically when trying to draw from an empty draw pile.
     */
    private void reshuffleIfNeeded() {
        if (drawPile.isEmpty() && !discardPile.isEmpty()) {
            // Move all discard cards to draw pile
            drawPile.addAll(discardPile);
            discardPile.clear();
            
            // Shuffle the new draw pile
            shuffle();
        }
    }
    
    @Override
    public String toString() {
        return String.format("Deck{drawPile=%d, discardPile=%d}", 
                           drawPile.size(), discardPile.size());
    }
}