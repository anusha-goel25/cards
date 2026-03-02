import java.util.Collections;

import processing.core.PApplet;

public class RedKing extends CardGame {
    RedKingComputer computerPlayer;
    boolean chooseDrawDecision = false;
    boolean chooseSwapCard = false;
    boolean chooseQueenSwapCard = false;
    boolean chooseQueenSwapGiveaway = false;
    boolean chooseJackLook = false;
    RedKingCard cardDrawn;
    RedKingCard lookCard;
    RedKingCard pendingQueenSwapCard;
    int timer = 0;

    ClickableRectangle[] ChoiceButtons;
    static String[] ChoiceLabels = { "Discard", "Switch" };
    static int[] choiceButtonX = { 160, 340 };
    static int[] choiceButtonY = { 490, 490 };
    static int choiceButtonWidth = 100;
    static int choiceButtonHeight = 35;

    static String[] colors = { "Hearts", "Diamonds", "Clovers", "Spades" };
    static String[] values = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace" };

    public RedKing() {
        initializeGame();
    }

    @Override
    protected void createDeck() {
        // Create deck (Red King uses a standard 52 card deck)
        for (String color : colors) {
            for (String value : values) {
                if (value == "Jack") {
                    deck.add(createCard(color, "Jack", 11));
                } else if (value == "Queen") {
                    deck.add(createCard(color, "Queen", 12));
                } else if (value == "King" && (color == "Hearts" || color == "Diamonds")) {
                    deck.add(createCard(color, "King", 0));
                } else if (value == "King" && (color == "Spades" || color == "Clovers")) {
                    deck.add(createCard(color, "King", 13));
                } else if (value == "Ace") {
                    deck.add(createCard(color, "Ace", 1));
                } else {
                    deck.add(createCard(color, value, Integer.parseInt(value)));
                }
            }
        }
    }

    @Override
    protected void dealCards(int numCards) {
        Collections.shuffle(deck);
        for (int j = 0; j < numCards; j++) {
            Card newcard = deck.remove(0);
            playerOneHand.addCard(newcard);
            newcard.setTurned(true);

            Card newcardcomputer = deck.remove(0);
            newcardcomputer.setTurned(true);
            playerTwoHand.addCard(newcardcomputer);
        }

        playerOneHand.positionCardsInGrid(20, 270, 80, 120, 130, 2);
        playerTwoHand.positionCardsInGrid(370, 50, 80, 120, 130, 2);
    }

    @Override
    protected void initializeGame() {
        super.initializeGame();
        computerPlayer = new RedKingComputer();
        dealCards(4);
        // Place first card on discard pile
        lastPlayedCard = deck.remove(0);
        if (lastPlayedCard.suit.equals("Wild")) {
            System.out.println("setting wild to a random color");
            // If first card is wild, set it to a random color
            lastPlayedCard.suit = colors[(int) (Math.random() * colors.length)];
        }
        discardPile.add(lastPlayedCard);
        initializeDrawChoiceButtons();
    }

    @Override
    public void switchTurns() {
        if (playerOneHand.getSize() == 0){
            endGame();
        }
        else if (playerTwoHand.getSize() == 0){
            endGame();
        }
        playerOneTurn = !playerOneTurn;
        playerOneHand.positionCardsInGrid(20, 270, 80, 120, 130, 2);
        playerTwoHand.positionCardsInGrid(370, 50, 80, 120, 130, 2);
    }

    private RedKingCard createCard(String suit, String value, int points) {
        RedKingCard card = new RedKingCard(suit, value, points); // Image loading can be added later
        card.suit = suit;
        card.value = value;
        card.points = points;
        return card;
    }

    @Override
    public boolean playCard(Card card, Hand hand) {
        super.playCard(card, hand);
        handleSpecialCards(card);
        return true;
    }

    public String cardChoice(int clickedChoice) {
        String switchCard = "switchChosen";
        String discardCard = "discardChosen";
        if (clickedChoice == 0) {
            return switchCard;
        } else {
            return discardCard;
        }
    }

