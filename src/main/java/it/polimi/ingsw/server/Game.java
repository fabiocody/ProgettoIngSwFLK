package it.polimi.ingsw.server;

import it.polimi.ingsw.objectivecards.*;
import it.polimi.ingsw.toolcards.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Game {

    private List<Player> players;
    private ObjectiveCardsGenerator objectiveCardsGenerator;
    private DiceGenerator diceGenerator;
    private List<ToolCard> toolCards;
    private List<ObjectiveCard> objectiveCards;

    public Game(List<String> players) {
        this.players = players.stream().map(Player::new).collect(Collectors.toList());
    }

    public int getNumberOfPlayers() {
        return this.players.size();
    }

    private ObjectiveCardsGenerator getObjectiveCardsGenerator() {
        if (this.objectiveCardsGenerator == null)
            this.objectiveCardsGenerator = new ObjectiveCardsGenerator(this.getNumberOfPlayers());
        return objectiveCardsGenerator;
    }

    private DiceGenerator getDiceGenerator() {
        if (this.diceGenerator == null)
            this.diceGenerator = new DiceGenerator();
        return this.diceGenerator;
    }

    public List<ToolCard> getToolCards() {
        if (this.toolCards == null)
            return new ArrayList<>();
        return new ArrayList<>(this.toolCards);
    }

    public List<ObjectiveCard> getObjectiveCards() {
        if (this.objectiveCards == null)
            return new ArrayList<>();
        return new ArrayList<>(this.objectiveCards);
    }

    public void setup() {
        for (Player p : this.players)
            p.setPrivateObjectiveCard(this.getObjectiveCardsGenerator().dealPrivate());
        // TODO Window patterns
        // TODO Favor tokens
        this.toolCards = ToolCardsGenerator.generate();
        this.objectiveCards = this.getObjectiveCardsGenerator().generatePublic();
        Collections.shuffle(this.players);
    }

}


class DiceGenerator {   // TODO Remove mock up

}
