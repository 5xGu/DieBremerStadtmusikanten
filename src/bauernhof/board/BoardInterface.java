package bauernhof.board;

import bauernhof.preset.GameConfiguration;
import bauernhof.preset.card.Card;

/**
* Interface for the game board.
* @author Valeria Stankova
*/
public interface BoardInterface {
    /**
    * Gets the game configuretion.
    */
    public GameConfiguration getGameConfiguration();
    /**
    * Gets the number of players in the game.
    */
    public int getNumberOfPlayers();
    /**
    * Gets the player points with given player id.
    * @param playerid The player id
    */
    public int getPlayerPoints (int playerid);
    /**
    * Gets the player card hand with given player id.
    * @param playerid  The player id
    */
    public Card[] getPlayerHand(int playerid);
    /**
    * Gets how many cards are in the draw pile.
    */
    public int getNrOfCardsInDraw();
    /**
    * Gets the draw pile.
    */
    public Card[] getDrawPile();
    /**
    * Gets the number of cards in the disposal pile.
    */
    public int getNrOfCardsInDeposit(); 
    /**
    * Gets the disposal pile.
    */  
    public Card[] getDepositPile();
    /**
    * Gets the player id of the player on turn.
    */
    public int getPlayerOnTurn();
    /**
    * Gets the current round (note it should be getRound not Turn).
    */
    public int getTurn();
}

