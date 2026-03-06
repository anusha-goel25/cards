import java.util.ArrayList;

public class RedKingComputer {

	// At the start of the game, the computer only knows 2 of its cards like the player
	// Through playing however, it can learn of its other cards which helps it estimate points better
	// If it does not know the point value of a card,
	// it takes into account the point values of other cards that it's seen to make a decision
	ArrayList<RedKingCard> cardsItKnows = new ArrayList<>();
	ArrayList<RedKingCard> seenCards = new ArrayList<>();

	public void initialKnowledge(Hand hand){
		if (hand.getCard(0) != null){
			cardsItKnows.add((RedKingCard) hand.getCard(0));
		}
		if (hand.getCard(1) != null){
			cardsItKnows.add((RedKingCard) hand.getCard(1));
		}
	}

	public void addKnownCard(RedKingCard learnedCard){
		cardsItKnows.add(learnedCard);
	}

	public void updateSeenCards(RedKingCard seenCard){
		seenCards.add(seenCard);
	}

	public void cardIsRemoved(RedKingCard removedCard) {
        cardsItKnows.remove(removedCard);
    }

	public int guessTotalPoints(Hand hand){
		// baseline is six, so if it hasn't seen any cards yet then it will just choose 6 as an average
		int averageSeenCards = 6;
		if (seenCards.size() > 0){
			int seenTotal = 0;
			for (int i = 0; i < seenCards.size(); i++){
				seenTotal = seenTotal + seenCards.get(i).points;
			}
			averageSeenCards = seenTotal / seenCards.size();
		}
		int guessedTotal = 0;
		for (int i = 0; i < hand.getSize(); i++){
			RedKingCard currentCard = (RedKingCard) hand.getCard(i);
			if (cardsItKnows.contains(currentCard)){
				guessedTotal = guessedTotal + currentCard.points;
			}
			else {
				guessedTotal = guessedTotal + averageSeenCards;
			}
		}
		return guessedTotal;
	}

	public RedKingCard decideToSwap(Hand hand, RedKingCard drawnCard){
		RedKingCard bestOption = null;
		for (int i = 0; i < hand.getSize(); i++){
			RedKingCard handCard = (RedKingCard) hand.getCard(i);
			if (handCard.points > drawnCard.points) {
                if (bestOption == null || handCard.points > bestOption.points) {
                    bestOption = handCard;
                }
            }
		}

		// basically saying that if it's higher than a known card swap it before considering anything else
		// if there is any best option, always choose it basically
		if (bestOption != null){
			return bestOption;
		}
		
		// In the case that all known cards are lower than the drawn card, depending on value, switch with unkown
		if (drawnCard.points <= 6){
			for (int i = 0; i < hand.getSize(); i++){
				RedKingCard unknownCard = (RedKingCard) hand.getCard(i);
				if (!cardsItKnows.contains(unknownCard)){
					return unknownCard;
				}
			}
		}

		// if none of the other options are good for swapping, it discards the card
		return null;
	}

	public RedKingCard permanentDiscard(Hand hand, RedKingCard lastPlayedCard){
		for (int i = 0; i < hand.getSize(); i++){
			RedKingCard currentCard = (RedKingCard) hand.getCard(i);
			if (currentCard != null && currentCard.value == lastPlayedCard.value){
            return currentCard;
        }
			}
			// if it doesn't match any of them, returning null prevents anything from happening
			// the computer moves on to drawing a card instead
			return null;
		}
	}

