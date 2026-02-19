import java.util.Collections;

import processing.core.PApplet;

public class RedKing extends CardGame {
    // Uno-specific state
    RedKingComputer computerPlayer;
    boolean choosingWildColor = false;
    RedKingCard pendingWildCard;
    ClickableRectangle[] wildColorButtons;
    int wildButtonSize = 24;
    int wildCenterX = 300;
    int wildCenterY = 300;
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

 
    protected void dealCards(int numCards){
        Collections.shuffle(deck);
        for (int j = 0; j < numCards; j++){
            Card newcard = deck.remove(0);
            newcard.setTurned(true);
            playerOneHand.addCard(newcard);

            Card newcardcomputer = deck.remove(0);
            newcardcomputer.setTurned(true);
            playerTwoHand.addCard(newcardcomputer);
        }

        playerOneHand.positionCardsInGrid(20, 270, 80, 120, 130, 2);
        playerTwoHand.positionCardsInGrid(370, 50, 80, 120, 130,2);
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

        initializeWildColorButtons();
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

    private void handleSpecialCards(Card card) {
        if (card.value.equals("Skip") || card.value.equals("Reverse")) {
            // right now this only supports 2 players, so Reverse is the same as Skip
            System.out.println("Skipping opponent's turn");
            switchTurns(); // Skip opponent's turn
        } else if (card.value.startsWith("Draw ")) {
            System.out.println("Skipping opponent's turn");
            int drawNum = "Draw Two".equals(card.value) ? 2 : 4;
            for (int i = 0; i < drawNum; i++) {
                // refactored into superclass, assuming you've already switched turns to the
                // opponent
                drawCard(playerOneTurn ? playerOneHand : playerTwoHand);
            }
            switchTurns();
        }
    }

    @Override
    public void handleDrawButtonClick(int mouseX, int mouseY) {
        if (choosingWildColor) {
            return;
        }
        super.handleDrawButtonClick(mouseX, mouseY);
    }

    @Override
    protected boolean isValidPlay(Card card) {
        RedKingCard unoCard = (RedKingCard) card;
        // Wild cards are always valid
        if (unoCard.suit.equals("Wild")) {
            return true;
        }
        // Card must match suit or value of last played card
        RedKingCard lastUno = (RedKingCard) lastPlayedCard;
        return unoCard.suit.equals(lastUno.suit) ||
                unoCard.value.equals(lastUno.value);
    }

    @Override
    public void handleCardClick(int mouseX, int mouseY) {
        if (choosingWildColor) {
            handleWildChooserClick(mouseX, mouseY);
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
            if ("Wild".equals(selectedCard.suit)) {
                pendingWildCard = (RedKingCard) selectedCard;
                choosingWildColor = true;
                return;
            }
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
            playerOneHand.positionCards(50, 450, 80, 120, 20);
            playerTwoHand.positionCards(50, 50, 80, 120, 20);
        } else {
            System.out.println("ERROR, player two / computer chose an invalid play");
        }
    }

    @Override
    public void drawChoices(PApplet app) {
        drawWildChooser(app);
    }

    public void drawWildChooser(PApplet app) {
        if (!choosingWildColor) {
            return;
        }
        app.push();
        app.fill(255, 255, 255, 230);
        app.noStroke();
        app.rect(wildCenterX - 50, wildCenterY - 50, 100, 100, 8);

        for (int i = 0; i < wildColorButtons.length; i++) {
            ClickableRectangle button = wildColorButtons[i];
            switch (colors[i]) {
                case "Red":
                    app.fill(255, 0, 0);
                    break;
                case "Yellow":
                    app.fill(255, 255, 0);
                    break;
                case "Green":
                    app.fill(0, 255, 0);
                    break;
                case "Blue":
                    app.fill(40, 40, 210);
                    break;
                default:
                    app.fill(200);
                    break;
            }
            app.rect(button.x, button.y, button.width, button.height, 4);
        }
        app.pop();
    }

    private void handleWildChooserClick(int mouseX, int mouseY) {
        int raiseAmount = 15;
        for (int i = 0; i < wildColorButtons.length; i++) {
            if (wildColorButtons[i].isClicked(mouseX, mouseY)) {
                if (playCard(pendingWildCard, playerOneHand)) {
                    // Set the wild card's suit to the chosen color AFTER it is validated
                    pendingWildCard.suit = colors[i];
                    pendingWildCard.setSelected(false, raiseAmount);
                    pendingWildCard = null;
                    choosingWildColor = false;
                    selectedCard = null;
                    playerOneHand.positionCards(50, 450, 80, 120, 20);
                    playerTwoHand.positionCards(50, 50, 80, 120, 20);
                }
                return;
            }
        }
    }

    private void initializeWildColorButtons() {
        wildColorButtons = new ClickableRectangle[4];
        int offset = 28;
        int half = wildButtonSize / 2;
        System.out.println("Initializing wild color buttons at center (" + wildCenterX + ", " + wildCenterY
                + ") with offset " + offset);
        wildColorButtons[0] = createWildButton(wildCenterX - offset - half, wildCenterY - half);
        wildColorButtons[1] = createWildButton(wildCenterX + offset - half, wildCenterY - half);
        wildColorButtons[2] = createWildButton(wildCenterX - half, wildCenterY - offset - half);
        wildColorButtons[3] = createWildButton(wildCenterX - half, wildCenterY + offset - half);
    }

    private ClickableRectangle createWildButton(int x, int y) {
        ClickableRectangle button = new ClickableRectangle();
        button.x = x;
        button.y = y;
        button.width = wildButtonSize;
        button.height = wildButtonSize;
        return button;
    }
}
