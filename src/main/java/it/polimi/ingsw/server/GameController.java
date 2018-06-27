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


public class GameController extends BaseController implements Observer {

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

    List<String> getCurrentPlayers() {
        return this.game.getPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    Map<String, Integer> getFinalScores() {
        return this.game.getFinalScores().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getNickname(),
                        Map.Entry::getValue
                ));
    }

    List<ObjectiveCard> getPublicObjectiveCards() {
        return this.game.getPublicObjectiveCards();
    }

    List<ToolCard> getToolCards() {
        return this.game.getToolCards();
    }

    Player getPlayer(UUID id) {
        Optional<Player> result = this.game.getPlayers().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("No Player object for uuid " + id);
    }

    String getActivePlayer(){
        Optional<Player> result = this.game.getPlayers().stream()
                .filter(Player::isActive)
                .findFirst();
        if (result.isPresent()) return result.get().getNickname();
        else throw new NoSuchElementException("No Active Player found");
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

    int getFavorTokensOf(String nickname) {
        Optional<Integer> result = this.game.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .map(Player::getFavorTokens)
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("Cannot retrieve favor tokens because no Player object with specified nickname has been found");
    }

    WindowPattern getWindowPatternOf(String nickname) {
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

    List<Die> getRoundTrackDice() {
        return new Vector<>(this.game.getRoundTrack().getAllDice());
    }

    RoundTrack getRoundTrack(){ return this.game.getRoundTrack(); }

    List<Die> getDraftPool() {
        return this.game.getDiceGenerator().getDraftPool();
    }

    void choosePattern(UUID id, int patternIndex){
        getPlayer(id).chooseWindowPattern(patternIndex);
    }

    void placeDie(UUID id, int draftPoolIndex, int x, int y) throws DieAlreadyPlacedException, InvalidPlacementException {
        Die d = this.getDraftPool().get(draftPoolIndex);
        getPlayer(id).placeDie(d, x, y);
        this.game.removeDieFromDraftPool(draftPoolIndex);
        forEachServerNetwork(ServerNetwork::fullUpdate);
    }

    JsonObject requiredData(int toolCardIndex, UUID id){
        JsonObject data = this.game.getToolCards().get(toolCardIndex).requiredData();
        if(this.getPlayer(id).getFavorTokens() < 2 && this.getToolCards().get(toolCardIndex).isUsed() ||
                (this.getPlayer(id).getFavorTokens() < 1 && !this.getToolCards().get(toolCardIndex).isUsed())){
            data.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.NO_FAVOR_TOKENS,InterfaceMessages.NO_FAVOR_TOKENS_MESSAGE);
        }
        else{
            if(data.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.DRAFT_POOL_INDEX) && this.getDraftPool().isEmpty()){
                data.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD, InterfaceMessages.EMPTY_DRAFT_POOL_MESSAGE);
            }
        }
        return data;
    }

    void useToolCard(UUID uuid, int cardIndex, JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        ToolCard toolCard = getToolCards().get(cardIndex);
        data.addProperty(JsonFields.PLAYER, getPlayer(uuid).getNickname());
        toolCard.effect(data);
        if (!data.has(JsonFields.CONTINUE)) {
            if (!toolCard.isUsed()) {
                getPlayer(uuid).setFavorTokens(getPlayer(uuid).getFavorTokens() - 1);
                Logger.debug("Removed 1 favor token");
            } else {
                getPlayer(uuid).setFavorTokens(getPlayer(uuid).getFavorTokens() - 2);
                Logger.debug("Removed 2 favor tokens");
            }
            toolCard.setUsed();
        }
        forEachServerNetwork(ServerNetwork::fullUpdate);
    }

    void nextTurn() {
        this.game.nextTurn();
    }

    public void update(Observable o, Object arg) {
        String stringArg = String.valueOf(arg);
        if (o instanceof CountdownTimer) {
            if (stringArg.startsWith(NotificationMessages.TURN_MANAGER)) {
                String tick = stringArg.split(" ")[1];
                Logger.debug("Game Timer tick (from update): " + tick);
                forEachServerNetwork(network -> network.updateTimerTick(Methods.GAME_TIMER_TICK, tick));
            }
        } else if (o instanceof Game) {
            switch (stringArg) {
                case NotificationMessages.TURN_MANAGEMENT:
                case NotificationMessages.SUSPENDED:
                    if (!getRoundTrack().isGameOver())
                        this.game.getTurnManager().subscribeToTimer(this);
                    forEachServerNetwork(ServerNetwork::fullUpdate);
                    break;
                case NotificationMessages.GAME_OVER:
                    forEachServerNetwork(ServerNetwork::fullUpdate);
                    forEachServerNetwork(ServerNetwork::updateFinalScores);
                    //forEachServerNetwork(ServerNetwork::turnManagement);
                    SagradaServer.getInstance().getGameControllers().remove(this);

                    break;
            }
        } else if (o instanceof Player) {
            if (game.arePlayersReady()) {
                if (!getRoundTrack().isGameOver())
                    this.game.getTurnManager().subscribeToTimer(this);
                forEachServerNetwork(ServerNetwork::fullUpdate);
            }
        }
    }
}
