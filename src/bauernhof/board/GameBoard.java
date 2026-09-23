package bauernhof.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

import bauernhof.preset.Either;
import bauernhof.preset.*;
import bauernhof.preset.GameConfiguration;
import bauernhof.preset.card.*;
import bauernhof.preset.ImmutableList;

public class GameBoard implements BoardInterface{
    /**
     * configData is a GameConfiguration intance used through out the program to retrieve game
     * config data like amount of players, cards in the game and other things.
     */
    private final GameConfiguration configData;
    /**
     * In amountOfPlayers, the amount of players is stored, it is being retrieved
     * via the game configuration.
     */
    private final int amountOfPlayers;
    /**
     * maxCardsInDiscardPile contains the cards in the discard pile, necessary to figure out when the 
     * game ends and to loop through the pile.
     */
    private final int maxCardsInDiscardPile;
    /**
     * cardsInHand contains the cards in the hand and is defined via the game configuration.
     */
    private final int cardsInHand;
    /**
     * discardPile is a ArrayList containing all the cards in the discard pile of this instance.
     */
    private ArrayList<Card> discardPile = new ArrayList<Card>();
    /**
     * drawPile is a Stack of Cards containing the cards of the drawpile.
     */
    private Stack<Card> drawPile = new Stack<>();
    /**
     * hand is a Card[][] object containing all cards for all players of this instance.
     */
    private Card[][] hand;
    /**
     * playerOnTurn contains the id of the player currently on turn.
     */
    private int playerOnTurn = 1;
    /**
     * round keeps track of the round the board instance is in.
     */
    private int round = 1;
    /**
     * Constructor of the GameBoard. Uses the game config to set the attributes and also sets the
     * drawPile. It is also assigning the hands at the beginning of the game and modifying the 
     * drawPile accordingly via a function.
     * @param configData GameConfiguration object to access the game configuration.
     * @param amountOfPlayers amountOfPlayers varies so it has to be passed to the constructor.
     * @param drawPile The drawPile at the beginning of the game.
     */
    
    public GameBoard(GameConfiguration configData,int amountOfPlayers,ImmutableList<Card> drawPile){
        this.configData = configData;
        maxCardsInDiscardPile = configData.getNumDepositionAreaSlots();
        cardsInHand = configData.getNumCardsPerPlayerHand();
        this.amountOfPlayers = amountOfPlayers;
        for (Card drawPileCard : drawPile) {
            this.drawPile.push(drawPileCard);
        }
        hand = new Card[amountOfPlayers][cardsInHand];
        assignHands();
    }

    /**
     * assignHands is being used in the constructor to create the player hands at the beginning of the game
     * and it is also automatically removing the cards from the drawpile. It is using call be reference so
     * no value is being returned.
     */
    private void assignHands(){
        for(int i=0;i<amountOfPlayers;i++){
            for(int n=0;n<cardsInHand;n++){
                hand[i][n] = drawPile.pop();
            }
        }
    }

    /**
     * This modifies the hand of the player.
     * @param player The player id of the player where we want to modify the hand.
     * @param newHand The new hand as a Card[] to assign to the player
     */
    public void setHandOfPlayer(int player,Card[] newHand){
        this.hand[player-1]=newHand;
    }

    /**
     * Modify the drawPile of the board.
     * @param newDrawPile The drawPile as a Stack<Card> which we want to be the new draw pile.
     */
    public void setDrawPileOfPlayer(Stack<Card> newDrawPile){
        this.drawPile=newDrawPile;
    }

    /**
     * Modify the discardPile of the board
     * @param newDiscardPile The discardPile as a ArrayList<Card> which we want to be the new discard pile.
     */
    public void setDiscardPile(ArrayList<Card> newDiscardPile){
        this.discardPile=newDiscardPile;
    }

    /**
     * This function changes which player is on turn right now.
     * @param playerOnTurn The id of the player which we want to be on turn now.
     */
    public void setPlayerOnTurn(int playerOnTurn){
        this.playerOnTurn = playerOnTurn;
    }

    /**
     * This function executes a move and changes the discard pile, draw pile and hand of the player accordingly.
     * It is always doing this for the player which is currently on turn.
     * @param currentMove Is a Move object which contains the card which is being discarded and the one being drawn.
     * @return Is a int which is 0 if the move is not valid, otherwise 1 if it was successful
     */
    public int executeMove(Move currentMove) throws IllegalStateException{
        if(isGameOver()){
            throw new IllegalStateException("Game is already over, dont make moves anymore!");
        }
        int playerOnTurn = this.playerOnTurn - 1;
        Move[] allMoves = this.getAllMoves(this.playerOnTurn);
        boolean valid = false;
        for (Move move : allMoves) {
            if (currentMove.getTaken() == move.getTaken() && currentMove.getDeposited() == move.getDeposited()) {
                valid = true;
                break;
            }
        }
        if (!valid){
            return 0;
        }
        Card taken = currentMove.getTaken();
        Card deposited = currentMove.getDeposited();
        if(drawPile.contains(taken)){
            drawPile.remove(taken);
        }
        else if(discardPile.contains(taken)){
            discardPile.remove(taken);
        }
        for(int i=0;i<cardsInHand;i++){
            if(hand[playerOnTurn][i]==deposited){
                hand[playerOnTurn][i] = taken;
                break;
            }
        }
        discardPile.add(deposited);
        if(!isGameOver()){
            nextTurn();
        }
        return 1;
    }

