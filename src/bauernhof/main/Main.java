package bauernhof.main;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import bauernhof.preset.*;
import bauernhof.preset.card.*;
import bauernhof.preset.networking.C2SConnection;
import bauernhof.preset.networking.S2CConnection;

import bauernhof.configuration.BremerConfigurationParser;
import bauernhof.board.GameBoard;
import bauernhof.player.*;
import bauernhof.graphics.GUI;

import java.io.File;

/**
 * Main programm that is called when the game shall be played.
 * @author Eva Ristevska: parse the settings, initialise players, basic structure of the server game logic, calculation of winners, the auxiliary methods
 * @author Tobias Plattner: network game, basic structure of the server game logic, calculation of winners
 */

public class Main{
	/**
	 * Name of the project
	 */
	private static String projectName = "Die Bremer Stadtmusikanten";
	/**
	 * Variable for parsed settings
	 */
	public static Settings settings;
	/**
	 * Configuration used for this game
	 */
	private static GameConfiguration config;
	/**
	 * The Board of the server or the local game
	 */
	private static GameBoard board;
	/**
	 * List of Cards that represent the drawPile
	 */
	private static ImmutableList<Card> drawPile;
	/**
	 * Number of players for the game
	 */
	private static int numPlayers;
	/**
	 * The GUI of the server or the local game
	 */
	private static GUI graphic;
	/**
	 * List of Players of the server or the local game
	 */
	private static List<Player> players;
	/**
	 * List of PlayerTypes of the server or the local game
	 */
	private static List<PlayerType> playerTypes;
	/**
	* List of PlayerNames used for initialisation of remote player and winner calculation
	*/
	private static ImmutableList<String> playerNames;
	/**
	 * If this programm is the server, we need a ServerSocket object
	 */
	private static ServerSocket serverSocket;
	/**
	 * socket for either this client or this server
	 */
	private static Socket socket;
	//I **THINK** we will use the socket constructor of java.net.Socket taking hostname and port, as both are specified by the settings
	/**
	 * port number for network games, initialised later on depending on settings
	 */
	private static int port;
	/**
	 * Hostname to which the client shall connect, iff settings specify this as a client
	 */
	private static String hostname;
	/**
	 * Variable to distinguish local game from client
	 */
	private static boolean isClient = false;
    /**
     * Specified number of rounds
     */
    private static int numTournamentRounds;
    /**
     * Statistics variable for tournament game
     */
    private static ArrayList<Integer> statistics = new ArrayList<Integer>();

