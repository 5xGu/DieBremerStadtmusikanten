package bauernhof.player;

import bauernhof.preset.GameConfiguration;
import bauernhof.preset.GameConfigurationException;
import bauernhof.preset.ImmutableList;
import bauernhof.preset.Move;
import bauernhof.preset.Player;
import bauernhof.preset.card.Card;

import java.util.Stack;

import bauernhof.board.GameBoard;

 /**
  * Abstract class for the player classes that implements the interface Player. Defines all attributes and methods the player types share.
  * @author Tobias Kai Lorenz Plattner
  */
public abstract class AbstractPlayer implements Player{

    /********* Attributes *********/
    /**
     * Name of the player.
     */
    private final String name;
    /**
     * Keep track of initialization, so an error can be thrown when a player is initialized more than once.
     */
    private boolean init = false;
    /**
     * The players private GameBoard
     */
    private GameBoard gameboard; 
    //This cant be changed to final, because it cannot be initialised in the constructor. If done nethertheless the compiler throws 
    //error: cannot assign a value to final variable gameboard.
    /**
     * Specifications clearly state that ids are 1,...,numofplayers. So just have one id per player. 
     */
    private int id;
    /**
     * have an instance variable that is initialised from class index variable and represents the number of calls of update method.
     */
    private int nof_updatecalls;
    /**
     * Variable to ensure that VerifyGame() is called only once.
     */
    private boolean isVerified = false;
    /**
     * round variable to keep track of player round state for exception handling.
     */
    protected int round = 0; //in GameBoard class round is initialised with 1, i.e. the player "lacks behind" by one round.
    /**
     * Variable for LOCAL Tournaments to reset isVerified and allow for multiple rounds. For REMOTE tournament the specifications were
     * contradictory.
     */
    protected int nrOfRounds;
    

    /********* Constructor *********/
    /**
     * Constructor for player types that gives the player a name, necessary so getName() can be called prior to initialisation.
     * @param name specifies the name of the player.
     */
    //no IllegalArgumentException because there are no conditions for the names to be checked here.
    public AbstractPlayer(String name){
        this.name = name;
    }

    /********* Interface Methods *********/
    /**
     * This method initialises the player.
     * @param gameConfiguration determines the start configuration of the game and is used to create the players gameboard.
     * @param initialDrawPile specifies the initial state of the draw pile.
     * @param numplayers specifies the number of players in the game.
     * @param playerid specifies the id of the player initialized.
     * @exception IllegalStateException if the player is already initialised or if the passed ID is not unique.
     * @exception GameConfigurationException if the GameConfiguration passed is null.
     * @exception IllegalArgumentException if the specfied arguments violate the game conditions.
     */
    @Override
    public void init(GameConfiguration gameConfiguration, ImmutableList<Card> initialDrawPile, int numplayers, int playerid) throws IllegalStateException, GameConfigurationException, IllegalArgumentException{
        /***** Check all cases were intialisation fails and throw appropriate exceptions *****/
        if(this.init)
            throw new IllegalStateException("Player already initialised!");
        else this.init = true;
        if (gameConfiguration == null)
            throw new GameConfigurationException("No configuration for the game was detected!");
        if (numplayers < 2)
            throw new IllegalArgumentException("Too few players, two needed at least!");
        if (numplayers > 4)
            throw new IllegalArgumentException("Too many players, at most four allowed!");
        if (playerid < 1)
            throw new IllegalArgumentException("PlayerID has to equal 1 at least!");
        if (playerid > numplayers)
            throw new IllegalArgumentException("PlayerID cannot be greater than total number of players!");

        /***** If all is fine: initialise *****/
        this.id = playerid;
        this.gameboard = new GameBoard(gameConfiguration, numplayers, initialDrawPile);
        this.nof_updatecalls = this.getGameBoard().getNumberOfPlayers()-1;
    }
    

    /**
     * This method returns the name of the player for which it is called and can be called at any time and as often as wished for.
     * @return name of the player.
     * @exception NullPointerException if name == null.
     */
    public String getName() throws NullPointerException{
        if (this.name == null)
            throw new NullPointerException("Name cannot have value null!");
        return this.name;
    }

