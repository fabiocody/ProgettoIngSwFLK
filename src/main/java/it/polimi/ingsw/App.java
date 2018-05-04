package it.polimi.ingsw;

import  it.polimi.ingsw.patterncards.*;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        PatternCardsGenerator a = new PatternCardsGenerator();
        List <WindowPattern> b = a.getCards();

        for(WindowPattern c: b)
            c.dump();
    }
}
