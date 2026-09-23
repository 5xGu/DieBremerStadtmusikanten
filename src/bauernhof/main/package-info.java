/**
 * This package contains the main class that handles and combines the different packages and classes to create the game.
 * To play the game, you need to execute {@link bauernhof.main.Main} and specifiy the settings for that game.
 * If the game is supposed to be a network game, both client and server need to do this, but with different settings. For example, the
 * client needs to specify the hostname of the server. For more details see {@link bauernhof.preset}.
 * 
 * A tournament for local games is implemented and can be selected via the settings. This is also possible for REMOTE games, 
 * and the mode works for it. However, the clients would need to connect again after each round. The tournament statistics are handled
 * by {@link bauernhof.main.TournamentStats}, where, for better formatting, the printing of basic stats of a tournament is handled
 * in the constructor already. It is possible, however, to call further methods for more individual reports.
 */
package bauernhof.main;