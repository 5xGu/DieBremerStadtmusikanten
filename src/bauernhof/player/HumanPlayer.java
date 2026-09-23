package bauernhof.player;

import bauernhof.preset.Move;
import bauernhof.preset.PlayerGUIAccess;

/**
 * Class for HUMAN type players that cannot be extended, because there is nothing to be added to this within the context of the game rules.
 * @author Tobias Kai Lorenz Plattner
 */
public final class HumanPlayer extends AbstractPlayer {

    /**
     * HUMANs need a graphical interface that handles making moves for them. This is guaranteed by the interface PlayerGUIAccess.
     */
    private PlayerGUIAccess gui = null;
    /**
     * If the HUMAN is also REMOTE this variable stores the connection address.
     */
    private RemoteConnection conn = null;
    /**
     * Variable to indicate whether HUMAN is REMOTE.
     */
    private boolean isRemote;
    /**
     * Constructor for HUMAN calling AbstractPlayer constructor. If isRemote == false, then HUMAN is local player and needs to be passed
     * a {@link bauernhof.preset.PlayerGUIAccess}. If isRemote == true, HUMAN needs to be passed a {@link bauernhof.preset.networking.C2SConnection}.
     * @param name of the HumanPlayer object
     */
    public HumanPlayer(String name, PlayerGUIAccess gui, boolean isRemote, RemoteConnection c2Sconn){
        super(name);
        if (!isRemote)
        	this.gui = gui;
        this.isRemote = isRemote;
        if (isRemote == true){
        	if(c2Sconn == null)
        		throw new IllegalArgumentException("Remote Player needs a Connection object!");
        	else this.conn = c2Sconn;
	    }
    }
    
    /**
     * Method that returns a Move object, by returning the return value of {@link bauernhof.preset.PlayerGUIAccess requestMoveFromCurrentHumanPlayer()}
     * @return Move HUMAN decided for.
     * @exception IllegalStateException is thrown if {@link bauernhof.player.AbstractPlayer request()} throws one.
     */
    @Override
    public Move getMove() throws IllegalStateException {
        Move move = null;
        if (isRemote)
        	move = conn.getGUI().requestMoveFromCurrentHumanPlayer();
        else move = gui.requestMoveFromCurrentHumanPlayer();
        
        this.getGameBoard().executeMove(move);

        return move;
    }
}
