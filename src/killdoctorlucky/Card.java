package killdoctorlucky;

/**
 * Abstract base class for all cards in the Kill Doctor Lucky game.
 * Cards are playable objects that can be held in a player's hand and executed
 * to produce various effects during gameplay.
 */
public abstract class Card implements Playable {
    private final String name;
    private final String description;
    
    /**
     * Creates a new card with the specified name and description.
     * 
     * @param name the display name of the card
     * @param description a detailed explanation of what the card does
     * @throws IllegalArgumentException if name or description is null or empty
     */
    public Card(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Card name cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Card description cannot be null or empty");
        }
        
        this.name = name.trim();
        this.description = description.trim();
    }
    
    /**
     * Gets the display name of this card.
     * 
     * @return the card's name, never null or empty
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Gets a detailed description of this card's effect.
     * 
     * @return the card's description, never null or empty
     */
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * Executes this card's specific effect on the game state.
     * Each card type implements this method differently based on its purpose.
     * 
     * @param game the current game instance
     * @param player the player playing this card
     * @throws IllegalArgumentException if game or player is null
     */
    @Override
    public abstract void execute(Game game, Player player);
    
    @Override
    public String toString() {
        return String.format("%s{name='%s'}", this.getClass().getSimpleName(), name);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return name.equals(card.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}