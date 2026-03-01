import java.util.Collections;

import processing.core.PApplet;

public class RedKing extends CardGame {
    RedKingComputer computerPlayer;
    boolean chooseDrawDecision = false;
    boolean chooseSwapCard = false;
    boolean chooseQueenSwapCard = false;
    boolean chooseJackLook = false;
    RedKingCard cardDrawn;
    RedKingCard cardToLookAt;
    RedKingCard pendingWildCard;

    ClickableRectangle[] ChoiceButtons;
    static String[] ChoiceLabels = {"Discard", "Switch"};
    int[] ChoiceButtonX = {160, 340};
    int[] ChoiceButtonY = {490, 490};
    int ChoiceButtonWidth = 100;
    int ChoiceButtonHeight = 35;

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
        if (clickedChoice == 0){
            return switchCard;
        }
        else {
            return discardCard;
        }
    }

    @Override
     public void drawCard(Hand hand) {
        super.drawCard(hand);
        if (deck != null && !deck.isEmpty()) {
            hand.addCard(deck.remove(0));
        } else if (discardPile != null && discardPile.size() > 1) {
            // Reshuffle discard pile into deck if deck is empty
            lastPlayedCard = discardPile.remove(discardPile.size() - 1);
            deck.addAll(discardPile);
            discardPile.clear();
            discardPile.add(lastPlayedCard);
            Collections.shuffle(deck);

            if (!deck.isEmpty()) {
                hand.addCard(deck.remove(0));
            }
        }
    }

    
    private void handleSpecialCards(Card card) {
        if (card.value == "Queen") {
            System.out.println("Switch one of your cards with someone else'");
            chooseQueenSwapCard = true;
        } else if (card.value == "Jack") {
            System.out.println("You can look at one of your cards");
            chooseJackLook = true;
        }
    }

    @Override
    public void handleDrawButtonClick(int mouseX, int mouseY) {
        if (chooseDrawDecision) {
            return;
        }
        super.handleDrawButtonClick(mouseX, mouseY);
    }

    @Override
    protected boolean isValidPlay(Card card) {
        RedKingCard redkingCard = (RedKingCard) card;
        // Card must match suit or value of last played card
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
            handleQueenSwapTarget(mouseX, mouseY);
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

    public void handleDrawCardClick(int mouseX, int mouseY){
    }

    public void handleQueenSwapTarget(int mouseX, int mouseY){ 
        RedKingCard clickedCard = (RedKingCard) getClickedCard(mouseX, mouseY);
    }

    public void handleQueenSwapGiveaway (int mouseX, int mouseY){
        RedKingCard clickedCard = (RedKingCard) getClickedCard(mouseX, mouseY);
    }

    public void handleSwapClick(int mouseX, int mouseY){
        RedKingCard clickedCard = (RedKingCard) getClickedCard(mouseX, mouseY);
    }

    public void handleJackLook(int mouseX, int mouseY){ 
        RedKingCard clickedCard = (RedKingCard) getClickedCard(mouseX, mouseY);
    }

    @Override
    public void handleComputerTurn() {
        RedKingCard choice = computerPlayer.playCard(playerTwoHand, (RedKingCard) lastPlayedCard);
        if (choice == null) {
            drawCard(playerTwoHand);
            playerTwoHand.getCard(0).setTurned(false);
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
}