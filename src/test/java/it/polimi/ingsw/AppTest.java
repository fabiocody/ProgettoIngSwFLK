package it.polimi.ingsw;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AppTest {

    private int sum(int a, int b) {
        return a + b;
    }

    @Test
    public void test() {
        assertEquals(4, sum(2, 2));
        assertFalse(5 == sum(2, 2));
    }

}