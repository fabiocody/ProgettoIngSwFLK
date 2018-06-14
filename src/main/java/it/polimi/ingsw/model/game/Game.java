package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.dice.DiceGenerator;
import it.polimi.ingsw.model.objectivecards.*;
import it.polimi.ingsw.model.patterncards.PatternCardsGenerator;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.util.NotificationsMessages;

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
    // Is observed by ServerSocketHandler

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
        this.getRoundTrack().getAllDice();
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
    public Player getPlayerForNickname(String nickname) {
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
    public int getNumberOfPlayers() {
        synchronized (playersLock) {
            return this.players.size();
        }
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
        synchronized (turnManagerLock) {
            if (this.turnManager == null) {
                this.turnManager = new TurnManager(this.players);
                this.turnManager.addObserver(this.getRoundTrack());
            }
            return this.turnManager;
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the Round Track instance associated with the Game.
     */
    public RoundTrack getRoundTrack() {
        synchronized (roundTrackLock) {
            if (this.roundTrack == null) {
                this.roundTrack = new RoundTrack();
                this.roundTrack.addObserver(this);
            }
            return this.roundTrack;
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the map if the final scores of the players.
     * @throws IllegalStateException thrown when this method is called before the Game is over.
     */
    public Map<Player, Integer> getFinalScores() {
        synchronized (finalScoresLock) {
            if (!this.getRoundTrack().isGameOver())
                throw new IllegalStateException("Cannot get final scores before game over");
            else if (this.finalScores == null)
                this.finalScores = new ConcurrentHashMap<>();
            return this.finalScores;
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the Objective Card Generator associated with the Game.
     */
    private ObjectiveCardsGenerator getObjectiveCardsGenerator() {
        synchronized (objectiveCardsGeneratorLock) {
            if (this.objectiveCardsGenerator == null)
                this.objectiveCardsGenerator = new ObjectiveCardsGenerator(this.getNumberOfPlayers());
            return this.objectiveCardsGenerator;
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the Pattern Cards Generator associated with the Game.
     */
    private PatternCardsGenerator getPatternCardsGenerator() {
        synchronized (patternCardsGeneratorLock) {
            if (this.patternCardsGenerator == null)
                this.patternCardsGenerator = new PatternCardsGenerator(this.getNumberOfPlayers());
            return this.patternCardsGenerator;
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the Dice Generator associated with the Game.
     */
    public DiceGenerator getDiceGenerator() {
        synchronized (diceGeneratorLock) {
            if (this.diceGenerator == null)
                this.diceGenerator = new DiceGenerator(this.getNumberOfPlayers());
            return this.diceGenerator;
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the list of Public Objective Cards associated with the Game.
     * @throws IllegalStateException thrown when this method is called but the Public Objective Cards hasn't been generated yet.
     */
    public List<ObjectiveCard> getPublicObjectiveCards() {
        synchronized (publicObjectiveCardsLock) {
            if (this.publicObjectiveCards == null)
                throw new IllegalStateException("Cannot get Public Objective Cards before they are generated");
            return new Vector<>(this.publicObjectiveCards);
        }
    }

    /**
     * @author Fabio Codiglioni
     * @return the list of Tool Cards associated with the Game
     * @throws IllegalStateException thrown when this method is called but the Tool Cards hasn't been generated yet.
     */
    public synchronized List<ToolCard> getToolCards() {
        synchronized (toolCardsLock) {
            if (this.toolCards == null)
                throw new IllegalStateException("Cannot get Public Tool Cards before they are generated");
            return new Vector<>(this.toolCards);
        }
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
            // TODO Choose card
        }
        this.toolCards = ToolCardsGenerator.generate(this);
        this.toolCards.forEach(card -> card.addObserver(this));
        this.publicObjectiveCards = this.getObjectiveCardsGenerator().generatePublic();
        this.getDiceGenerator().generateDraftPool();
    }

    public void removeDieFromDraftPool(int draftPoolIndex){
        this.diceGenerator.drawDieFromDraftPool(draftPoolIndex);
        setChanged();
        notifyObservers(NotificationsMessages.DRAFT_POOL);
    }

    public void nextTurn() {
        this.turnManager.nextTurn();
        if(!this.roundTrack.isGameOver()) {
            setChanged();
            notifyObservers(NotificationsMessages.TURN_MANAGEMENT);
        }
    }

    /**
     * This method is used to compute Victory Points for each player.
     * <code>getFinalScores</code> must be called to retrieve the scores.
     *
     * @author Fabio Codiglioni
     */
    private void endGame() {
        this.players.forEach(this::calcScoreForPlayer);
        assert this.getFinalScores().keySet().containsAll(this.players);
    }

    /**
     * This method computes the VP for a given Player.
     *
     * @author Fabio Codiglioni
     * @param player the player to consider for the computing of VPs.
     */
    private void calcScoreForPlayer(Player player) {
        int score = 0;
        for (ObjectiveCard c : this.getPublicObjectiveCards()) score += c.calcScore(player.getWindowPattern().getGrid());
        score += player.getPrivateObjectiveCard().calcScore(player.getWindowPattern().getGrid());
        score += player.getFavorTokens();
        score -= Arrays.stream(player.getWindowPattern().getGrid()).filter(c -> c.getPlacedDie() == null).count();
        this.getFinalScores().put(player, score);
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
            if (arg.equals(NotificationsMessages.ROUND_INCREMENTED)) {
                this.getRoundTrack().putDice(this.getDiceGenerator().getDraftPool());
                this.getDiceGenerator().generateDraftPool();
                setChanged();
                notifyObservers(NotificationsMessages.ROUND_TRACK);
            } else if (arg.equals(NotificationsMessages.GAME_OVER)) {
                this.endGame();
                setChanged();
                notifyObservers(NotificationsMessages.GAME_OVER);
            }
        } else if (o instanceof Player) {
            if (arg != null && (arg.equals(NotificationsMessages.PLACE_DIE) || arg.equals(NotificationsMessages.SUSPENDED))) {
                setChanged();
                notifyObservers(arg);
            } else if (this.arePlayersReady()) {
                setChanged();
                notifyObservers(NotificationsMessages.TURN_MANAGEMENT);
            }
        } else if (o instanceof ToolCard) {
            if (arg.equals(NotificationsMessages.USE_TOOL_CARD)) {
                setChanged();
                notifyObservers(arg);
            }
        }
    }

}
