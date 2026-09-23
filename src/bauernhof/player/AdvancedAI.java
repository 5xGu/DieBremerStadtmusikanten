package bauernhof.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import bauernhof.board.GameBoard;
import bauernhof.preset.GameConfiguration;
import bauernhof.preset.ImmutableList;
import bauernhof.preset.Move;
import bauernhof.preset.card.Card;

/**
 * An AdvancedAI player that makes moves based on calculations over the next move. More like a slightly advanced AI.
 * @author Tobias Kai Lorenz Plattner
 */
public class AdvancedAI extends AbstractPlayer{
    /**
     * Depth for which the AI calculates, e.g. 3 means 3 moves ahead (including the one being made).
     */
    private final int DEPTH = 3;
    /**
     * Board to simulate moves.
     */
    private List<GameBoard> simuBoardList = new ArrayList<GameBoard>();
    
    /**
     * Constructor for the AdvancedAI player.
     * @param name The name of this player.
     */
    public AdvancedAI(String name, GameConfiguration config, int nrOfPlayers, ImmutableList<Card> initDrawPile){
        super(name);
        for (int i = 0; i < DEPTH; i++)
            this.simuBoardList.add(new GameBoard(config, nrOfPlayers, initDrawPile));
    }

    /**
     * Method to request moves from the AdvancedAI, which returns a randomly selected Move Object.
     * @return Move the AdvancedAI makes.
     * @exception IllegalStateException is thrown if {@link bauernhof.player.AbstractPlayer request()} throws one.
     */
    @Override
    public Move getMove() throws IllegalStateException {
        //get the current hand, discard pile, draw pile
        //then go through all moves and simulate the execution of a move, calculate the points. Do that, lets say, for three moves in 
        //advance. Save the move that results in the highest score.
        //at the end set the hand, discard pile and draw pile back to the original state.
        //We could also go through the hands of the other players and try to find a move that gives one or all other players minus points
        //but this player plus points, e.g. if we take the score of this player and substract from it the sum of the scores of all other players
        //we choose the one that is the highest
        //to set up the simuBoard to the newest state of the game
        //also make moves using this strategy for eveyr other player to rly simulate the game. GameBoard allows to get every players hands,
        //s.t. all of the moves can be made for all players and all highest scores calculated and then we choose the one that returns the highest
        //score for us.
        Card [] discardPileReset = this.getGameBoard().getDepositPile(); //this method returns an array, but the setter method expects an ArrayList...
        Card [] hand = this.getGameBoard().getPlayerHand(this.getID());
        Card [] drawPileReset = this.getGameBoard().getDrawPile(); //this metod returns an array, but setter expects a Stack
        ArrayList<Card> discardPile = new ArrayList<Card>(Arrays.asList(discardPileReset));
        Stack<Card> drawPile = this.arrToStack(drawPileReset);

        //setup the simulation boards
        //for all players, with me being the first, do all moves:
        //I do a move, update the board of the other players, then another one makes a move, updates everyone else, etc.
        //do this 3 times in total

        //use a board to simulate the moves, to decide which one is next, then do this again for two times
        //store the points after each move in a 3D array, then sum the indexes up and safe the indexes as well as the max
        //do the move that brings the max points in this turn, i.e. the move with the same index as determined for the first dimension
               
        //go through moves and find the ones with the highest score
        Move moves[] = this.getGameBoard().getAllMoves(this.getID());
        ArrayList<Integer> scores = new ArrayList<Integer>();
        Move move = null;
        int maxScore = Collections.max(scores);
        for (int i = 0; i < moves.length; i++){
            if(scores.get(i) == maxScore)
                move = moves[i];
        }

        this.getGameBoard().executeMove(move);
        return move;
    }
    
    /**
     * Method to create the GameBoard on which moves can be simulated.
     */
    private void initSimuBoard(GameBoard origin, GameBoard simu){
        Card [] discardPileOrigin = origin.getDepositPile(); //this method returns an array, but the setter method expects an ArrayList...
        Card [] handOrigin = origin.getPlayerHand(this.getID());
        Card [] drawPileResetOrigin = origin.getDrawPile(); //this metod returns an array, but setter expects a Stack
        ArrayList<Card> discardPile = new ArrayList<Card>(Arrays.asList(discardPileOrigin));
        Stack<Card> drawPile = this.arrToStack(drawPileResetOrigin);

        simu.setDiscardPile(discardPile);
        simu.setDrawPileOfPlayer(drawPile);

        
    }

    /**
     * This method is needed here for {@link #getMove()} to be able to reset the gameboard. For full description see 
     * {@link bauernhof.player.AbstractPlayer arrToStack(Card[])}.
     * @param cards An array of cards that is to be transformed into a stack of card objects.
     * @return Stack of card objects.
     */
    @Override
    protected Stack<Card> arrToStack(Card[] cards){
        return super.arrToStack(cards);
    }

}