    private void handleSpecialCards(Card card) {
        if (card.value.equals("Queen")) {
            System.out.println("Switch one of your cards with someone else'");
            chooseQueenSwapCard = true;
        } else if (card.value.equals("Jack")) {
            System.out.println("You can look at one of your cards");
            chooseJackLook = true;
        }
    }

    @Override
    public void handleDrawButtonClick(int mouseX, int mouseY) {
        System.out.println("drawButton x=" + drawButton.x + " y=" + drawButton.y + " w=" + drawButton.width + " h="
                + drawButton.height);
        if (!drawButton.isClicked(mouseX, mouseY)) {
            return;
        }
        // making sure that if you click draw during another choice time, you won't mess
        // up the game
        if (choiceInProgress()) {
            return;
        }
        // Makes sure that if it's not your turn you can't accidentally click something
        // and mess things up
        if (!playerOneTurn) {
            return;
        }

        // Basically making a temporary hand to hold the cards in the same way that the
        // original class does
        // before transferring the cards to the permanent hand (this will happen in
        // drawChoices once chooseDrawDecision is turned on)
        Hand temporary = new Hand();
        drawCard(temporary);
        cardDrawn = (RedKingCard) temporary.getCard(0);
        if (cardDrawn == null) {
            return;
        }
        cardDrawn.setTurned(false);
        cardDrawn.setPosition(220, 350, 80, 120);
        chooseDrawDecision = true;

    }

    // Makes sure that if you're in the middle of making a choice, you can't
    // accidentally click on something random and also tells you what choice is happening
    private boolean choiceInProgress() {
        return chooseDrawDecision || chooseSwapCard || chooseQueenSwapCard || chooseQueenSwapGiveaway || chooseJackLook;
    }

    @Override
    protected boolean isValidPlay(Card card) {
        RedKingCard redkingCard = (RedKingCard) card;

        // the card needs to match the value of last played card
        RedKingCard lastRedKing = (RedKingCard) lastPlayedCard;
        return redkingCard.value.equals(lastRedKing.value);
    }

    @Override
    public void handleCardClick(int mouseX, int mouseY) {
        if (!choiceInProgress()){
            RedKingCard clickedCard = (RedKingCard) getClickedCard(mouseX, mouseY);
            // if it's the same number you get to get rid of a card
            if (isValidPlay(clickedCard)){
                playerOneHand.removeCard(clickedCard);
                discardPile.add(clickedCard);
                lastPlayedCard = clickedCard;
                switchTurns();
                return;          
            // if it's not the same number, you gain an additional card
            } else {
                drawCard(playerOneHand);
                switchTurns();
                return;
            }
        }

        if (chooseDrawDecision) {
            handleDrawCardClick(mouseX, mouseY);
            return;
        }
        if (chooseSwapCard) {
            handleSwapClick(mouseX, mouseY);
            return;
        }
        if (chooseQueenSwapCard) {
            handleQueenChooseTarget(mouseX, mouseY);
            return;
        }
        if (chooseQueenSwapGiveaway) {
            handleQueenSwapGiveaway(mouseX, mouseY);
            return;
        }
        if (chooseJackLook) {
            handleJackLook(mouseX, mouseY);
            return;
        }
        RedKingCard clickedCard = (RedKingCard) getClickedCard(mouseX, mouseY);
        if (clickedCard == null) {
            return;
        }
        // this is for the first time
        if (selectedCard == null) {
            selectedCard = clickedCard;
            selectedCard.setSelected(true, selectedCardRaiseAmount);
            return;
        }
        // this is the second time
        if (clickedCard == selectedCard) {
            System.out.println("playing card: " + selectedCard.value + " of " + selectedCard.suit);
            if (playCard((RedKingCard) selectedCard, playerOneHand)) {
                selectedCard.setSelected(false, selectedCardRaiseAmount);
                selectedCard = null;
            }
            return;
        }

        selectedCard.setSelected(false, selectedCardRaiseAmount);
        selectedCard = clickedCard;
        selectedCard.setSelected(true, selectedCardRaiseAmount);
    }