    /**
     * This function updates the round of the game. The round is only increasing when the last player has made their move.
     * It also updates which player is on turn now and resets it to one if the last player made their move.
     * Furthermore, it uses isGameOver() to check if the game is over.
     * @return 
     * @throws IllegalStateException
     */
    private int nextTurn() throws IllegalStateException {
        if (isGameOver()){
                throw new IllegalStateException("This game ends now!");
        }
        if(playerOnTurn==amountOfPlayers){
            round++;  
        }
        playerOnTurn++;
        if (playerOnTurn > amountOfPlayers) {
            playerOnTurn = 1;
        }
        return 1;
    }

    /**
     * Deterimnes if the game is over. Stops if 30 rounds were played or the discard pile has a certain size.
     * @return A boolean, true of the game is over, false if it is not.
     */
    public boolean isGameOver(){
        if(round==31){
            return true;
        }
        if(this.discardPile.size() >= maxCardsInDiscardPile){
            return true;
        }
        return false;
    }

    /**
     * Gets the game configuration.
     * @return The game configuration as a GameConfiguration object.
     */
    public GameConfiguration getGameConfiguration(){
        return this.configData;
    }

    /**
     * Gets the number of the players.
     * @return The number of the players as an int.
     */
    @Override
    public int getNumberOfPlayers(){
        return this.amountOfPlayers;
    }

    /**
     * Gets the depostPile.
     * @return The depostPile as Card[].
     */
    @Override
    public Card[] getDepositPile(){
        return this.discardPile.toArray(new Card[discardPile.size()]);
    }

    /**
     * Gets the drawPile of the game board instance.
     * @return The drawPile as Card[].
     */
    @Override
    public Card[] getDrawPile(){
        return this.drawPile.toArray(new Card[drawPile.size()]);
    }

    /**
     * Gets the hand of a player.
     * @param player The id of the player.
     * @return The hand of the player as Card[].
     */
    @Override
    public Card[] getPlayerHand(int player){
        return this.hand[player-1];
    }

    /**
     * Gets the number of cards in the deposit pile.
     * @return The discardPile.size() as an int.
     */
    @Override
    public int getNrOfCardsInDeposit(){
        return this.discardPile.size();
    }

    /**
     * Gets the number of cards in the draw pile.
     * @return The drawPile.size() as an int.
     */
    @Override
    public int getNrOfCardsInDraw(){
        return this.drawPile.size();
    }

    /**
     * Gets the id of the player on turn.
     * @return The id of the player as an int
     */
    @Override
    public int getPlayerOnTurn(){
        return this.playerOnTurn;
    }

    /**
     * Returns the round the GameBoard instance is in.
     * @return The round of the GameBoard as an int.
     */
    @Override
    public int getTurn(){
        return this.round;
    }

    /**
     * Creates a full list of all moves possible for the player according to their hand and also
     * the discard pile and draw pile.
     * @param player The id of the player you want to get all moves of.
     * @return A array of move objects representing all possible moves.
     */
    public Move[] getAllMoves(int player){
        ArrayList<Move> allMoves = new ArrayList<>();
        for(int i=0;i<cardsInHand;i++){
            for(int n=0;n<discardPile.size();n++){
                if (discardPile.size() != 0){
                    allMoves.add(new Move(discardPile.get(n),hand[player-1][i]));
                }
            }
            if (drawPile.size() > 0) {
                allMoves.add(new Move(drawPile.get(drawPile.size()-1),hand[player-1][i]));
            }
        }
        for(int i=0;i<discardPile.size();i++){
            allMoves.add(new Move(discardPile.get(i),discardPile.get(i)));
        }
        if (drawPile.size() > 0){
            allMoves.add(new Move(drawPile.get(drawPile.size()-1),drawPile.get(drawPile.size()-1)));
        }
        return allMoves.toArray(new Move[1]);
    }

