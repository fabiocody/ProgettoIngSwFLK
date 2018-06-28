package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import java.util.stream.Collectors;


public class WaitingRoomController extends BaseController {

    private static WaitingRoomController instance;

    private WaitingRoomController() {
        super();
        WaitingRoom.getInstance().addObserver(this);
        WaitingRoom.getInstance().getTimer().addObserver(this);
    }

    static WaitingRoomController getInstance() {
        if (instance == null)
            instance = new WaitingRoomController();
        return instance;
    }

    UUID addPlayer(String nickname) throws LoginFailedException, NicknameAlreadyUsedInGameException {
        try {
            return WaitingRoom.getInstance().addPlayer(nickname);
        } catch (LoginFailedException e) {
            throw e;
        } catch (NicknameAlreadyUsedInGameException e) {
            Game game = e.getGame();
            try {
                GameController controller = SagradaServer.getInstance().getGameController(game);
                e.setController(controller);
                throw e;
            } catch (NoSuchElementException f) {
                throw new LoginFailedException(nickname);
            }
        }
    }

    void removePlayer(String nickname){
        WaitingRoom.getInstance().removePlayer(nickname);
    }

    List<String> getWaitingPlayers() {
        return WaitingRoom.getInstance().getWaitingPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof WaitingRoom) {
            if (arg instanceof List /*&& getUuid() != null*/) {
                Logger.debug("Updating waiting players");
                forEachServerNetwork(ServerNetwork::updateWaitingPlayers);
            }
        } else if (o instanceof CountdownTimer) {
            String stringArg = String.valueOf(arg);
            if (stringArg.startsWith(NotificationMessages.WAITING_ROOM)) {
                String tick = stringArg.split(" ")[1];
                Logger.debug("WR Timer tick (from update): " + tick);
                forEachServerNetwork(network -> network.updateTimerTick(Methods.WR_TIMER_TICK, tick));
            }
        }
    }
}
