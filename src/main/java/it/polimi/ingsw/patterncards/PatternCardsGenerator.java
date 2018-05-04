package it.polimi.ingsw.patterncards;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class PatternCardsGenerator {

    private List<WindowPattern> generatedCards = new ArrayList<>();

    public PatternCardsGenerator(){
        List <Integer> randomNumbers= new ArrayList<>();
        for(int i = 0; i < 12; i++){
            randomNumbers.add(i);
        }
        Collections.shuffle(randomNumbers);
        for(int i = 0; i < 8; i++){
            generatedCards.add(new WindowPattern(2*(randomNumbers.get(i))));
            generatedCards.add(new WindowPattern((2*randomNumbers.get(i))+1));
        }
    }

    public List <WindowPattern> getCards(){
        return this.generatedCards;
    }
}
