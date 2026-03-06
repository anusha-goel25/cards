public class RedKingHand extends Hand {
// needed to create a RedKing specific Hand because the positions need to be constant 
// so that cards can fill empty spots - basically allowing certain slots to be null
// so that they can be refilled later

// my dad helped me with this quite a bit - i was having a lot of trouble with cards
// dissapearing and stuff so he helped me with some of the logic

int gridstartingx;
int gridstartingy;
int cardWidth;
int cardHeight;
int cardsPerRow;
int spacing;

    RedKingHand(){
        super();
    }

    // really only used for the penalty draw, since all the other places you're swapping
    @Override
    public void addCard(Card card){
        for (int i = 0; i < cards.size(); i++){
            if (cards.get(i) == null){
                // used https://www.w3schools.com/java/java_arraylist.asp
                // to remember how to change an element of an arraylist
                // also got the information there that you need to use a return so that it doesn't
                // keep looping through all the null spots and putting cards in them
                cards.set(i,card);
                return;
            }
        }
        cards.add(card);
    }

    @Override
    // in RedKing remove card is only used when a card needs to be permanently discarded
    // not during a swap because I don't want to mess up positioning
    public void removeCard(Card card){
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == card){
                cards.set(i, null);
                return;
            }
        }
    }

    // maintains positioning by specifically swapping the new card to the location of the old one
    public void swapSlot(Card cardRemoved, Card cardAdded){
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == cardRemoved) {
                cardAdded.setPosition(cardRemoved.x, cardRemoved.y, cardRemoved.width, cardRemoved.height);
                cards.set(i, cardAdded);
                return;
            }
        }
    }
    @Override
    public void positionCardsInGrid(int startX, int startY, int cardWidth, int cardHeight, int spacing, int cardsPerRow) {
        this.gridstartingx = startX;
        this.gridstartingy = startY;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.spacing = spacing;
        this.cardsPerRow = cardsPerRow;
        super.positionCardsInGrid(gridstartingx, gridstartingy, cardWidth, cardHeight, spacing, cardsPerRow);
    }

    // used the original position cards in a grid to figure out math stuff for this
    public void positionAddedCard(Card addedCard) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == addedCard) {
                int row = i / cardsPerRow;
                int col = i % cardsPerRow;
                int x = gridstartingx + (col * spacing);
                int y = gridstartingy + (row * spacing);
                addedCard.setPosition(x, y, cardWidth, cardHeight);
                return;
            }
        }
    }
}
