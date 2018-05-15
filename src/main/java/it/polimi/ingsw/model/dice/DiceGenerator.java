package it.polimi.ingsw.model.dice;

import it.polimi.ingsw.util.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class DiceGenerator {

    private ThreadLocalRandom random;
    private List<Die> draftPool;
    private int numberOfPlayers;
    private List<Die> generatedDice;

    public DiceGenerator(int numOfStartingPlayers) {
        random = ThreadLocalRandom.current();
        draftPool = new Vector<>();
        numberOfPlayers = numOfStartingPlayers;
        generatedDice = new ArrayList<>();
        for (Colors c : Colors.values()) {
            if (c != Colors.RESET) {
                for (int i=0; i<18; i++) {
                    Die d = new Die(c);
                    d.roll();
                    generatedDice.add(d);
                }
            }
        }
        Collections.shuffle(generatedDice);
    }

    public synchronized List<Die> getDraftPool() {
        return draftPool;
    }

    public synchronized Die draw() {
        if (generatedDice.isEmpty()) {
            throw new NoMoreDiceException("there are no more dice");
        }
        return generatedDice.remove(0);
    }

    public synchronized void putAway (Die die) {
        int previousSize = generatedDice.size();
        generatedDice.add(die);
        Collections.shuffle(generatedDice);
        assert generatedDice.size() == previousSize + 1;
    }

    public synchronized void generateDraftPool() {
        for (int i = 0; i < (2*numberOfPlayers)+1; i++) {
            Die die = draw();
            draftPool.add(die);
        }
    }

    public synchronized Die drawDieFromDraftPool(int index) {
        return draftPool.remove(index);
    }

    public synchronized String toString() {
        return "Remaining dice in bag: " + generatedDice.size();
    }
}

