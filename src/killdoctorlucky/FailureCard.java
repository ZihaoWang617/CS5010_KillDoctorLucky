package killdoctorlucky;

/**
 * Represents a failure card that can be used to block murder attempts.
 * Failure cards have a block value that reduces the effectiveness of other players' 
 * murder attempts against Doctor Lucky. They represent bad luck, distractions, or 
 * obstacles that interfere with assassination plans.
 */
public class FailureCard extends Card {
    private final int blockValue;
    
    /**
     * Creates a new failure card with the specified name and block value.
     * 
     * @param name the name of the failure event (e.g., "Bad Luck", "Distraction")
     * @param blockValue the strength of this card for blocking murder attempts
     * @throws IllegalArgumentException if name is null/empty or blockValue is negative
     */
    public FailureCard(String name, int blockValue) {
        super(name, "A failure card that can block murder attempts with " + blockValue + " blocking power.");
        
        if (blockValue < 0) {
            throw new IllegalArgumentException("Block value cannot be negative");
        }
        
        this.blockValue = blockValue;
    }
    
    /**
     * Gets the block value of this failure card.
     * This value is used to reduce the effectiveness of murder attempts.
     * 
     * @return the card's block value, always non-negative
     */
    public int getBlockValue() {
        return blockValue;
    }
    
    /**
     * Executes this failure card to block another player's murder attempt.
     * The card's block value is subtracted from the murder attempt's total damage.
     * Playing this card removes it from the player's hand.
     * 
     * @param game the current game instance where the murder attempt is being blocked
     * @param player the player using this card to interfere with the murder attempt
     * @throws IllegalArgumentException if game or player is null
     */
    @Override
    public void execute(Game game, Player player) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        // The actual blocking logic will be handled by the Game class
        // This method is called when the failure card is played to block an attack
        // The Game class will access the block value via getBlockValue()
    }
    
    @Override
    public String toString() {
        return String.format("FailureCard{name='%s', blockValue=%d}", getName(), blockValue);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        FailureCard that = (FailureCard) obj;
        return blockValue == that.blockValue;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * 31 + blockValue;
    }
}