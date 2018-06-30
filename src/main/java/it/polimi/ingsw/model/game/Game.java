package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.dice.DiceGenerator;
import it.polimi.ingsw.model.objectivecards.*;
import it.polimi.ingsw.model.patterncards.PatternCardsGenerator;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * This class contains all the information about the current game
 *
 * @author Fabio Codiglioni
 */
public class Game extends Observable implements Observer {
    // Observes RoundTrack and Player(s)
    // Is observed by GameController

    // Game
    private List<Player> players;
    private TurnManager turnManager;
    private RoundTrack roundTrack;
    private Map<String, Scores> finalScores;

    // Generators
    private ObjectiveCardsGenerator objectiveCardsGenerator;
    private DiceGenerator diceGenerator;
    private PatternCardsGenerator patternCardsGenerator;

    // Cards
    private List<ObjectiveCard> publicObjectiveCards;
    private List<ToolCard> toolCards;

    /**
     * @author Fabio Codiglioni
     * @param players the list of players taking part in the game.
     * @param doSetup must be true if you want the game set up along its creation.
     */
    public Game(List<Player> players, boolean doSetup) {
        this.players = new Vector<>(players);
        for (Player p : players) {
            p.addObserver(this);
        }
        Collections.shuffle(this.players);
        if (doSetup) this.setup();
        this.turnManager = new TurnManager(this.players);
        this.getRoundTrack().getFlattenedDice();
        this.turnManager.addObserver(this.getRoundTrack());
    }

    /**
     * This is a shorthand to <code>Game(players, true)</code>.
     *
     * @author Fabio Codiglioni
     * @param players the list of players taking part in the Game.
     */
    public Game(List<Player> players) {
        this(players, true);
    }

    /**
     * @author Fabio Codiglioni
     * @return a thread-safe copy of the list of players.
     */
    public List<Player> getPlayers() {
        return new Vector<>(this.players);
    }

    public boolean isNicknameUsedInThisGame(String nickname) {
        return this.players.stream()
                .anyMatch(p -> p.getNickname().equals(nickname));
    }

    /**
     * @author Fabio Codiglioni
     * @param nickname the nickname to look for.
     * @return the Player object having the specified nickname.
     */
    public Player getPlayer(String nickname) {
        Optional<Player> result = this.players.stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException(nickname);
    }

    /**
     * @author Fabio Codiglioni
     * @return the number of players taking part in the Game.
     */
    private int getNumberOfPlayers() {
        return this.players.size();
    }

    public boolean arePlayersReady() {
        return this.players.stream()
                .allMatch(Player::isWindowPatternChosen);
    }

