import processing.core.PApplet;
import processing.core.PImage;

public class RedKingCard extends Card {
    public int points;
    public static PImage cardBack;

    public RedKingCard(String value, String suit, int points) {
        super(value, suit);
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public void draw(PApplet sketch) {
        // if the card is turned down, it won't display the number
        if (turned) {
            sketch.image(cardBack, x, y, width, height);
        } else {
            sketch.image(img, x, y, width, height);
        }
        sketch.stroke(0,0,0);
        sketch.noFill();
        sketch.rect(x, y, width, height);
    }
}
