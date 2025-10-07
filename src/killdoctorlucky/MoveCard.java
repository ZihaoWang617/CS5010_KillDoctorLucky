package killdoctorlucky;

/**
 * Represents a movement card that allows players to move additional spaces.
 * Move cards provide extra movement points that players can use to travel
 * further through the mansion during their turn, enabling better positioning
 * for strategic gameplay.
 */
public class MoveCard extends Card {
    private final int moveDistance;
    
    /**
     * Creates a new move card with the specified name and movement distance.
     * 
     * @param name the name of the movement type (e.g., "Quick Step", "Sprint")
     * @param distance the number of additional rooms this card allows movement through
     * @throws IllegalArgumentException if name is null/empty or distance is negative
     */
    public MoveCard(String name, int distance) {
        super(name, "A movement card that allows you to move " + distance + " additional rooms.");
        
        if (distance < 0) {
            throw new IllegalArgumentException("Move distance cannot be negative");
        }
        
        this.moveDistance = distance;
    }
    
    /**
     * Gets the movement distance provided by this card.
     * This value represents additional rooms the player can move through.
     * 
     * @return the number of additional rooms this card allows movement, always non-negative
     */
    public int getMoveDistance() {
        return moveDistance;
    }
    
    /**
     * Executes this move card to grant additional movement to the player.
     * The player gains extra movement points equal to this card's move distance.
     * Playing this card removes it from the player's hand.
     * 
     * @param game the current game instance where movement is being enhanced
     * @param player the player using this card to gain extra movement
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
        
        // The actual movement logic will be handled by the Game class
        // This method is called when the move card is played for extra movement
        // The Game class will access the move distance via getMoveDistance()
    }
    
    @Override
    public String toString() {
        return String.format("MoveCard{name='%s', moveDistance=%d}", getName(), moveDistance);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        MoveCard that = (MoveCard) obj;
        return moveDistance == that.moveDistance;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * 31 + moveDistance;
    }
}