package it.polimi.ingsw.dice;

import it.polimi.ingsw.util.Colors;
import it.polimi.ingsw.util.Colorify;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class DiceGenerator {

    private ThreadLocalRandom random;
    private List<Die> draftPool;
    private int numberOfPlayers; 
    private Map<Colors, Integer> remainingDice;

    public DiceGenerator(int numOfStartingPlayers) {
        random = ThreadLocalRandom.current();
        draftPool = new ArrayList<>();
        numberOfPlayers = numOfStartingPlayers;
        remainingDice = new HashMap<>();
        remainingDice.put(Colors.RED, 18);
        remainingDice.put(Colors.GREEN, 18);
        remainingDice.put(Colors.YELLOW, 18);
        remainingDice.put(Colors.BLUE, 18);
        remainingDice.put(Colors.PURPLE, 18);

    }

    public List<Die> getDraftPool() {
        return draftPool;
    }

    public Die generate() throws NoMoreDiceException {
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

    public void generateDraftPool() {
        for (int i = 0; i < (2*numberOfPlayers)+1; i++) {
            Die die = generate();
            draftPool.add(die);
        }
    }

    public Die drawDieFromDraftPool(int index) {
        return draftPool.remove(index);
    }

    public String toString() {
        return "in the bag there are: " + "\n"+ Colors.RED.escape() + "[" + remainingDice.get(Colors.RED)+ "]" + Colors.RESET.escape() + " red dice remaining" + "\n"
                + Colors.GREEN.escape() + "[" + remainingDice.get(Colors.GREEN)+ "]" + Colors.RESET.escape() + " green dice remaining" + "\n"
                + Colors.YELLOW.escape() + "[" + remainingDice.get(Colors.YELLOW)+ "]" + Colors.RESET.escape() + " yellow dice remaining" + "\n"
                + Colors.BLUE.escape() + "[" + remainingDice.get(Colors.BLUE)+ "]" + Colors.RESET.escape() + " blue dice remaining" + "\n"
                + Colors.PURPLE.escape() + "[" + remainingDice.get(Colors.PURPLE)+ "]" + Colors.RESET.escape() + " purple dice remaining";
    }
}

