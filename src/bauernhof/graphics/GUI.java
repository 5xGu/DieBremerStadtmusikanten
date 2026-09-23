package bauernhof.graphics;

import java.awt.Color;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import bauernhof.board.BoardInterface;
import bauernhof.preset.*;
import bauernhof.preset.ArgumentParser;
import bauernhof.preset.LogLevel;
import bauernhof.preset.Logger;
import bauernhof.preset.OptionalFeature;
import bauernhof.preset.Settings;
import bauernhof.preset.card.Card;
import bauernhof.preset.card.CardColor;
import bauernhof.preset.card.Effect;
import bauernhof.preset.card.GCard;

import sag.LayerPosition;
import sag.SAGFrame;
import sag.SAGPanel;
import sag.elements.GElement;
import sag.elements.GGroup;
import sag.elements.shapes.GCircle;
import sag.elements.shapes.GLine;
import sag.elements.shapes.GRect;
import sag.elements.shapes.GEllipse;
import sag.elements.GText;

import sag.events.MouseButtonEvent;
import sag.events.MouseEventAdapter;
import sag.events.MouseMotionEvent;
import sag.events.MouseWheelEvent;

/**
* The GUI class that allows players to see the board and play the game by mouse selecting cards.
* @author Valeria Stankova
*/
public class GUI implements PlayerGUIAccess {
    /**
    * The game board.
    */    
    private BoardInterface board;
    /**
    * The game configuration taken from the board.
    */
    GameConfiguration gameConfig;
    /**
    * Mouse event adapter used for the end turn button and the draw/disposal pop up window buttons.
    */
    MouseEventAdapter mainAdapter; 
    /**
    * Mouse event for the cards on the lower hand.
    */
    MouseEventAdapter lowerCards; 
    /**
    * Mouse event for the cards on the upper hand.
    */
    MouseEventAdapter upperCards;
    /**
    * Mouse event for the cards on the left hand (when available).
    */
    MouseEventAdapter leftCards;
    /**
    * Mouse event for the cards on the right hand (when available).
    */
    MouseEventAdapter rightCards;
    /**
    * Mouse event adapter for the draw pile cards.
    * Includes draw pop up and related buttons.
    */
    MouseEventAdapter drawPileAdapter;
    /**
    * Mouse event adapter for the disposal pile cards.
    * Includes disposal pop up and related buttons.
    */
    MouseEventAdapter disposalPileAdapter;
    /**
    * Mouse event adapter for the card at the very top of the draw pile.
    * Allows the most upper draw card to be selected fast, without having to search for it in the draw pile pop up. 
    */
    MouseEventAdapter topCardFromDrawPileAdapter;
    /**
    * Mouse event adapter for the card at the very top of the disposal pile.
    * Allows the most upper disposal card to be selected fast, without having to search for it in the disposal pile pop up.
    */
    MouseEventAdapter topCardFromDisposalPileAdapter;
    /**
    * Allows or disallows clicking on anything under the pop ups for the draw/disposal piles when they are open.
    * Also doesnt allow opening a second pop up after one has already been opened.
    */
    boolean allowClicksUnderPopUps = true;
    /**
    * If a card to be taken has been chosen, the boolean makes sure no other card is selected for that purpose.
    */
    boolean cardToBeTakenHasBeenChosen = false;
    /**
    * If a card to be returned has been chosen, the boolean makes sure no other card is selected for that purpose.
    */
    boolean cardToBeReturnedHasBeenChosen = false;
    /**
    * Used when a preview window is opened to forbid preview from other piles, opening pile pup ups or choosing cards accidentaly. 
    */
    boolean allowAnyMove = true;
    /**
    * Used to stop clicking on the top draw pile card after one of the top cards has been already selected.
    * Avoids unwanted movements of the already selected card, caused by copyCard being used everywhere. 
    */ 
    boolean dontAllowDrawTopCardClick = false;
    /**
    * Used to stop clicking on the top disposal pile card after one of the top cards has been already selected.
    * Avoids unwanted movements of the already selected card, caused by copyCard being used everywhere. 
    */ 
    boolean dontAllowDisposalTopCardClick = false;
    /**
    * Is true if we have chosen the top card of the draw pile. Used for the deletion of the top card from any other place.
    */
    boolean topDrawCardPlaced = false;
    /**
    * Is true if we have chosen the top card of the disposal pile. Used for the deletion of the top card from any other place.
    */
    boolean topDisposalCardPlaced = false;
    /**
    * Does not allow the player to pick a card in the draw pile which is not the most upper card.
    */
    boolean blockSelectDraw = false;
    /**
    * Is true if the draw pile pop up window page is not fully filled with cards.
    */
    boolean goToPreviousWhenNotFullPage = false;
    /**
    * Is true if the go to the previous page button in the draw pop up window has been pressed.
    */
    boolean goToPreviousPagePushed = false;
    /**
    * Is true if going to the next page is allowed.
    */
    boolean changePageForwardIsAllowed = true;
    /**
    * Is true if going to the previous page is allowed.
    */
    boolean changePageBackwardsIsAllowed = false;
    /**
    * Stops a card from the lower hand which has been chosen to be previewed.
    */
    boolean disalowClick = false;
    /**
    * Stops a card from the lower hand which is currently in preview from being chosen before it returns back to its place.
    */
    boolean disalowChoosing = false;
    /**
    * Allows the lower hand cards to be previewed, is true if cards from other hand are currently not in preview.
    */
    boolean allowShowLowerCards = true;
    /**
    * Allows the left hand cards to be previewed, is true if cards from other hand are currently not in preview.
    */
    boolean allowShowLeftCards = true;
    /**
    * Allows the right hand cards to be previewed, is true if cards from other hand are currently not in preview.
    */
    boolean allowShowRightCards = true;
    /**
    * Allows the upper hand cards to be previewed, is true if cards from other hand are currently not in preview.
    */
    boolean allowShowUpperCards = true;
    /**
    * Saves the X position of the copyCard.
    */
    float copyPositionX;
    /**
    * Saves the Y position of the copyCard.
    */
    float copyPositionY;
    /**
    * Saves the X position of the selected lower hand card.
    */    
    float positionOfPlCardX;
    /**
    * Saves the Y position of the selected lower hand card.
    */ 
    float positionOfPlCardY;
    float xPosi, yPosi, xPosi2, xPosi3, xPosi4, yPosi2, yPosi3, yPosi4;
    /**
    * Group with all the elements in the game panel.
    */
    GGroup gamePanel;
    /**
    * A group for displaying the lower hand cards.
    */
    GGroup groupForLowerHandCards;
    /**
    * A group for displaying the upper hand cards.
    */
    GGroup groupForUpperHandCards;
    /**
    * A group for displaying the left hand cards (if available).
    */
    GGroup groupForLeftHandCards;
    /**
    * A group for displaying the right hand cards (if available).
    */
    GGroup groupForRightHandCards;
    /**
    * A group for displaying the draw cards (only the most upper and the one under it are displayed).
    */
    GGroup groupForDrawCards;
    /**
    * A group for displaying the disposal cards (only the most upper and the one under it are displayed).
    */             
    GGroup groupForDisposalCards;
    /**
    * A group for displaying draw cards in the draw pop up window.
    */
    GGroup groupForDrawPop;
    /**
    * A group for displaying disposal cards in the disposal pop up window.
    */
    GGroup groupForDisposalPop;
    /**
    * GGroup for a card that has been selected from the draw pile, helps set position, gets added and removed to the game panel.
    */
    GGroup groupChosenCardFromDraw = new GGroup();
    /**
    * GGroup for a card that has been selected from the disposal pile, helps set position, gets added and removed to the game panel.
    */
    GGroup groupChosenCardFromDisposal = new GGroup();
 
    /**
    * The GCard where the card to be taken gets saved when we select it from anywhere.
    */
    GCard copyCard;
    /**
    * The most upper card from the draw pile.
    */
    GCard mostUpperDrawCard;
    /**
    * The most upper card from the disposal pile.
    */
    GCard mostUpperDisposalCard;

    /**
    * A copy GCard array of the draw GCard array.
    */
    GCard[] copyOfDrawCards;
    /**
    * A copy GCard array of the disposal GCard array.
    */
    GCard[] copyOfDisposalCards;

    /**
    * Card array with the size of 2 where index 0 is the card to be taken and index 1 is the card to be returned.
    */
    Card cardsToBeSwitched[] = new Card[2];
    /**
    * Card array containining the cards shown in the lower hand, at the beginning player 1 cards.
    */
    Card[] lowerHandCardArray;
    /**
    * Card array containining the cards shown in the upper hand, at the beginning player 2 cards.
    */
    Card[] upperHandCardArray;
    /**
    * Card array containining the cards shown in the left hand, at the beginning player 3 cards.
    */
    Card[] leftHandCardArray;
    /**
    * Card array containining the cards shown in the right hand, at the beginning player 4 cards.
    */
    Card[] rightHandCardArray;
    /**
    * Card array containining the draw pile cards.
    */
    Card[] DrawCards;
    /**
    * Card array containining the disposal pile cards.
    */ 
    Card[] DisposalCards;
    /**
    * The amount of cards each player gets in the beginning.
    */
    int nrCardsAtStart;
    /**
    * The amount of cards in the draw pile.
    */
    int nrCardsAtDraw;
    /**
    * The amount of cards in the disposal pile.
    */
    int nrCardsAtDisposal;
    /**
    * Counts the lines that have been filled with cards in the draw pop up window.
    */
    int countLines = 0;
    /**
    * Counts how many cards have been placed on one line in the draw pop up window.
    */
    int countCardsOnLine = 1;
    /**
    * Counts how many cards have been placed on one page in the draw pop up window.
    */
    int countCardsOnPage = 1;
    /**
    * Saves until which card we have displayed the draw card pile in the draw pop up window.
    */ 
    int savedIndex = 0;
    /**
    * Counts how many cards have been placed on one page in the draw pop up window, important when the page does not get completely filled.
    */
    int howManyCardsPlacedOnPageDraw = 0;
    /**
    * Represends how many players are playing the game.
    */
    int numberOfPlayersInGame;
    /**
    * Shows the current round.
    */
    int currentRound = 0;
    /**
    * The text that shows which card has been selected from the disposal pop up window.
    */
    GText cardChosenDisposalText = new GText("Card Chosen: ");
    /**
    * The text that shows which card is allowed to be chosen from the draw pop up window.
    */
    GText cardChosenDrawText = new GText("");
    /**
    * Text showing who the player on turn is.
    */
    GText whoIsOnTurn = new GText("");
    /**
    * Text showing which round it is.
    */
    GText whichRoundIsIt = new GText("");
    /**
    * Text for the end turn button.
    */
    GText endTurnText = new GText("");
    /**
    * The first text in the middle showing the points of the lower hand player.
    */
    GText lowerPlayerPointsTag = new GText("");
    /**
    * The second text in the middle showing the points of the upper hand player.
    */
    GText upperPlayerPointsTag = new GText("");
    /**
    * The third text in the middle showing the points of the left hand player (when available).
    */
    GText leftPlayerPointsTag = new GText("");
    /**
    * The forth text in the middle showing the points of the right hand player (when available).
    */
    GText rightPlayerPointsTag = new GText("");
    /**
    * Text showing the name of the lower hand player.
    */
    GText lowerHandPlayerName = new GText("");
    /**
    * Text showing the name of the upper hand player.
    */
    GText upperHandPlayerName = new GText("");
    /**
    * Text showing the name of the left hand player.
    */
    GText leftHandPlayerName = new GText("");
    /**
    * Text showing the name of the right hand player.
    */
    GText rightHandPlayerName = new GText("");
    /**
    * Text showing the number of the lower hand player.
    */
    GText lowerHandPlayerNumber = new GText("");
    /**
    * Text showing the number of the upper hand player.
    */
    GText upperHandPlayerNumber = new GText(""); 
    /**
    * Text showing the number of the left hand player.
    */   
    GText leftHandPlayerNumber = new GText("");
    /**
    * Text showing the number of the right hand player.
    */
    GText rightHandPlayerNumber = new GText("");
    /**
    * List with the player names.
    */
    ImmutableList<String> listWithPlayerNames;    