    public List<String> getSuspendedPlayers() {
        return this.players.stream()
                .filter(Player::isSuspended)
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    /**
     * @author Fabio Codiglioni
     * @return the Turn Manager instance associated with the Game.
     */
    public TurnManager getTurnManager() {
        if (this.turnManager == null) {
            this.turnManager = new TurnManager(this.players);
            this.turnManager.addObserver(this.getRoundTrack());
        }
        return this.turnManager;
    }

    /**
     * @author Fabio Codiglioni
     * @return the Round Track instance associated with the Game.
     */
    public RoundTrack getRoundTrack() {
        if (this.roundTrack == null) {
            this.roundTrack = new RoundTrack();
            this.roundTrack.addObserver(this);
        }
        return this.roundTrack;
    }

    /**
     * @author Fabio Codiglioni
     * @return the map if the final scores of the players.
     * @throws IllegalStateException thrown when this method is called before the Game is over.
     */
    public Map<String, Scores> getFinalScores() {
        if (!this.getRoundTrack().isGameOver())
            throw new IllegalStateException("Cannot get final scores before game over");
        else if (this.finalScores == null)
            this.finalScores = new ConcurrentHashMap<>();
        return this.finalScores;
    }

    /**
     * @author Fabio Codiglioni
     * @return the Objective Card Generator associated with the Game.
     */
    private ObjectiveCardsGenerator getObjectiveCardsGenerator() {
        if (this.objectiveCardsGenerator == null)
            this.objectiveCardsGenerator = new ObjectiveCardsGenerator(this.getNumberOfPlayers());
        return this.objectiveCardsGenerator;
    }

    /**
     * @author Fabio Codiglioni
     * @return the Pattern Cards Generator associated with the Game.
     */
    private PatternCardsGenerator getPatternCardsGenerator() {
        if (this.patternCardsGenerator == null)
            this.patternCardsGenerator = new PatternCardsGenerator(this.getNumberOfPlayers());
        return this.patternCardsGenerator;
    }

    /**
     * @author Fabio Codiglioni
     * @return the Dice Generator associated with the Game.
     */
    public DiceGenerator getDiceGenerator() {
        if (this.diceGenerator == null)
            this.diceGenerator = new DiceGenerator(this.getNumberOfPlayers());
        return this.diceGenerator;
    }

    /**
     * @author Fabio Codiglioni
     * @return the list of Public Objective Cards associated with the Game.
     * @throws IllegalStateException thrown when this method is called but the Public Objective Cards hasn't been generated yet.
     */
    public List<ObjectiveCard> getPublicObjectiveCards() {
        if (this.publicObjectiveCards == null)
            throw new IllegalStateException("Cannot get Public Objective Cards before they are generated");
        return new Vector<>(this.publicObjectiveCards);
    }

    /**
     * @author Fabio Codiglioni
     * @return the list of Tool Cards associated with the Game
     * @throws IllegalStateException thrown when this method is called but the Tool Cards hasn't been generated yet.
     */
    public List<ToolCard> getToolCards() {
        if (this.toolCards == null)
            throw new IllegalStateException("Cannot get Public Tool Cards before they are generated");
        return new Vector<>(this.toolCards);
    }

    /**
     * This method makes the game ready to be played.
     * It handles patterns and cards dealing.
     *
     * @author Fabio Codiglioni
     */
    private void setup() {
        for (Player player : this.players) {
            player.setPrivateObjectiveCard(this.getObjectiveCardsGenerator().dealPrivate());
            player.setWindowPatternList(this.getPatternCardsGenerator().getCardsForPlayer());
        }
        this.toolCards = new Vector<>(ToolCardsGenerator.generate(this));
        this.publicObjectiveCards = new Vector<>(getObjectiveCardsGenerator().generatePublicCards());
        this.getDiceGenerator().generateDraftPool();
    }

    /**
     * This method is used to compute Victory Points for each player.
     * <code>getFinalScores</code> must be called to retrieve the scores.
     *
     * @author Fabio Codiglioni
     */
    private void endGame() {
        this.turnManager.cancelTimer();
        List<Scores> scores = new ArrayList<>();
        List<String> nicknames = this.players.stream().map(Player::getNickname).sorted().collect(Collectors.toList());
        this.players.forEach(player -> scores.add(calcScores(player)));
        assert scores.stream()
                .map(Scores::getNickname)
                .collect(Collectors.toList())
                .equals(this.players.stream()
                        .map(Player::getNickname)
                        .collect(Collectors.toList()));
        breakTies(scores, nicknames);
        scores.forEach(score -> getFinalScores().put(score.getNickname(), score));
        Scores winner = scores.get(0);
        getFinalScores().put(JsonFields.WINNER, winner);
        nicknames.add(JsonFields.WINNER);
        assert getFinalScores().keySet().containsAll(nicknames);
    }

    /**
     * This method computes the VP for a given Player.
     *
     * @author Fabio Codiglioni
     * @param player the player to consider for the computing of VPs.
     */
    private Scores calcScores(Player player) {
        Scores scores = new Scores(player.getNickname());

        // Public Objective Cards
        int score = this.getPublicObjectiveCards().stream()
                .mapToInt(card -> card.calcScore(player.getWindowPattern().getGrid()))
                .sum();

        // Private Objective Card
        int privateObjectiveCardScore = player.getPrivateObjectiveCard().calcScore(player.getWindowPattern().getGrid());
        scores.setPrivateObjectiveCardScore(privateObjectiveCardScore);
        score += privateObjectiveCardScore;

        // Favor Tokens
        scores.setFavorTokensScore(player.getFavorTokens());
        score += player.getFavorTokens();

        // Empty cells
        score -= Arrays.stream(player.getWindowPattern().getGrid())
                .filter(c -> c.getPlacedDie() == null)
                .count();

        scores.setFinalScore(score);
        return scores;
    }

    static void breakTies(List<Scores> scores, List<String> nicknames) {
        scores.sort(Comparator.comparingInt(Scores::getFinalScore)
                .thenComparingInt(Scores::getPrivateObjectiveCardScore)
                .thenComparingInt(Scores::getFavorTokensScore));
        Collections.reverse(scores);
        Scores first = scores.get(0);
        Scores second = scores.get(1);
        if (first.getFinalScore() == second.getFinalScore() &&
                first.getPrivateObjectiveCardScore() == second.getPrivateObjectiveCardScore() &&
                first.getFavorTokensScore() == second.getFavorTokensScore()) {
            Collections.reverse(nicknames);
            scores.sort(Comparator.comparing(item -> nicknames.indexOf(item.getNickname())));
        }
    }

    /**
     * This method is called as a result of a change in an observed object.
     * Specifically, it handles round incrementing and game over.
     *
     * @author Fabio Codiglioni
     * @param o the object that has triggered the notification.
     * @param arg the (optional) argument of the notification.
     */
    public void update(Observable o, Object arg) {
        if (o instanceof RoundTrack) {
            if (arg.equals(NotificationMessages.ROUND_INCREMENTED)) {
                getRoundTrack().putDice(this.getDiceGenerator().getDraftPool());
                getDiceGenerator().generateDraftPool();
                setChanged();
                notifyObservers(NotificationMessages.ROUND_TRACK);
            } else if (arg.equals(NotificationMessages.GAME_OVER)) {
                getTurnManager().cancelTimer();
                getRoundTrack().putDice(this.getDiceGenerator().getDraftPool());
                endGame();
                setChanged();
                notifyObservers(arg);
            } else if (arg.equals(NotificationMessages.GAME_INTERRUPTED)) {
                getTurnManager().cancelTimer();
                Optional<String> winner = players.stream()
                        .filter(player -> !player.isSuspended())
                        .map(Player::getNickname)
                        .findFirst();
                Scores winnerScores = new Scores(winner.orElse(null));
                getFinalScores().put(JsonFields.WINNER, winnerScores);
                setChanged();
                notifyObservers(arg);
            }
        }
    }

}