    public void handleDrawCardClick(int mouseX, int mouseY) {
        if (ChoiceButtons[0].isClicked(mouseX, mouseY)) {
            discardPile.add(cardDrawn);
            lastPlayedCard = cardDrawn;
            cardDrawn = null;
            chooseDrawDecision = false;

            // making sure that special cards can be used
            handleSpecialCards(lastPlayedCard);
            if (!chooseQueenSwapCard && !chooseJackLook) {
                switchTurns();
            }
        } else if (ChoiceButtons[1].isClicked(mouseX, mouseY)) {
            chooseDrawDecision = false;
            chooseSwapCard = true;
        }
    }

    public void handleSwapClick(int mouseX, int mouseY) {
        RedKingCard clickedCard = (RedKingCard) getClickedCard(mouseX, mouseY);
        System.out.println("handleSwapClick called, clickedCard=" + clickedCard);
        if (clickedCard == null) {
            return;
        }
        playerOneHand.removeCard(clickedCard);
        discardPile.add(clickedCard);
        clickedCard.setTurned(false);
        lastPlayedCard = clickedCard;

        cardDrawn.setTurned(true);
        playerOneHand.addCard(cardDrawn);
        cardDrawn = null;
        chooseSwapCard = false;
        switchTurns();
    }

    public void handleQueenChooseTarget(int mouseX, int mouseY) {
        for (int i = 0; i < playerTwoHand.getSize(); i++) {
            Card snatchedCard = playerTwoHand.getCard(i);
            if (snatchedCard != null && snatchedCard.isClicked(mouseX, mouseY)) {
                // adjusting the pendingWild variable so that it instead holds the
                // QueenSwapTarget chosen for the switch
                // so that it can be used in QueenSwapGiveaway
                pendingQueenSwapCard = (RedKingCard) snatchedCard;
                chooseQueenSwapCard = false;
                chooseQueenSwapGiveaway = true;
                return;
            }
        }
    }

    public void handleQueenSwapGiveaway(int mouseX, int mouseY) {
        RedKingCard clickedCard = (RedKingCard) getClickedCard(mouseX, mouseY);
        if (clickedCard == null) {
            return;
        }
        playerOneHand.removeCard(clickedCard);
        playerTwoHand.removeCard(pendingQueenSwapCard);
        playerTwoHand.addCard(clickedCard);
        playerOneHand.addCard(pendingQueenSwapCard);

        clickedCard.setTurned(true);
        pendingQueenSwapCard.setTurned(true);
        pendingQueenSwapCard = null;
        chooseQueenSwapGiveaway = false;
        switchTurns();
    }

    public void handleJackLook(int mouseX, int mouseY) {
        RedKingCard clickedCard = (RedKingCard) getClickedCard(mouseX, mouseY);
        if (clickedCard == null) {
            return;
        }
        lookCard = clickedCard;
        lookCard.setTurned(false);

        // timer is initialized here, but the actual counting down happens in
        // drawChoices to use the frames
        timer = 120;
        chooseJackLook = false;
        switchTurns();
    }

