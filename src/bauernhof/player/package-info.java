/**
 * Provides the classes and logic for HUMAN, RANDOM_AI, SIMPLE_AI and REMOTE players, for the game logic see {@link bauernhof.logic}.
 * Implements an abstract class for all player types, as well as the classes needed to implement the network game. While there is a class
 * {@link bauernhof.preset.networking.RemotePlayer}, the class {@link bauernhof.player.RemoteConnection} implements the logic for its methods.
 * Furthermore there are two custom exception classes {@link bauernhof.player.GameNotEndedException} and {@link bauernhof.player.IllegalMoveException}
 * that are used within this package.
 */
package bauernhof.player;