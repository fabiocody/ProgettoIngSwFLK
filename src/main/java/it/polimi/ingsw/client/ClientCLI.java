package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.shared.util.*;
import org.fusesource.jansi.AnsiConsole;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;
import static org.fusesource.jansi.Ansi.*;

/**
 * This is the command line interface of the game
 * @author Team
 */
public class ClientCLI extends Client implements Observer {

    private String ttyConfig;
    private static final String TIMER_PROMPT = "timerPrompt";
    private static final String WAITING_ROOM_PROMPT = "waitingRoomPrompt";
    private static final String RECONNECTION_PROMPT = "reconnectionPrompt";

    private BufferedReader stdin;
    private StringBuilder stdinBuffer = new StringBuilder();
    private final Object stdinBufferLock = new Object();

    private boolean stopAsyncInput = false;
    private boolean bypassWaitingRoom = false;

    private String wrTimer;
    private String wrPlayers;
    private String gameTimer = "00";
    private int draftPoolLength;
    private int roundTrackLength = 0;

    private String input = "";
    private String privateObjectiveCard;

    /**
     * @param debug whether or not to start the client in debug mode
     */
    ClientCLI(boolean debug) {
        super(debug);
        ClientNetwork.getInstance().addObserver(this);
    }

    /**
     * This method starts a CLI client and executes the game actions
     */
    void start() {
        AnsiConsole.systemInstall();
        Logger.print(ansi().eraseScreen().cursor(0, 0).toString());
        try {
            this.stdin = new BufferedReader(new InputStreamReader(System.in));
            do addPlayer(); while (!this.isLogged());

            if (!bypassWaitingRoom) {
                do {
                    input = asyncInput(WAITING_ROOM_PROMPT);
                    if (input.equalsIgnoreCase("exit")) throw new InterruptedException();
                } while (!stopAsyncInput && !bypassWaitingRoom);
            }

            if (!this.isPatternChosen()) this.choosePatternMessage();

            while (!this.isGameOver()) {

                if (!isGameOver()) {

                    input = "";
                    while (isSuspended() && !input.equals(getNickname())) {
                        input = asyncInput(RECONNECTION_PROMPT);
                        if (input.equals(getNickname())) {
                            ClientNetwork.getInstance().addPlayer(getNickname());
                        }
                    }
                }

                while (!this.isActive() && !this.isGameOver()) Thread.sleep(10);

                if (!isGameOver()) {
                    Logger.println(InterfaceMessages.ITS_YOUR_TURN);
                    boolean turnOver = false;
                    do {
                        try {
                            this.chooseActionMessage();
                            try {
                                int instructionIndex = Integer.parseInt(input);

                                switch (instructionIndex){
                                    case 1:
                                        this.placeDieMove();
                                        break;
                                    case 2:
                                        this.useToolCardMove();
                                        break;
                                    case 3:
                                        this.setActive(false);
                                        ClientNetwork.getInstance().nextTurn();
                                        turnOver = true;
                                        break;
                                    default:
                                        Logger.println(InterfaceMessages.INVALID_MOVE_CAPITALIZED);
                                        break;
                                }
                            } catch (NumberFormatException e) {
                                if (isSuspended()) break;
                            }
                        } catch (CancelException e) {
                            Logger.println(InterfaceMessages.CANCELED_MOVE);
                        }
                    } while (!turnOver);
                }
            }
        } catch (IOException | InterruptedException e) {
            Logger.println("");
            System.exit(Constants.EXIT_ERROR);
        } finally {
            exit();
        }
    }


    /**
     * This method closes the client
     */
    private void exit() {
        Logger.println("");
        try {
            ClientNetwork.getInstance().teardown();
            AnsiConsole.systemUninstall();
        } catch (IOException e) {
            Logger.error("Exception raised while tearing down");
            Logger.printStackTrace(e);
        }
        System.exit(Constants.EXIT_ERROR);
    }


