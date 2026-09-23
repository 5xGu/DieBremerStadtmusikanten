package bauernhof.configuration;

import java.util.Set;

import bauernhof.preset.card.Card;
import bauernhof.preset.card.CardColor;
import bauernhof.preset.GameConfiguration;

/**
 * Class to extract the needed information from the XML file, important for the game configuration.
 * @author Eva Ristevska
 */

public class BremerConfiguration implements GameConfiguration{

	/**
	 * Contains the description for the game.
	 */
	private String description;

	/**
	 * The number of allowed cards that each player can have per hand.
	 */
	private int numCardsPerPlayer;

	/**
	 * The maximum number of allowed cards in the deposition pile.
	 */
    private int numDepositionArea;

	/**
	 * Set of CardColor that are defined from the xml file.
	 */
    private Set<CardColor> cardColors;
	
	/**
	 * Set of Cards that are defined from the xml file.
	 */
    private Set<Card> cards;

	/**
	 * The file content of the xml file as a string.
	 */
	private String rawConfiguration;

	/*
	 * Default Constructor
	 */
	 public BremerConfiguration(){
	 }
	 
    /** 
     * Creates a BremerConfiguration object better with given parameters.
	 * @param descrption the game description.
	 * @param numCardPerPlayer number of cards a player can have per hand.
	 * @param numDepositionArea maximum number of allowed crads in the deposition area.
	 * @param cardColors a set of the defined card colors.
	 * @param cards a set of cards.
     */

    public BremerConfiguration(String description, int numCardsPerPlayer, int numDepositionArea, Set<CardColor> cardColors, Set<Card> cards, String rawConfiguration){
        this.description = description;
        this.numCardsPerPlayer = numCardsPerPlayer;
        this.numDepositionArea = numDepositionArea;
		this.cardColors = cardColors;
        this.cards = cards;
		this.rawConfiguration = rawConfiguration;
    }

    /**
	 * The description of the game configuration.
	 * @return The description-
	 */
	public String getConfigDescription(){
        return this.description;
    }

    /**
	 * The maximum number of cards in the deposition area.
	 *
	 * <p>
	 * The game ends as soon as the deposition area is full.
	 * </p>
	 * <p>
	 * Must be at least 2 and cannot be more than 12.
	 * </p>
	 *
	 * @return The number of slots in the deposition area.
	 */
	public int getNumDepositionAreaSlots(){
        return this.numDepositionArea;
    }

    /**
	 * The number of cards dealt to each player on the start of the game.
	 *
	 * <p>
	 * Must be at least 2 and cannot be more than 10.
	 * </p>
	 *
	 * @return The number of cards dealt to each player.
	 */
	public int getNumCardsPerPlayerHand(){
        return this.numCardsPerPlayer;	
    }
    /**
	 * All {@link CardColor CardColors}.
	 *
	 * <p>
	 * Every cardcolor must have a unique name.<br>
	 * Every cardcolor must be used by at least one card.
	 * </p>
	 * @return The cardcolors.
	 */
    public Set<CardColor> getCardColors(){		
		return this.cardColors;
    };
	/**
	 * All {@link Card Cards}.
	 *
	 * <p>
	 * Every card must have a unique name.<br>
	 * There must be at least ({@link #getNumDepositionAreaSlots()} + {@link #getNumCardsPerPlayerHand()} * 4) cards.
	 * </p>
	 * @return The card.
	 */
	public Set<Card> getCards(){
		return this.cards;
	}
	/**
	 * Get a card by its name.
	 * <p>
	 * This function is used by {@link bauernhof.preset.networking.S2CConnection}.
	 * </p>
	 * @param cardname The unique name of the card.
	 * @return The card or null if no card by that name was found.
	 */
	public Card getCardByName(String cardname){
		for(Card card: cards){
			if(card.getName().equals(cardname))
				return card;
		}
		return null;
	}

	/**
	 * The contents of the xml file that was used for creating this {@link GameConfiguration}.
	 * <p>
	 * This function is used by {@link bauernhof.preset.networking.S2CConnection}.
	 * </p>
	 * @return The raw xml of this configuration.
	 */
	 public String getRawConfiguration(){	
		return this.rawConfiguration;
	 }
}
