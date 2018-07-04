package it.polimi.ingsw.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.shared.util.*;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.shared.util.Constants.*;


public class GameController extends BaseController {

    private Game game;

    GameController(Game game) {
        super();
        this.game = game;
        this.game.addObserver(this);
        this.game.getPlayers().forEach(p -> p.addObserver(this));
    }

    Game getGame() {
        return game;
    }

    Map<String, Scores> getFinalScores() {
        return this.game.getFinalScores();
    }

    List<ObjectiveCard> getPublicObjectiveCards() {
        return this.game.getPublicObjectiveCards();
    }

    List<ToolCard> getToolCards() {
        return this.game.getToolCards();
    }

    List<String> getPlayers() {
        return this.game.getPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    Player getPlayer(UUID id) {
        Optional<Player> result = this.game.getPlayers().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("No Player object for uuid " + id);
    }

    String getActivePlayer() {
        return game.getTurnManager().getActivePlayer();
    }

    void suspendPlayer(UUID id) {
        this.getPlayer(id).setSuspended(true);
    }

    void unsuspendPlayer(UUID id) {
        this.getPlayer(id).setSuspended(false);
    }

    List<String> getSuspendedPlayers() {
        return this.game.getSuspendedPlayers();
    }

    int getFavorTokens(String nickname) {
        Optional<Integer> result = this.game.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .map(Player::getFavorTokens)
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("Cannot retrieve favor tokens because no Player object with specified nickname has been found");
    }

    WindowPattern getWindowPattern(String nickname) {
        Optional<WindowPattern> result = this.game.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .map(Player::getWindowPattern)
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("Cannot retrieve WindowPattern because no Player object with specified nickname has been found");
    }

    int getCurrentRound() {
        return this.game.getRoundTrack().getCurrentRound();
    }

    List<List<Die>> getRoundTrackDice() {
        return new Vector<>(this.game.getRoundTrack().getDice());
    }

    RoundTrack getRoundTrack() { return this.game.getRoundTrack(); }

    List<Die> getDraftPool() {
        return this.game.getDiceGenerator().getDraftPool();
    }

    void choosePattern(UUID id, int patternIndex){
        getPlayer(id).chooseWindowPattern(patternIndex);
    }

    // throws DieAlreadyPlacedException, InvalidPlacementException
    void placeDie(UUID id, int draftPoolIndex, int x, int y) {
        Die d = this.getDraftPool().get(draftPoolIndex);
        getPlayer(id).placeDie(d, x, y);
        this.game.getDiceGenerator().drawDieFromDraftPool(draftPoolIndex);
        forEachNetwork(ServerNetwork::fullUpdate);
    }

    private String emptyDraftPoolMessage() {
        return this.getDraftPool().isEmpty() ? InterfaceMessages.EMPTY_DRAFT_POOL : "";
    }

    private String emptyGridMessage(UUID id) {
        return this.getWindowPattern(this.getPlayer(id).getNickname()).isGridEmpty() ? InterfaceMessages.EMPTY_GRID : "";
    }

    private String oneOrLessDieMessage(UUID id) {
        return (this.getWindowPattern(this.getPlayer(id).getNickname()).isGridEmpty() || this.getWindowPattern(this.getPlayer(id).getNickname()).checkIfOnlyOneDie()) ? InterfaceMessages.ONE_OR_LESS_DIE_MESSAGE : "";
    }

    private String emptyRoundTrackMessage() {
        return this.getRoundTrack().isRoundTrackEmpty() ? InterfaceMessages.EMPTY_ROUND_TRACK : "";
    }

    private String firstHalfOfRoundMessage() {
        return !this.getGame().getTurnManager().isSecondHalfOfRound() ? InterfaceMessages.FIRST_HALF_OF_ROUND : "";
    }

    private String dieAlreadyPlacedMessage(UUID id) {
        return  this.getPlayer(id).isDiePlacedInThisTurn() ? InterfaceMessages.DIE_ALREADY_PLACED_IN_THIS_TURN : "";
    }

    private String dieNotPlacedYetMessage(UUID id) {
        return !this.getPlayer(id).isDiePlacedInThisTurn() ? InterfaceMessages.DIE_NOT_YET_PLACED_IN_THIS_TURN : "";
    }

    private void unusabilityMessages(int toolCardIndex, UUID id, JsonObject data) {
        String unusabilityMessage = "";
        switch (game.getToolCards().get(toolCardIndex).getName()){
            case TOOL_CARD_1_NAME:
            case TOOL_CARD_6_NAME:
            case TOOL_CARD_9_NAME:
            case TOOL_CARD_10_NAME:
            case TOOL_CARD_11_NAME:
                unusabilityMessage += emptyDraftPoolMessage();
                break;
            case TOOL_CARD_2_NAME:
            case TOOL_CARD_3_NAME:
                unusabilityMessage += emptyGridMessage(id);
                break;
            case TOOL_CARD_4_NAME:
                unusabilityMessage += oneOrLessDieMessage(id);
                break;
            case TOOL_CARD_5_NAME:
                unusabilityMessage += emptyDraftPoolMessage();
                unusabilityMessage += STRING_SEPARATOR + emptyRoundTrackMessage();
                break;
            case TOOL_CARD_7_NAME:
                unusabilityMessage += emptyDraftPoolMessage();
                unusabilityMessage += STRING_SEPARATOR + firstHalfOfRoundMessage();
                unusabilityMessage += STRING_SEPARATOR + dieAlreadyPlacedMessage(id);
                break;
            case TOOL_CARD_8_NAME:
                unusabilityMessage += emptyDraftPoolMessage();
                unusabilityMessage += STRING_SEPARATOR + dieNotPlacedYetMessage(id);
                break;
            case TOOL_CARD_12_NAME:
                unusabilityMessage += emptyDraftPoolMessage();
                unusabilityMessage += STRING_SEPARATOR + oneOrLessDieMessage(id);
                break;
            default:
                break;
        }
        if(unusabilityMessage.endsWith(STRING_SEPARATOR)) unusabilityMessage = unusabilityMessage.substring(0,unusabilityMessage.length()- STRING_SEPARATOR.length());
        if(unusabilityMessage.startsWith(STRING_SEPARATOR)) unusabilityMessage = unusabilityMessage.replaceFirst(STRING_SEPARATOR ,"");
        if(!unusabilityMessage.equals("")) data.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, unusabilityMessage + "!");
    }

