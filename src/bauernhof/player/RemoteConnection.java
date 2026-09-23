package bauernhof.player;

import java.io.IOException;
import java.net.Socket;

import bauernhof.graphics.GUI;
import bauernhof.preset.GameConfiguration;
import bauernhof.preset.GameConfigurationException;
import bauernhof.preset.GameConfigurationParser;
import bauernhof.preset.ImmutableList;
import bauernhof.preset.Move;
import bauernhof.preset.PlayerGUIAccess;
import bauernhof.preset.PlayerType;
import bauernhof.preset.Settings;
import bauernhof.preset.card.Card;
import bauernhof.preset.networking.C2SConnection;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Implements the logic of REMOTE players left abstract by the conneciton used by REMOTE players. Note that REMOTE is not a type in the same
 * sense as HUMAN or RANDOM_AI are; REMOTE is the converse to local (i.e. not-REMOTE), but does not indicate the type of the player. This class
 * cannot be extended, because there is nothing to be added to this within the context of the game rules.
 * @author Tobias Kai Lorenz Plattner
 */
public final class RemoteConnection extends C2SConnection{
    //Variables are not static, s.t. multiple REMOTEs can join from the same PC.
    /**
     * Variable to store the Player object which will be created depending on the player. Only extensions of AbstractPlayer are allowed.
     */
    private AbstractPlayer player;
    /**
     * Type of the player as specified in the settings with which the program is executed.
     */
    private PlayerType type;
    /** 
     * Name of player as specified in the settings with which the program is executed. Takes only the first one specified and ignores the rest.
     */
    private String name;
    /**
    * Value to indicate whether the GUI should be displayed.
    */
    private boolean showGUI;
    /**
    * GUI of the Remote Player.
    */
    private PlayerGUIAccess gui; //TODO: check what happens if this is static and multiple remotes join from the same client. Test this in general, also for not static
    /**
    * Player names passed by onInit, used for winner calculation.
    */
    private ImmutableList<String> playerNames;
    
    /***** Constructor *****/
    /**
     * Constructor of the RemoteConnection object.
     * @param connection The connection object that is used by this RemoteConnection.
     * @param gameConfigurationParser The parser used by the server, containing all relevant game information.
     * @param projectName The name of the project.
     * @param settings The settings with which the program of the client are executed.
     * @throws IOException when a network error occurred.
     */
    public RemoteConnection(Socket connection, GameConfigurationParser gameConfigurationParser, String projectName, Settings settings, int nrOfRounds) throws IOException {
        super(connection, gameConfigurationParser, projectName);
        type = settings.playerTypes.get(0);
        name = settings.playerNames.get(0);
        showGUI = settings.showGUI;
        //create Player object depending on type
        switch(type){
            case ADVANCED_AI:
                throw new UnsupportedOperationException("ADVANCED_AI not implemented yet");
            case HUMAN:
                player = new HumanPlayer(name, null, true, this);
                break;
            case RANDOM_AI:
                player = new RandomAI(name);
                break;
            case REMOTE:
                throw new IllegalArgumentException("The type of REMOTE player needs to be something else than REMOTE!");
            case SIMPLE_AI:
                player = new SimpleAI(name);
                break;
            default:
                throw new IllegalArgumentException("Invalid player type was specified!");
        }
    }

    /**
     * Method that gets the players name.
     * @return Name of the player.
     */
    @Override
    protected String onGetName() throws Exception {
        return player.getName();
    }

    /**
     * Method that gets the Score of the Player.
     * @return Score of the player.
     * @exception NullPointerException is thrown when {@link bauernhof.player.AbstractPlayer getScore()} throws a NullPointerException.
     */
    @Override
    public int onGetScore() throws NullPointerException {
        return player.getScore();
    }

    /**
     * Method that initialises the REMOTE. Uses {@link bauernhof.player.AbstractPlayer init()}.
     * @param config determines the start configuration of the game and is used to create the players gameboard.
     * @param drawPile specifies the initial state of the draw pile.
     * @param playerNames specifies the names of players in the game. Used to get the number of players in the game and winner calculation.
     * @param playerID specifies the id of the player initialized.
     * @exception IllegalStateException is thrown when {@link bauernhof.player.AbstractPlayer init()} throws one.
     * @exception GameConfigurationException is thrown when {@link bauernhof.player.AbstractPlayer init()} throws one.
     * @exception IllegalArgumentException is thrown when {@link bauernhof.player.AbstractPlayer init()} throws one.
     */
    @Override
    protected void onInit(GameConfiguration config, ImmutableList<Card> drawPile, ImmutableList<String> playerNames, int playerID) throws IllegalStateException, GameConfigurationException, IllegalArgumentException{
        this.playerNames = playerNames;
        int nrOfPlayers = playerNames.size();
        player.init(config, drawPile, nrOfPlayers, playerID);
        if (showGUI ||   this.type == PlayerType.HUMAN)
            this.gui = new GUI(player.getGameBoard(), playerNames);
    }

