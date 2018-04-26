package it.polimi.ingsw.server;

import it.polimi.ingsw.objectivecards.*;
import it.polimi.ingsw.toolcards.*;
import java.util.*;
import java.util.stream.Collectors;


public class Game implements Runnable {

    private List<Player> players;
    private ObjectiveCardsGenerator objectiveCardsGenerator;
    private DiceGenerator diceGenerator;
    private List<ToolCard> toolCards;
    private List<ObjectiveCard> objectiveCards;

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

    private synchronized DiceGenerator getDiceGenerator() {
        if (this.diceGenerator == null)
            this.diceGenerator = new DiceGenerator();
        return this.diceGenerator;
    }

    public synchronized List<ToolCard> getToolCards() {
        if (this.toolCards == null)
            return new Vector<>();
        return new Vector<>(this.toolCards);
    }

    public synchronized List<ObjectiveCard> getObjectiveCards() {
        if (this.objectiveCards == null)
            return new Vector<>();
        return new Vector<>(this.objectiveCards);
    }

    private synchronized void setup() {
        this.players.forEach(p -> p.setPrivateObjectiveCard(this.getObjectiveCardsGenerator().dealPrivate()));
        // TODO Window patterns
        this.players.forEach(p -> p.setFavorTokens(p.getWindowPattern().getFavorTokens()));
        this.toolCards = ToolCardsGenerator.generate();
        this.objectiveCards = this.getObjectiveCardsGenerator().generatePublic();
        Collections.shuffle(this.players);
    }

    public void run() {
        this.setup();
        // TODO
    }
}


class DiceGenerator {   // TODO Remove mock up

}
