package it.polimi.ingsw.patterncards;

import java.util.*;


public class PatternCardsGenerator {

    private final List<WindowPattern> generatedCards = new ArrayList<>();

    public PatternCardsGenerator(int numberOfPlayers){

        if(numberOfPlayers < 2 || numberOfPlayers > 4)
            throw new InvalidNumberOfPlayersException();

        List <Integer> randomNumbers= new ArrayList<>();
        for(int i = 0; i < 12; i++){
            randomNumbers.add(i);
        }
        Collections.shuffle(randomNumbers);
        for(int i = 0; i < numberOfPlayers*2; i++){
            generatedCards.add(new WindowPattern(2*(randomNumbers.get(i))));
            generatedCards.add(new WindowPattern((2*randomNumbers.get(i))+1));
        }
    }

    public synchronized List<WindowPattern> getCards(){
        return this.generatedCards;
    }

    public synchronized List<WindowPattern> getCardsForPlayer(){
        if(this.generatedCards.isEmpty())
            throw new IllegalStateException("No more pattern cards");

        List<WindowPattern> card = new Vector<>();
        for (int i = 0; i < 4; i++) card.add(this.generatedCards.remove(0));
        return card;
    }
}
