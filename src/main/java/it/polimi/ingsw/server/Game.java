package it.polimi.ingsw.server;

import it.polimi.ingsw.model.dice.DiceGenerator;
import it.polimi.ingsw.model.objectivecards.*;
import it.polimi.ingsw.model.patterncards.PatternCardsGenerator;
import it.polimi.ingsw.model.toolcards.*;
import java.util.*;
import java.util.concurrent.*;


// This class contains all the information about the current game
public class Game implements Observer {
    // Observes RoundTrack

    // Game
    private List<Player> players;
    private TurnManager turnManager;
    private RoundTrack roundTrack;
    private Map<Player, Integer> finalScores;

    // Generators
    private ObjectiveCardsGenerator objectiveCardsGenerator;
    private DiceGenerator diceGenerator;
    private PatternCardsGenerator patternCardsGenerator;

    // Cards
    private List<ObjectiveCard> publicObjectiveCards;
    private List<ToolCard> toolCards;

    // Locks
    private final Object playersLock = new Object();
    private final Object turnManagerLock = new Object();
    private final Object roundTrackLock = new Object();
    private final Object finalScoresLock = new Object();
    private final Object objectiveCardsGeneratorLock = new Object();
    private final Object diceGeneratorLock = new Object();
    private final Object patternCardsGeneratorLock = new Object();
    private final Object publicObjectiveCardsLock = new Object();
    private final Object toolCardsLock = new Object();

    public Game(List<Player> players) {
        this(players, true);
    }

    public Game(List<Player> players, boolean doSetup) {
        this.players = new Vector<>(players);
        Collections.shuffle(this.players);
        if (doSetup) this.setup();
        this.turnManager = new TurnManager(this.players);
        this.turnManager.addObserver(this.getRoundTrack());
    }

    public List<Player> getPlayers() {
        return new Vector<>(this.players);
    }

    public Player getPlayerForNickname(String nickname) {
        Optional<Player> result = this.players.stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException(nickname);
    }

    public int getNumberOfPlayers() {
        synchronized (playersLock) {
            return this.players.size();
        }
    }

    public TurnManager getTurnManager() {
        synchronized (turnManagerLock) {
            return this.turnManager;
        }
    }

    public RoundTrack getRoundTrack() {
        synchronized (roundTrackLock) {
            if (this.roundTrack == null) {
                this.roundTrack = new RoundTrack();
                this.roundTrack.addObserver(this);
            }
            return this.roundTrack;
        }
    }

    public Map<Player, Integer> getFinalScores() {
        synchronized (finalScoresLock) {
            if (!this.getRoundTrack().isGameOver())
                throw new IllegalStateException("Cannot get final scores before game over");
            else if (this.finalScores == null)
                this.finalScores = new ConcurrentHashMap<>();
            return this.finalScores;
        }
    }

    private ObjectiveCardsGenerator getObjectiveCardsGenerator() {
        synchronized (objectiveCardsGeneratorLock) {
            if (this.objectiveCardsGenerator == null)
                this.objectiveCardsGenerator = new ObjectiveCardsGenerator(this.getNumberOfPlayers());
            return this.objectiveCardsGenerator;
        }
    }

    private PatternCardsGenerator getPatternCardsGenerator() {
        synchronized (patternCardsGeneratorLock) {
            if (this.patternCardsGenerator == null)
                this.patternCardsGenerator = new PatternCardsGenerator(this.getNumberOfPlayers());
            return this.patternCardsGenerator;
        }
    }

    public DiceGenerator getDiceGenerator() {
        synchronized (diceGeneratorLock) {
            if (this.diceGenerator == null)
                this.diceGenerator = new DiceGenerator(this.getNumberOfPlayers());
            return this.diceGenerator;
        }
    }

    public List<ObjectiveCard> getPublicObjectiveCards() {
        synchronized (publicObjectiveCardsLock) {
            if (this.publicObjectiveCards == null)
                throw new IllegalStateException("Cannot get Public Objective Cards before they are generated");
            return new Vector<>(this.publicObjectiveCards);
        }
    }

    public synchronized List<ToolCard> getToolCards() {
        synchronized (toolCardsLock) {
            if (this.toolCards == null)
                throw new IllegalStateException("Cannot get Public Tool Cards before they are generated");
            return new Vector<>(this.toolCards);
        }
    }

    // Setup method to be called at game starting
    private void setup() {
        for (Player player : this.players) {
            player.setPrivateObjectiveCard(this.getObjectiveCardsGenerator().dealPrivate());
            player.setWindowPatternList(this.getPatternCardsGenerator().getCardsForPlayer());
            // TODO Choose card
        }
        this.toolCards = ToolCardsGenerator.generate(this);
        this.publicObjectiveCards = this.getObjectiveCardsGenerator().generatePublic();
        this.getDiceGenerator().generateDraftPool();
    }

    // This method is supposed to be counting VP for each player.
    // Scores are stored in a map.
    private void endGame() {
        ExecutorService pool = Executors.newFixedThreadPool(this.getNumberOfPlayers());
        for (Player p : this.players) {
            pool.execute(() ->
                this.calcScoreForPlayer(p)
            );
        }
        pool.shutdown();
        try {
            pool.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        assert this.getFinalScores().keySet().containsAll(this.players);
    }

    private void calcScoreForPlayer(Player player) {
        int score = 0;
        for (ObjectiveCard c : this.getPublicObjectiveCards()) score += c.calcScore(player.getWindowPattern().getGrid());
        score += player.getPrivateObjectiveCard().calcScore(player.getWindowPattern().getGrid());
        score += player.getFavorTokens();
        score -= Arrays.stream(player.getWindowPattern().getGrid()).filter(c -> c.getPlacedDie() == null).count();
        this.getFinalScores().put(player, score);
    }

    public void update(Observable o, Object arg) {
        if (o instanceof RoundTrack) {
            if (arg.equals("Round incremented")) {
                this.getRoundTrack().putDice(this.getDiceGenerator().getDraftPool());
                this.getDiceGenerator().generateDraftPool();
            } else if (arg.equals("Game over"))
                new Thread(this::endGame).start();
        }
    }

}
