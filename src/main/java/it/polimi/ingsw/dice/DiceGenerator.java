package it.polimi.ingsw.dice;

import it.polimi.ingsw.util.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class DiceGenerator {

    private ThreadLocalRandom random;
    private List<Die> draftPool;
    private int numberOfPlayers; 
    private Map<Colors, Integer> remainingDice;

    public DiceGenerator(int numOfStartingPlayers) {
        random = ThreadLocalRandom.current();
        draftPool = new Vector<>();
        numberOfPlayers = numOfStartingPlayers;
        remainingDice = new EnumMap<>(Colors.class);
        remainingDice.put(Colors.RED, 18);
        remainingDice.put(Colors.GREEN, 18);
        remainingDice.put(Colors.YELLOW, 18);
        remainingDice.put(Colors.BLUE, 18);
        remainingDice.put(Colors.PURPLE, 18);

    }

    public synchronized List<Die> getDraftPool() {
        return draftPool;
    }

    public synchronized Die generate() {
        if (remainingDice.isEmpty()) {
            throw new NoMoreDiceException("there are no more dice");
        }
        List<Colors> remainingColors = new ArrayList<>(remainingDice.keySet());
        Colors randomColor = remainingColors.get(random.nextInt(0, remainingColors.size()));
        Integer value = remainingDice.get(randomColor);
        Die die = new Die(randomColor);
        die.roll();
        remainingDice.replace(randomColor, value, value-1);
        if ( remainingDice.get(randomColor) == 0 )
            remainingDice.remove(randomColor);
        return die;
    }

    public synchronized void generateDraftPool() {
        for (int i = 0; i < (2*numberOfPlayers)+1; i++) {
            Die die = generate();
            draftPool.add(die);
        }
    }

    public synchronized Die drawDieFromDraftPool(int index) {
        return draftPool.remove(index);
    }

    public synchronized String toString() {
        return "in the bag there are: " + "\n"
                + Colorify.colorify("[" + remainingDice.get(Colors.RED) + "]" , Colors.RED) + " red dice remaining" + "\n"
                + Colorify.colorify("[" + remainingDice.get(Colors.GREEN) + "]" , Colors.GREEN) + " green dice remaining" + "\n"
                + Colorify.colorify("[" + remainingDice.get(Colors.YELLOW) + "]" , Colors.YELLOW) + " yellow dice remaining" + "\n"
                + Colorify.colorify("[" + remainingDice.get(Colors.BLUE) + "]" , Colors.BLUE) + " blue dice remaining" + "\n"
                + Colorify.colorify("[" + remainingDice.get(Colors.PURPLE) + "]" , Colors.PURPLE) + " purple dice remaining";
    }
}

