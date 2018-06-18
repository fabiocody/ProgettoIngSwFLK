package it.polimi.ingsw.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.dice.Die;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.objectivecards.ObjectiveCard;
import it.polimi.ingsw.model.patterncards.*;
import it.polimi.ingsw.shared.rmi.GameAPI;
import it.polimi.ingsw.model.toolcards.*;
import it.polimi.ingsw.shared.util.*;

import java.util.*;
import java.util.stream.Collectors;


public class GameController implements GameAPI, Observer {

    private Game game;
    private List<ServerNetwork> serverNetworks = new Vector<>();

    GameController(Game game) {
        this.game = game;
        this.game.addObserver(this);
        this.game.getPlayers().forEach(p -> p.addObserver(this));
    }

    Game getGame() {
        return game;
    }

    void addServerNetwork(ServerNetwork network) {
        serverNetworks.add(network);
    }

    void removeServerNetwork(ServerNetwork network) {
        serverNetworks.remove(network);
    }

    @Override
    public List<String> getCurrentPlayers() {
        return this.game.getPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getFinalScores() {
        return this.game.getFinalScores().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getNickname(),
                        Map.Entry::getValue
                ));
    }

    @Override
    public List<ObjectiveCard> getPublicObjectiveCards() {
        return this.game.getPublicObjectiveCards();
    }

    @Override
    public List<ToolCard> getToolCards() {
        return this.game.getToolCards();
    }

    @Override
    public Player getPlayer(UUID id) {
        Optional<Player> result = this.game.getPlayers().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("No Player object for uuid " + id);
    }

    @Override
    public String getActivePlayer(){
        Optional<Player> result = this.game.getPlayers().stream()
                .filter(Player::isActive)
                .findFirst();
        if (result.isPresent()) return result.get().getNickname();
        else throw new NoSuchElementException("No Active Player found");
    }

    @Override
    public void suspendPlayer(UUID id) {
        this.getPlayer(id).setSuspended(true);
    }

    @Override
    public void unsuspendPlayer(UUID id) {
        this.getPlayer(id).setSuspended(false);
    }

    @Override
    public List<String> getSuspendedPlayers() {
        return this.game.getSuspendedPlayers();
    }

    @Override
    public int getFavorTokensOf(String nickname) {
        Optional<Integer> result = this.game.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .map(Player::getFavorTokens)
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("Cannot retrieve favor tokens because no Player object with specified nickname has been found");
    }

    @Override
    public WindowPattern getWindowPatternOf(String nickname) {
        Optional<WindowPattern> result = this.game.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .map(Player::getWindowPattern)
                .findFirst();
        if (result.isPresent()) return result.get();
        else throw new NoSuchElementException("Cannot retrieve WindowPattern because no Player object with specified nickname has been found");
    }

    @Override
    public void choosePattern(UUID id, int patternIndex){
        getPlayer(id).chooseWindowPattern(patternIndex);
    }

    @Override
    public int getCurrentRound() {
        return this.game.getRoundTrack().getCurrentRound();
    }

    @Override
    public List<Die> getRoundTrackDice() {
        return new Vector<>(this.game.getRoundTrack().getAllDice());
    }

    public RoundTrack getRoundTrack(){ return this.game.getRoundTrack(); }

    @Override
    public List<Die> getDraftPool() {
        return this.game.getDiceGenerator().getDraftPool();
    }

    @Override
    public void placeDie(UUID id, int draftPoolIndex, int x, int y) {
        Die d = this.getDraftPool().get(draftPoolIndex);
        getPlayer(id).placeDie(d,x,y);
        this.game.removeDieFromDraftPool(draftPoolIndex);
    }

    @Override
    public void useToolCard(UUID uuid, int cardIndex, JsonObject data) throws InvalidEffectResultException, InvalidEffectArgumentException {
        ToolCard toolCard = getToolCards().get(cardIndex);
        data.addProperty(JsonFields.PLAYER, getPlayer(uuid).getNickname());
        toolCard.effect(data);
        if (!data.has(JsonFields.CONTINUE)) {
            if (!toolCard.isUsed()) {
                getPlayer(uuid).setFavorTokens(getPlayer(uuid).getFavorTokens() - 1);
                Logger.debug("removed 1 favor token");
            } else {
                getPlayer(uuid).setFavorTokens(getPlayer(uuid).getFavorTokens() - 2);
                Logger.debug("removed 2 favor tokens");
            }
            toolCard.setUsed();
        }
    }

    @Override
    public JsonObject requiredData(int toolCardsIndex){
        return this.game.getToolCards().get(toolCardsIndex).requiredData();
    }

    @Override
    public void nextTurn() {
        this.game.nextTurn();
    }

    @Override
    public void update(Observable o, Object arg) {
        String stringArg = String.valueOf(arg);
        if (o instanceof CountdownTimer) {
            if (stringArg.startsWith(NotificationsMessages.TURN_MANAGER)) {
                String tick = stringArg.split(" ")[1];
                Logger.debug("Game Timer tick (from update): " + tick);
                serverNetworks.forEach(network -> network.updateTimerTick(Methods.GAME_TIMER_TICK, tick));
            }
        } else if (o instanceof Game) {
            switch (stringArg) {
                case NotificationsMessages.TURN_MANAGEMENT:
                case NotificationsMessages.SUSPENDED:
                    if (!getRoundTrack().isGameOver())
                        this.game.getTurnManager().subscribeToTimer(this);
                    serverNetworks.forEach(ServerNetwork::fullUpdate);
                    break;
                case NotificationsMessages.PLACE_DIE:
                case NotificationsMessages.USE_TOOL_CARD:
                    serverNetworks.forEach(ServerNetwork::fullUpdate);
                    break;
                case NotificationsMessages.GAME_OVER:
                    serverNetworks.forEach(ServerNetwork::fullUpdate);
                    serverNetworks.forEach(ServerNetwork::updateFinalScores);
                    //serverNetworks.forEach(ServerNetwork::turnManagement);
                    SagradaServer.getInstance().getGameControllers().remove(this);

                    break;
            }
        }
    }
}
