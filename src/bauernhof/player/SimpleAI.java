package bauernhof.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import bauernhof.preset.Move;
import bauernhof.preset.card.Card;

/**
 * An AIplayer that follows simple rules and should beat a {@link bauernhof.preset.PlayerType.RANDOM_AI} player in ca. two-thirds of games. 
 * (It is a bit less, more like 60-63%, but SIMPLE_AI is a bit too proud to admit that.) SIMPLE_AI thought about its strategy for a long time, 
 * until it became to entangled in the different effects and calculations and got distracted by the big numbers on some of the cards. Because
 * everyone knows that big equals better, it decided to follow its intuition and take always cards with big numbers, while depositing cards
 * with small numbers. It became a real collector this way! Because it cares more about its collection than the game, it just does a random
 * move that fits its strategy.
 * @author Tobias Kai Lorenz Plattner
 */
public class SimpleAI extends AbstractPlayer{
    /**
     * Constructor for the SimpleAI player.
     * @param name The name of this player.
     */
    public SimpleAI(String name){
        super(name);
    }

     /**
     * Method to get the SimpleAI player to make a move according to its strategy.
     * @return Move the SimpleAI makes.
     * @exception IllegalStateException is thrown if {@link bauernhof.player.AbstractPlayer request()} throws one.
     */
    @Override
    public Move getMove() throws IllegalStateException {
        //get all moves and sort them according to strategy
        Move [] moves = this.getGameBoard().getAllMoves(this.getID());
        Move notSoGoodMove = moves[0];
        List<Move> movesListTemp = Arrays.asList(moves);
        ArrayList<Move> movesList = new ArrayList<Move>(movesListTemp);

        Move goodMove = null;

        //get good moves for Taken and Deposited
        ArrayList<Move> goodMovesTaken = removeMovesWithLowBasePointsTaken(movesList);
        ArrayList<Move> goodMovesDeposited = removeMovesWithHighBasePointsDiscared(movesList);
        //see if they can be combined
        //No need to check for null values, worst case they are empty

        //check if there is a perfect move
        ArrayList<Move> goodMovesTakenTemp = new ArrayList<Move>(goodMovesTaken);
        goodMovesTakenTemp.retainAll(goodMovesDeposited);
        if(goodMovesTakenTemp.size() > 0){
            goodMove = goodMovesTakenTemp.get(0);
            this.getGameBoard().executeMove(goodMove);
            return goodMove;
        }

        //if not, then take a suboptimal move
        if (goodMovesTaken.size() > 0){
            goodMove = goodMovesTaken.get(0);
        } else {
            if (goodMovesDeposited.size() > 0){
                goodMove = goodMovesDeposited.get(0);
            } else {
                goodMove = notSoGoodMove;
            }
        }

        this.getGameBoard().executeMove(goodMove);
        return goodMove;   
    }

    /**
     * Method to get the move(s) having the card(s) with the highest basepoints.
     * @param moves List of moves to sort through.
     * @return A list of moves containing only moves with cards with high basepoints.
     */
    private ArrayList<Move> removeMovesWithLowBasePointsTaken(List<Move> moves){
        ArrayList<Card> moveCards = new ArrayList<Card>();
        for (Move move : moves){
            moveCards.add(move.getTaken());
        }

        //find base values
        ArrayList<Integer> baseValue = new ArrayList<Integer>();
        for (Card card : moveCards){
            baseValue.add(card.getBaseValue());
        }
        
        int max = Collections.max(baseValue);
        ArrayList<Move> highValueMoves = new ArrayList<Move>(moves);
        //remove moves with low value taken cards
        for (Move move : moves){
            if (move.getTaken().getBaseValue() < max){
                highValueMoves.remove(move);
            }
        }

        return highValueMoves;
    }

    /**
     * Method to remove moves from the moves list of the player that would discard a card with high base points.
     * @param moves A list of moves from which high base value cards should be removed.
     * @return a moves list without moves discarding cards with a high base value.
     */
    private ArrayList<Move> removeMovesWithHighBasePointsDiscared(List<Move> moves){
        ArrayList<Card> moveCards = new ArrayList<Card>();
        for (Move move : moves){
            moveCards.add(move.getDeposited());
        }
        //find base values
        ArrayList<Integer> baseValue = new ArrayList<Integer>();
        for (Card card : moveCards){
            baseValue.add(card.getBaseValue());
        }

        int max = Collections.max(baseValue);
        ArrayList<Move> highValueMoves = new ArrayList<Move>(moves);
        //remove moves with low value taken cards
        for (Move move : moves){
            if (move.getTaken().getBaseValue() == max){
                highValueMoves.remove(move);
            }
        }

        return highValueMoves;
    }
}