    /**
     * This method prompts the user with the choose pattern message
     * @throws IOException thrown by the <code>readLine</code> used to take user input
     * @throws InterruptedException when the thread is interrupted
     */
    private void choosePatternMessage() throws IOException, InterruptedException{
        try {
            Integer patternIndex = Constants.INDEX_CONSTANT;
            Logger.println("");
            do {
                this.input = input("Scegli la tua carta Schema [1-4] >>>");
                try {
                    patternIndex = Integer.valueOf(input);
                } catch (NumberFormatException e) {
                    Logger.printStackTraceConditionally(e);
                }
            } while (patternIndex <= 0 || patternIndex > 4);
            ClientNetwork.getInstance().choosePattern(patternIndex - 1);
            this.setPatternChosen(true);
            if (!isGameStarted()) {
                Logger.println(InterfaceMessages.patternSelected(patternIndex) + "\n");
                while (!this.isGameStarted()) Thread.sleep(10);
            }
        } catch (IOException | InterruptedException e){
            Logger.printStackTraceConditionally(e);
            throw e;
        }
    }

    /**
     * This method prompts the user with the choose action message
     * @throws IOException thrown by the <code>readLine</code> used to take user input
     */
    private void chooseActionMessage() throws IOException{
        Logger.println("\nPremi 1 per piazzare un dado\nPremi 2 per usare una carta strumento\nPremi 3 per " +
                "passare il turno.");
        Logger.println("Scegli cosa fare [1-3]");
        input = asyncInput(TIMER_PROMPT);
    }

    /**
     * this method handles log in of a player
     *
     * @throws IOException socket error
     */
    private void addPlayer() throws IOException {
        String nickname = this.input("Nickname >>>");
        if (nickname.equals("")) {
            Logger.println(InterfaceMessages.LOGIN_FAILED_EMPTY);
        } else if (nickname.contains(" ")) {
            Logger.println(InterfaceMessages.LOGIN_FAILED_SPACES);
        } else if (nickname.length() > Constants.MAX_NICKNAME_LENGTH) {
            Logger.println(InterfaceMessages.LOGIN_FAILED_LENGTH);
        } else {
            this.setNickname(nickname);
            setUUID(ClientNetwork.getInstance().addPlayer(this.getNickname()));
            setLogged(this.getUUID() != null);
            if (!isLogged()) {
                Logger.println(InterfaceMessages.LOGIN_FAILED_USED);
            }
        }
    }

    /**
     * This method is called when the player wants to place a die
     * @throws IOException thrown by the <code>readLine</code> used to take user input
     * @throws CancelException thrown if the user decides to cancel the move
     */
    private void placeDieMove() throws IOException, CancelException {
        int draftPoolIndex;
        int x;
        int y;
        draftPoolIndex = this.getInputIndex("\nQuale dado vuoi piazzare [1-" + draftPoolLength + "]? " + InterfaceMessages.CANCEL_MESSAGE,0,draftPoolLength,true);
        x = this.getInputIndex("\nIn quale colonna vuoi piazzarlo [1-5]? " + InterfaceMessages.CANCEL_MESSAGE,0,Constants.NUMBER_OF_PATTERN_COLUMNS,true);
        y = this.getInputIndex("\nIn quale riga vuoi piazzarlo [1-4]? " + InterfaceMessages.CANCEL_MESSAGE,0,Constants.NUMBER_OF_PATTERN_ROWS,true);
        JsonObject result = ClientNetwork.getInstance().placeDie(draftPoolIndex,x,y);
        if(result.get(JsonFields.RESULT).getAsBoolean())
            Logger.println(InterfaceMessages.SUCCESSFUL_DIE_PLACEMENT);
        else
            Logger.println(InterfaceMessages.UNSUCCESSFUL_DIE_PLACEMENT + result.get(JsonFields.ERROR_MESSAGE).getAsString());
    }

