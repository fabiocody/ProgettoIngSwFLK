package it.polimi.ingsw.server;

import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.shared.util.*;
import java.util.*;
import java.util.stream.Collectors;


public class WaitingRoomController extends BaseController {

    private static WaitingRoomController instance;

    /**
     * the constructor for the waiting room controller, which adds an observer on the waiting room and on the timer
     */
    private WaitingRoomController() {
        super();
        WaitingRoom.getInstance().addObserver(this);
        WaitingRoom.getInstance().getTimer().addObserver(this);
    }

    /**
     * @return the istance of waiting room controller
     */
    static WaitingRoomController getInstance() {
        if (instance == null)
            instance = new WaitingRoomController();
        return instance;
    }

    /**
     * @param nickname the nickname of the player logging in
     * @return a random UUID if the user has been added, null if login has failed
     * @throws LoginFailedException thrown in case of a failed login
     * @throws NicknameAlreadyUsedInGameException thrown if ithe nickname is already used
     */
    UUID addPlayer(String nickname) throws LoginFailedException, NicknameAlreadyUsedInGameException {
        try {
            return WaitingRoom.getInstance().addPlayer(nickname);
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

    /**
     * @param nickname of the player to remove
     */
    void removePlayer(String nickname){
        WaitingRoom.getInstance().removePlayer(nickname);
    }

    /**
     * @return list of the players waiting in the waiting room
     */
    List<String> getWaitingPlayers() {
        return WaitingRoom.getInstance().getWaitingPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    /**
     * Observer method. Can update waiting players or the timer depending on the arg
     *
     * @param o the object that has triggered the update
     * @param arg the arguments of the update
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof WaitingRoom) {
            if (arg instanceof List /*&& getUUID() != null*/) {
                Logger.debug("Updating waiting players");
                forEachNetwork(ServerNetwork::updateWaitingPlayers);
            }
        } else if (o instanceof CountdownTimer) {
            String stringArg = String.valueOf(arg);
            if (stringArg.startsWith(NotificationMessages.WAITING_ROOM)) {
                String tick = stringArg.split(" ")[1];
                Logger.debug("WR Timer tick (from update): " + tick);
                forEachNetwork(network -> network.updateTimerTick(Methods.WR_TIMER_TICK, tick));
            }
        }
    }
}
