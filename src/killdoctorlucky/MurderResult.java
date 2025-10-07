package killdoctorlucky;
/**
 * Enumeration representing all possible outcomes when a player attempts to murder Doctor Lucky.
 * Each result indicates why the murder attempt succeeded or the specific reason it failed,
 * which affects game flow and player strategy decisions.
 */
public enum MurderResult {
  SUCCESS,/** Murder was successful and player wins the game */
  FAILED_WITNESS_PRESENT,/** Murder failed because other players could see the attempt */
  FAILED_BLOCKED_BY_CARDS,/** Murder failed because other players played cards to block it */
  FAILED_INSUFFICIENT_WEAPON/** Murder failed because the weapon was not strong enough */
}