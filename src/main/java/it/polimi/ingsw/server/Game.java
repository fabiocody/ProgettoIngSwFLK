package it.polimi.ingsw.server;

import it.polimi.ingsw.objectivecards.*;
import it.polimi.ingsw.toolcards.*;
import java.util.*;
import java.util.stream.Collectors;


// This class contains all the information about the current game
public class Game implements Runnable {

    private List<Player> players;
    private ObjectiveCardsGenerator objectiveCardsGenerator;
    private List<ObjectiveCard> publicObjectiveCards;
    private DiceGenerator diceGenerator;
    private List<ToolCard> toolCards;
    private RoundTrack roundTrack;

    Game(List<String> players) {
        this.players = players.stream().map(Player::new).collect(Collectors.toList());
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public int getNumberOfPlayers() {
        return this.players.size();
    }

    private synchronized ObjectiveCardsGenerator getObjectiveCardsGenerator() {
        if (this.objectiveCardsGenerator == null)
            this.objectiveCardsGenerator = new ObjectiveCardsGenerator(this.getNumberOfPlayers());
        return objectiveCardsGenerator;
    }

    public synchronized List<ObjectiveCard> getPublicObjectiveCards() {
        if (this.publicObjectiveCards == null)
            return new Vector<>();
        return new Vector<>(this.publicObjectiveCards);
    }

    public synchronized DiceGenerator getDiceGenerator() {
        if (this.diceGenerator == null)
            this.diceGenerator = new DiceGenerator();
        return this.diceGenerator;
    }

    public synchronized List<ToolCard> getToolCards() {
        if (this.toolCards == null)
            return new Vector<>();
        return new Vector<>(this.toolCards);
    }

    public synchronized RoundTrack getRoundTrack() {
        if (this.roundTrack == null)
            this.roundTrack = new RoundTrack();
        return this.roundTrack;
    }

    // Setup method to be called at game starting
    private void setup() {
        this.players.forEach(p -> p.setPrivateObjectiveCard(this.getObjectiveCardsGenerator().dealPrivate()));
        // TODO Window patterns setup
        this.players.forEach(p -> p.setFavorTokens(p.getWindowPattern().getFavorTokens()));
        this.toolCards = ToolCardsGenerator.generate();
        this.publicObjectiveCards = this.getObjectiveCardsGenerator().generatePublic();
        Collections.shuffle(this.players);
    }

    // This method is supposed to be counting VP for each player.
    // Scores are stored in a map.
    private void endGame() {
        Map<Player, Integer> scores = new HashMap<>();
        for (Player p : this.players) {
            int score = 0;
            for (ObjectiveCard c : this.getPublicObjectiveCards()) score += c.calcScore();
            score += p.getPrivateObjectiveCard().calcScore();
            score += p.getFavorTokens();
            // TODO -1 VP for each open space in the window
            // pseudo:  windowPatter.grid.stream().filter(c -> c.die == null).count()
            scores.put(p, score);
        }
    }

    public void run() {
        this.setup();
        // TODO
        this.endGame();
    }
}


class DiceGenerator {   // TODO Remove mock up

}