    /**
     * Get the cards of the selector which are in the players hand.
     * @param player The id of the player you want to get the cards of the selector of.
     * @param cardsAndColorsOfSelector A set of Either objects which are either Card or CardColor objects, representing the selectors.
     * @return A list of Card objects representing all the cards the player has with the passed selectors.
     */
    private ArrayList <Card> getCardsOfSelectorInHand(int player,Set<Either<Card,CardColor>> cardsAndColorsOfSelector){
        ArrayList <Card> cardsOfSelectorInHand = new ArrayList<Card>();
        for (Either<Card, CardColor> cardOrColor : cardsAndColorsOfSelector) {
            if(cardOrColor.isLeft()){
                for(int i=0;i<cardsInHand;i++){
                    if(hand[player-1][i].getName()==cardOrColor.getLeft().getName()){
                        cardsOfSelectorInHand.add(cardOrColor.getLeft());
                    }
                }
            }
            else{
                for(int i=0;i<cardsInHand;i++){
                    if(hand[player-1][i].getColor().getName()==cardOrColor.getRight().getName()){
                        cardsOfSelectorInHand.add(hand[player-1][i]);
                    }
                }
            }
        }
        return cardsOfSelectorInHand;
    }

    /**
     * This function gets all cards of the passed selectors in the game.
     * @param cardsAndColorsOfSelector A set of Either objects which are either Card or CardColor objects, representing the selectors.
     * @return All the cards in the game with the given selector.
     */
    private ArrayList <Card> getAllCardsOfSelector(Set<Either<Card,CardColor>> cardsAndColorsOfSelector){
        ArrayList <Card> allCardsOfSelector = new ArrayList<Card>();
        Set <Card> allCards = configData.getCards();
        for (Either<Card, CardColor> cardOrColor : cardsAndColorsOfSelector) {
            if(cardOrColor.isLeft()){
                allCardsOfSelector.add(cardOrColor.getLeft());
            }
            else{
                for(Card card : allCards){
                    if(card.getColor()==cardOrColor.getRight()){
                        allCardsOfSelector.add(card);
                    }
                }
            }
        }
        return allCardsOfSelector;
    }
    
    /**
     * Calculate the player points based on the cards currently in the hand of the player. First it goes through all
     * the cards with blocking effects and makes a set of cards which are not blocked. It then adds up the base values
     * of the cards left and also applies their effects.
     * @param player Id of the player you want to get the points of.
     * @return The points of the player as an int.
     */
    @Override
    public int getPlayerPoints(int player){
        ArrayList<Card> resultHand = new ArrayList<>(Arrays.asList(Arrays.copyOf(hand[player-1], hand[player-1].length)));
        for (int i = 0; i < cardsInHand; i++) {
            Set<Effect> effectSet = hand[player-1][i].getEffects();
            for (Effect effect : effectSet){
                switch(effect.getType()){
                    case BLOCKED_IF_WITH:
                        if(getCardsOfSelectorInHand(player, effect.getSelector()).size()>0){
                            resultHand.set(i,null);
                        }
                        break;
                    case BLOCKED_IF_WITHOUT:
                        if(getCardsOfSelectorInHand(player, effect.getSelector()).size()==0){
                            resultHand.set(i,null);
                        }
                        break;
                    case BLOCKS_EVERY:     
                        ArrayList <Card> blockedCards = getCardsOfSelectorInHand(player, effect.getSelector());
                        for(int j = 0; j<blockedCards.size();j++){
                            for(int n = 0;n<resultHand.size();n++){
                                if(blockedCards.get(j)==resultHand.get(n)){
                                    resultHand.set(n,null);
                                }
                            }
                        }
                        break;
                    default:
                        break;    
                }
            }
        }
        resultHand.removeAll(Collections.singleton(null));
        int basePoints=0;
        for (int i = 0; i <resultHand.size(); i++) {
            Set<Effect> effectSet = hand[player-1][i].getEffects();
            basePoints+=resultHand.get(i).getBaseValue();
            for (Effect effect : effectSet){
                switch(effect.getType()){
                    case POINTS_FOREACH:
                        basePoints+=effect.getEffectValue()*(getCardsOfSelectorInHand(player, effect.getSelector()).size());
                        break;
                    case POINTS_SUM_BASEVALUES:
                        ArrayList <Card> cardsForBaseValues = getCardsOfSelectorInHand(player, effect.getSelector());
                        for (Card c : cardsForBaseValues){
                            basePoints += c.getBaseValue();;
                        }
                        break;
                    case POINTS_FLAT_DISJUNCTION:
                        if(effect.getEffectValue()*(getCardsOfSelectorInHand(player, effect.getSelector()).size())>0){
                            basePoints += effect.getEffectValue();
                        }
                        break;
                    case POINTS_FLAT_CONJUNCTION:
                        ArrayList <Card> cardsOfSelectorInHand = getCardsOfSelectorInHand(player, effect.getSelector());
                        ArrayList <Card> allCardsOfSelector = getAllCardsOfSelector(effect.getSelector());
                        if(cardsOfSelectorInHand.size()==allCardsOfSelector.size()){
                            basePoints += effect.getEffectValue();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return basePoints;
    }
}