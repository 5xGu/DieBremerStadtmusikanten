package bauernhof.card;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

 /* Either is a generic class representing a data type that can hold either a left or a right element, i.e. the choice between two 
 alternatives.*/
import bauernhof.preset.Either;
import bauernhof.preset.card.Card;
import bauernhof.preset.card.CardColor;
    


/**
 * Class for Selector objects for effects of cards. For every effect, there is supposed to be a selector. This class extends the Either
 * class to ensure compatibility and enforce that the set representing the selector contains only the desired kinds of objects.
 * @author Tobias Kai Lorenz Plattner
 */
public class Selector<E extends Either<Card, CardColor>> {
    /**
     * It is required by the interface of Effects to have the getSelector method return a Set&lt;Either&lt;Card, CardColor>> object, which 
     * was taken into account here.
     */
    private Set<Either<Card, CardColor>> selector = new HashSet<>();


    /********** Empty/Default Constructor **********/
    /**
     * This is the default constructor for Selector objects. The actual initialization takes place using the add methods.
     * Protected because it is supposed to be called only by the Effects Class.
     */
    protected Selector(){}

    /********** Add and get methods (required) **********/
    /**
     * Method to get the Set that makes up this selector object. Can be adressed via Card/Effect classes.
     * @return Set collection of instances of Either&lt;Card, Cardcolor>, i.e. a set in which each element is either a Card, or a CardColor object.
     */
    protected Set<Either<Card,CardColor>> getSelector(){
        return this.selector;
    }

    /**
     * Method that adds elements to the selector. 
     * @param either is an Either&lt;Card, CardColor> object to be added to the selector.
     * @return true iff operation was successfull, false else.
     */
    protected boolean add (Either<Card, CardColor> either){
        return this.selector.add(either);
    }

    /**
     * Method that adds all elements of a collection to the selector.
     * @param c the Collection&lt;Either&lt;Card,CardColor>> to be added to the selector.
     * @return true iff operation was successful, false else.
     */
    protected boolean addAll(Collection <Either<Card, CardColor>> c){
        return this.selector.addAll(c);
    }

    /**
     * Method that returns the size of the selector.
     * @return size of selector.
     */  
    protected int size(){
        return selector.size();
    }

    /**
     * 
     * Method that returns true iff the object used for input was removed from the selector, and false in all other cases.
     * @param o is an Object of any type.
     * @return true iff specified object was removed, else false.
     */
    protected boolean remove(Object o){
        return selector.remove(o);
    }

    /**
     * Method that clears the selector.
     */
    protected void clear(){
        this.selector.clear();
    }

    /**
     * Method that returns true iff all elements of the parameter have been removed from the selector, false else.
     * @param c a collection of either objects with Card or CardColor objects.
     * @return true iff the elements of the collection were removed from the selector, false else.
     */
    protected boolean removeAll(Collection<Either<Card,CardColor>> c){
        return this.selector.removeAll(c);
    }

    /**
     * Method that returns true iff the selector is empty, false else.
     * @return true iff the selector is empty, false in all other cases.
     */
    protected boolean isEmpty() {
        return this.selector.isEmpty();
    }

    /**
     * Method that returns true if the paramter is contained in the selector.
     * @param o an Object of any type.
     * @return true iff the object is contained in the selector, false in all other cases.
     */
    protected boolean contains(Object o) {
        return this.selector.contains(o);
    }

    /**
     * Method that checks if the selector contains all elements of the input collection.
     * @param c a collection of Either&lt;Card,CardColor> objects.
     * @return true iff all elements of the collection are contained in the selector, false else.
     */
    protected boolean containsAll(Collection<Either<Card,CardColor>> c) {
        return this.selector.containsAll(c);
    }
}