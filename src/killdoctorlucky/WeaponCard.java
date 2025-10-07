package killdoctorlucky;

/**
 * Represents a weapon card that can be used in murder attempts.
 * Weapon cards have an attack value that contributes to the success of killing Doctor Lucky.
 * The higher the attack value, the more likely a murder attempt will succeed.
 */
public class WeaponCard extends Card {
    private final int attackValue;
    
    /**
     * Creates a new weapon card with the specified name and attack value.
     * 
     * @param name the name of the weapon (e.g., "Knife", "Revolver")
     * @param attackValue the strength of this weapon for murder attempts
     * @throws IllegalArgumentException if name is null/empty or attackValue is negative
     */
    public WeaponCard(String name, int attackValue) {
        super(name, "A weapon that can be used to attack Doctor Lucky with " + attackValue + " attack power.");
        
        if (attackValue < 0) {
            throw new IllegalArgumentException("Attack value cannot be negative");
        }
        
        this.attackValue = attackValue;
    }
    
    /**
     * Gets the attack value of this weapon.
     * This value is used to calculate the effectiveness of murder attempts.
     * 
     * @return the weapon's attack value, always non-negative
     */
    public int getAttackValue() {
        return attackValue;
    }
    
    /**
     * Executes this weapon card during a murder attempt.
     * The weapon's attack value is added to the murder attempt's total damage.
     * Playing this card removes it from the player's hand.
     * 
     * @param game the current game instance where the murder attempt is happening
     * @param player the player using this weapon to attack Doctor Lucky
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
        
        // The actual murder attempt logic will be handled by the Game class
        // This method is called when the weapon is played as part of an attack
        // The Game class will access the attack value via getAttackValue()
    }
    
    @Override
    public String toString() {
        return String.format("WeaponCard{name='%s', attackValue=%d}", getName(), attackValue);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        WeaponCard that = (WeaponCard) obj;
        return attackValue == that.attackValue;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * 31 + attackValue;
    }
}