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
        deck.add(1, createCard("Hearts","Jack",11));
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
    // accidentally click on something random
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

        playerOneHand.positionCardsInGrid(20, 270, 80, 120, 130, 2);
        playerTwoHand.positionCardsInGrid(370, 50, 80, 120, 130, 2);
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

        playerOneHand.positionCardsInGrid(20, 270, 80, 120, 130, 2);
        playerTwoHand.positionCardsInGrid(370, 50, 80, 120, 130, 2);

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
        RedKingCard choice = computerPlayer.playCard(playerTwoHand, (RedKingCard) lastPlayedCard);
        if (choice == null) {
            drawCard(playerTwoHand);
            System.out.println("player two draws");
            switchTurns();
            return;
        }
        if (playCard(choice, playerTwoHand)) {
            if ("Wild".equals(choice.suit)) {
                choice.suit = computerPlayer.chooseComputerWildColor(playerTwoHand);
            }
            playerOneHand.positionCardsInGrid(20, 270, 80, 120, 130, 2);
            playerTwoHand.positionCardsInGrid(370, 50, 80, 120, 130, 2);
        } else {
            System.out.println("ERROR, player two / computer chose an invalid play");
        }
    }

    // For making/initializing the draw choice buttons,
    // I basically use the same exact logic that was used to make wild buttons in
    // UNO
    // the only main changes are that I hard coded the positioning + drew buttons
    // directly in drawChoices
    // https://processing.org/reference - used this to refresh my memory on
    // processing syntax

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
}