	/*
	 * A method to initialize the instances inn the main class, players and the game board
	 */
	public static void initialize(String[] args) throws Exception, InterruptedException {
		
		/***** Variables needed for the argument parser *****/
		List<String> names = new ArrayList<>();
		names.add("Maxim Barnstorf");
		names.add("Valeria Stankova");
		names.add("Eva Ristevska");
		names.add("Tobias Kai Lorenz Plattner");
		List<OptionalFeature> features = new ArrayList<>();
        features.add(OptionalFeature.TOURNAMENTS);
		features.add(OptionalFeature.SIMPLE_AI);
		settings = new ArgumentParser(args, projectName, "1.0.0", names, features);

		/***** Information Server and Clients need *****/
		GameConfigurationParser parser = new BremerConfigurationParser();
		
		//needed for isServer decision
		playerTypes = settings.playerTypes;
		
		if (settings.port != 0) //there is a port 0, but we would not want to use it
			port = settings.port;

		if (settings.connectToHostname != null){
			hostname = settings.connectToHostname;
			if ((playerTypes.size() == 1))
				isClient = true;

		} else {
				if (playerTypes.contains(PlayerType.REMOTE)){
					serverSocket = new ServerSocket(port);
					serverSocket.setSoTimeout(Integer.MAX_VALUE); //timeout of accept() method
					hostname = null;
				}
		}

		if(isClient){
			socket = new Socket(hostname, port);
			C2SConnection c2SConnection = new RemoteConnection(socket, parser, projectName, settings, numTournamentRounds);
			c2SConnection.handlePackets();
			//s.t. game ending isnt that sudden
			TimeUnit.MILLISECONDS.sleep(1000);
		
			//close the game
			System.out.println("Thank you for playing, we hope you had fun! See you next time. Game closes in 10 seconds!");	
			TimeUnit.MILLISECONDS.sleep(10000);
			
			System.exit(0);
			return;
			
		} else {  
			/**** Validate the settings for local game or server ****/
			if (settings.gameConfigurationFile == null){
				File defaultConfig = new File("defaultConfig/default.xml");
				config = parser.parse(defaultConfig);
			}

			else config =  parser.parse(settings.gameConfigurationFile);

			if (config == null)
				throw new Exception("Something went wrong parsing the GameConfiguration.");
				
			//Get the number of players 
			numPlayers = settings.playerNames.size();
			
			/** Create shuffled draw Pile */
			Set<Card> cards = config.getCards();
			List<Card> cardList = new ArrayList<>(cards);
			Collections.shuffle(cardList);
			drawPile = new ImmutableList<Card>(cardList);
			
			board = new GameBoard(config, numPlayers, drawPile);
			//Server checks for valid board
			if(board.getDepositPile().length != 0)
				throw new IllegalStateException("The discard pile is not empty, PLEASE DO NOT PUT CARDS BEFORE PLAYING THE GAME!");
			if(board.getDepositPile() == null)
				throw new IllegalStateException("There is no discard pile specified");
		}

		/***** From here on out, only server executes the code because client handle method is blocking *****/
		
        numTournamentRounds = settings.numTournamentRounds;

		//playerNames needed for player instantiation and list to keep track of players
		List<String> playernames = settings.playerNames;
        playerNames = new ImmutableList<>(playernames);
		players = new ArrayList<>();

        if(settings.showGUI)
			graphic = new GUI(board, playerNames);
		//Create the players
		for(int i = 0; i<numPlayers; i++){
			PlayerType playerType = playerTypes.get(i);
			switch(playerType){
				case HUMAN:
					players.add(new HumanPlayer(playerNames.get(i), graphic, false, null));
					break;
				case RANDOM_AI:
					players.add(new RandomAI(playerNames.get(i)));
					break;
				case SIMPLE_AI:
					players.add(new SimpleAI(playerNames.get(i)));
					break;
				case ADVANCED_AI:
					throw new IllegalArgumentException("ADVANCED_AI coming soon!");
				case REMOTE:
						try{
							socket = serverSocket.accept(); //Accept()Returns ClientSocket if connection happens, blocks code until it does or timeout is reached
							S2CConnection s2CConnection = new S2CConnection(socket);
							ImmutableList<String> playerNamesImmutable = new ImmutableList<String>(playerNames);
							s2CConnection.setPlayerNames(playerNamesImmutable); //required before init of remote player
							players.add(s2CConnection.getRemotePlayer());
						} catch (SocketTimeoutException timeout){
							System.out.println("A remote player could not connect in time.");
						}
					break;
				default:
					throw new IllegalArgumentException("The given Player Type is not defined in enum PlayerType");
			}
		}
		//intialise the players
		for(int j = 0; j < players.size(); j++){
			players.get(j).init(config, drawPile, players.size(), j+1);
		}

	}

	/**
	 * Method that regulates the Gameplay
	 * @throws Exception if an illegal move was made, a player had multiple moves or when the game is over.
	 * @throws InterruptedException when {@link TimeUnit.MILLISECONDS.sleep()} throws one.
	 */
	public static void run() throws Exception, InterruptedException {
		while(!board.isGameOver()){
			//moves
			Player p = players.get(board.getPlayerOnTurn() - 1);
			Move move = p.request();
			board.executeMove(move);
			if(isAIPlayer(playerTypes.get(board.getPlayerOnTurn() - 1)))
  					TimeUnit.MILLISECONDS.sleep(settings.delay);

			//update
			if(graphic != null){
				graphic.update();
			}
			for(Player p2: players){
				if (p != p2)
					p2.update(move);
			}
		}
	}