    private final Object moveWaitMonitor = new Object();
    /**
    * The button in the draw pop up window which allows the player to go to the next page.
    */
    GEllipse goToNextPageDraw = new GEllipse((float) 617, (float) -300, 15, 40);
    /**
    * The button in the draw pop up window which allows the player to go to the previous page.
    */
    GEllipse goToPreviousPageDraw = new GEllipse((float) 617, (float) -200, (float) 15 , (float) 40);
    /**
    * One of the lines that form the forwards arrow on the next page button.
    */    
    GLine pageLine1;
    /**
    * One of the lines that form the forwards arrow on the next page button.
    */ 
    GLine pageLine2;
    /**
    * One of the lines that form the forwards arrow on the next page button.
    */ 
    GLine pageLine3;
    /**
    * One of the lines that form the backwards arrow on the previous page button.
    */ 
    GLine pageLine4;
    /**
    * One of the lines that form the backwards arrow on the previous page button.
    */ 
    GLine pageLine5;
    /**
    * One of the lines that form the backwards arrow on the previous page button.
    */ 
    GLine pageLine6;
    /**
    * The GUI constructor.
    * @param board used to get all information of the game's status and cards.
    * @param playerNames is an immutable list with the player names, so that the gui can show them
    */
    public GUI(BoardInterface board, ImmutableList<String> playerNames){
        SAGFrame frame = new SAGFrame("Bauernhof", 30, 1280, 720);
        SAGPanel panel = new SAGPanel(1600, 900);
        panel.setBgColor​(new Color(189, 142, 81));            
        frame.setSAGPanel(panel);
        frame.setVisible(true);
        gamePanel = panel.addLayer(LayerPosition.CENTER);

        this.board = board;            
        gameConfig = board.getGameConfiguration();

        nrCardsAtStart = gameConfig.getNumCardsPerPlayerHand();
        nrCardsAtDisposal = board.getNrOfCardsInDeposit();
    
        numberOfPlayersInGame = board.getNumberOfPlayers();
        listWithPlayerNames = playerNames;

        lowerHandCardArray = board.getPlayerHand(1);
        upperHandCardArray = board.getPlayerHand(2);
        if(numberOfPlayersInGame >= 3){
            leftHandCardArray = board.getPlayerHand(3);
        }
        if(numberOfPlayersInGame == 4){
            rightHandCardArray = board.getPlayerHand(4);
        }            
        //---------------------------------Game Elements And Buttons----------------------------------//
        /**
        * The button that ends the turn of a player when clicked by them.
        */    
        GEllipse endTurnButton = new GEllipse(0, -145, 60, 20);
        endTurnButton.setFill(new Color(113, 134, 57));  
        /**
        * The text "End Turn" for the end turn button.
        */
        endTurnText = new GText("End Turn");
        endTurnText.setPosition(-48.5f,-138f);
        endTurnText.setFill(Color.WHITE);
        endTurnText.setFontSize(20);
        endTurnText.setBold(true);
        /**
        * The circle where the player on turn is shown on the board.
        */
        GCircle plOnTurnCircle = new GCircle(30);
        plOnTurnCircle.setPosition(-40, -90); 
        Color plOnTurnCircleC = new Color(110, 93, 38);             
        plOnTurnCircle.setFill(plOnTurnCircleC);   
        gamePanel.addChild(plOnTurnCircle);
        /**
        * The square where the current round is shown on the baord.
        */
        GRect currentRoundSquare = new GRect(40, -90, 55, 55, true);
        currentRoundSquare.setFill(plOnTurnCircleC);   
        gamePanel.addChild(currentRoundSquare);
        /**
        * The rectangle where the upper player hand is shown
        */
        GRect plHandHorizontal = new GRect(-2, -320, 1235,  GCard.HEIGHT+33, true);
        plHandHorizontal.setFill(plOnTurnCircleC);
        gamePanel.addChild(plHandHorizontal);
        /**
        * The rectangle where the lower player hand is shown
        */
        GRect plHandHorizontal2 = new GRect(-2, 320, 1235,  GCard.HEIGHT+33, true);    
        plHandHorizontal2.setFill(plOnTurnCircleC);       
        gamePanel.addChild(plHandHorizontal2);
        /**
        * The small extra rectangle for the player hand where the upper player number is written.
        */
        GRect extraHorizontal = new GRect(575, -145, 46 ,60, true);
        extraHorizontal.setFill(plOnTurnCircleC);
        gamePanel.addChild(extraHorizontal);
        /**
        * The small extra rectangle for the player hand where the lower player number is written.
        */
        GRect extraHorizontal2 = new GRect(-575, 145, 46 ,60, true);
        extraHorizontal2.setFill(plOnTurnCircleC);
        gamePanel.addChild(extraHorizontal2);
        /**
        * The small white circle for the player hand where the upper player number is written.
        */
        GCircle circleHorizontalHand = new GCircle(20);
        circleHorizontalHand.setPosition(575, -144);
        circleHorizontalHand.setFill(Color.WHITE);
        gamePanel.addChild(circleHorizontalHand);
        /**
        * The small white circle for the player hand where the lower player number is written.
        */
        GCircle circleHorizontalHand2 = new GCircle(20);
        circleHorizontalHand2.setPosition(-575, 144);
        circleHorizontalHand2.setFill(Color.WHITE);
        gamePanel.addChild(circleHorizontalHand2);  
 
        if(numberOfPlayersInGame >= 3){
            /**
            * The rectangle where the left player hand is shown.
            */
            GRect plHandVertical = new GRect(-750, 1, GCard.WIDTH+33, 875, true);
            plHandVertical.setFill(plOnTurnCircleC);
            gamePanel.addChild(plHandVertical);
            /**
            * The small extra rectangle for the player hand where the left player number is written.
            */
            GRect extraVertical = new GRect(-610, -130, 60 ,46, true);
            extraVertical.setFill(plOnTurnCircleC);
            gamePanel.addChild(extraVertical);
            /**
            * The small white circle for the player hand where the left player number is written.
            */
            GCircle circleVerticalHand = new GCircle(20);
            circleVerticalHand.setPosition(-610, -130);
            circleVerticalHand.setFill(Color.WHITE);
            gamePanel.addChild(circleVerticalHand); 
        }

        if(numberOfPlayersInGame == 4){
            /**
            * The rectangle where the right player hand is shown.
            */
            GRect plHandVertical2 = new GRect(746.5f, 1, GCard.WIDTH+33, 875, true);
            plHandVertical2.setFill(plOnTurnCircleC);
            gamePanel.addChild(plHandVertical2); 
            /**
            * The small extra rectangle for the player hand where the right player number is written.
            */
            GRect extraVertical2 = new GRect(610, 130, 60 ,46, true);
            extraVertical2.setFill(plOnTurnCircleC);
            gamePanel.addChild(extraVertical2);
            /**
            * The small white circle for the player hand where the right player number is written.
            */
            GCircle circleVerticalHand2 = new GCircle(20);
            circleVerticalHand2.setPosition(610, 130);
            circleVerticalHand2.setFill(Color.WHITE);
            gamePanel.addChild(circleVerticalHand2);    
        }               
        /**
        * Color for the lines on the pop up close buttons.
        */
        Color closeLineC = new Color(206, 134, 119);
        /**
        * One of the lines of the pop up close buttons.
        */
        GLine closeLine1 = new GLine(-655, -363, -625, -333);
        closeLine1.setStroke(closeLineC, 2f);
        /**
        * One of the lines of the pop up close buttons.
        */
        GLine closeLine2 = new GLine(-655, -333, -625, -363);
        closeLine2.setStroke(closeLineC, 2f);
        /**
        * One of the lines of the pop up close buttons.
        */
        GLine closeLine3 = new GLine(-655, -333, -655, -363);
        closeLine3.setStroke(closeLineC, 2f);
        /**
        * One of the lines of the pop up close buttons.
        */
        GLine closeLine4 = new GLine(-625, -333, -625, -363);
        closeLine4.setStroke(closeLineC, 2f);
        /**
        * One of the lines of the pop up close buttons.
        */
        GLine closeLine5 = new GLine(-655, -333, -625, -333);
        closeLine5.setStroke(closeLineC, 2f);
        /**
        * One of the lines of the pop up close buttons.
        */
        GLine closeLine6 = new GLine(-655, -363, -625, -363);
        closeLine6.setStroke(closeLineC, 2f);
        /**
        * Color for the lines that form the forwards and backwards arrows on the next and previous page buttons.
        */
        Color pageLineC = new Color(115, 101, 76);
        
        pageLine1 = new GLine(615, -285, 615, -315);
        pageLine1.setStroke(pageLineC, 3f);
        
        pageLine2 = new GLine(615, -285, 622, -300);
        pageLine2.setStroke(pageLineC, 3f);
        
        pageLine3 = new GLine(615, -315, 622, -300);
        pageLine3.setStroke(pageLineC, 3f);
        
        pageLine4 = new GLine(619, -185, 619, -215);
        pageLine4.setStroke(pageLineC, 3f);
        
        pageLine5 = new GLine(619, -185, 612, -200);
        pageLine5.setStroke(pageLineC, 3f);
        
        pageLine6 = new GLine(619, -215, 612, -200);
        pageLine6.setStroke(pageLineC, 3f);
        /**
        * The button on the right that opens the draw pop up window.
        */    
        GEllipse buttonForDrawPop = new GEllipse((float) 325, (float) 0, (float) 20 , (float) 90);
        /**
        * Color used for the pop up window buttons and the pop up window background.
        */
        Color popButtonC = new Color(115, 101, 76);
        buttonForDrawPop.setFill(popButtonC);
        /**
        * The button that closes the draw pop up window.
        */
        GRect closeDrawPopUp = new GRect​((float) -640, (float) -348, (float) 30 , (float) 30 , true);
        /**
        * Color for the pop up window close button.
        */
        Color closeWindowC = new Color(193, 32, 0);
        closeDrawPopUp.setFill(closeWindowC);
        /**
        * The draw pop up window background.
        */
        GRect popUpDrawField = new GRect​((float) -2, (float) 0, (float) GCard.WIDTH*6+100 , (float) GCard.HEIGHT*2+200 , true);
        popUpDrawField.setFill(popButtonC);
        /**
        * The background blur that occurs when the draw pop up window is open.
        */
        GRect blurTheRestDraw = new GRect(0, 0, 2000, 900, true);
        blurTheRestDraw.setOpacity(0.5f);
        /**
        * The button that should be clicked when a player wants to select a card from the draw pop up window.
        */
        GEllipse selectCardButtonDraw = new GEllipse((float) 0, (float) -316, (float) 65 , (float) 20);
        /**
        * Color used for the select card button and the text that shows which card the player has selected (in disposal) or can be selected (in draw).
        */
        Color selectCardFromPopC = new Color(81, 71, 54);
        selectCardButtonDraw.setFill(selectCardFromPopC);  // select and accept the disposal card button
        /**
        * The "Choose Card" text on the select card button in the draw pop up window.
        */
        GText selectCardTextDraw = new GText ("Choose Card");
        selectCardTextDraw.setPosition((float) -57, (float) -310);
        selectCardTextDraw.setFill(Color.WHITE);
        selectCardTextDraw.setFontSize((float) 18);

        cardChosenDrawText.setFill(selectCardFromPopC);
        cardChosenDrawText.setBold(true);
        
        goToNextPageDraw.setFill(Color.WHITE);
        goToPreviousPageDraw.setFill(Color.WHITE);
        /**
        * The button on the left that opens the disposal pop up window.
        */
        GEllipse buttonForDisposalPop = new GEllipse((float) -325, (float) 0, (float) 20 , (float) 90);  
        buttonForDisposalPop.setFill(popButtonC);       // button for the disposal pile
        /**
        * The button that closes the disposal pop up window.
        */
        GRect closeDisposalPopUp = new GRect​((float) -640, (float) -348, (float) 30 , (float) 30 , true);
        closeDisposalPopUp.setFill(closeWindowC);
        /**
        * The disposal pop up window background.
        */
        GRect popUpDisposalField= new GRect​((float) -2, (float) 0, (float) GCard.WIDTH*6+100 , (float) GCard.HEIGHT*2+200 , true);
        popUpDisposalField.setFill(popButtonC);
        /**
        * The background blur that occurs when the disposal pop up window is open.
        */
        GRect blurTheRestDisposal = new GRect(0, 0, 2000, 900, true);
        blurTheRestDisposal.setOpacity(0.5f);
        /**
        * The button that should be clicked when a player wants to select a card from the disposal pop up window.
        */
        GEllipse selectCardButtonDisposal = new GEllipse((float) 0, (float) -316, (float) 65 , (float) 20);
        selectCardButtonDisposal.setFill(selectCardFromPopC);  // select and accept the disposal card button
        /**
        * The "Choose Card" text on the select card button in the disposal pop up window.
        */
        GText selectCardTextDisposal = new GText ("Choose Card");
        selectCardTextDisposal.setPosition((float) -57, (float) -310);
        selectCardTextDisposal.setFill(Color.WHITE);
        selectCardTextDisposal.setFontSize((float) 18);

        cardChosenDisposalText.setFill(selectCardFromPopC);
        cardChosenDisposalText.setBold(true);
        /**
        * The rectangle for the "take" label for the erea where the take cards are placed.
        */
        GRect takeLabel = new GRect(200, -140, GCard.WIDTH, 35, true);
        /**
        * Color for the take and return labels.
        */
        Color takeLabelC = new Color(93, 110, 47);
        takeLabel.setFill(takeLabelC);
        gamePanel.addChild(takeLabel);
        /**
        * The "take" label text.
        */
        GText takeCardText = new GText("Take");
        takeCardText.setPosition(174, -133.5f);
        /**
        * Color for the take and return text.
        */
        Color takeCardTextC = new Color (179, 195, 135);
        takeCardText.setFill(takeCardTextC); 
        takeCardText.setBold(true);
        takeCardText.setFontSize(20);
        gamePanel.addChild(takeCardText);
        /**
        * The rectangle for the "return" label for the erea where the return cards are placed.
        */
        GRect returnLabel = new GRect(-200, -140, GCard.WIDTH, 35, true);
        returnLabel.setFill(takeLabelC);
        gamePanel.addChild(returnLabel);
        /**
        * The "return" label text.
        */
        GText returnCardText = new GText("Return");
        returnCardText.setPosition(-238, -133.5f);
        returnCardText.setFill(takeCardTextC);
        returnCardText.setBold(true);
        returnCardText.setFontSize(20);
        gamePanel.addChild(returnCardText); 
        /**
        * The first text background in the middle where the number, name and points of the current player on turn is shown.
        */
        GRect lowerPlayerRect = new GRect​(0f, -27f, 190f, 50f , true);
        lowerPlayerRect.setFill(new Color(176, 205, 96));
        gamePanel.addChild(lowerPlayerRect);
        /**
        * The second text background in the middle where the number, name and points of the next player in turn is shown.
        */
        GRect upperPlayerRect = new GRect​(0f, 23f, 190f , 50f , true);
        upperPlayerRect.setFill(new Color(153, 179, 82));           
        gamePanel.addChild(upperPlayerRect);

        if(numberOfPlayersInGame >= 3){
            /**
            * The third text background in the middle where the number, name and points of the 3rd player in turn (when available) is shown.
            */
            GRect leftPlayerRect = new GRect​(0f, 73f, 190f, 50f, true);
            leftPlayerRect.setFill(new Color(137, 162, 69));
            gamePanel.addChild(leftPlayerRect);
        }

        if(numberOfPlayersInGame == 4){
            /**
            * The fourth text background in the middle where the number, name and points of the 4th player in turn (when available) is shown.
            */
            GRect rightPlayerRect = new GRect​(0f, 123f, 190f, 50f, true);
            rightPlayerRect.setFill(new Color(113, 134, 57));           
            gamePanel.addChild(rightPlayerRect);
        }
        /**
        * The preview window that comes out when a player wants to have a look at a card from their or someone elses hand.
        */
        GRect previewWindow = new GRect(0f, 0f, GCard.WIDTH+18f, GCard.HEIGHT+53f, true);
        previewWindow.setFill(new Color(88, 77, 59));
        previewWindow.setOpacity(0);
        gamePanel.addChild(previewWindow);
        /**
        * The "Preview" text that appears with the preview window.
        */
        GText previewText = new GText("Preview");
        previewText.setPosition(-40, -135);
        previewText.setFill(Color.WHITE);
        previewText.setBold(true);
        previewText.setFontSize(18);
        previewText.setOpacity(0);
        gamePanel.addChild(previewText);
        //--------------------------------------------------------------------------------------------//             
        copyOfDrawCards = new GCard[nrCardsAtDraw];
        copyOfDisposalCards = new GCard[nrCardsAtDisposal];

        groupForLowerHandCards = new GGroup();     
        groupForUpperHandCards = new GGroup();
        
        if(numberOfPlayersInGame >= 3){
            groupForLeftHandCards = new GGroup();
        }

        if(numberOfPlayersInGame == 4){ 
            groupForRightHandCards = new GGroup();
        }    

        groupForDrawCards = new GGroup();
        groupForDisposalCards = new GGroup(); 
        groupForDrawPop = new GGroup();
        groupForDisposalPop = new GGroup();      
        /**
        * A group for displaying the chosen card from the lower hand.
        */  
        GGroup lowerCardChoice = new GGroup();   
        //------------------------------------------Adapters------------------------------------------//
        topCardFromDrawPileAdapter = new MouseEventAdapter(){
            @Override
            public void mouseClicked(MouseButtonEvent event, GElement self){                         
                switch (event.getMouseButton()){
                    case LEFT:
                        if(cardToBeTakenHasBeenChosen == false && allowAnyMove == true && allowClicksUnderPopUps == true){
                            dontAllowDisposalTopCardClick = true;

                            copyCard = (GCard) self;                        // copy the card
                            copyPositionX = self.getPositionX();             // copy the x position
                            copyPositionY = self.getPositionY();            // copy the y position

                            cardToBeTakenHasBeenChosen = true;
                            groupChosenCardFromDraw.addChild(copyCard , (-copyPositionX) + 200, -(copyPositionY-25));    // adds the card to the "take" area
                            cardsToBeSwitched[0] = ((GCard) copyCard).getCard();                                        // the card becomes the take card
                            gamePanel.addChild(groupChosenCardFromDraw);
                            groupForDrawPop.removeChild(copyOfDrawCards[nrCardsAtDraw-1]);                              // remove the top card from the draw pop up
                            topDrawCardPlaced = true;
                        }
                        break;
                    case RIGHT:
                        if(dontAllowDrawTopCardClick == false && allowClicksUnderPopUps == true){
                            dontAllowDrawTopCardClick = true;
                            cardsToBeSwitched[1] = ((GCard) copyCard).getCard();                                        // the card becomes the return card
                            groupChosenCardFromDraw.removeChild(copyCard);                                              // removes it from the "take" area
                            copyPositionX = copyCard.getPositionX();
                            copyPositionY = copyCard.getPositionY();
                            groupChosenCardFromDraw.addChild( copyCard, -(2f*copyPositionX), -(2f*(copyPositionY-25)));  // adds it to the "return" area
                            cardToBeReturnedHasBeenChosen = true;
                        }
                        break;                     
                    case MIDDLE: break;
                    case UNKNOWN: break;
                }
                event.setHandled();
            }
        };
        topCardFromDisposalPileAdapter = new MouseEventAdapter(){                
            @Override
            public void mouseClicked(MouseButtonEvent event, GElement self){                 
                switch (event.getMouseButton()){
                    case LEFT:
                        if(cardToBeTakenHasBeenChosen == false && allowAnyMove == true && allowClicksUnderPopUps == true){
                            dontAllowDrawTopCardClick = true;

                            copyCard = (GCard) self;                        // copy the card
                            copyPositionX = self.getPositionX();             // copy the x position
                            copyPositionY = self.getPositionY();            // copy the y position

                            cardToBeTakenHasBeenChosen = true;
                            groupChosenCardFromDisposal.addChild(copyCard , -(copyPositionX) + 200, -(copyPositionY-25));// adds the card to the "take" area
                            cardsToBeSwitched[0] = ((GCard) copyCard).getCard();                                        // the card becomes the take card
                            gamePanel.addChild(groupChosenCardFromDisposal);
                            groupForDisposalPop.removeChild(copyOfDisposalCards[nrCardsAtDisposal-1]);                   // remove the top card from the disposal pop up
                            topDisposalCardPlaced = true;
                        }
                        break;                                               
                    case RIGHT:
                        if(dontAllowDisposalTopCardClick == false && allowClicksUnderPopUps == true){
                            dontAllowDisposalTopCardClick = true;
                            cardsToBeSwitched[1] = ((GCard) copyCard).getCard();                                        // the card becomes the return card
                            groupChosenCardFromDisposal.removeChild(copyCard);                                          // removes it from the "take" area
                            copyPositionX = copyCard.getPositionX();
                            copyPositionY = copyCard.getPositionY();
                            groupChosenCardFromDisposal.addChild(copyCard, (-copyPositionX) - 200, -(copyPositionY-25)); // adds it to the "return" area
                            cardToBeReturnedHasBeenChosen = true;
                        }
                        break;
                    case MIDDLE: break;
                    case UNKNOWN: break;
                }
                event.setHandled();
            }
        };
        drawPileAdapter = new MouseEventAdapter(){
            @Override
            public void mouseClicked(MouseButtonEvent event, GElement self){                   
                switch (event.getMouseButton()){
                    case LEFT:
                        if(self == closeDrawPopUp){
                            gamePanel.removeChild(blurTheRestDraw);     // remove draw pup up elements
                            gamePanel.removeChild(popUpDrawField);
                            gamePanel.removeChild(self);
                            gamePanel.removeChild(selectCardButtonDraw);
                            gamePanel.removeChild(selectCardTextDraw);
                            gamePanel.removeChild(groupForDrawPop);
                            gamePanel.removeChild(cardChosenDrawText);

                            changesOpToOne();                           // makes all text visible
                            lowerHandPlayerNumber.setOpacity(1f);
                            upperHandPlayerNumber.setOpacity(1f);
                            leftHandPlayerNumber.setOpacity(1f);
                            rightHandPlayerNumber.setOpacity(1f);

                            gamePanel.removeChild(closeLine1);          // remove the lines for the close button
                            gamePanel.removeChild(closeLine2);
                            gamePanel.removeChild(closeLine3);
                            gamePanel.removeChild(closeLine4);
                            gamePanel.removeChild(closeLine5);
                            gamePanel.removeChild(closeLine6);
                    
                            allowClicksUnderPopUps = true;
                            
                            if(cardToBeTakenHasBeenChosen == true){     // if a card from the draw pop up has been chosen, add it to "take" area
                                gamePanel.addChild(groupChosenCardFromDraw);
                            }                                         
                        }
                        if(self == selectCardButtonDraw && cardToBeTakenHasBeenChosen == false && blockSelectDraw == false){
                            dontAllowDisposalTopCardClick = true;
                            dontAllowDrawTopCardClick = true;
                            cardToBeTakenHasBeenChosen = true;
                            topDrawCardPlaced = true;                               // as we can only choose the top card

                            groupForDrawPop.removeChild(copyCard);                  // removes card from the pop up
                            groupChosenCardFromDraw.addChild(copyCard , (-copyPositionX) + 200, -(copyPositionY-25));
                            cardsToBeSwitched[0] = ((GCard) copyCard).getCard();    // sets it as take card
                            groupForDrawCards.removeChild(mostUpperDrawCard);       // removes the most upper draw card from the game board
                        }        
                        if(self == goToNextPageDraw && changePageForwardIsAllowed == true){
                            for(int f1 = 0; f1 < savedIndex; f1++){
                                groupForDrawPop.removeChild((GCard)copyOfDrawCards[f1]);
                            }                     
                            update();                    
                            changesOpToZero();      // make text and player numbers invisible
                            lowerHandPlayerNumber.setOpacity(0f);    
                            upperHandPlayerNumber.setOpacity(0f);
                            leftHandPlayerNumber.setOpacity(0f);
                            rightHandPlayerNumber.setOpacity(0f);            
                        }         
                        if(self == goToPreviousPageDraw && changePageBackwardsIsAllowed == true){
                            for(int f2 = 0; f2 < savedIndex; f2++){
                                groupForDrawPop.removeChild((GCard)copyOfDrawCards[f2]);
                            }
                            goToPreviousPagePushed = true;
                            if(howManyCardsPlacedOnPageDraw != 0){
                                goToPreviousWhenNotFullPage = true;
                                savedIndex = savedIndex - howManyCardsPlacedOnPageDraw - 12;
                                goToPreviousPagePushed = false;
                            }
                            update();
                            changesOpToZero();
                            lowerHandPlayerNumber.setOpacity(0f);
                            upperHandPlayerNumber.setOpacity(0f);
                            leftHandPlayerNumber.setOpacity(0f);
                            rightHandPlayerNumber.setOpacity(0f);                    
                        }
                        else{                                                           // if its a card from the draw pop up
                            if(cardToBeTakenHasBeenChosen == false){
                                if(self == (GCard) copyOfDrawCards[nrCardsAtDraw-1]){   // as we can only pick the most upper card                           
                                    blockSelectDraw = false;                            // we dont block the card selction button
                                    copyCard = (GCard) self;                            // copy card and its position
                                    copyPositionX = self.getPositionX();
                                    copyPositionY = self.getPositionY();
                                }
                                else{                                                   // if its not the last card then we block the selection button
                                    blockSelectDraw = true;
                                }
                            }
                        }
                        break;     
                    case RIGHT: 
                        if(self == (GCard) copyCard && cardToBeReturnedHasBeenChosen == false && allowClicksUnderPopUps == true){
                            cardsToBeSwitched[1] = ((GCard) copyCard).getCard();        // card becomes return card
                            groupChosenCardFromDraw.removeChild(copyCard);
                            copyPositionX = copyCard.getPositionX();
                            copyPositionY = copyCard.getPositionY();
                            groupChosenCardFromDraw.addChild( copyCard, -(2f*copyPositionX), -(2f*(copyPositionY-25)));
                            cardToBeReturnedHasBeenChosen = true;
                        }
                        break;
                    case MIDDLE: break;
                    case UNKNOWN: break;
                }
                event.setHandled();
            }
		};
		disposalPileAdapter = new MouseEventAdapter(){
            @Override
            public void mouseClicked(MouseButtonEvent event, GElement self){
                switch (event.getMouseButton()){
                    case LEFT:
                        if(self == closeDisposalPopUp){
                            gamePanel.removeChild(blurTheRestDisposal);         //remove disposal pop up elements
                            gamePanel.removeChild(popUpDisposalField);
                            gamePanel.removeChild(self);
                            gamePanel.removeChild(selectCardButtonDisposal);
                            gamePanel.removeChild(selectCardTextDisposal);
                            gamePanel.removeChild(groupForDisposalPop);
                            gamePanel.removeChild(cardChosenDisposalText);
  
                            changesOpToOne();                                   // makes all text visible
                            lowerHandPlayerNumber.setOpacity(1f);
                            upperHandPlayerNumber.setOpacity(1f);
                            leftHandPlayerNumber.setOpacity(1f);
                            rightHandPlayerNumber.setOpacity(1f);

                            gamePanel.removeChild(closeLine1);                  // remove disposal close button lines
                            gamePanel.removeChild(closeLine2);
                            gamePanel.removeChild(closeLine3);
                            gamePanel.removeChild(closeLine4);
                            gamePanel.removeChild(closeLine5);
                            gamePanel.removeChild(closeLine6);

                            allowClicksUnderPopUps = true;

                            if(cardToBeTakenHasBeenChosen == true){             // if a card from disposal has been chosen, add it to the game panel
                                gamePanel.addChild(groupChosenCardFromDisposal);
                            }
                        }
                        if(self == selectCardButtonDisposal && cardToBeTakenHasBeenChosen == false){
                                dontAllowDrawTopCardClick = true;
                                dontAllowDisposalTopCardClick = true;
                                cardToBeTakenHasBeenChosen = true;

                                groupForDisposalPop.removeChild(copyCard);
                                groupChosenCardFromDisposal.addChild(copyCard, (-copyPositionX) + 200, -(copyPositionY-25));
                                cardsToBeSwitched[0] = ((GCard) copyCard).getCard();                                // card becomes take card
                                if((copyCard.getCard()).getName() == (mostUpperDisposalCard.getCard()).getName()){  // removes most upper disposal card
                                    groupForDisposalCards.removeChild(mostUpperDisposalCard);
                                    topDisposalCardPlaced = true;
                                }
                        }
                        else{                                                   // if its a card from the disposal pop up
                            if(cardToBeTakenHasBeenChosen == false){
                                gamePanel.removeChild(cardChosenDisposalText);      // remove label "card chosen"
                                cardChosenDisposalText = new GText("Card Chosen: " + ((GCard)self).getCard().getName()); // change to the new selected card
                                cardChosenDisposalText.setFill(new Color(81, 71, 54));
                                cardChosenDisposalText.setBold(true);
                                cardChosenDisposalText.setPosition((float) -220, (float) 330);
                                gamePanel.addChild(cardChosenDisposalText);         // add it again

                                copyCard = (GCard) self;                        // copy the card and its positions as copyCard
                                copyPositionX = self.getPositionX();
                                copyPositionY = self.getPositionY();
                            }
                        }
                        break;               
                    case RIGHT: 
                        if(self == (GCard) copyCard && cardToBeReturnedHasBeenChosen == false && allowClicksUnderPopUps == true){
                            cardsToBeSwitched[1] = ((GCard) copyCard).getCard();
                            groupChosenCardFromDisposal.removeChild(copyCard);  
                            copyPositionX = copyCard.getPositionX();
                            copyPositionY = copyCard.getPositionY();
                            groupChosenCardFromDisposal.addChild(copyCard, -(2f*copyPositionX), -(2f*(copyPositionY-25)));
                            cardToBeReturnedHasBeenChosen = true;                      
                        }  
                        break;                     
                    case MIDDLE: self.move(0,0); break;
                    case UNKNOWN: break;
                }
                event.setHandled();
            }
		};
        lowerCards = new MouseEventAdapter(){
            GElement previouslyShowed = null;
            @Override
            public void mouseClicked(MouseButtonEvent event, GElement self){
                switch(event.getMouseButton()){
                    case LEFT:
                        if(allowClicksUnderPopUps == true && cardToBeTakenHasBeenChosen == true && cardToBeReturnedHasBeenChosen == false && disalowChoosing == false){
                            disalowClick = true;
                            dontAllowDisposalTopCardClick = true;
                            dontAllowDrawTopCardClick = true;

                            positionOfPlCardX = self.getPositionX();            // save its x position
                            positionOfPlCardY = self.getPositionY();            // save its y position

                            cardsToBeSwitched[1] = ((GCard) self).getCard();    // card is now return card

                            gamePanel.removeChild(self);
                            lowerCardChoice.addChild(self, (-(positionOfPlCardX))-200, (-positionOfPlCardY+25));
                            gamePanel.addChild(lowerCardChoice);

                            cardToBeReturnedHasBeenChosen = true;
                        }
                        break;
                    case RIGHT:
                        if(allowShowLowerCards == true && disalowClick == false && allowClicksUnderPopUps == true){
                            disalowChoosing = true;
                            allowAnyMove = false;

                            if(previouslyShowed != null){
			                    previouslyShowed.move(xPosi, yPosi);                
		                    }

                            xPosi = self.getPositionX();
                            yPosi = self.getPositionY()-10;

                            previewWindow.setOpacity(1);            // makes preview window and text visible
                            previewText.setOpacity(1);
                            changesOpToZero();                  // makes text invisible      
                            endTurnButton.setOpacity(0);

		                    self.move(-xPosi, -yPosi);
		                    previouslyShowed = self;
		                    allowShowLeftCards =false; allowShowRightCards = false; allowShowUpperCards = false;            
                        }
                        break;
                    case MIDDLE: 
                        if(previouslyShowed == self && disalowClick == false && allowClicksUnderPopUps == true){
                            disalowChoosing = false;
					        previouslyShowed.move(xPosi, yPosi);
					        previouslyShowed = null;

                            previewWindow.setOpacity(0);            // makes preview window and text invisible
                            previewText.setOpacity(0);

                            changesOpToOne();                   // makes text visible
                            endTurnButton.setOpacity(1);

                            allowShowLeftCards = true; allowShowRightCards = true; allowShowUpperCards = true;                     
                            allowAnyMove = true;
				        }
                        break;
                    case UNKNOWN: break;
                }
                event.setHandled();              
            }
        };
        upperCards = new MouseEventAdapter(){          
            GElement previouslyShowed = null;       
            @Override
            public void mouseClicked(MouseButtonEvent event, GElement self){                                       
                switch(event.getMouseButton()){
                    case LEFT: break;
                    case RIGHT:         
                        if(allowShowUpperCards == true && allowClicksUnderPopUps == true){                          
                            allowAnyMove = false;

                            if(previouslyShowed != null){
                                previouslyShowed.move(xPosi4, yPosi4);
		                    }
                            xPosi4 = self.getPositionX();
                            yPosi4 = self.getPositionY()-10;

                            previewWindow.setOpacity(1f);
                            previewText.setOpacity(1f);      

                            changesOpToZero();
                            endTurnButton.setOpacity(0f);

		                    self.move(-xPosi4, -yPosi4);
		                    previouslyShowed = self;
                            allowShowLowerCards =false; allowShowLeftCards = false; allowShowRightCards = false;  
                        }
                        break;             
                    case MIDDLE: 
                        if(previouslyShowed == self && allowClicksUnderPopUps == true){
					        previouslyShowed.move(xPosi4, yPosi4);
					        previouslyShowed = null;

                            previewWindow.setOpacity(0);
                            previewText.setOpacity(0);

                            changesOpToOne();
                            endTurnButton.setOpacity(1);

                            allowAnyMove = true;
                            allowShowLowerCards = true; allowShowLeftCards = true; allowShowRightCards = true;
				        }   
                        break;
                    case UNKNOWN: break;
                }
                event.setHandled();
            }
		};
        leftCards = new MouseEventAdapter(){
            GElement previouslyShowed = null;
            @Override
            public void mouseClicked(MouseButtonEvent event, GElement self){                                       
                switch(event.getMouseButton()){
                    case LEFT: break;
                    case RIGHT:
                        if(allowShowLeftCards == true && allowClicksUnderPopUps == true){
                            allowAnyMove = false;

                            if(previouslyShowed != null){
			                    previouslyShowed.move(xPosi2, yPosi2);
		                    }
                            xPosi2 = self.getPositionX();
                            yPosi2 = self.getPositionY()-10;

                            previewWindow.setOpacity(1);
                            previewText.setOpacity(1);

                            changesOpToZero();
                            endTurnButton.setOpacity(0);

		                    self.move(-xPosi2, -yPosi2);
		                    previouslyShowed = self;
                            allowShowLowerCards =false; allowShowRightCards = false; allowShowUpperCards = false;
                        }
                        break;             
                    case MIDDLE:
                        if(previouslyShowed == self && allowClicksUnderPopUps == true){
					        previouslyShowed.move(xPosi2, yPosi2);
					        previouslyShowed = null;

                            previewWindow.setOpacity(0);
                            previewText.setOpacity(0);
   
                            changesOpToOne();
                            endTurnButton.setOpacity(1);

                            allowAnyMove = true;
                            allowShowLowerCards = true; allowShowRightCards = true; allowShowUpperCards = true;
				        }
                        break;
                    case UNKNOWN: break;
                }
                event.setHandled();
            }
		};
        rightCards = new MouseEventAdapter(){
            GElement previouslyShowed = null;
            @Override
            public void mouseClicked(MouseButtonEvent event, GElement self){                                       
                switch(event.getMouseButton()){
                    case LEFT: break;
                    case RIGHT:
                        if(allowShowRightCards == true && allowClicksUnderPopUps == true){
                            allowAnyMove = false;

                            if(previouslyShowed != null){
			                    previouslyShowed.move(xPosi3, yPosi3);
		                    }
                            xPosi3 = self.getPositionX();
                            yPosi3 = self.getPositionY()-10;

                            previewWindow.setOpacity(1);
                            previewText.setOpacity(1); 

                            changesOpToZero();
                            endTurnButton.setOpacity(0);

		                    self.move(-xPosi3, -yPosi3);
		                    previouslyShowed = self;
                            allowShowLowerCards = false; allowShowLeftCards = false; allowShowUpperCards = false;
                        }
                        break;
                    case MIDDLE:
                        if(previouslyShowed == self && allowClicksUnderPopUps == true){
					        previouslyShowed.move(xPosi3, yPosi3);
					        previouslyShowed = null;

                            previewWindow.setOpacity(0);
                            previewText.setOpacity(0);

                            changesOpToOne();
                            endTurnButton.setOpacity(1);

                            allowAnyMove = true;
                            allowShowLowerCards = true; allowShowLeftCards = true; allowShowUpperCards = true;
				        }
                        break;
                    case UNKNOWN: break;
                }
                event.setHandled();
            }
		};
        mainAdapter = new MouseEventAdapter(){
            @Override
            public void mouseClicked(MouseButtonEvent event, GElement self){
                switch(event.getMouseButton()){
                    case LEFT:
                        if(self == buttonForDrawPop && allowClicksUnderPopUps == true && allowAnyMove == true){
                            allowClicksUnderPopUps = false;

                            changesOpToZero();                                  // makes text invisible
                            lowerHandPlayerNumber.setOpacity(0);
                            upperHandPlayerNumber.setOpacity(0);
                            leftHandPlayerNumber.setOpacity(0);
                            rightHandPlayerNumber.setOpacity(0);

                            gamePanel.addChild(blurTheRestDraw);                // adds all draw pop up elements
                            gamePanel.addChild(popUpDrawField);
                            gamePanel.addChild(closeDrawPopUp.setMouseEventListener(drawPileAdapter));

                            gamePanel.addChild(closeLine1);
                            gamePanel.addChild(closeLine2);
                            gamePanel.addChild(closeLine3);
                            gamePanel.addChild(closeLine4);
                            gamePanel.addChild(closeLine5);
                            gamePanel.addChild(closeLine6);

                            gamePanel.addChild(selectCardButtonDraw.setMouseEventListener(drawPileAdapter));
                            gamePanel.addChild(selectCardTextDraw);    
                            gamePanel.addChild(groupForDrawPop);                // the draw cards

                            cardChosenDrawText = new GText("You can choose: " + ((copyOfDrawCards[nrCardsAtDraw-1].getCard()).getName()));
                            cardChosenDrawText.setPosition((float) -200, (float) 330);
                            cardChosenDrawText.setFill(new Color(81, 71, 54));
                            cardChosenDrawText.setBold(true);
                            gamePanel.addChild(cardChosenDrawText);
                        }
                        if(self == buttonForDisposalPop && allowClicksUnderPopUps == true && allowAnyMove == true){
                            allowClicksUnderPopUps = false;

                            changesOpToZero();
                            lowerHandPlayerNumber.setOpacity(0);
                            upperHandPlayerNumber.setOpacity(0);
                            leftHandPlayerNumber.setOpacity(0);
                            rightHandPlayerNumber.setOpacity(0);

                            gamePanel.addChild(blurTheRestDisposal);
                            gamePanel.addChild(popUpDisposalField);     
                            gamePanel.addChild(closeDisposalPopUp.setMouseEventListener(disposalPileAdapter));
                            
                            gamePanel.addChild(closeLine1);
                            gamePanel.addChild(closeLine2);
                            gamePanel.addChild(closeLine3);
                            gamePanel.addChild(closeLine4);
                            gamePanel.addChild(closeLine5);
                            gamePanel.addChild(closeLine6);

                            gamePanel.addChild(selectCardButtonDisposal.setMouseEventListener(disposalPileAdapter));
                            gamePanel.addChild(selectCardTextDisposal);
                            gamePanel.addChild(groupForDisposalPop);            // the disposal cards
                            cardChosenDisposalText.setPosition((float) -220, (float) 330);
                            gamePanel.addChild(cardChosenDisposalText);
                        }
                        if(self == endTurnButton && cardToBeReturnedHasBeenChosen == true && cardToBeTakenHasBeenChosen == true && allowClicksUnderPopUps == true && allowAnyMove == true){
                            gamePanel.removeChild(groupChosenCardFromDraw);
                            gamePanel.removeChild(groupChosenCardFromDisposal);
                            gamePanel.removeChild(lowerCardChoice);

                            groupChosenCardFromDraw.removeChild(copyCard);
                            groupChosenCardFromDisposal.removeChild(copyCard);
                            // reset everything necessary for the new move
                            cardChosenDisposalText = new GText("Card Chosen: ");
                            cardChosenDisposalText.setFill(new Color(81, 71, 54));
                            cardChosenDisposalText.setBold(true);

                            cardToBeTakenHasBeenChosen = false;
                            cardToBeReturnedHasBeenChosen = false;

                            topDrawCardPlaced = false;
                            topDisposalCardPlaced = false;
                            blockSelectDraw = false;          
                            disalowClick = false;
                            dontAllowDisposalTopCardClick = false;
                            dontAllowDrawTopCardClick = false;

                            copyCard = null;
                            mostUpperDrawCard = null;
                            mostUpperDisposalCard = null;

                            copyPositionX = 0.0f;
                            copyPositionY = 0.0f;
                            // for the next/previous page in draw pop
                            savedIndex = 0;
                            howManyCardsPlacedOnPageDraw = 0;
                            countCardsOnLine = 1;
                            countCardsOnPage = 1;
                            countLines = 0;
                            goToPreviousWhenNotFullPage = false;
                            goToPreviousPagePushed = false;

                            synchronized(moveWaitMonitor){
                                moveWaitMonitor.notifyAll();
                            }
                        }
                        break;
                    case RIGHT: break;                     
                    case MIDDLE: break;
                    case UNKNOWN: break;
                }
                event.setHandled();
            }
		};
        //--------------------------------------------------------------------------------------------//   
        gamePanel.addChild(buttonForDrawPop.setMouseEventListener(mainAdapter));
        gamePanel.addChild(buttonForDisposalPop.setMouseEventListener(mainAdapter));             
        gamePanel.addChild(endTurnButton.setMouseEventListener(mainAdapter));
   
        update();
    }
    /**
    * Removes all cards from the given card group.
    * @param cardGroup can be any GGroup but in this case its GGroups of the card piles and player hands
    */
    private void deleteAllCardsFrom(GGroup cardGroup){
        ArrayList<GElement> temp = new ArrayList<>();
        for(GElement el : cardGroup){
            temp.add(el);
        }
        for(GElement el : temp){
            cardGroup.removeChild(el);          
        }
    }
    /**
    * Changes the opacity of a few text elements to 0, making them "invisible".
    */
    public void changesOpToZero(){
        whoIsOnTurn.setOpacity(0);
        whichRoundIsIt.setOpacity(0);      
        endTurnText.setOpacity(0);

        lowerPlayerPointsTag.setOpacity(0);
        upperPlayerPointsTag.setOpacity(0);
        leftPlayerPointsTag.setOpacity(0);
        rightPlayerPointsTag.setOpacity(0);
        
        lowerHandPlayerName.setOpacity(0);
        upperHandPlayerName.setOpacity(0);
        leftHandPlayerName.setOpacity(0);
        rightHandPlayerName.setOpacity(0);
    }
    /**
    * Changes the opacity of a few text elements to 1, making them "visible".
    */
    public void changesOpToOne(){
        whoIsOnTurn.setOpacity(1);
        whichRoundIsIt.setOpacity(1);
        endTurnText.setOpacity(1);

        lowerPlayerPointsTag.setOpacity(1);
        upperPlayerPointsTag.setOpacity(1);
        leftPlayerPointsTag.setOpacity(1);
        rightPlayerPointsTag.setOpacity(1);

        lowerHandPlayerName.setOpacity(1);
        upperHandPlayerName.setOpacity(1);
        leftHandPlayerName.setOpacity(1);
        rightHandPlayerName.setOpacity(1);       
    }
    /**
    * "Calculates" who is the next player.
    * @param from The player on turn
    * @param by 1,2 or 3 
    * @param nr The number of players in the game
    */
    public static int getPlayerNextFromBy(int from, int by, int nr){
        if(from + by <= nr){
            return from + by;
        }
        return (from + by) % nr;
    }
    /**
    * Gets called when the board has changed (a move has been made).
    */
    public void update(){
        gamePanel.addChild(endTurnText); 
        // removing all the cards from all groups
        deleteAllCardsFrom(groupForLowerHandCards);
        deleteAllCardsFrom(groupForUpperHandCards);

        if(numberOfPlayersInGame >= 3){
            deleteAllCardsFrom(groupForLeftHandCards);
        }

        if(numberOfPlayersInGame == 4){
            deleteAllCardsFrom(groupForRightHandCards); 
        }   
        deleteAllCardsFrom(groupForDisposalCards);
        deleteAllCardsFrom(groupForDrawCards);
              
        deleteAllCardsFrom(groupForDisposalPop);
        deleteAllCardsFrom(groupForDrawPop); 
                 
        // get newest card information from board.
        /**
        * Integer referencing to the player on turn, taken from the board.
        */
        int playerOnTurn = board.getPlayerOnTurn();

        lowerHandCardArray = board.getPlayerHand(playerOnTurn);
        upperHandCardArray = board.getPlayerHand(getPlayerNextFromBy(playerOnTurn, 1, numberOfPlayersInGame));

        if(numberOfPlayersInGame >= 3){
            leftHandCardArray = board.getPlayerHand(getPlayerNextFromBy(playerOnTurn, 2, numberOfPlayersInGame));
        }

        if(numberOfPlayersInGame == 4){
            rightHandCardArray = board.getPlayerHand(getPlayerNextFromBy(playerOnTurn, 3, numberOfPlayersInGame));
        }

        DisposalCards = board.getDepositPile();
        DrawCards = board.getDrawPile();

        nrCardsAtDraw = board.getNrOfCardsInDraw();
        nrCardsAtDisposal = DisposalCards.length;

        copyOfDrawCards = new GCard[nrCardsAtDraw];
        copyOfDisposalCards = new GCard[nrCardsAtDisposal];

        //remove, add and modify player on turn label
        gamePanel.removeChild(whoIsOnTurn); 
        whoIsOnTurn = new GText(String.valueOf(playerOnTurn));    
        whoIsOnTurn.setPosition((float) -50, (float) -80);
        whoIsOnTurn.setFill(Color.WHITE);
        whoIsOnTurn.setBold(true);
        whoIsOnTurn.setFontSize((float) 28);       
        gamePanel.addChild(whoIsOnTurn);

        currentRound = board.getTurn();

        //remove, add and modify round label
        gamePanel.removeChild(whichRoundIsIt);
        whichRoundIsIt = new GText(String.valueOf(currentRound));    
        whichRoundIsIt.setPosition((float) 31, (float) -80);
        if(currentRound > 9){
            whichRoundIsIt.setPosition((float) 21, (float) -80);
        }
        whichRoundIsIt.setFill(Color.WHITE);
        whichRoundIsIt.setBold(true);
        whichRoundIsIt.setFontSize((float) 28);       
        gamePanel.addChild(whichRoundIsIt);
        /**
        * Color for the circles around the player numbers.
        */
        Color extraCircles = new Color(110, 93, 38);
        /**
        * Color for the player name and point labels
        */
        Color playerLabelsC = new Color(61, 54, 42); 
        // add and modify player labels
        gamePanel.removeChild(lowerPlayerPointsTag);
        lowerPlayerPointsTag = new GText("Player " + String.valueOf(playerOnTurn) + ": " + board.getPlayerPoints(playerOnTurn) + "P");   
        lowerPlayerPointsTag.setPosition((float) -86, (float) -30);
        lowerPlayerPointsTag.setFill(playerLabelsC);
        lowerPlayerPointsTag.setItalic(true);
        lowerPlayerPointsTag.setFontSize((float) 24);
        gamePanel.addChild(lowerPlayerPointsTag);

        gamePanel.removeChild(lowerHandPlayerName);
        lowerHandPlayerName = new GText(listWithPlayerNames.get(playerOnTurn-1));
        lowerHandPlayerName.setPosition((float) -86, (float) -10);
        lowerHandPlayerName.setFill(playerLabelsC);
        lowerHandPlayerName.setItalic(true);
        lowerHandPlayerName.setFontSize((float) 18);
        gamePanel.addChild(lowerHandPlayerName); 

        gamePanel.removeChild(lowerHandPlayerNumber);
        lowerHandPlayerNumber = new GText(String.valueOf(playerOnTurn));
        lowerHandPlayerNumber.setPosition((float) -583, (float) 152);
        lowerHandPlayerNumber.setFill(extraCircles);
        lowerHandPlayerNumber.setBold(true);
        lowerHandPlayerNumber.setFontSize((float) 24);
        gamePanel.addChild(lowerHandPlayerNumber); 

        gamePanel.removeChild(upperPlayerPointsTag);
        upperPlayerPointsTag = new GText("Player " + String.valueOf(getPlayerNextFromBy(playerOnTurn, 1, numberOfPlayersInGame)) + ": " + board.getPlayerPoints(getPlayerNextFromBy(playerOnTurn, 1, numberOfPlayersInGame)) + "P");          
        upperPlayerPointsTag.setPosition((float) -86, (float) 20);
        upperPlayerPointsTag.setFill(playerLabelsC);
        upperPlayerPointsTag.setItalic(true);
        upperPlayerPointsTag.setFontSize((float) 24);

        gamePanel.addChild(upperPlayerPointsTag);

        gamePanel.removeChild(upperHandPlayerName);
        upperHandPlayerName = new GText(listWithPlayerNames.get(getPlayerNextFromBy(playerOnTurn, 1, numberOfPlayersInGame)-1));
        upperHandPlayerName.setPosition((float) -86, (float) 40);
        upperHandPlayerName.setFill(playerLabelsC);
        upperHandPlayerName.setItalic(true);
        upperHandPlayerName.setFontSize((float) 18);
        gamePanel.addChild(upperHandPlayerName); 

        gamePanel.removeChild(upperHandPlayerNumber);
        upperHandPlayerNumber = new GText(String.valueOf(getPlayerNextFromBy(playerOnTurn, 1, numberOfPlayersInGame)));
        upperHandPlayerNumber.setPosition((float) 567, (float) -136);
        upperHandPlayerNumber.setFill(extraCircles);
        upperHandPlayerNumber.setBold(true);
        upperHandPlayerNumber.setFontSize((float) 24);
        gamePanel.addChild(upperHandPlayerNumber); 
        
        if(numberOfPlayersInGame >= 3){
            gamePanel.removeChild(leftPlayerPointsTag);
            leftPlayerPointsTag = new GText("Player " + String.valueOf(getPlayerNextFromBy(playerOnTurn, 2, numberOfPlayersInGame)) + ": " + board.getPlayerPoints(getPlayerNextFromBy(playerOnTurn, 2, numberOfPlayersInGame)) + "P");          
            leftPlayerPointsTag.setPosition((float) -86, (float) 70);
            leftPlayerPointsTag.setFill(playerLabelsC);
            leftPlayerPointsTag.setItalic(true);
            leftPlayerPointsTag.setFontSize((float) 24);       
            gamePanel.addChild(leftPlayerPointsTag);

            gamePanel.removeChild(leftHandPlayerName);
            leftHandPlayerName = new GText(listWithPlayerNames.get(getPlayerNextFromBy(playerOnTurn, 2, numberOfPlayersInGame)-1));
            leftHandPlayerName.setPosition((float) -86, (float) 90);
            leftHandPlayerName.setFill(playerLabelsC);
            leftHandPlayerName.setItalic(true);
            leftHandPlayerName.setFontSize((float) 18);
            gamePanel.addChild(leftHandPlayerName); 

            gamePanel.removeChild(leftHandPlayerNumber);
            leftHandPlayerNumber = new GText(String.valueOf(getPlayerNextFromBy(playerOnTurn, 2, numberOfPlayersInGame)));
            leftHandPlayerNumber.setFill(extraCircles);
            leftHandPlayerNumber.setPosition((float) -618, (float) -121);
            leftHandPlayerNumber.setBold(true);
            leftHandPlayerNumber.setFontSize((float) 24);
            gamePanel.addChild(leftHandPlayerNumber); 
        }

        if(numberOfPlayersInGame == 4){
            gamePanel.removeChild(rightPlayerPointsTag);           
            rightPlayerPointsTag = new GText("Player " + String.valueOf(getPlayerNextFromBy(playerOnTurn, 3, numberOfPlayersInGame)) + ": " + board.getPlayerPoints(getPlayerNextFromBy(playerOnTurn, 3, numberOfPlayersInGame)) + "P");          
            rightPlayerPointsTag.setPosition((float) -86, (float) 120);
            rightPlayerPointsTag.setFill(playerLabelsC);
            rightPlayerPointsTag.setItalic(true);
            rightPlayerPointsTag.setFontSize((float) 24);
            gamePanel.addChild(rightPlayerPointsTag);

            gamePanel.removeChild(rightHandPlayerName);
            rightHandPlayerName = new GText(listWithPlayerNames.get(getPlayerNextFromBy(playerOnTurn, 3, numberOfPlayersInGame)-1));
            rightHandPlayerName.setPosition((float) -86, (float) 140);
            rightHandPlayerName.setFill(playerLabelsC);
            rightHandPlayerName.setItalic(true);
            rightHandPlayerName.setFontSize((float) 18);
            gamePanel.addChild(rightHandPlayerName); 
    
            gamePanel.removeChild(rightHandPlayerNumber);
            rightHandPlayerNumber = new GText(String.valueOf(getPlayerNextFromBy(playerOnTurn, 3, numberOfPlayersInGame)));
            rightHandPlayerNumber.setFill(extraCircles);
            rightHandPlayerNumber.setPosition((float) 602, (float) 138);
            rightHandPlayerNumber.setBold(true);
            rightHandPlayerNumber.setFontSize((float) 24);
            gamePanel.addChild(rightHandPlayerNumber);
        }
        /**
        * Needed for calculating the card distance when adding card hands which have even number horizontally.
        */
        float evenCardsDistance = nrCardsAtStart / 2 + 0.35f;
        /**
        * Needed for calculating the card distance when adding the card hands which have uneven number horizontally.
        */
        float unevenCardsDistance = nrCardsAtStart / 2 + 1;
        /**
        * Needed for calculating the card distance when adding the card hands which have even number vertically.
        */
        float evenCardDistanceSides = nrCardsAtStart / 2 + 0.725f;
        /**
        * Needed for calculating the card distance when adding the card hands which have uneven number vertically.
        */
        float uneavenCardsDistanceSides = nrCardsAtStart / 2 + 1.8f;
        float xSet = 0f; float ySet = 0f;

        for (int i = 0; i < nrCardsAtStart; i++){        
            if(nrCardsAtStart % 2 == 0){
                xSet = GCard.WIDTH * (0.6f * (float) (i - evenCardsDistance)) + GCard.WIDTH/2;     
                ySet = GCard.HEIGHT * (0.4f * (float) (i - evenCardDistanceSides)) + GCard.HEIGHT/2;
                if(nrCardsAtStart == 8 || nrCardsAtStart == 10){
                    ySet = GCard.HEIGHT * (0.25f * (float) (i - (nrCardsAtStart / 2 + 1.4755))) + GCard.HEIGHT/2;
                }
            }
            else{
                xSet = GCard.WIDTH * (0.6f * (float) (i - unevenCardsDistance)) + GCard.WIDTH/2;
                ySet = GCard.HEIGHT * (0.28f * (float) (i - uneavenCardsDistanceSides )) + GCard.HEIGHT/2;
            }
            
            groupForLowerHandCards.addChild(new GCard(lowerHandCardArray[i]).setMouseEventListener(lowerCards), xSet, (GCard.HEIGHT+51));
            groupForUpperHandCards.addChild(new GCard(upperHandCardArray[i]).setMouseEventListener(upperCards), xSet, -(GCard.HEIGHT+50));
            
            if(numberOfPlayersInGame >= 3){              
                groupForLeftHandCards.addChild(new GCard(leftHandCardArray[i]).setMouseEventListener(leftCards), -750, ySet);              
            }
            if(numberOfPlayersInGame == 4){ 
                groupForRightHandCards.addChild(new GCard(rightHandCardArray[i]).setMouseEventListener(rightCards), 750, ySet);
            }      
        } 
        // filling the groups for draw and disposal pile
        groupForDrawCards.addChild(new GCard(DrawCards[nrCardsAtDraw-2]), 450, 0);
        
        if(topDrawCardPlaced == false){
            mostUpperDrawCard = new GCard (DrawCards[nrCardsAtDraw-1]);
            groupForDrawCards.addChild(mostUpperDrawCard.setMouseEventListener(topCardFromDrawPileAdapter), 450, 0);
        }
              
        for (int i5 = 0; i5 < nrCardsAtDraw; i5++){
            copyOfDrawCards[i5] = new GCard (DrawCards[i5]);                     
        }

        if(nrCardsAtDisposal != 0){
                        
            if(nrCardsAtDisposal > 1){
                groupForDisposalCards.addChild(new GCard(DisposalCards[nrCardsAtDisposal - 2]), -450, 0);         
            }
            if(topDisposalCardPlaced == false){              
                mostUpperDisposalCard = new GCard (DisposalCards[nrCardsAtDisposal-1]);                
                groupForDisposalCards.addChild(mostUpperDisposalCard.setMouseEventListener(topCardFromDisposalPileAdapter), -450, 0); 
            }

        }
        for (int i6 = 0; i6 < nrCardsAtDisposal; i6++){
            copyOfDisposalCards[i6] = new GCard (DisposalCards[i6]);        
        }
               
        // filling groups for the draw pop up window
        float spacingDraw, spacingDisposal;
        /**
        * Array with two distances for the draw pop up window card placement.
        */
        int countLinesArr [] = {-140, 140};
        
        groupForDrawPop.addChild(goToNextPageDraw.setMouseEventListener(drawPileAdapter));
        groupForDrawPop.addChild(pageLine1);
        groupForDrawPop.addChild(pageLine2);
        groupForDrawPop.addChild(pageLine3);
        groupForDrawPop.addChild(goToPreviousPageDraw.setMouseEventListener(drawPileAdapter));
        groupForDrawPop.addChild(pageLine4);
        groupForDrawPop.addChild(pageLine5);
        groupForDrawPop.addChild(pageLine6);
        
        for(int drIntPop = 0; savedIndex < nrCardsAtDraw; drIntPop++){         
            spacingDraw  = GCard.WIDTH * (1.05f * (float) ((drIntPop % 6) - 2.9825)) + GCard.WIDTH /2;               
                              
            if (goToPreviousPagePushed == true){
                savedIndex = savedIndex - 12 * 2;          // 12 is how many we have on the whole page               
                howManyCardsPlacedOnPageDraw = 0;        
                goToPreviousPagePushed = false; 
            }
            
            if(goToPreviousWhenNotFullPage == true){
                countCardsOnLine = 1;                     // start counting the cards from the beginning
                countCardsOnPage = 1;
                countLines = 0;
                howManyCardsPlacedOnPageDraw = 0;
                goToPreviousWhenNotFullPage = false;
            }
            
            if(countCardsOnPage > 12){
                countCardsOnLine = 1;
                countCardsOnPage = 1;
                countLines = 0;
                howManyCardsPlacedOnPageDraw = 0;                     
                break;
            }
            
            if(countCardsOnLine > 6){             // if there are more than 6 cards we would print the next ones below
                countLines++;
                if(countLines == 2){              // reset the lines
                    countLines = 0;
                }
                countCardsOnLine = 1;             // start counting new line
            }
            
            if(countCardsOnLine <= 6 && countCardsOnPage < 13){      // countCards 6 is because we have 6 cards on every line, big card counter has to be 12 as there are maximum of 12 per page
                
                if(savedIndex <= 0){
                    changePageForwardIsAllowed = true;
                    changePageBackwardsIsAllowed = false;
                }

                if(savedIndex > 11){
                    changePageForwardIsAllowed = true;
                    changePageBackwardsIsAllowed = true;
                } 
 
                if(savedIndex == nrCardsAtDraw-1){
                    changePageBackwardsIsAllowed = true;
                    changePageForwardIsAllowed = false;
                }               

                if(savedIndex == nrCardsAtDraw-1 && topDrawCardPlaced == false){
                    groupForDrawPop.addChild(copyOfDrawCards[savedIndex].setMouseEventListener(drawPileAdapter), spacingDraw, countLinesArr[countLines]);
                }

                else{
                    if(savedIndex != nrCardsAtDraw-1){
                        groupForDrawPop.addChild((copyOfDrawCards[savedIndex].setOpacity(0.65f)).setMouseEventListener(drawPileAdapter), spacingDraw, countLinesArr[countLines]);
                    }
                }      
                howManyCardsPlacedOnPageDraw++;
                savedIndex++;
            }
            countCardsOnPage++; 
            countCardsOnLine++;     
        }
        // filling groups for the disposal pop up window
        for(int dispIntPop = 0; dispIntPop < nrCardsAtDisposal; dispIntPop++){ 
            if(copyOfDisposalCards.length != 0){                                
                spacingDisposal  = GCard.WIDTH * (1.05f * (float) ((dispIntPop % 6) - 2.9825)) + GCard.WIDTH /2;
                
                if(dispIntPop <= 5 && dispIntPop != nrCardsAtDisposal-1){       
                    groupForDisposalPop.addChild(copyOfDisposalCards[dispIntPop].setMouseEventListener(disposalPileAdapter), spacingDisposal, -140);
                }  
   
                if(dispIntPop > 5 && dispIntPop != nrCardsAtDisposal-1){
                    groupForDisposalPop.addChild(copyOfDisposalCards[dispIntPop].setMouseEventListener(disposalPileAdapter), spacingDisposal, 140);
                }

                if(dispIntPop == nrCardsAtDisposal-1 && topDisposalCardPlaced == false){
                    if(dispIntPop <= 5){              
                        groupForDisposalPop.addChild(copyOfDisposalCards[dispIntPop].setMouseEventListener(disposalPileAdapter), spacingDisposal, -140);
                    }     
                    if(dispIntPop > 5){
                        groupForDisposalPop.addChild(copyOfDisposalCards[dispIntPop].setMouseEventListener(disposalPileAdapter), spacingDisposal, 140);
                    }
                }

                if(copyCard != null){
                    if((copyOfDisposalCards[dispIntPop].getCard()).getName() == (copyCard.getCard()).getName() ){
                        groupForDisposalPop.removeChild(copyOfDisposalCards[dispIntPop]);
                    }
                }        
            }
        }  
        // adding the groups with player, disposal and draw cards to the game panel
        gamePanel.addChild(groupForLowerHandCards);
        gamePanel.addChild(groupForUpperHandCards);
        if(numberOfPlayersInGame >= 3){
            gamePanel.addChild(groupForLeftHandCards);
        }
        if(numberOfPlayersInGame == 4){    
            gamePanel.addChild(groupForRightHandCards);
        }     
        gamePanel.addChild(groupForDisposalCards);
        gamePanel.addChild(groupForDrawCards);
    }
    /**
    * Is called when someone has cheated.
    */ 
    public void showCheated(){
        /**
        * Blurs the game board.
        */
        GRect blurGame = new GRect(0, 0, 2000, 900, true);
        blurGame.setOpacity(0.8f);
        gamePanel.addChild(blurGame);
        /**
        * The window that pops up when someone has cheated.
        */
        GRect cheaterRect = new GRect(0, 0, 750, 200, true);
        cheaterRect.setFill(new Color (206, 0,0));
        gamePanel.addChild(cheaterRect);
        /**
        * The "game over" text.
        */
        GText gameOverTextCheat = new GText("GAME OVER");
        gameOverTextCheat.setPosition(-335, -200);
        gameOverTextCheat.setFill(new Color(242, 212, 161));
        gameOverTextCheat.setFontSize(100);
        gameOverTextCheat.setBold(true);
        gamePanel.addChild(gameOverTextCheat);
        /**
        * The cheater text.
        */
        GText cheaterText = new GText("Someone has cheated!");
        cheaterText.setPosition(-320,10);
        cheaterText.setFill(Color.WHITE);      
        cheaterText.setBold(true);
        cheaterText.setFontSize(50);
        gamePanel.addChild(cheaterText);
    }
    /**
    * Gets called when the game is over and no one has cheated. Shows the winner/s.
    * @param winnerNames A string array with the winner/winners, used for showing the winner window when the game finishes
    * @param winnerPoints An int array with the amount of points each winner has.
    */
    public void gameOver(String winnerNames[], int winnerPoints[]) throws Exception{
        /**
        * Integer that shows the amount of names of the winners.
        */
        int winnerNamesLength = winnerNames.length;
        /**
        * Integer that shows the amount of points of the winners.
        */
        int winnerPointsLength = winnerPoints.length;

        if(winnerNamesLength != winnerPointsLength){
            throw new Exception ("Winner names and the amount of winner points do not match!");
        }
        /**
        * Blurs the game board when the game is over.
        */
        GRect blurGame2 = new GRect(0, 0, 2000, 900, true);
        blurGame2.setOpacity(0.8f);
        gamePanel.addChild(blurGame2);
        /**
        * Color for the screen that opens when the game ends.
        */
        Color gameOverScreenC = new Color(115, 101, 76);
        /**
        * The "game over" text that appears when the game ends.
        */
        GText gameOverText = new GText("GAME OVER");
        gameOverText.setPosition(-330, -300);
        gameOverText.setFill(new Color(242, 212, 161));
        gameOverText.setFontSize(100);
        gameOverText.setBold(true);
        gamePanel.addChild(gameOverText);
        /**
        * The "thanks for playing" text that appears when the game ends.
        */
        GText thanksForPlaying = new GText("Thank you for playing!");
        thanksForPlaying.setPosition(-260, -70);
        thanksForPlaying.setFill(new Color(242, 212, 161));
        thanksForPlaying.setFontSize(40);
        thanksForPlaying.setBold(true);
        /**
        * The winner is/winners are text that appears when the game ends.
        */
        GText winnerIs = new GText("THE WINNER IS");
        /**
        * Rectangle background for winner 1.
        */
        GRect winner1Rect = new GRect(0, -205, 600, 150, true);
        winner1Rect.setFill(gameOverScreenC);
        /**
        * Rectangle background for winner 2.
        */
        GRect winner2Rect = new GRect(0, -85, 600, 100, true);
        winner2Rect.setFill(gameOverScreenC);
        /**
        * Rectangle background for winner 3.
        */
        GRect winner3Rect = new GRect(0, 10, 600, 100, true);
        winner3Rect.setFill(gameOverScreenC);
        /**
        * Rectangle background for winner 4.
        */
        GRect winner4Rect = new GRect(0, 130, 600, 150, true);
        winner4Rect.setFill(gameOverScreenC);
        /**
        * Text with the winner 1 name.
        */
        GText winner1 = new GText(winnerNames[0]);
        winner1.setPosition(-270,-170);
        winner1.setBold(true);
        winner1.setFontSize(30);
        winner1.setFill(Color.WHITE);

        gamePanel.addChild(winner1Rect);
        gamePanel.addChild(winner1);
        /**
        * Text "with" for the 1st winner.
        */
        GText withText1 = new GText("with:");
        withText1.setPosition(110, -170);
        withText1.setFill(Color.WHITE);
        withText1.setFontSize(25);
        gamePanel.addChild(withText1);
        /**
        * Text "with" for the 2nd winner.
        */
        GText withText2 = new GText("with:");
        withText2.setPosition(110, -80);
        withText2.setFill(Color.WHITE);
        withText2.setFontSize(25);
        /**
        * Text "with" for the 3rd winner.
        */
        GText withText3 = new GText("with:");
        withText3.setPosition(110, 10);
        withText3.setFill(Color.WHITE);
        withText3.setFontSize(25);
        /**
        * Text "with" for the 4th winner.
        */
        GText withText4 = new GText("with:");
        withText4.setPosition(110, 100);
        withText4.setFill(Color.WHITE);
        withText4.setFontSize(25);
 
        if(winnerNamesLength >= 2){
            winnerIs = new GText("THE WINNERS ARE");
            /**
            * Text with the winner 2 name.
            */
            GText winner2 = new GText(winnerNames[1]);
            winner2.setPosition(-270,-80);
            winner2.setBold(true);
            winner2.setFontSize(30);
            winner2.setFill(Color.WHITE);
            gamePanel.addChild(winner2Rect);
            gamePanel.addChild(winner2);
            gamePanel.addChild(withText2);
            thanksForPlaying.setPosition(-248, 20);
        }
        if(winnerNamesLength >= 3){
            GText winner3 = new GText(winnerNames[2]);
            /**
            * Text with the winner 3 name.
            */
            winner3.setPosition(-270,10);
            winner3.setBold(true);
            winner3.setFontSize(30);
            winner3.setFill(Color.WHITE);
            gamePanel.addChild(winner3Rect);
            gamePanel.addChild(winner3);
            gamePanel.addChild(withText3);
            thanksForPlaying.setPosition(-248, 110);
        }
        if(winnerNamesLength == 4){
            GText winner4 = new GText(winnerNames[3]);
            /**
            * Text with the winner 4 name.
            */
            winner4.setPosition(-270, 100);
            winner4.setBold(true);
            winner4.setFontSize(30);
            winner4.setFill(Color.WHITE);
            gamePanel.addChild(winner4Rect);
            gamePanel.addChild(winner4);
            gamePanel.addChild(withText4);
            thanksForPlaying.setPosition(-248, 170);
        }
        /**
        * Text with the winner 1 points.
        */
        GText winner1Points = new GText(String.valueOf(winnerPoints[0]) + " P");
        winner1Points.setPosition(180,-170);
        winner1Points.setFill(Color.WHITE);
        winner1Points.setBold(true);
        winner1Points.setFontSize(28);
        gamePanel.addChild(winner1Points);

        if(winnerPointsLength >= 2){
            /**
            * Text with the winner 2 points.
            */
            GText winner2Points = new GText(String.valueOf(winnerPoints[1]) + " P");
            winner2Points.setPosition(180,-80);
            winner2Points.setFill(Color.WHITE);
            winner2Points.setBold(true);
            winner2Points.setFontSize(28);
            gamePanel.addChild(winner2Points);
        }
        if(winnerPointsLength >= 3){
            /**
            * Text with the winner 3 points.
            */
            GText winner3Points = new GText(String.valueOf(winnerPoints[2]) + " P");
            winner3Points.setPosition(180,10);
            winner3Points.setFill(Color.WHITE);
            winner3Points.setBold(true);
            winner3Points.setFontSize(28);
            gamePanel.addChild(winner3Points);
        }
        if(winnerPointsLength == 4){
            /**
            * Text with the winner 4 points.
            */
            GText winner4Points = new GText(String.valueOf(winnerPoints[3]) + " P");
            winner4Points.setPosition(180,100);
            winner4Points.setFill(Color.WHITE);
            winner4Points.setBold(true);
            winner4Points.setFontSize(28);
            gamePanel.addChild(winner4Points);
        }

        winnerIs.setPosition(-180, -230);
        winnerIs.setFill(new Color(77, 59, 29));
        winnerIs.setBold(true);
        winnerIs.setFontSize(40);
        gamePanel.addChild(winnerIs);
        gamePanel.addChild(thanksForPlaying);
    }
    /**
    * Gets called when the game is over and no one has cheated in tournament. Shows the winner/s.
    * @param winnerNames A string array with the winner/winners, used for showing the winner window when the game finishes
    */
    public void gameOverTournament(String winnerNames[]){
        /**
        * Integer that shows the amount of names of the winners in tournament.
        */
        int winnerNamesLengthTournament = winnerNames.length;
        /**
        * Blurs the game board when the game is over in tournament.
        */
        GRect blurGame2Tournament = new GRect(0, 0, 2000, 900, true);
        blurGame2Tournament.setOpacity(0.8f);
        gamePanel.addChild(blurGame2Tournament);
        /**
        * Color for the screen that opens when the game ends in tournament.
        */
        Color gameOverScreenTournamentC = new Color(115, 101, 76);
        /**
        * The "game over" text that appears when the game ends in tournament.
        */
        GText gameOverTextTournament = new GText("GAME OVER");
        gameOverTextTournament.setPosition(-330, -300);
        gameOverTextTournament.setFill(new Color(242, 212, 161));
        gameOverTextTournament.setFontSize(100);
        gameOverTextTournament.setBold(true);
        gamePanel.addChild(gameOverTextTournament);
        /**
        * The "thanks for playing" text that appears when the game ends in tournament.
        */
        GText thanksForPlayingTournament = new GText("Thank you for playing!");
        thanksForPlayingTournament.setPosition(-260, -70);
        thanksForPlayingTournament.setFill(new Color(242, 212, 161));
        thanksForPlayingTournament.setFontSize(40);
        thanksForPlayingTournament.setBold(true);
        /**
        * The winner is/winners are text that appears when the game ends in tournament.
        */
        GText winnerIsTournament = new GText("THE WINNER IS");
        /**
        * Rectangle background for winner 1 in tournament.
        */
        GRect winner1RectTournament = new GRect(0, -205, 600, 150, true);
        winner1RectTournament.setFill(gameOverScreenTournamentC);
        /**
        * Rectangle background for winner 2 in tournament.
        */
        GRect winner2RectTournament = new GRect(0, -85, 600, 100, true);
        winner2RectTournament.setFill(gameOverScreenTournamentC);
        /**
        * Rectangle background for winner 3 in tournament.
        */
        GRect winner3RectTournament = new GRect(0, 10, 600, 100, true);
        winner3RectTournament.setFill(gameOverScreenTournamentC);
        /**
        * Rectangle background for winner 4 in tournament.
        */
        GRect winner4RectTournament = new GRect(0, 130, 600, 150, true);
        winner4RectTournament.setFill(gameOverScreenTournamentC);
        /**
        * Text with the winner 1 name in tournament.
        */
        GText winner1Tournament = new GText(winnerNames[0]);
        winner1Tournament.setPosition(-270,-170);
        winner1Tournament.setBold(true);
        winner1Tournament.setFontSize(30);
        winner1Tournament.setFill(Color.WHITE);

        gamePanel.addChild(winner1RectTournament);
        gamePanel.addChild(winner1Tournament);

        if(winnerNamesLengthTournament >= 2){
            winnerIsTournament = new GText("THE WINNERS ARE");
            /**
            * Text with the winner 2 name in tournament.
            */
            GText winner2Tournament = new GText(winnerNames[1]);
            winner2Tournament.setPosition(-270,-80);
            winner2Tournament.setBold(true);
            winner2Tournament.setFontSize(30);
            winner2Tournament.setFill(Color.WHITE);
            gamePanel.addChild(winner2RectTournament);
            gamePanel.addChild(winner2Tournament);
            thanksForPlayingTournament.setPosition(-248, 20);
        }
        if(winnerNamesLengthTournament >= 3){
            GText winner3Tournament = new GText(winnerNames[2]);
            /**
            * Text with the winner 3 name in tournament.
            */
            winner3Tournament.setPosition(-270,10);
            winner3Tournament.setBold(true);
            winner3Tournament.setFontSize(30);
            winner3Tournament.setFill(Color.WHITE);
            gamePanel.addChild(winner3RectTournament);
            gamePanel.addChild(winner3Tournament);

            thanksForPlayingTournament.setPosition(-248, 110);
        }
        if(winnerNamesLengthTournament == 4){
            GText winner4Tournament = new GText(winnerNames[3]);
            /**
            * Text with the winner 4 name in tournament.
            */
            winner4Tournament.setPosition(-270, 100);
            winner4Tournament.setBold(true);
            winner4Tournament.setFontSize(30);
            winner4Tournament.setFill(Color.WHITE);
            gamePanel.addChild(winner4RectTournament);
            gamePanel.addChild(winner4Tournament);

            thanksForPlayingTournament.setPosition(-248, 170);
        }

        winnerIsTournament.setPosition(-180, -230);
        winnerIsTournament.setFill(new Color(77, 59, 29));
        winnerIsTournament.setBold(true);
        winnerIsTournament.setFontSize(40);
        gamePanel.addChild(winnerIsTournament);
        gamePanel.addChild(thanksForPlayingTournament);
    }
    /**
    * Requests move from a human player.
    */   
    public Move requestMoveFromCurrentHumanPlayer(){
        synchronized(moveWaitMonitor){
            try{
                moveWaitMonitor.wait();
            }
            catch(Exception e){
                System.exit(0);
            }
            // we got notified that move was confirmed when we reached here
            /**
            * The move containing a card to be taken and a card to be removed.
            */
            Move m = new Move(cardsToBeSwitched[0], cardsToBeSwitched[1]);
            return m;
        }
    }
}
