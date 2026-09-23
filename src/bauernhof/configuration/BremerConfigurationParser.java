package bauernhof.configuration;

import java.util.*;

import java.io.File;
import java.io.StringReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;    
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bauernhof.preset.card.Card;
import bauernhof.preset.card.CardColor;
import bauernhof.preset.card.Effect;
import bauernhof.preset.card.EffectType;
import bauernhof.preset.GameConfiguration;
import bauernhof.preset.GameConfigurationParser;
import bauernhof.preset.GameConfigurationException;
import bauernhof.preset.Either;

import bauernhof.card.*;

/**
 * A parser for a {@link GameConfiguration}.
 * @author Eva Ristevska
 */
public class BremerConfigurationParser implements GameConfigurationParser {
	/**
	 * Default constructor
	 */
	public BremerConfigurationParser()
	{
	}
	/**
	 * Parse the contents of game configuration file to a {@link GameConfiguration}.
	 * @param filecontents The contents of the game configuration file (xml).
	 * @throws GameConfigurationException The file contents is not a valid game configuration.
	 * @return The parsed {@link GameConfiguration}.
	 */
	public GameConfiguration parse(String filecontents) throws GameConfigurationException{
		try{
			// Initialise and parse the XML file
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(filecontents));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();

			// Check specified conditions
			// getElementsByTagname returns a NodeList containing all elements with that tag
			if(Integer.parseInt(root.getAttribute("version")) != 1 )
				throw new IllegalArgumentException ("Illegal version number for the game configuration!");
			if(root.getElementsByTagName("Description").getLength() != 1 )
				throw new IllegalArgumentException ("Illegal number of descriptions!");
			if(root.getElementsByTagName("NumCardsPerPlayerHand").getLength() != 1 )
				throw new IllegalArgumentException ("Illegal number of elements for NumCardsPerPlayerHand!");
			if(root.getElementsByTagName("NumDepositionAreaSlots").getLength() != 1 )
				throw new IllegalArgumentException ("Illegal number of elements for NumDepositionAreaSlots!");
			if(root.getElementsByTagName("CardColors").getLength() != 1 )
				throw new IllegalArgumentException ("Illegal number of CardColors elements!");
			if(root.getElementsByTagName("Cards").getLength() != 1 )
				throw new IllegalArgumentException ("Illegal number of Cards elements!");

			String description = root.getElementsByTagName("Description").item(0).getTextContent();
			if(description == null)
				throw new IllegalArgumentException("No description text!");
			
			int numCardsPerPlayerHand =Integer.parseInt(root.getElementsByTagName("NumCardsPerPlayerHand").item(0).getTextContent());
			validateNumCardsPerPlayerHand(numCardsPerPlayerHand);

			int numDepoAreaSlots = Integer.parseInt(root.getElementsByTagName("NumDepositionAreaSlots").item(0).getTextContent());
			validateNumDepositionAreaSlots(numDepoAreaSlots);
			
			int leastNumOfCards = (numDepoAreaSlots + (numCardsPerPlayerHand * 4));

			Set<CardColor> cardColors = new HashSet<>();
			// Hashmap to store the card color objects, the string (in the example colorName) is used as a key to store the cardColor object
			HashMap<String, CardColor> cardColorMap = new HashMap<>();

			Element cardColorsElement = (Element) root.getElementsByTagName("CardColors").item(0);
			NodeList colorElements = cardColorsElement.getElementsByTagName("CardColor");
			if(colorElements.getLength() < 1)
				throw new IllegalArgumentException("There aren't any CardColor elements defined");
			
			// The decodeColor(String) throws an  NumberFormatException, don't need to check that

			for (int i = 0; i < colorElements.getLength(); i++) {
				Element colorElement = (Element) colorElements.item(i);
				String colorName = colorElement.getTextContent();
				String colorValue = colorElement.getAttribute("color");

		    	if(colorValue == null)
					throw new IllegalArgumentException("CardColor should have a color attribute!");
			   
			    // Check if the card color has an unique name
				if(cardColorMap.containsKey(colorName))
					throw new IllegalArgumentException("Card color name: " +  colorName + "already defined.");
				
				CardColor cardColor = new CardColor(colorName, colorValue);
				cardColorMap.put(colorName, cardColor);
		        cardColors.add(cardColor);
		    }

		    Set<Card> cards = new HashSet<>();
			// To map each card to its Effect
		    HashMap<Card, NodeList> cardEffectMap = new HashMap<>();
			// To map the name of the card to the card
		    HashMap<String, Card> cardMap = new HashMap<>();
			// To check if each suit/CardColor is being used by at least one card we use a set, because they store only unique elements
			Set<String> usedCardColor = new HashSet<>();

		    Element cardsElement = (Element) root.getElementsByTagName("Cards").item(0);
		    NodeList cardElements = cardsElement.getElementsByTagName("Card");
			if(cardElements.getLength() < 1)
				throw new IllegalArgumentException("There aren't any Card elements defined.");
		    
		    for(int l = 0; l<cardElements.getLength(); l++){
		    	Element cardElement = (Element) cardElements.item(l);
		        String color = cardElement.getAttribute("color");
		        String baseValue = cardElement.getAttribute("basevalue");
		        String name = cardElement.getAttribute("name");
		        String image = cardElement.getAttribute("image");

				if(image == null)
					throw new IllegalArgumentException("The image is not defined!");

				if (!cardColorMap.containsKey(color))
					throw new IllegalArgumentException("Color is not defined!");
				
				// Check if the card name is unique	
				if(cardMap.containsKey(name))
					throw new IllegalArgumentException("Name is not unique.");
				
				// Check if the card has at least one effect
				NodeList effectElements = cardElement.getElementsByTagName("Effect");
				if(effectElements.getLength() == 0)
					throw new IllegalArgumentException("The card has no effects.");
				
				BCard card = new BCard(name, Integer.parseInt(baseValue), cardColorMap.get(color), image, new HashSet<>());
				
				usedCardColor.add(color);
				cardMap.put(name, card);
				cards.add(card);
		    	cardEffectMap.put(card, effectElements);
		    }

			if(usedCardColor.size() != cardColors.size())
				throw new IllegalArgumentException("The CardColor is not being used by at least one card!");

		    for (Card cardorg : cards) {
				BCard card = (BCard)cardorg;
			   	// Get the effects for each card, they have two attributes type and effectValue
				NodeList effectElements = cardEffectMap.get(card);
				int count = effectElements.getLength();
		        for (int j = 0; j < count; j++) {
		            Element effectElement = (Element) effectElements.item(j);
		            String type = effectElement.getAttribute("type");
		            String effectValue = effectElement.getAttribute("effectvalue");
		            //Validate if the type of the effect is defined in the enum EffectType
		            validateEffectType(type);
		            
					Set<Either<Card, CardColor>> cardOrColorRefs = new HashSet<>();
					//Get all CardRef and CardColorRef elements from the Effect element
					NodeList cardRefElements = effectElement.getElementsByTagName("CardRef");
					NodeList cardColorRefElements = effectElement.getElementsByTagName("CardColorRef");

					if(cardRefElements.getLength() < 1 && cardColorRefElements.getLength() < 1)
						throw new IllegalArgumentException("CardRef and CardColorRef Elements are not defined!");

					for (int k = 0; k<cardRefElements.getLength(); k++){
						Element cardRefElement = (Element) cardRefElements.item(k);
						String cardRefName = cardRefElement.getTextContent();
						
						Card cardRef = cardMap.get(cardRefName);
						
						// Check if the referenced card is already defined
						if(!cardMap.containsKey(cardRefName))
							throw new IllegalArgumentException("Referenced card not defined: " + cardRefName);
						
						Either<Card, CardColor> cardOrColorRef = new Either<>(cardRef,null);
						cardOrColorRefs.add(cardOrColorRef);
					}
					
					for (int p = 0; p < cardColorRefElements.getLength(); p++) {
						Element cardColorRefElement = (Element) cardColorRefElements.item(p);
						String cardColorRefName = cardColorRefElement.getTextContent();
						
						CardColor cardColorRef = cardColorMap.get(cardColorRefName);

						// Check if the referenced card is already defined
						if (!cardColorMap.containsKey(cardColorRefName)) 
							throw new IllegalArgumentException("Referenced card color not defined: " + cardColorRefName);
						
						Either<Card, CardColor> cardOrColorRef = new Either<>(null, cardColorRef);
						cardOrColorRefs.add(cardOrColorRef);
					}
					Effect effect = new Effects(Integer.parseInt(effectValue), toEffectType(type));

					effect.getSelector().addAll(cardOrColorRefs);					
					card.addEffect(effect); 
		    	}
		    }
		    
		    //Validate if there are at least (numDepoAreaSlots + (numCardsPerPlayerHand * 4));
			if(!(cards.size()>=leastNumOfCards))
				throw new IllegalArgumentException("Not enough cards defined!");
			
			BremerConfiguration ret = new BremerConfiguration(description, numCardsPerPlayerHand, numDepoAreaSlots, cardColors, cards, filecontents);
			
			return ret;
		} catch(ParserConfigurationException | SAXException | IOException e){
			throw new GameConfigurationException("Could not parse the game configuration", e);
		}
	}
	
	/**
	 * Parse a game configuration file to a {@link GameConfiguration}.
	 * @param file The game configuration file (xml).
	 * @throws GameConfigurationException The file contents is not a valid game configuration.
	 * @throws IOException Failed reading the file.
	 * @return The parsed {@link GameConfiguration}.
	 */
	public GameConfiguration parse(File file) throws GameConfigurationException, IOException{
		try{
	  		//check if the file is a xml file,its extension
			if(!file.getName().endsWith(".xml"))
				throw new GameConfigurationException("Wrong file format. XML file needed.");
			
	  		String str = FiletoStr(file);
	  
	  		return parse(str);
	  		
	  	}catch(GameConfigurationException e){
			throw new GameConfigurationException("Could not parse the game configuration", e);
		}
	}

	//-----------------------------------Auxiliary methods-----------------------------------

	/**
	 * Method that transforms a file into a String.
	 * @param File the xml file.
	 * @return the xml as a String.
	 */
	private String FiletoStr(File file) throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
    	Scanner scanner = new Scanner(file);
    	while (scanner.hasNextLine()){
        	sb.append(scanner.nextLine());
		}
    	scanner.close();
    	return sb.toString();
	}

	/**
	 * A method to validate the NumCardsPerPlayerHand.
	 * @param ncpph Number of cards per player Hand.
	 * @throws GameConfigurationException number is not allowed.
	 * @return true if the given number is accepted by the game configuration.
	 */
	private boolean validateNumCardsPerPlayerHand(int ncpph) throws GameConfigurationException{
		try{
			if(ncpph >= 2 && ncpph <= 10)
        		return true;	
        	else
        		throw new GameConfigurationException("Allowed number of cards per player hand must be between 2 and 10");
        }catch(GameConfigurationException e){
        	throw new GameConfigurationException("Could not parse the game configuration", e);
        }
	}
	
	/**
	 * A method to validate the NumDepositionAreaSlots.
	 * @param ndas number of deposition area.
	 * @throws GameConfigurationException number of deposition area is not allowed.
	 * @return true if the number of deposition area is accepted.
	 */
	private boolean validateNumDepositionAreaSlots(int ndas) throws GameConfigurationException{
		try{
		 	if(ndas >= 2 && ndas <= 12)
		    	return true;
		    else
		    	throw new GameConfigurationException("Allowed number of deposition area slots must be between 2 and 10");
		} catch(GameConfigurationException e){
			throw new GameConfigurationException("Could not parse the game configuration", e);
		}
	}

	/**
	 * Method to get an EffectType.
	 * @param str the Effect Type from the xml file as a String.
	 * @return an EffectType.
	 */
	private EffectType toEffectType (String str){
		EffectType et = EffectType.valueOf(str);
		return et;
	}

	/**
	 * A method to validate if the given Effect type is already defined.
	 * @param str the Effect Type from the xml file as a String.
	 * @throws GameConfigurationException The Effect Type is not defined.
	 * @return true if the EffectType is here.
	 */
	private boolean validateEffectType(String str) throws GameConfigurationException{
		EffectType et = toEffectType(str);
		for(EffectType effType: EffectType.values()){
			if(effType.equals(et))
				return true;
		}
		throw new GameConfigurationException("The Effect Type is not defined!");
	}
}
