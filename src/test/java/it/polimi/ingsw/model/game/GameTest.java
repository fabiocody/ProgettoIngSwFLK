package it.polimi.ingsw.model.game;

import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class GameTest {

    private List<String> nicknames;

    @BeforeEach
    void setup() {
        nicknames = new ArrayList<>(Arrays.asList("aaa", "bbb", "ccc"));
    }

    private void assertOrder(List<Scores> scores) {
        for (int i = 0; i < scores.size() - 1; i++) {
            assertTrue(scores.get(i).getFinalScore() >= scores.get(i+1).getFinalScore());
        }
    }

    @Test
    void breakTiesTest() {
        //System.out.println("breakTiesTest");
        List<Scores> scores = new ArrayList<>();
        Scores score = new Scores("aaa");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(3);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        score = new Scores("bbb");
        score.setPrivateObjectiveCardScore(7);
        score.setFavorTokensScore(2);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        score = new Scores("ccc");
        score.setPrivateObjectiveCardScore(5);
        score.setFavorTokensScore(3);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        Game.breakTies(scores, nicknames);
        assertOrder(scores);
        //System.out.println("\n\n");
    }

    @Test
    void breakTiesSameFinalScoreTest() {
        //System.out.println("breakTiesSameFinalScoreTest");
        List<Scores> scores = new ArrayList<>();
        Scores score = new Scores("aaa");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(3);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        score = new Scores("bbb");
        score.setPrivateObjectiveCardScore(7);
        score.setFavorTokensScore(0);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        score = new Scores("ccc");
        score.setPrivateObjectiveCardScore(5);
        score.setFavorTokensScore(0);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        Game.breakTies(scores, nicknames);
        assertOrder(scores);
        assertEquals(scores.get(0).getNickname(), "bbb");
        //System.out.println("\n\n");
    }

    @Test
    void breakTiesSamePOCSTest() {
        //System.out.println("breakTiesSamePOCSTest");
        List<Scores> scores = new ArrayList<>();
        Scores score = new Scores("aaa");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(3);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        score = new Scores("bbb");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(2);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore() + 1);
        scores.add(score);
        score = new Scores("ccc");
        score.setPrivateObjectiveCardScore(5);
        score.setFavorTokensScore(0);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        Game.breakTies(scores, nicknames);
        assertOrder(scores);
        assertEquals(scores.get(0).getNickname(), "aaa");
        //System.out.println("\n\n");
    }

    @Test
    void breakTwoTiesTest() {
        //System.out.println("breakTwoTiesTest");
        List<Scores> scores = new ArrayList<>();
        Scores score = new Scores("aaa");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(3);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        score = new Scores("bbb");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(2);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore() + 1);
        scores.add(score);
        score = new Scores("ccc");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(1);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore() + 2);
        scores.add(score);
        score = new Scores("ddd");
        score.setPrivateObjectiveCardScore(1);
        score.setFavorTokensScore(2);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        nicknames.add("ddd");
        Game.breakTies(scores, nicknames);
        assertOrder(scores);
        assertEquals(scores.get(0).getNickname(), "aaa");
        //System.out.println("\n\n");
    }

    @Test
    void breakTiesPlayersOrderTest() {
        //System.out.println("breakTiesPlayersOrderTest");
        List<Scores> scores = new ArrayList<>();
        Scores score = new Scores("aaa");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(3);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        score = new Scores("bbb");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(3);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        score = new Scores("ccc");
        score.setPrivateObjectiveCardScore(4);
        score.setFavorTokensScore(3);
        score.setFinalScore(score.getPrivateObjectiveCardScore() + score.getFavorTokensScore());
        scores.add(score);
        Game.breakTies(scores, nicknames);
        //scores.forEach(System.out::println);
        assertOrder(scores);
        assertEquals(scores.get(0).getNickname(), "ccc");
        //System.out.println("\n\n");
    }

}