    @Override
    public void handleComputerTurn() {
        // The game ends if the deck is empty
        if (deck.isEmpty()) {
            endGame();
            return;
        }
        // first the computer needs to check if it can get rid of a card
        RedKingCard discardCard = computerPlayer.permanentDiscard(playerTwoHand, (RedKingCard) lastPlayedCard);
        if (discardCard != null) {
            playerTwoHand.removeCard(discardCard);
            computerPlayer.cardIsRemoved(discardCard);
            discardCard.setTurned(false);
            discardPile.add(discardCard);
            lastPlayedCard = discardCard;
            switchTurns();
            return;
        }
        // basically the same as the earlier code for the player
        // creates a temporary hadn while the computer decides what to do
        Hand temporary = new Hand();
        drawCard(temporary);
        cardDrawn = (RedKingCard) temporary.getCard(0);
        computerPlayer.addKnownCard(cardDrawn);
        cardDrawn.setPosition(220, 350, 80, 120);

        RedKingCard swapCard = computerPlayer.decideToSwap(playerTwoHand,cardDrawn);

        // basically if it found a card to swap with it will do the swap
        // decideToSwap is written so that if it chooses nothing it will return null,
        // so whenever it is not null that means that a swap needs to occur
        if (swapCard != null){
            System.out.println("The computer chose to swap its card");
            playerTwoHand.removeCard(swapCard);
            computerPlayer.cardIsRemoved(swapCard);

            discardPile.add(swapCard);
            swapCard.setTurned(false);
            lastPlayedCard = swapCard;

            cardDrawn.setTurned(true);
            playerTwoHand.addCard(cardDrawn);

        }
        else if (swapCard == null){
            System.out.println("The computer chose to discard its card");
            discardPile.add(cardDrawn);
            cardDrawn.setTurned(false);
            lastPlayedCard = cardDrawn;
        }
        switchTurns();
    }

    // For making/initializing the draw choice buttons,
    // I basically use the same exact logic that was used to make wild buttons in UNO
    // the only main changes are that I hard coded the positioning + drew buttons directly in drawChoices
    // https://processing.org/reference - used this to refresh my memory on processing syntax

    @Override
    public void drawChoices(PApplet app) {
        // running the timer during the jackLook
        if (lookCard != null && timer > 0) {
            timer = timer - 1;
            if (timer <= 0) {
                lookCard.setTurned(true);
                lookCard = null;
            }
        }

        if (!chooseDrawDecision) {
            return;
        }
        // drawing the card that you've just drawn
        cardDrawn.draw(app);
        app.push();
        app.textSize(14);
        app.textAlign(app.CENTER, app.CENTER);
        // drawing the buttons
        for (int i = 0; i < ChoiceButtons.length; i++) {
            ClickableRectangle button = ChoiceButtons[i];
            app.fill(200);
            app.rect(button.x, button.y, button.width, button.height, 6);
            app.fill(0);
            app.text(ChoiceLabels[i], button.x + button.width / 2, button.y + button.height / 2);
        }
        app.pop();
    }

    private void initializeDrawChoiceButtons() {
        ChoiceButtons = new ClickableRectangle[2];
        ChoiceButtons[0] = createDrawButton(choiceButtonX[0], choiceButtonY[0]);
        ChoiceButtons[1] = createDrawButton(choiceButtonX[1], choiceButtonY[1]);
    }

    private ClickableRectangle createDrawButton(int x, int y) {
        ClickableRectangle button = new ClickableRectangle();
        button.x = x;
        button.y = y;
        button.width = choiceButtonWidth;
        button.height = choiceButtonHeight;
        return button;
    }

    // lets you calculate 
    public int calculatePoints(Hand hand){
        int totalPoints = 0;
        for (int i = 0; i < hand.getSize(); i++){
            RedKingCard currentCard = (RedKingCard) hand.getCard(i);
            totalPoints = totalPoints + currentCard.points;
        }
        return totalPoints;
    }

    // code to end the game turns the cards over, calculates the total points for each player, and determines a winner
    public void endGame(){
        for (int i = 0; i < playerOneHand.getSize(); i++){
            playerOneHand.getCard(i).setTurned(false);
        }
        for (int i = 0; i < playerTwoHand.getSize(); i++){
            playerTwoHand.getCard(i).setTurned(false);
        }

        int playerPoints = calculatePoints(playerOneHand);
        int computerPoints = calculatePoints(playerTwoHand);

        if (playerPoints < computerPoints){
            System.out.println("Computer Wins :(");
        }
        else if (playerPoints == computerPoints){
            System.out.println("Tie!");
        }
        else if (playerPoints > computerPoints){
            System.out.println("You Win :)");
        }
    }
}