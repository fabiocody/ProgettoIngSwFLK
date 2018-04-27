package it.polimi.ingsw.dice;

import it.polimi.ingsw.util.Colors;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class DiceGenerator {

    private ThreadLocalRandom random;
    private List<Die> draftPool;
    private int numberOfPlayers; //serve classe Game
    private Map<Colors, Integer> remainingDice;

    public DiceGenerator() {
        random = ThreadLocalRandom.current();
        draftPool = new ArrayList<>();
        numberOfPlayers = Game.getNumberOfPlayers(); //serve implementazione classe Game per metodo getNumberOfPlayers()

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
        Die die = new Die();

        if (remainingDice.isEmpty()) {
            throw new NoMoreDiceException("there are no more dice");
        }

        Colors randomColor = Colors.values()[random.nextInt(0, Colors.values().length - 1)];
        Integer value = remainingDice.get(randomColor);

        die.color = randomColor;
        die.roll();
        remainingDice.replace(randomColor, value, value-1);

        if ( remainingDice.get(randomColor) == 0 )
            remainingDice.remove(randomColor);

        return die;
    }

    public void generateDraftPool() {
        for (int i = 0; i<=numberOfPlayers+1; i++) {
            Die die = generate();
            draftPool.add(die);
        }
    }

    public Die drawDieFromDraftPool(int index) {
        Die die = draftPool.remove(index);
        return die;
    }
}