    /**
     * This method asks the user the data required by the selected tool card
     * @param requiredData the required data by the tool card
     * @param cardIndex the index of the tool card
     * @return whether or not the usage was successful
     * @throws IOException thrown by the <code>readLine</code> used to take user input
     * @throws CancelException thrown if the user decides to cancel the move
     */
    private boolean useData(JsonObject requiredData, int cardIndex) throws IOException, CancelException{
        int draftPoolIndex;
        int roundTrackIndex;
        int delta;
        int newValue;
        int fromCellX;
        int fromCellY;
        int toCellX;
        int toCellY;
        try{
            if (!(requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.STOP) && requiredData.get(JsonFields.DATA).getAsJsonObject().get(JsonFields.STOP).getAsBoolean())) {
                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.DRAFT_POOL_INDEX)) {
                    draftPoolIndex = this.getInputIndex("\nQuale dado della riserva vuoi utilizzare [1-" + draftPoolLength + "]? " + InterfaceMessages.CANCEL_MESSAGE, 0, draftPoolLength, true);
                    requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.DRAFT_POOL_INDEX, draftPoolIndex);
                }
                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.ROUND_TRACK_INDEX)) {
                    roundTrackIndex = this.getInputIndex("\nQuale dado del round track vuoi utilizzare [1-" + roundTrackLength + "]? " + InterfaceMessages.CANCEL_MESSAGE, 0, (int) roundTrackLength, true);
                    requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.ROUND_TRACK_INDEX, roundTrackIndex);
                }
                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.DELTA)) {
                    delta = this.getInputIndex("\nVuoi aumentare[1] o diminuire[-1] il valore del dado? " + InterfaceMessages.CANCEL_MESSAGE);
                    requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.DELTA, delta);
                }
                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.NEW_VALUE)) {
                    newValue = this.getInputIndex("\nQuale valore vuoi assegnare al dado[1-6]? " + InterfaceMessages.CANCEL_MESSAGE, 1, 7, false);
                    requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.NEW_VALUE, newValue);
                }
                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.FROM_CELL_X)) {
                    fromCellX = this.getInputIndex("\nDa quale colonna vuoi muoverlo [1-5]? " + InterfaceMessages.CANCEL_MESSAGE, 0, Constants.NUMBER_OF_PATTERN_COLUMNS, true);
                    requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.FROM_CELL_X, fromCellX);
                }
                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.FROM_CELL_Y)) {
                    fromCellY = this.getInputIndex("\nDa quale riga vuoi muoverlo [1-4]? " + InterfaceMessages.CANCEL_MESSAGE, 0, Constants.NUMBER_OF_PATTERN_ROWS, true);
                    requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.FROM_CELL_Y, fromCellY);
                }
                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.TO_CELL_X)) {
                    toCellX = this.getInputIndex("\nIn quale colonna vuoi piazzarlo [1-5]? " + InterfaceMessages.CANCEL_MESSAGE, 0, Constants.NUMBER_OF_PATTERN_COLUMNS, true);
                    requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.TO_CELL_X, toCellX);
                }
                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.TO_CELL_Y)) {
                    toCellY = this.getInputIndex("\nIn quale riga vuoi piazzarlo [1-4]? " + InterfaceMessages.CANCEL_MESSAGE, 0, Constants.NUMBER_OF_PATTERN_ROWS, true);
                    requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.TO_CELL_Y, toCellY);
                }
            }
            JsonObject result = ClientNetwork.getInstance().useToolCard(cardIndex,requiredData.get(JsonFields.DATA).getAsJsonObject());
            if(result.get(JsonFields.RESULT).getAsBoolean()){
                if(!requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.CONTINUE)) Logger.println("\nCarta strumento usata con successo!");
                return true;
            }
            else {
                Logger.println("\nCarta strumento non usata: " + result.get(JsonFields.ERROR_MESSAGE).getAsString());
                return false;
            }
        } catch (CancelException e) {
            ClientNetwork.getInstance().cancelToolCardUsage(cardIndex);
            throw e;
        }
    }

    /**
     * This method is called when the player wants to use a tool card
     * @throws IOException thrown by the <code>readLine</code> used to take user input
     * @throws CancelException thrown if the user decides to cancel the move
     */
    private void useToolCardMove() throws IOException, CancelException {
        int cardIndex;
        boolean stop;
        JsonObject requiredData;
        boolean valid;
        cardIndex = this.getInputIndex("\nQuale carta strumento vuoi usare [1-3]? " + InterfaceMessages.CANCEL_MESSAGE, 0, 3,true);
        requiredData = ClientNetwork.getInstance().requiredData(cardIndex);
        requiredData.remove(JsonFields.METHOD);
        if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.NO_FAVOR_TOKENS) || requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD)) {
            Logger.println("\n" + InterfaceMessages.UNSUCCESSFUL_TOOL_CARD_USAGE + requiredData.get(JsonFields.DATA).getAsJsonObject().get(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD).getAsString());
        } else {
            valid = this.useData(requiredData,cardIndex);
            if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.CONTINUE) && valid) {
                requiredData = ClientNetwork.getInstance().requiredData(cardIndex);
                requiredData.remove(JsonFields.METHOD);
                if(requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.STOP)) {
                    stop = !this.getInputStop();
                    requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.STOP, stop);
                }
                do{
                    valid = this.useData(requiredData,cardIndex);
                } while (!valid);
            }
        }
    }

    /**
     * This method is used to read from stdin.
     *
     * @param prompt the prompt to the input.
     * @return the string that has been read.
     * @throws IOException thrown if an IO error occurs.
     */
    private String input(String prompt) throws IOException {
        Logger.print(prompt + " ");
        return stdin.readLine();
    }

    /**
     * This methods asks for input from the user and simultaneously prints messages
     * @param methodName the name of the method creating the message (Java Reflection)
     * @return the input taken from the user
     * @throws IOException thrown if an IO error occurs.
     */
    private String asyncInput(String methodName) throws IOException {
        stopAsyncInput = false;
        int escape = 0;
        String bufferString = "";
        try {
            setTerminalToRawMode();
            synchronized (stdinBufferLock) {
                if (stdinBuffer == null) stdinBuffer = new StringBuilder();
            }
            Method method = Class.forName(ClientCLI.class.getName()).getDeclaredMethod(methodName);
            Logger.print(method.invoke(this).toString());
            while (!stopAsyncInput) {
                if (System.in.available() != 0) {
                    int c = System.in.read();
                    if (c == 0x0A) {
                        synchronized (stdinBufferLock) {
                            bufferString = stdinBuffer.toString();
                            stdinBuffer = new StringBuilder();
                        }
                        Logger.println();
                        break;
                    } else if (c == 0x7F) {
                        synchronized (stdinBufferLock) {
                            if (stdinBuffer.length() > 0) stdinBuffer.deleteCharAt(stdinBuffer.length() - 1);
                        }
                    } else if (c == 0x1B) {
                        escape = 1;
                    } else if (escape == 1 && c == 0x5B) {
                        escape = 2;
                    } else if (escape == 2 && c >= 0x41 && c <= 0x44) {
                        escape = 0;
                    } else {
                        synchronized (stdinBufferLock) {
                            stdinBuffer.append((char) c);
                        }
                    }
                    method = Class.forName(ClientCLI.class.getName()).getDeclaredMethod(methodName);
                    Logger.print(method.invoke(this).toString());
                }
                Thread.sleep(10);
            }
        } catch (InterruptedException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Logger.printStackTrace(e);
        } finally {
            try {
                stty(ttyConfig.trim());
            }
            catch (Exception e) {
                Logger.error("Exception restoring tty config");
            }
        }
        if (!stopAsyncInput && !methodName.equals(TIMER_PROMPT)) {
            try {
                Method method = Class.forName(ClientCLI.class.getName()).getDeclaredMethod(methodName);
                Logger.print(method.invoke(this).toString());
            } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
                Logger.printStackTrace(e);
            }
        }
        return bufferString;
    }

    /**
     * This method is used to put the terminal in raw mode, in order to simultaneously read from stdin and write to stdout.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void setTerminalToRawMode() throws IOException, InterruptedException {
        ttyConfig = stty("-g");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                stty(ttyConfig.trim());
            } catch (IOException | InterruptedException e) {
                Logger.printStackTrace(e);
            }
        }));
        stty("-icanon min 1");      // set the console to be character-buffered instead of line-buffered
        stty("-echo");              // disable character echoing
    }

    /**
     *  This method execute the stty command with the specified arguments
     *  against the current active terminal.
     */
    private static String stty(String args) throws IOException, InterruptedException {
        String cmd = "stty " + args + " < /dev/tty";
        return exec(new String[] {"sh", "-c", cmd});
    }

    /**
     *  Execute the specified command and return the output
     *  (both stdout and stderr).
     */
    private static String exec(String[] cmd) throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();
        while ((c = in.read()) != -1) bout.write(c);
        in = p.getErrorStream();
        while ((c = in.read()) != -1) bout.write(c);
        p.waitFor();
        return new String(bout.toByteArray());
    }

    /**
     * This method takes numeric input from the user with bounds.
     * @param cliMessage the message to print on screen
     * @param lowerBound the lowest valid number
     * @param higherBound the highest valid number
     * @param scale whether or not to use zero-based numbering
     * @return the number typed by the user
     * @throws IOException thrown by the <code>readLine</code> used to take user input
     * @throws CancelException thrown if the user decides to cancel the move
     */
    private int getInputIndex(String cliMessage, int lowerBound, int higherBound, boolean scale) throws IOException, CancelException {
        int index = Constants.INDEX_CONSTANT;
        int cancelValue = 0;
        String userInput;
        do {
            try {
                Logger.println(cliMessage);
                userInput = asyncInput(TIMER_PROMPT);
                index = Integer.valueOf(userInput);
                if (scale){
                    index -= 1;
                    cancelValue -= 1;
                }
                if (index == cancelValue) throw new CancelException();
            } catch (NumberFormatException e) {
                Logger.println(InterfaceMessages.INDEX_NOT_VALID);
            }
        } while (index < lowerBound || index >= higherBound);
        return index;
    }

    /**
     * This method takes numeric input from the user without bounds.
     * @param cliMessage the message to print on screen
     * @return the number typed by the user
     * @throws IOException thrown by the <code>readLine</code> used to take user input
     * @throws CancelException thrown if the user decides to cancel the move
     */
    private int getInputIndex(String cliMessage) throws CancelException, IOException {
        int index = Constants.INDEX_CONSTANT;
        String userInput;
        do {
            try {
                Logger.println(cliMessage);
                userInput = asyncInput(TIMER_PROMPT);
                index = Integer.valueOf(userInput);
                if (index == 0) throw new CancelException();
            } catch (NumberFormatException e) {
                Logger.println(InterfaceMessages.INDEX_NOT_VALID);
            }
        } while (!(index == 1|| index == -1));
        return index;
    }

    /**
     * This method is used to get whether or not the user wants to continue using a tool card that has the
     * <code>JsonFields.STOP</code> field. The user cannot cancel the action, hence <code>CancelException</code>
     * is not thrown
     * @return the boolean that corresponds to the user's answer
     * @throws IOException thrown by the <code>readLine</code> used to take user input
     */
    private boolean getInputStop() throws IOException {
        int index = Constants.INDEX_CONSTANT;
        String userInput;
        do {
            try {
                Logger.println(InterfaceMessages.STOP_MESSAGE);
                userInput = asyncInput(TIMER_PROMPT);
                index = Integer.valueOf(userInput);
            } catch (NumberFormatException e) {
                Logger.println(InterfaceMessages.INDEX_NOT_VALID);
            }
        } while (!(index == 0|| index == 1));
        return index == 1;
    }

    /**
     * This method is used to horizontally align two window patterns
     * @param pattern1 the first pattern
     * @param pattern2 the second pattern
     * @return the string that describes two window patterns horizontally aligned
     */
    private static String concatWindowPatterns(String[] pattern1, String[] pattern2) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pattern1.length; i++) {
            builder.append(pattern1[i]);
            for (int k = 0; k < 5; k++) builder.append(" ");
            builder.append(pattern2[i]);
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }


    /**
     * This method is used to build a string describing the window patterns
     * @param patterns the players' window patterns
     * @return the string that describes the patterns
     */
    private static String windowPatternsMessage(List<String> patterns) {
        String message = concatWindowPatterns(patterns.get(0).split("\n"), patterns.get(1).split("\n"));
        if (patterns.size() == 3)
            message += "\n\n" + patterns.get(2);
        else if (patterns.size() == 4)
            message += "\n\n" + concatWindowPatterns(patterns.get(2).split("\n"), patterns.get(3).split("\n"));
        return message;
    }


    /**
     * @return the waiting room string to be displayed
     */
    private String waitingRoomPrompt() {
        synchronized (stdinBufferLock) {
            return ansi().eraseLine().cursorUpLine().eraseLine().cursorUpLine().eraseLine()
                    .a("Giocatori in attesa: ")
                    .a(this.wrPlayers)
                    .a("\n")
                    .a("Tempo rimasto: ")
                    .a(this.wrTimer != null ? this.wrTimer : "∞")
                    .a("\n")
                    .a(">>> ")
                    .a(stdinBuffer.toString()).toString();
        }
    }

    /**
     * @return the timer string to be displayed
     */
    private String timerPrompt() {
        synchronized (stdinBufferLock) {
            String prompt = String.format("[%s] >>> %s", gameTimer, stdinBuffer.toString());
            return updateLine(prompt);
        }
    }

    /**
     * @param line the line to append to be returned string
     * @return the update string to be displayed
     */
    private String updateLine(String line) {
        return ansi().eraseLine()
                .a('\r')
                .a(line)
                .toString();
    }

    /**
     * @return the reconnection string to be displayed
     */
    private String reconnectionPrompt() {
        synchronized (stdinBufferLock) {
            String prompt = "Per riconnetterti, inserisci il tuo nickname >>> " + stdinBuffer.toString();
            return updateLine(prompt);
        }
    }

    // OBSERVER METHODS

    /**
     * This method handles the updates received from the server
     *
     * @param o observable object
     * @param arg JsonObject containing the information for the update
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ClientNetwork && arg instanceof JsonObject) {
            JsonObject jsonArg = (JsonObject) arg;
            if (jsonArg.has(JsonFields.METHOD)) {
                Methods method = Methods.fromString(jsonArg.get(JsonFields.METHOD).getAsString());
                switch (method) {
                    case ADD_PLAYER:
                        addPlayerUpdateHandler(jsonArg);
                        break;
                    case UPDATE_WAITING_PLAYERS:
                        waitingPlayersUpdateHandler(jsonArg);
                        break;
                    case WR_TIMER_TICK:
                        this.wrTimer = jsonArg.get(JsonFields.TICK).getAsString();
                        if (isLogged()) Logger.print(waitingRoomPrompt());
                        break;
                    case GAME_TIMER_TICK:
                        gameTimerTickUpdateHandler(jsonArg);
                        break;
                    case GAME_SETUP:
                        privateObjectiveCardUpdateHandler(jsonArg);
                        selectableWindowPatternsUpdateHandler(jsonArg);
                        break;
                    case WINDOW_PATTERNS:
                        windowPatternsUpdateHandler(jsonArg);
                        break;
                    case TOOL_CARDS:
                        toolCardsUpdateHandler(jsonArg);
                        break;
                    case PUBLIC_OBJECTIVE_CARDS:
                        publicObjectiveCardsUpdateHandler(jsonArg);
                        break;
                    case DRAFT_POOL:
                        draftPoolUpdateHandler(jsonArg);
                        break;
                    case TURN_MANAGEMENT:
                        turnManagementUpdateHandler(jsonArg);
                        break;
                    case ROUND_TRACK:
                        String roundTrack = jsonArg.get(JsonFields.CLI_STRING).getAsString();
                        roundTrackLength = (int) roundTrack.chars().filter(ch -> ch == ']').count();
                        Logger.println("Tracciato del Round\n" + roundTrack + "\n");
                        break;
                    case PLAYERS:
                        Logger.print(ansi().eraseScreen().cursor(0, 0).toString());
                        break;
                    case FAVOR_TOKENS:
                        favorTokensUpdateHandler(jsonArg);
                        break;
                    case FINAL_SCORES:
                        finalScoresUpdateHandler(jsonArg);
                        break;
                    default:
                        throw new IllegalStateException("This was not supposed to happen! " + method.toString());
                }
            } else if (jsonArg.has(JsonFields.EXIT_ERROR)) {
                exit();
            }
        }
    }

    /**
     * This method handles the add player message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     *
     */
    private void addPlayerUpdateHandler(JsonObject jsonArg) {
        if (jsonArg.get(JsonFields.RECONNECTED).getAsBoolean()) {
            bypassWaitingRoom = true;
            stopAsyncInput = true;
            this.setPatternChosen(true);
            this.setSuspended(false);
            JsonObject cardJson = jsonArg.getAsJsonObject(JsonFields.PRIVATE_OBJECTIVE_CARD);
            privateObjectiveCard = "Obiettivo privato: " +
                    cardJson.get(JsonFields.NAME).getAsString() +
                    " - " +
                    cardJson.get(JsonFields.DESCRIPTION).getAsString();
        }
    }

    /**
     * This method handles the update waiting room message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void waitingPlayersUpdateHandler(JsonObject jsonArg) {
        if (!this.isPatternChosen()) {
            JsonArray playersArray = jsonArg.get(JsonFields.PLAYERS).getAsJsonArray();
            this.wrPlayers = StreamSupport.stream(playersArray.spliterator(), false)
                    .map(JsonElement::getAsString)
                    .reduce((s, r) -> s + ", " + r)
                    .orElse(null);
            if (isLogged()) Logger.print(waitingRoomPrompt());
        }
    }

    /**
     * This method handles the game timer tick message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void gameTimerTickUpdateHandler(JsonObject jsonArg) {
        this.gameTimer = jsonArg.get(JsonFields.TICK).getAsString();
        if (isActive())
            Logger.print(timerPrompt());
        else {
            String timerString = InterfaceMessages.itsHisHerTurn(getActiveNickname()) + " [" + gameTimer + "]";
            if (isSuspended()) {
                Logger.print(ansi()
                        .eraseLine(Erase.ALL)
                        .cursorUpLine()
                        .eraseLine(Erase.ALL)
                        .a(timerString)
                        .a('\n')
                        .a(reconnectionPrompt())
                        .toString()
                );
            } else {
                Logger.print(updateLine(timerString));
            }
        }
    }
    /**
     * This method handles message received from the server containg the pattern that the player can choose
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void selectableWindowPatternsUpdateHandler(JsonObject jsonArg) {
        stopAsyncInput = true;
        JsonArray windowPatternsArray = jsonArg.getAsJsonArray(JsonFields.WINDOW_PATTERNS);
        List<String> windowPatterns = new ArrayList<>();
        for (int i = 0; i < windowPatternsArray.size(); i++) {
            JsonObject windowPattern = windowPatternsArray.get(i).getAsJsonObject();
            StringBuilder indexString = new StringBuilder();
            indexString.append(i+1);
            while (indexString.length() < Constants.MAX_NICKNAME_LENGTH) indexString.append(' ');
            windowPatterns.add(indexString.toString() + "\n" + windowPattern.get(JsonFields.CLI_STRING).getAsString());
        }
        Logger.println();
        Logger.println(windowPatternsMessage(windowPatterns));
    }

    /**
     * This method handles the window patterns update message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void windowPatternsUpdateHandler(JsonObject jsonArg) {
        JsonObject windowPatternsJson = jsonArg.getAsJsonObject(JsonFields.WINDOW_PATTERNS);
        List<String> windowPatterns = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : windowPatternsJson.entrySet()) {
            StringBuilder patternString = new StringBuilder(entry.getKey());
            while (patternString.length() < Constants.MAX_NICKNAME_LENGTH)
                patternString.append(' ');
            patternString.append('\n')
                    .append(entry.getValue().getAsJsonObject().get(JsonFields.CLI_STRING).getAsString());
            windowPatterns.add(patternString.toString());
        }
        Logger.println(windowPatternsMessage(windowPatterns));
    }

    /**
     * This method handles the tool card update message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void toolCardsUpdateHandler(JsonObject jsonArg) {
        JsonArray toolCards = jsonArg.getAsJsonArray(JsonFields.TOOL_CARDS);
        StringBuilder toolCardsString = new StringBuilder();
        for (int i = 0; i < toolCards.size(); i++) {
            JsonObject toolCard = toolCards.get(i).getAsJsonObject();
            toolCardsString
                    .append("Carta strumento ")
                    .append(i+1)
                    .append(": ")
                    .append(toolCard.get(JsonFields.NAME).getAsString())
                    .append("\nEffetto: ")
                    .append(toolCard.get(JsonFields.DESCRIPTION).getAsString())
                    .append("\nPrezzo: ")
                    .append(toolCard.get(JsonFields.USED).getAsBoolean() ? 2 : 1)
                    .append(toolCard.get(JsonFields.USED).getAsBoolean() ? " segnalini" : " segnalino")
                    .append(" favore")
                    .append("\n\n");
        }
        Logger.println(toolCardsString.toString());
    }

    /**
     * This method handles the public objective cards update message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void publicObjectiveCardsUpdateHandler(JsonObject jsonArg) {
        JsonArray publicObjectiveCards = jsonArg.getAsJsonArray(JsonFields.PUBLIC_OBJECTIVE_CARDS);
        StringBuilder objectiveCardsString = new StringBuilder();
        for (JsonElement element : publicObjectiveCards){
            JsonObject obj = element.getAsJsonObject();
            objectiveCardsString
                    .append("Obiettivo pubblico: ")
                    .append(obj.get(JsonFields.NAME).getAsString())
                    .append("\nDescrizione: ")
                    .append(obj.get(JsonFields.DESCRIPTION).getAsString())
                    .append("\nPunti Vittoria (PV) per ogni set completo di questo tipo: ")
                    .append(obj.get(JsonFields.VICTORY_POINTS).isJsonNull() ? "#" : obj.get(JsonFields.VICTORY_POINTS).getAsInt())
                    .append("\n\n");
        }
        objectiveCardsString.append(privateObjectiveCard).append("\n\n");
        Logger.println(objectiveCardsString.toString());
    }

    /**
     * This method handles the private objective cards update message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void privateObjectiveCardUpdateHandler(JsonObject jsonArg) {
        Logger.print(ansi().eraseScreen().cursor(0, 0).toString());
        JsonObject cardJson = jsonArg.getAsJsonObject(JsonFields.PRIVATE_OBJECTIVE_CARD);
        privateObjectiveCard = "Obiettivo privato: " +
                cardJson.get(JsonFields.NAME).getAsString() +
                " - " +
                cardJson.get(JsonFields.DESCRIPTION).getAsString();
        Logger.println(privateObjectiveCard);
    }

    /**
     * This method handles the draft pool update message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void draftPoolUpdateHandler(JsonObject jsonArg) {
        JsonArray draftPoolDice = jsonArg.getAsJsonArray(JsonFields.DICE);
        StringBuilder draftPoolString = new StringBuilder("Riserva: ");
        this.draftPoolLength = 0;
        for (JsonElement element : draftPoolDice){
            draftPoolString.append(element.getAsJsonObject().get(JsonFields.CLI_STRING).getAsString());
            this.draftPoolLength++;
        }
        Logger.println("\n" + draftPoolString.toString() + "\n");
    }

    /**
     * This method handles the turn management update message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void turnManagementUpdateHandler(JsonObject jsonArg) {
        if (!this.isGameStarted()) this.setGameStarted();
        this.setGameOver(jsonArg.get(JsonFields.GAME_OVER).getAsBoolean());
        List<String> suspendedPlayers = StreamSupport.stream(jsonArg.getAsJsonArray(JsonFields.SUSPENDED_PLAYERS).spliterator(), false)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
        this.setSuspendedPlayers(suspendedPlayers);
        this.getSuspendedPlayers().stream()
                .reduce((s, r) -> s + ", " + r)
                .ifPresent(sp -> Logger.println("Giocatori sospesi: " + sp + "\n"));
        this.setActive(jsonArg.get(JsonFields.ACTIVE_PLAYER).getAsString());
        if (!isActive() && !isSuspended() && !isGameOver()) Logger.println(InterfaceMessages.WAIT_FOR_YOUR_TURN);
        stopAsyncInput = true;
    }

    /**
     * This method handles the favor tokens update message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void favorTokensUpdateHandler(JsonObject jsonArg) {
        StringBuilder favorTokenString = new StringBuilder("\nSegnalini Favore");
        Set<Map.Entry<String, JsonElement>> entrySet = jsonArg.get(JsonFields.FAVOR_TOKENS).getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            favorTokenString.append("\n").append(entry.getKey()).append(": ").append(entry.getValue().getAsInt());
        }
        Logger.println(favorTokenString.toString());
    }

    /**
     * This method handles the final scores update message received from the server
     * @param jsonArg the message from the server (message format is described in the SocketProtocol)
     */
    private void finalScoresUpdateHandler(JsonObject jsonArg) {
        StringBuilder finalScoresString = new StringBuilder("\nLa partita è finita!\nRisultati finali");
        Set<Map.Entry<String, JsonElement>> entrySet = jsonArg.get(JsonFields.FINAL_SCORES).getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String name = entry.getKey();
            if (name.equals(JsonFields.WINNER)) {
                String value = entry.getValue().isJsonNull() ? null : entry.getValue().getAsString();
                finalScoresString.append("\n").append(name).append(": ").append(value);
            } else {
                int value = entry.getValue().getAsInt();
                finalScoresString.append("\n").append(name).append(": ").append(value);
            }
        }
        Logger.println(finalScoresString.toString());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    ClientNetwork.getInstance().teardown();
                } catch (IOException e) {
                    Logger.printStackTrace(e);
                }
                AnsiConsole.systemUninstall();
                System.exit(Constants.EXIT_STATUS);
            }
        }, 100);
    }

}