	/**
	 * This method evaluates the game after it ended and calculates the winner(s)
	 * @throws Exception to cover especially the player methods throwing exceptions. Not sure why this is needed, as their
	 * exceptions are handled explicitly.
	 * @throws IllegalStateException when the game is not over yet.
	 * @throws InterruptedException when {@link TimeUnit.MILLISECONDS.sleep()} throws one.
	 */
	public static void evaluteGame() throws Exception, IllegalStateException, InterruptedException{
		if (!board.isGameOver())
			throw new IllegalStateException("This method may only be called after the game has ended");

		System.out.println("Game done, calculating scores... ");

        //Get scores
		ArrayList<Integer> listFinalScores =  new ArrayList<Integer>();
		for(Player pl: players){
			listFinalScores.add(pl.getScore());
		}

		//Calculate the winners
		ArrayList<String> winners = new ArrayList<String>();
		ArrayList<Integer> winnersScores = new ArrayList<Integer>();
		for(int i = 0; i < board.getNumberOfPlayers(); i++){
			int isGreaterEqual = 0;
			for (int j = 0; j < board.getNumberOfPlayers(); j++){
				if(listFinalScores.get(i) >= listFinalScores.get(j))
					isGreaterEqual++;
			}
			if (isGreaterEqual == board.getNumberOfPlayers()){
				winners.add(players.get(i).getName());
				winnersScores.add(players.get(i).getScore());
			}
		}

		//prior to winner calc check if game is valid 
		for(Player pl: players){
			try{
				pl.verifyGame(new ImmutableList<>(listFinalScores));
			} catch (IllegalStateException e){
				graphic.showCheated();
				TimeUnit.MILLISECONDS.sleep(12000);
				System.exit(0);
			}
		}

		//use arrays because GUI does
        String [] winnerNames = new String[winners.size()];
        for (int i = 0; i < winners.size(); i++)
            winnerNames[i] = winners.get(i);
        int [] finalScores = new int[winnersScores.size()];
        for (int i = 0; i < winnersScores.size(); i++)
            finalScores[i] = winnersScores.get(i);

		if (graphic != null){
				graphic.gameOver(winnerNames, finalScores);
		}
		
        //for tournament mode
        if(numTournamentRounds != 0)
            statistics.addAll(listFinalScores);

		//dramatic effect
		System.out.println("Drum roll...");
			if(graphic != null){ //give additional time for winner display
					TimeUnit.MILLISECONDS.sleep(5000);
				
		}

		TimeUnit.MILLISECONDS.sleep(2000);

		//temporary print statements, maybe leave them in so even if no GUI is present the winner is announced
		if (winners.size() > 1){
			System.out.println("The winners are: ");
			for (int i = 0; i < winners.size(); i++)
				System.out.println("CONGRATULATIONS TO... " + winners.get(i) + "!!!");
		} else System.out.println("CONGRATULATIONS TO... " + winners.get(0) + "!!!");

		//s.t. game ending isnt that sudden
		TimeUnit.MILLISECONDS.sleep(1000);
		
	}
		

	public static void main(String[] args) throws Exception, InterruptedException{
		try {
			initialize(args);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		if(!isClient){
            //only one round
            if (numTournamentRounds == 0){
                try {
                    run();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                try{
                    evaluteGame();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
            //multiple rounds
            /* Disclaimer: 
            Only for local games as agreed upon with Leo Pflug. For tournaments involving REMOTE players the specs are contradictory.
            E.g. players verifyGame may be called only once in total, s.t. player have to be created and initialised again prior to the
            next round. BUT the remote player should not be created and initialised again. This is said explicitly in S2CConnection class
            for the setKeepAlive method.
            Now, we could implement a networkTournament. But this would not be playable, because we cannot change the Settings class and
            thus the mode could never be selected when wanted. 
            */
            else {
                for (int i = 0; i < numTournamentRounds; i++){
                    //for first round init already took place
                    if(i != 0){
                    	try {
                        	initialize(args);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    try {
                        run();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    try{
                        evaluteGame();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
				System.out.println("");
				System.out.println("Tournament is over, thank you for playing! Rendering statistics...");
				
				TimeUnit.MILLISECONDS.sleep(1500);
				
                //game and winner statistics get printed in constructor, but further methods could be used
                TournamentStats stats = new TournamentStats(statistics, numPlayers, playerNames);
				ArrayList<String> totalWinners = stats.totalWinners();
				//use array because GUI does
				Object [] temp = totalWinners.toArray();
				String [] totalWinnersNames = new String[temp.length];
				for (int i = 0; i < temp.length; i++)
					totalWinnersNames[i] = (String) temp[i];
				graphic.gameOverTournament(totalWinnersNames);
            }
			
			//close the game
			System.out.println("Thank you for playing, we hope you had fun! See you next time. Game closes in 10 seconds!");
			TimeUnit.MILLISECONDS.sleep(10000);
			
		}
		System.exit(0);
	}

	//-----------------------------------Auxiliary methods-----------------------------------


	/**
	 * Method to check if the current player is an AI Player
	 * @param pt List of player Types
	 * @return true if the players type's is AI
	 */
	private static boolean isAIPlayer(PlayerType pt){
		if(pt == PlayerType.RANDOM_AI || pt == PlayerType.ADVANCED_AI || pt == PlayerType.SIMPLE_AI){
			return true;
		}
		return false;
	}

}
