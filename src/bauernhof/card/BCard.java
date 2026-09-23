package bauernhof.card;

import java.util.Set;
import java.util.HashSet;
import bauernhof.preset.card.Card;
import bauernhof.preset.card.CardColor;
import bauernhof.preset.card.Effect;

 /**
  * Class for the playing cards of the game.
  * @author Eva Ristevska
  */
public class BCard implements Card{
	/**
	 * Unique name of the card.
	 */
    private String name;

	/**
	 * The base value of the card.
	 */
    private int baseValue;

	/**
	 * Each card has a CardColor.
	 */
    private CardColor cardColor;

	/**
	 * Each card has an image
	 */
    private String image;

	/**
	 * Set&lt;Effect> for the effects for each card
	 */
    private Set<Effect> effects;

    /**
	 * Constructor that creates a card with the following parameters
	 * @param name unique name for the card
	 * @param baseValue base value of the card
	 * @param cardColor the card color of the card
	 * @param image the image of the card as a String 
	 * @param effects Set of the object Effect for the effects of the card  
	 */
    public BCard(String name, int baseValue, CardColor cardColor, String image, Set<Effect> effects){
        this.name = name;
        this.baseValue = baseValue;
        this.cardColor = cardColor;
        this.image = image;
        this.effects = effects;
    }

    /**
	 * Get the name of the card.
	 *
	 * <p>
	 * Card names must be unique in the game and thus can be used as an identifier.
	 * </p>
	 *
	 * @return The name of the card.
	 */
	public String getName(){
        return this.name;
    };

	/**
	 * Get the base value of the card.
	 *@return The base value of the card.
	 */
	public int getBaseValue(){
        return this.baseValue;
    }

	/**
	 * Get the cardcolor.
	 * @return The cardcolor.
	 * @see bauernhof.preset.card.CardColor
	 */
	public CardColor getColor(){
        return this.cardColor;
    };

	/**
	 * Get the name of the image that should be drawn on the card.
	 * @return The name of the image.
	 */
	public String getImage(){
        return this.image;
    };

	/**
	 * Get the effects of this card.
	 * @return The effects.
	 * @see bauernhof.preset.card.Effect
	 */
	public Set<Effect> getEffects(){
        return new HashSet<Effect>(this.effects);
    };

    /**
	 * Set the effect of the Card
	 * @see bauernhof.preset.card.Effect
	 */
    public void addEffect(Effect e) {
        effects.add(e);
    }
    /*
     * Compares two cards by their names.
     * @return true if the cards are the same, else false
     */
	@Override
	public boolean equals(Object obj){
		if(obj instanceof BCard)
			return getName().equals(((BCard)obj).getName());
		else return false;
			
	}
	/*
	 * Function for the hash code value for the object card
	 * @return the hash code value
	 */
	@Override
	public int hashCode(){
		return getName() != null ? getName().hashCode() : 0;
	}
}
