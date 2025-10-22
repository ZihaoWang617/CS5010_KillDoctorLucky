package killdoctorlucky;

/**
 * Represents a user or AI command that performs an action on the game model.
 */
public interface Command {

  /**
   * Executes this command on the given game, writing output to the given Appendable.
   *
   * @param game the game model to execute on (non-null)
   * @param out  the Appendable to write messages to (non-null)
   * @throws IllegalArgumentException if game or out is null
   */
  void execute(Game game, Appendable out);

  /**
   * get the description of the command.
   * @return a short description of this command (for help or logging)
   */
  String getDescription();
}