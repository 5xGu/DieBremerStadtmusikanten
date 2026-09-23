package bauernhof.player;

import bauernhof.preset.Move;


/**
 * Class for RANDOM_AI that cannot be extended, because there is nothing to be added to this within the context of the game rules.
 * @author Tobias Kai Lorenz Plattner
 */
public final class RandomAI extends AbstractPlayer{

    /**
     * Constructor of the RandomAI. No exception for the name is thrown because there are no conditions to be fulfilled here.
     * @param name of the RandomAI.
     */
    public RandomAI(String name){
        super(name);
    }

    /**
     * Method to request moves from the RandomAI, which returns a randomly selected Move Object.
     * @return Move the RandomAI makes.
     * @exception IllegalStateException is thrown if {@link bauernhof.player.AbstractPlayer request()} throws one.
     */
    @Override
    public Move getMove() throws IllegalStateException {
        Move moves[] = this.getGameBoard().getAllMoves(this.getID());
        Move move = moves[(int) (moves.length*Math.random())];
        this.getGameBoard().executeMove(move);
        return move;
    }
}