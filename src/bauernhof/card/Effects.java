package bauernhof.card;

import java.util.Set;

 /* Either is a generic class representing a data type that can hold either a left or a right element, i.e. the choice between two 
 alternatives.*/
import bauernhof.preset.Either;
import bauernhof.preset.card.Card;
import bauernhof.preset.card.CardColor;
import bauernhof.preset.card.Effect;
import bauernhof.preset.card.EffectType;


/**
 * Class to implement effects for cards. Effects have a certain type, but are not determined completely by their type. It only determines
 * how the effect is to be evaluated. The (base)value then furthermore determines the actual impact of the effect.
 * @author Tobias Kai Lorenz Plattner
 */
public class Effects implements Effect {

    /******************** Class Variables ********************/
    /**
     * The selector set contains the cards which are affected by the effect.
     */
    private Selector<Either<Card, CardColor>> selector;
    /**
     * The effect value.
     */
    private int value;
    /**
     * The type of the effect, has to be in the enumeration EffectType.
     */
    private EffectType type;

    /******************** Class Methods ********************/
    /**
     * Constructor for an Effect. Automatically creates a selector for this effect.
     * @param value is the effect value
     * @param type is the effect type (check if it is a valid one, if not done so in Parser)
     */
    public Effects(int value, EffectType type){
        this.value = value;
        this.type = type;
        this.selector = new Selector<Either<Card, CardColor>> ();
    }

    /******************** Interface methods ********************/
    /**
     * Method that returns the value associated with this effect object.
     * @return effect value of this effect
     */
    @Override
    public int getEffectValue() {
        return this.value;
    }

    /**
     * This method returns the selector associated with this effect.
     * @return Set collection of instances of Either&lt;Card, Cardcolor&gt;, i.e. a set in which each element is either a Card, or a CardColor object
     * {@link bauernhof.preset.Either constructor}
     * {@link bauernhof.card.Selector getSelector}     
     */
    @Override
    public Set<Either<Card, CardColor>> getSelector() {
        return this.selector.getSelector();
    }

    /**
     * This method returns the effecttype of this effect.
     * @return Effecttype of this effect.
     */
    @Override
    public EffectType getType() {
        return this.type;
    }
}