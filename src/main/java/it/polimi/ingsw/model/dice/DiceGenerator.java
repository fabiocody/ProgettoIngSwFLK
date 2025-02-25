package it.polimi.ingsw.model.dice;

import it.polimi.ingsw.shared.util.Colors;
import it.polimi.ingsw.shared.util.*;
import java.util.*;


/**
 * This class is responsible of generating the dice of the dice bag and the Draft Pool
 */
public class DiceGenerator {

    private List<Die> draftPool;
    private int numberOfPlayers;
    private List<Die> generatedDice;

    /**
     * The constructor initiates the attributes of the class, getting as a parameter the number of players
     * that will be used to generate the correct amount of dice for the Draft Pool, and then fills the
     * ArrayList generatedDice (dice bag) with 18 dice of each color, shuffling it.
     *
     * @author Kai de Gast
     * @param numOfStartingPlayers number of the players at the start of the game
     */
    public DiceGenerator(int numOfStartingPlayers) {
        draftPool = new Vector<>();
        numberOfPlayers = numOfStartingPlayers;
        generatedDice = new ArrayList<>();
        for (Colors c : Colors.values()) {
            for (int i = 0; i<Constants.MAX_NUMBER_OF_SAME_COLOR_DICE; i++) {
                Die d = new Die(c);
                d.roll();
                generatedDice.add(d);
            }
        }
        Collections.shuffle(generatedDice);
    }

    /**
     * This method returns the remaining dice in the Draft Pool
     *
     * @author Kai de Gast
     * @return a list of the remaining dice in the Draft Pool
     */
    public List<Die> getDraftPool() {
        return draftPool;
    }

    /**
     * This method returns a random die from the dice bag
     *
     * @author Kai de Gast
     * @return a random die from the dice bag
     * @throws NoMoreDiceException thrown when there are no more
     * dice left in the dice bag
     */
    public Die draw() {
        if (generatedDice.isEmpty()) {
            throw new NoMoreDiceException("there are no more dice");
        }
        return generatedDice.remove(0);
    }

    /**
     * This method places a die in the dice bag from the Draft Pool
     *
     * @author Kai de Gast
     * @param die is the die that will be put in the dice bag
     */
    public void putAway(Die die) {
        int previousSize = generatedDice.size();
        generatedDice.add(die);
        Collections.shuffle(generatedDice);
        assert generatedDice.size() == previousSize + 1;
    }

    /**
     * This method generates the Draft Pool, drawing the correct amount of dice from the bag based on the
     * number of players
     *
     * @author Kai de Gast
     */
    public void generateDraftPool() {
        for (int i = 0; i < (2*numberOfPlayers)+1; i++) {
            Die die = draw();
            draftPool.add(die);
        }
    }

    /**
     * This method is used to pick a die from the Draft Pool
     *
     * @author Kai de Gast
     * @param index is the index of the selected die we want to extract from the Draft Pool
     * @return the selected die
     */
    public Die drawDieFromDraftPool(int index) {
        return draftPool.remove(index);
    }

    /**
     * This method returns the number of remaining dice in the dice bag
     *
     * @author Kai de Gast
     * @return a String with the number of remaining dice in the dice bag
     */
    public String toString() {
        return "Remaining dice in bag: " + generatedDice.size();
    }
}

