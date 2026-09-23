package bauernhof.main;

import java.util.ArrayList;
import java.util.Collections;

import bauernhof.preset.ImmutableList;

/**
 * Class to handle the statistics for the tournament, especially and foremost the output on the terminal, s.t. main class does not get
 * too clustered.
 * @author Tobias Kai Lorenz Plattner
 * @author Eva Ristevska
 */
public class TournamentStats{
    /**
     * List that scores the scores of the players.
     */
    private ArrayList<Integer> statistics;
    /**
     * Variable to store the number of players, needed because there can be more than one winner.
     */
    private int nrOfPlayers;
    /**
     * Names of the players in the tournament.
     */
    private ImmutableList<String> playerNames;

    /**
     * Constructor for the statistics of a tournament. Automatically prints the winners and scores of every game played as well as the
     * winners of the entire tournament.
     * @param stats Of the tournament, i.e. scores of every game played.
     * @param nrOfPlayers The number of players that participated in the tournament.
     * @param playerNames The names of the players who participated in the tournament.
     */
    public TournamentStats(ArrayList<Integer> stats, int nrOfPlayers, ImmutableList<String> playerNames){
        this.statistics = stats;
        this.nrOfPlayers = nrOfPlayers;
        this.playerNames = playerNames;

        System.out.println("");
        System.out.println("STATS OF THE TOURNAMENT:");
        System.out.println("");

        printWinnersAndScoresOfGames();
        System.out.print("");

        printTournamentWinners();
        System.out.println("");
    }

    /***** Methods to calculate the winners *****/
    /**
     * Method to calculate the winners of the tournament.
     * @return A list containing the names of the winners of the tournament.
     */
    public ArrayList<String> totalWinners(){
        ArrayList<String> totalWinners = new ArrayList<String>();
        ArrayList<String> allWinners = allWinners();
        int highestNrOfWins = highestNrOfWins();
        for (int i = 0; i < nrOfPlayers; i++){
            if(Collections.frequency(allWinners, playerNames.get(i)) == highestNrOfWins)
                totalWinners.add(playerNames.get(i));
        }

        return totalWinners;
    }

    /**
     * Method to get the highest number of wins achieved in the tournament.
     * @param allWinners The list containing all winners.
     * @return The highest number of wins achieved by a player.
     */
    private int highestNrOfWins(){
        ArrayList<String> allWinners = allWinners();
        ArrayList<Integer> NrOfWins = new ArrayList<Integer>();
        for (int i = 0; i < playerNames.size(); i++){
            NrOfWins.add(Collections.frequency(allWinners, playerNames.get(i)));
        }
        int maxNrOfWins = Collections.max(NrOfWins);

        return maxNrOfWins;
    }
    /**
     * Method to calculate the names of all players who won a game.
     * @return A list containing the names of all players who won at least one game.
     */
    private ArrayList<String> allWinners(){
        ArrayList<String> allWinners = new ArrayList<String>();
        for(int i = 0; i < (statistics.size()/nrOfPlayers); i++){
            allWinners.addAll(winnersOfGame(i));
        }

        return allWinners;
    }

    /**
     * Method to find the winner of a given game.
     * @param game for which the winner should be found.
     * @return The winner of the specified game.
     */
    private ArrayList<String> winnersOfGame(int game){
        ArrayList<String> winners = new ArrayList<String>();
        int winScore = maxOfSingleGame(game);
        for(int i = 0; i < nrOfPlayers; i++){
            if(statistics.get(game*nrOfPlayers+i) == winScore)
                winners.add(playerNames.get(i));
        }
        return winners;
    }

    /**
     * Method to get the max score of a single game.
     * @param game The game ID for which the max score should be calculated.
     * @return The max score for the specified game.
     */
    private int maxOfSingleGame(int game){
        int max = 0;

        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int j = 0; j < nrOfPlayers; j++){
            temp.add(statistics.get(game*nrOfPlayers+j));
        }
        max = Collections.max(temp);

        return max; 
    }

    /***** Methods to print *****/

    /**
     * Method to print the winners of the tournament.
     */
    public void printTournamentWinners(){
        ArrayList<String> totalWinners = totalWinners();
        System.out.println("**********");

        if (totalWinners.size() == 1){
            System.out.print("The winner of the Tournament is: " + totalWinners.get(0));
        } else {
            System.out.print("The winners of the tournament are: ");
            for (int i = 0; i < totalWinners.size(); i++){
                System.out.print(totalWinners.get(i) + " ");
            }
        }
        System.out.println("");
        System.out.println("**********");
    }

    /**
     * Method to print the winners and scores of all games played in the tournament.
     */
    public void printWinnersAndScoresOfGames(){
        for(int i = 0; i < (statistics.size()/nrOfPlayers); i++){
            printWinnersAndScoresOfSingleGame(i);
        }
    }

    /**
     * Method to print the winners and scores of a single game.
     * @param game The game for which the winners and scores should be printed.
     */
    protected void printWinnersAndScoresOfSingleGame(int game){
        printScoreOfSingleGame(game);
        printWinnersOfSingleGame(game);
    }

    /**
     * Method to print all players who won at least one game as well as the game(s) they won.
     */
    public void printWinnersOfGames(){
        for(int i = 0; i < (statistics.size()/nrOfPlayers); i++){
            printWinnersOfSingleGame(i);
        }
    }

    /**
     * Method to print the winners of a single game.
     * @param game The game for which the winners should be printed.
     */
    protected void printWinnersOfSingleGame(int game){
        if (game*nrOfPlayers > statistics.size() || game*nrOfPlayers < 0)
            throw new IllegalArgumentException("This game was not part of the tournament");
        ArrayList<String> winnersOfSingleGame = winnersOfGame(game);
        if (winnersOfSingleGame.size() == 1){
            System.out.println("Winner of game " + (game+1) + " is: " + winnersOfSingleGame.get(0));
        } else {
            System.out.print("Winners of game " + (game+1) + " are:");
            for(int i = 0; i < winnersOfSingleGame.size(); i++){
            System.out.print(" " + winnersOfSingleGame.get(i));
            }
        }
        System.out.println();
    }
    
    /**
     * Method to print out the scores of every player for every game played.
     */
    public void printScoresOfGames(){
        for(int i = 0; i < (statistics.size()/nrOfPlayers); i++){
            printScoreOfSingleGame(i);
        }
    }

    /**
     * Method to print out the stats for a single game played in the tournament.
     * @param game The game ID for which the stats should be printed.
     */
    protected void printScoreOfSingleGame(int game){
        if (game*nrOfPlayers > statistics.size() || game*nrOfPlayers < 0)
            throw new IllegalArgumentException("This game was not part of the tournament");
        System.out.print("Scores of game: " + (game + 1));
        for(int j = 0; j < nrOfPlayers; j++){
            System.out.print(" " + playerNames.get(j) + ": " + statistics.get((game*nrOfPlayers)+j) + " ");
        }
        System.out.println();
    }
}