    JsonObject requiredData(int toolCardIndex, UUID id){
        JsonObject data = this.game.getToolCards().get(toolCardIndex).requiredData();
        if(this.getPlayer(id).getFavorTokens() < 2 && this.getToolCards().get(toolCardIndex).isUsed() ||
                (this.getPlayer(id).getFavorTokens() < 1 && !this.getToolCards().get(toolCardIndex).isUsed())){
            data.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.NO_FAVOR_TOKENS,InterfaceMessages.NO_FAVOR_TOKENS);
        }
        else unusabilityMessages(toolCardIndex, id, data);
        return data;
    }

    void useToolCard(UUID id, int cardIndex, JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        ToolCard toolCard = getToolCards().get(cardIndex);
        data.addProperty(JsonFields.PLAYER, getPlayer(id).getNickname());
        toolCard.effect(data);
        if (!data.has(JsonFields.CONTINUE)) {
            if (!toolCard.isUsed()) {
                getPlayer(id).setFavorTokens(getPlayer(id).getFavorTokens() - 1);
                Logger.debug("Removed 1 favor token");
            } else {
                getPlayer(id).setFavorTokens(getPlayer(id).getFavorTokens() - 2);
                Logger.debug("Removed 2 favor tokens");
            }
            toolCard.setUsed();
        }
        forEachNetwork(ServerNetwork::fullUpdate);
    }

    void nextTurn() {
        game.getTurnManager().nextTurn();
        if (!getRoundTrack().isGameOver()) {
            game.getTurnManager().subscribeToTimer(this);
            forEachNetwork(ServerNetwork::fullUpdate);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        String stringArg = String.valueOf(arg);
        if (o instanceof CountdownTimer) {
            if (stringArg.startsWith(NotificationMessages.TURN_MANAGER)) {
                String tick = stringArg.split(" ")[1];
                Logger.debug("Game Timer tick (from update): " + tick);
                forEachNetwork(network -> network.updateTimerTick(Methods.GAME_TIMER_TICK, tick));
            }
        } else if (o instanceof Game) {
            switch (stringArg) {
                case NotificationMessages.TURN_MANAGEMENT:
                    if (!getRoundTrack().isGameOver())
                        this.game.getTurnManager().subscribeToTimer(this);
                    forEachNetwork(ServerNetwork::fullUpdate);
                    break;
                case NotificationMessages.GAME_INTERRUPTED:
                case NotificationMessages.GAME_OVER:
                    forEachNetwork(ServerNetwork::fullUpdate);
                    forEachNetwork(ServerNetwork::updateFinalScores);
                    closeNetworks();
                    SagradaServer.getInstance().getGameControllers().remove(this);
                    break;
                default:
                    break;
            }
        } else if (o instanceof Player && game.arePlayersReady()) {
            if (!getRoundTrack().isGameOver()) {
                this.game.getTurnManager().subscribeToTimer(this);
                forEachNetwork(ServerNetwork::fullUpdate);
            }
        } else if (o instanceof ServerNetwork) {
            String nickname = String.valueOf(arg);
            if (nickname.equals(getActivePlayer()) || game.getTurnManager().countNotSuspendedPlayers() <= 1)
                new Thread(this::nextTurn).start();
        }
    }
}