    /**
     * Method that requests the REMOTE to make a move.
     * @return return value of {@link bauernhof.player.AbstractPlayer request()}.
     * @exception IllegalStateException when {@link bauernhof.player.AbstractPlayer request()} throws an IllegalStateException.
     */
    @Override
    protected Move onRequest() throws IllegalStateException {
        return player.request();
    }

    /**
     * Method that updates the REMOTE player.
     * @param arg0 Move that was made and according to which the REMOTE needs to be updated.
     * @exception IllegalMoveException is thrown when {@link bauernhof.player.AbstractPlayer update()} throws one.
     * @exception IllegalStateException is thrown when {@link bauernhof.player.AbstractPlayer update()} throws one.
     */
    @Override
    protected void onUpdate(Move arg0) throws IllegalMoveException, IllegalStateException {
        player.update(arg0);
        if (gui != null)
            ((GUI) gui).update(); //Cast necessary because PlayerAccessGUI does not enforce update() method and we use only GUI class anyways
    }

    /**
     * Method that verifies the Game. If no exception is thrown, the game is regarded as verified.
     * @param listFinalScores is the list of final scores used to verify the game after it ended.
     * @exception IllegalStateException is thrown if {@link bauernhof.player.AbstractPlayer verifyGame(listFinalScores)} throws it.
     * @exception GameNotEndedException is thrown if {@link bauernhof.player.AbstractPlayer verifyGame(listFinalScores)} throws it.
     * @exception IllegalArgumentException is thrown if {@link bauernhof.player.AbstractPlayer verifyGame(listFinalScores)} throws it.
     * @exception Exception is thrown because {@link bauernhof.graphics.GUI.gameOver} requires it.
     */
    @Override
    protected void onVerifyGame(ImmutableList<Integer> listFinalScores) throws IllegalStateException, GameNotEndedException, IllegalArgumentException, Exception {
    	//verify the game
        try{
            player.verifyGame(listFinalScores);
        } catch (IllegalStateException e){
            ((GUI) this.gui).showCheated();
            throw new IllegalStateException("It appears someone cheated");
        }
        System.out.println("Game done, scores are calculated... ");
        
        //caculate winners on client side if no exceptions are thrown
        ArrayList<String> winners = new ArrayList<String>();
        for(int i = 0; i <  this.player.getGameBoard().getNumberOfPlayers(); i++){
            int isGreaterEqual = 0;

            for (int j = 0; j <  this.player.getGameBoard().getNumberOfPlayers(); j++){
                if(listFinalScores.get(i) >= listFinalScores.get(j))
                    isGreaterEqual++;
            }

            if (isGreaterEqual ==  this.player.getGameBoard().getNumberOfPlayers()){
                winners.add(playerNames.get(i));
            }
        }
        
        ArrayList<Integer> winnersScores = new ArrayList<Integer>();
        for (int j = 0; j < Collections.frequency(listFinalScores, Collections.max(listFinalScores)); j++)
            winnersScores.add(Collections.max(listFinalScores));
	
        //use arrays because GUI class does
        String [] winnerNames = new String[winners.size()];
        
        for (int i = 0; i < winners.size(); i++)
            winnerNames[i] = winners.get(i);
        int [] finalScores = new int[winnersScores.size()];
        for (int i = 0; i < winnersScores.size(); i++)
            finalScores[i] = winnersScores.get(i);
        if (gui != null){
            ((GUI) this.gui).gameOver(winnerNames, finalScores);
        }

        System.out.println("Drum roll...");
        try{
            TimeUnit.MILLISECONDS.sleep(5000); //more time for GUI aswell
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //temporary print statements
        if (winners.size() > 1){
            System.out.println("The winners are: ");
            for (int i = 0; i < winners.size(); i++)
                System.out.print("CONGRATULATIONS TO " + winners.get(i));
        } else System.out.println("CONGRATULATIONS TO " + winners.get(0));
    }
    
    /**
    * Method that returns the GUI of the remote player.
    * @return gui The GUI of the remote player, possibly null.
    */
    protected PlayerGUIAccess getGUI(){
    	return gui;	
    }

}