    /**
     * This method returns the (current) score of a player and can only be called after initialisation.
     * @return score of the player object for which this method is called.
     * @exception IllegalStateException is thrown if player is not intialised yet.
     */
    public int getScore() throws IllegalStateException{
        if (!(this.init))
            throw new IllegalStateException("Player not initialized yet. Score not retrievable!");
        return this.getGameBoard().getPlayerPoints(this.id);
    }
    /**
     * Method that is used to verify the validity of the game by comparing the player's scores with each other. The only valid call of this method
     * is after the game ended. If no exception is thrown the game is verified.
     * @param arg0 is a list of integers representing the scores of all players.
     * @exception IllegalStateException is thrown if the scores do not coincide with the ones on the players gameboard.
     * @throws GameNotEndedException if the game has not ended prior to method call.
     * @throws IllegalStateException when the player is not intialised or the method has been called already.
     * @throws IllegalArgumentException if the passed list is null, or does not contain as many scores as there are players.
     */
    @Override
    public void verifyGame(ImmutableList<Integer> arg0) throws IllegalStateException, GameNotEndedException, IllegalArgumentException {
        if (!(this.init))
            throw new IllegalStateException("Player not initialized yet. Score not retrievable!");
        if (this.isVerified)
            throw new IllegalStateException("The game is already verified!");
        else this.isVerified = true;
        if(!(this.getGameBoard().isGameOver()))
            throw new GameNotEndedException("The game has not ended yet");
        if(arg0.size() != this.getGameBoard().getNumberOfPlayers()) //takes care of case where passed list == null aswell
            throw new IllegalArgumentException("The passed list of final scores is either too small or too big, or equals null!");
            
        //compare the scores of the local gameboard to the final score list
        for(int i = 0; i < this.getGameBoard().getNumberOfPlayers(); i++){
            int final_score = arg0.get(i);
            int local_score = this.getGameBoard().getPlayerPoints(i+1);
            if (!(local_score == final_score))
                throw new IllegalStateException("Scores do not match on: " + this.getName() + "s (" + this.getID() + ") board!");
        }
    }
    

    /**
     * This method updates the gameboard of the player for which it is called.
     * @param opponentMove is a move made by an opponent for which the gameboard now has to be updated.
     * @exception IllegalMoveException when the executemove method indicates that an illegal move was made, replaces IllegalArgumentException.
     * @exception IllegalStateException when the player is not initialised, the method is not called after an opponent made a move, or it was called more often than there are opponents.
     */
    public void update(Move opponentMove) throws IllegalMoveException, IllegalStateException {
        if (!(this.init))
            throw new IllegalStateException("Player not initialized yet. GameBoard cannot be updated yet!");
        if (this.getGameBoard().getPlayerOnTurn() == this.getID())
            throw new IllegalStateException("Player " + this.getName() + " (" + this.getID() +") tried to update while its their turn");
        if (this.nof_updatecalls > 0){
            this.nof_updatecalls -= 1; //Count how often update was called, leave order of players calling this to main program
        } else throw new IllegalStateException("Slow your horses: too many moves!");
        int check = this.getGameBoard().executeMove(opponentMove); //update the players gameboard
        if (check != 1) //gameboard atm returns 1 if all worked out, TODO: Maxim wanted to update his method, so this needs to be updated aswell
            throw new IllegalMoveException("Move was illegal");
    }

    /**
     * Method that requests a player to make a move.
     * @return Move that was chosen to be made by the player.
     * @exception IllegalStateException when this player is not initialised or tries to make a move in another players turn.
     */
    public Move request() throws IllegalStateException {
        if (!(this.init))
            throw new IllegalStateException("Player not initialized yet. Move cannot be requested!");
        if (this.getGameBoard().getPlayerOnTurn() != this.getID())
            throw new IllegalStateException("Player may not make move in other players turn");
        this.round++; //keep track of round the player is in
        if (this.round > this.getGameBoard().getTurn()) //check if the player made multiple moves in the same round
            throw new IllegalStateException("This player made multiple moves in this round!");
        this.nof_updatecalls += this.getGameBoard().getNumberOfPlayers() -1; //make sure to "refresh" amount of times this method may be called
        return getMove(); //this is an abstract method that will be implemented for each player themselves
    }


    /********* Additional Methods for AbstractPlayers *********/
    /**
     * Method that returns the players ID.
     * @return the playerID of the object for which this method was called.
     * @exception IllegalStateException if the player is not initialised yet.
     */
    protected int getID() throws IllegalStateException{
        if (!(this.init))
            throw new IllegalStateException("Player not initialized yet. ID not retrievable!");
        return this.id;
    }

    /**
     * Method to retrieve the gameboard of the player.
     * @return the private gameboard of the player.
     * @exception IllegalStateException if the player is not initialised.
     */
    protected GameBoard getGameBoard() throws IllegalStateException{
        if (!(this.init))
            throw new IllegalStateException("Player not initialized yet. GameBoard not retrievable!");
        return this.gameboard;
    }

    /**
     * This method is necessary because the GameBoard class does not provide a method to transform arrays to stacks and also is inconsistent
     * with its getter and setter methods, at times requiring Stacks for the setter methods, but returning arrays. For the player classes
     * this is only relevant for arrays or stacks of card objects. This method assumes that the array element with index 0 is at supposed
     * to be at the top of the stack, e.g. is the top card of the draw pile. Thus the last element of the array is pushed first.
     * @param cards An array of cards that is to be transformed into a stack of card objects.
     * @return Stack of card objects.
     */
    protected Stack<Card> arrToStack(Card[] cards){
        Stack<Card> cardStack = new Stack<Card>();
        for (int i = cards.length-1; i > 0; i--)
            cardStack.push(cards[i]);
        return cardStack;
    }

    /********* Abstract Methods *********/
    /**
     * Method that every player extending AbstractPlayer will implement to make moves.
     */
    public abstract Move getMove() throws IllegalStateException;

}
