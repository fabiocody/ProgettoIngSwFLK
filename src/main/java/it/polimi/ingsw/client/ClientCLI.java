package it.polimi.ingsw.client;

import com.google.gson.*;
import it.polimi.ingsw.shared.util.*;
import org.fusesource.jansi.AnsiConsole;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;
import static it.polimi.ingsw.shared.util.Constants.*;
import static org.fusesource.jansi.Ansi.*;


public class ClientCLI extends Client {

    private String ttyConfig;

    private BufferedReader stdin;
    private StringBuilder stdinBuffer = new StringBuilder();
    private final Object stdinBufferLock = new Object();

    private boolean stopAsyncInput = false;
    private boolean showPrompt = true;
    private boolean bypassWaitingRoom = false;
    private int instructionIndex = INDEX_CONSTANT;

    private String wrTimeout;
    private String wrPlayers;
    private String gameTimeout = "00";
    private int cardIndex = INDEX_CONSTANT;

    private int draftPoolLength;
    private long roundTrackLength = 0;

    private String privateObjectiveCard;

    ClientCLI(ClientNetwork network, boolean debugActive) {
        super(network, debugActive);
    }

    void start() {
        AnsiConsole.systemInstall();
        Logger.print(ansi().eraseScreen().cursor(0, 0).toString());
        try {
            this.stdin = new BufferedReader(new InputStreamReader(System.in));
            do addPlayer(); while (!this.isLogged());
            String input = "";
            if (!bypassWaitingRoom) {
                do {
                    input = asyncInput("waitingRoomMessage");
                    if (input.equalsIgnoreCase("exit")) throw new InterruptedException();
                } while (!stopAsyncInput && !bypassWaitingRoom);
            }
            if (!this.isPatternChosen()) {
                Integer patternIndex = INDEX_CONSTANT;
                Logger.println("");
                do {
                    input = input("Scegli la tua carta Schema [1-4] >>>");
                    try {
                        patternIndex = Integer.valueOf(input);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                } while (patternIndex <= 0 || patternIndex > 4);
                this.getNetwork().choosePattern(patternIndex - 1);
                this.setPatternChosen(true);
                Logger.println("Hai scelto il pattern numero " + patternIndex + ".\nPer favore attendi che tutti i giocatori facciano la propria scelta.\n");
                while (!this.isGameStarted()) Thread.sleep(10);
            }
            while (!this.isGameOver()) {
                boolean dieAlreadyPlaced = false;
                boolean toolCardAlreadyUsed = false;
                int draftPoolIndex = INDEX_CONSTANT;
                int x = INDEX_CONSTANT;
                int y = INDEX_CONSTANT;
                int roundTrackIndex = INDEX_CONSTANT;
                int delta = INDEX_CONSTANT;
                int newValue = INDEX_CONSTANT;
                int fromCellX = INDEX_CONSTANT;
                int fromCellY = INDEX_CONSTANT;
                int toCellX = INDEX_CONSTANT;
                int toCellY = INDEX_CONSTANT;
                int continueIndex = INDEX_CONSTANT;
                boolean stop = false;

                if (!isGameOver()) {
                    this.getSuspendedPlayers().stream()
                            .reduce((s, r) -> s + ", " + r)
                            .ifPresent(sp -> Logger.println("Giocatori sospesi: " + sp + "\n"));

                    input = "";
                    while (isSuspended() && !input.equals(getNickname())) {
                        input = asyncInput("reconnectionPrompt");
                        if (input.equals(getNickname())) {
                            this.getNetwork().addPlayer(getNickname());
                        }
                    }
                }

                while (!this.isActive() && !this.isGameOver()) Thread.sleep(10);

                if (!isGameOver()) {
                    Logger.println("È il tuo turno!");
                    do {
                        try {
                            if (showPrompt) {
                                cardIndex = INDEX_CONSTANT;
                                Logger.println("Premi 1 per piazzare un dado\nPremi 2 per usare una carta strumento\nPremi 3 per " +
                                        "passare il turno.");
                                Logger.println("Scegli cosa fare [1-3]");
                                input = asyncInput("timerPrompt");
                            }
                            try {
                                if (showPrompt) this.instructionIndex = Integer.valueOf(input);
                                if (instructionIndex == 1) {
                                    if (dieAlreadyPlaced) {
                                        Logger.println("Hai già piazzato un dado questo turno!");
                                        this.instructionIndex = INDEX_CONSTANT;
                                    } else {
                                        do {
                                            draftPoolIndex = draftPoolLength;
                                            Logger.println("Quale dado vuoi piazzare [1-" + draftPoolLength + "]? " + EXIT_MESSAGE);
                                            input = asyncInput("timerPrompt");
                                            try {
                                                draftPoolIndex = Integer.valueOf(input) - 1;
                                                if (draftPoolIndex == -1) throw new CancelException();
                                            } catch (NumberFormatException e) {
                                                Logger.println("Indice non valido\n");
                                            }
                                        } while (draftPoolIndex < 0 || draftPoolIndex >= draftPoolLength);
                                        do {
                                            Logger.println("In quale colonna vuoi piazzarlo [1-5]? " + EXIT_MESSAGE);
                                            input = asyncInput("timerPrompt");
                                            try {
                                                x = Integer.valueOf(input) - 1;
                                                if (x == -1) throw new CancelException();
                                            } catch (NumberFormatException e) {
                                                Logger.println("Indice non valido\n");
                                            }
                                        } while (x < 0 || x >= NUMBER_OF_PATTERN_COLUMNS);
                                        do {
                                            Logger.println("In quale riga vuoi piazzarlo [1-4]? " + EXIT_MESSAGE);
                                            input = asyncInput("timerPrompt");
                                            try {
                                                y = Integer.valueOf(input) - 1;
                                                if (y == -1) throw new CancelException();
                                            } catch (NumberFormatException e) {
                                                Logger.println("Indice non valido\n");
                                            }
                                        } while (y < 0 || y >= NUMBER_OF_PATTERN_ROWS);
                                        if (this.getNetwork().placeDie(draftPoolIndex, x, y)) {
                                            Logger.println("Dado piazzato\n");
                                            dieAlreadyPlaced = true;
                                        } else {
                                            Logger.println("Posizionamento invalido\n");
                                        }
                                        this.instructionIndex = INDEX_CONSTANT;
                                    }
                                } else if (instructionIndex == 2) {  //Tool card
                                    if (toolCardAlreadyUsed) {
                                        Logger.println("Hai già usato una carta strumento questo turno!\n");
                                        this.instructionIndex = INDEX_CONSTANT;
                                    }
                                    if (showPrompt) {
                                        do {
                                            Logger.println("Quale carta strumento vuoi usare [1-3]?");
                                            input = asyncInput("timerPrompt");
                                            try {
                                                cardIndex = Integer.valueOf(input) - 1;
                                                if (cardIndex == -1) throw new CancelException();
                                            } catch (NumberFormatException e) {
                                                Logger.println("Indice non valido\n");
                                                continue;
                                            }
                                        } while (cardIndex < 0 || cardIndex >= 3);
                                    }
                                    JsonObject requiredData = this.getNetwork().requiredData(cardIndex);
                                    requiredData.remove("method");
                                    if (requiredData.get("data").getAsJsonObject().has(JsonFields.NO_FAVOR_TOKENS)) {
                                        Logger.println("Non hai abbastanza segnalini favore per utilizzare questa carta strumento");
                                        instructionIndex = INDEX_CONSTANT;
                                        showPrompt = true;
                                    } else {
                                        if (requiredData.get("data").getAsJsonObject().has("impossibleToUseToolCard")) {
                                            Logger.println("Non puoi utilizzare questa carta strumento: " + requiredData.get("data").getAsJsonObject().get("impossibleToUseToolCard").getAsString());
                                            instructionIndex = INDEX_CONSTANT;
                                            showPrompt = true;
                                        } else {
                                            if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.STOP)) {
                                                do {
                                                    Logger.println("Vuoi continuare [si 1/no 0]?");
                                                    input = asyncInput("timerPrompt");
                                                    try {
                                                        continueIndex = Integer.valueOf(input);
                                                        if (continueIndex == 1) stop = false;
                                                        else stop = true;
                                                        requiredData.get("data").getAsJsonObject().addProperty(JsonFields.STOP, stop);
                                                    } catch (NumberFormatException e) {
                                                        Logger.println("Indice non valido\n");
                                                    }
                                                } while (!(continueIndex == 1 || continueIndex == 0));
                                            }
                                            if (continueIndex != 0) {
                                                if (requiredData.get("data").getAsJsonObject().has(JsonFields.DRAFT_POOL_INDEX)) {
                                                    do {
                                                        Logger.println("Quale dado della riserva vuoi utilizzare [1-" + draftPoolLength + "]?");
                                                        input = asyncInput("timerPrompt");
                                                        try {
                                                            draftPoolIndex = Integer.valueOf(input) - 1;
                                                            if (draftPoolIndex == -1) throw new CancelException();
                                                            requiredData.get("data").getAsJsonObject().addProperty("draftPoolIndex", draftPoolIndex);
                                                        } catch (NumberFormatException e) {
                                                            Logger.println("Indice non valido\n");
                                                        }
                                                    } while (draftPoolIndex < 0 || draftPoolIndex >= draftPoolLength);
                                                }
                                                if (requiredData.get("data").getAsJsonObject().has(JsonFields.ROUND_TRACK_INDEX)) {
                                                    do {
                                                        Logger.println("Quale dado del round track vuoi utilizzare [1-" + roundTrackLength + "]?");
                                                        input = asyncInput("timerPrompt");
                                                        try {
                                                            roundTrackIndex = Integer.valueOf(input) - 1;
                                                            if (roundTrackIndex == -1) throw new CancelException();
                                                            requiredData.get("data").getAsJsonObject().addProperty("roundTrackIndex", roundTrackIndex);
                                                        } catch (NumberFormatException e) {
                                                            Logger.println("Indice non valido\n");
                                                        }
                                                    }
                                                    while (roundTrackIndex < 0 || roundTrackIndex >= roundTrackLength);
                                                }
                                                if (requiredData.get("data").getAsJsonObject().has(JsonFields.DELTA)) {
                                                    do {
                                                        Logger.println("Vuoi aunmentare[1] o diminuire[0] il valore del dado?");
                                                        input = asyncInput("timerPrompt");
                                                        try {
                                                            delta = Integer.valueOf(input);
                                                            if (delta == 0) delta = -1;
                                                            requiredData.get("data").getAsJsonObject().addProperty("delta", delta);
                                                        } catch (NumberFormatException e) {
                                                            Logger.println("Indice non valido\n");
                                                        }
                                                    } while (!(delta == 1 || delta == -1));
                                                }
                                                if (requiredData.get("data").getAsJsonObject().has(JsonFields.NEW_VALUE)) {
                                                    do {
                                                        Logger.println("Quale valore vuoi assegnare al dado[1-6]?");
                                                        input = asyncInput("timerPrompt");
                                                        try {
                                                            newValue = Integer.valueOf(input);
                                                            if (newValue == 0) throw new CancelException();
                                                            requiredData.get("data").getAsJsonObject().addProperty("newValue", newValue);
                                                        } catch (NumberFormatException e) {
                                                            Logger.println("Indice non valido\n");
                                                        }
                                                    } while (newValue < 1 || newValue > 6);
                                                }
                                                if (requiredData.get("data").getAsJsonObject().has(JsonFields.FROM_CELL_X)) {
                                                    do {
                                                        Logger.println("Da quale colonna vuoi muoverlo [1-5]?");
                                                        input = asyncInput("timerPrompt");
                                                        try {
                                                            fromCellX = Integer.valueOf(input) - 1;
                                                            if (fromCellX == -1) throw new CancelException();
                                                            requiredData.get("data").getAsJsonObject().addProperty("fromCellX", fromCellX);
                                                        } catch (NumberFormatException e) {
                                                            Logger.println("Indice non valido\n");
                                                        }
                                                    } while (fromCellX < 0 || fromCellX >= NUMBER_OF_PATTERN_COLUMNS);
                                                }
                                                if (requiredData.get("data").getAsJsonObject().has(JsonFields.FROM_CELL_Y)) {
                                                    do {
                                                        Logger.println("Da quale riga vuoi muoverlo [1-4]?");
                                                        input = asyncInput("timerPrompt");
                                                        try {
                                                            fromCellY = Integer.valueOf(input) - 1;
                                                            if (fromCellY == -1) throw new CancelException();
                                                            requiredData.get("data").getAsJsonObject().addProperty("fromCellY", fromCellY);
                                                        } catch (NumberFormatException e) {
                                                            Logger.println("Indice non valido\n");
                                                        }
                                                    } while (fromCellY < 0 || fromCellY >= NUMBER_OF_PATTERN_ROWS);
                                                }
                                                if (requiredData.get("data").getAsJsonObject().has(JsonFields.TO_CELL_X)) {
                                                    do {
                                                        Logger.println("In quale colonna vuoi piazzarlo [1-5]?");
                                                        input = asyncInput("timerPrompt");
                                                        try {
                                                            toCellX = Integer.valueOf(input) - 1;
                                                            if (toCellX == -1) throw new CancelException();
                                                            requiredData.get("data").getAsJsonObject().addProperty("toCellX", toCellX);
                                                        } catch (NumberFormatException e) {
                                                            Logger.println("Indice non valido\n");
                                                        }
                                                    } while (toCellX < 0 || toCellX >= NUMBER_OF_PATTERN_COLUMNS);
                                                }
                                                if (requiredData.get("data").getAsJsonObject().has(JsonFields.TO_CELL_Y)) {
                                                    do {
                                                        Logger.println("In quale riga vuoi piazzarlo [1-4]?");
                                                        input = asyncInput("timerPrompt");
                                                        try {
                                                            toCellY = Integer.valueOf(input) - 1;
                                                            if (toCellY == -1) throw new CancelException();
                                                            requiredData.get("data").getAsJsonObject().addProperty("toCellY", toCellY);
                                                        } catch (NumberFormatException e) {
                                                            Logger.println("Indice non valido\n");
                                                        }
                                                    } while (toCellY < 0 || toCellY >= NUMBER_OF_PATTERN_ROWS);
                                                }
                                                if (this.getNetwork().useToolCard(cardIndex, requiredData.get("data").getAsJsonObject())) {
                                                    if (requiredData.get("data").getAsJsonObject().has(JsonFields.CONTINUE)) {
                                                        toolCardAlreadyUsed = false;
                                                        showPrompt = false;
                                                    } else {
                                                        Logger.println("Carta strumento usata con successo\n");
                                                        toolCardAlreadyUsed = true;
                                                        showPrompt = true;
                                                    }
                                                    if (requiredData.get("data").getAsJsonObject().has(JsonFields.CONTINUE)) {
                                                        this.instructionIndex = 2;
                                                    } else {
                                                        this.instructionIndex = INDEX_CONSTANT;
                                                    }
                                                } else {
                                                    Logger.println("Carta strumento non usata");
                                                    this.instructionIndex = 2;
                                                    toolCardAlreadyUsed = false;
                                                    showPrompt = false;
                                                }
                                            } else {
                                                this.getNetwork().useToolCard(cardIndex, requiredData.get("data").getAsJsonObject());
                                                toolCardAlreadyUsed = true;
                                                showPrompt = true;
                                                instructionIndex = INDEX_CONSTANT;
                                            }
                                        }
                                    }
                                    continueIndex = INDEX_CONSTANT;
                                } else if (instructionIndex == 3) {
                                    this.setActive(false);
                                    this.getNetwork().nextTurn();
                                }
                            } catch (NumberFormatException e) {
                                if (isSuspended()) break;
                            } 
                        } catch (CancelException e) {
                            Logger.println("Mossa annullata.");
                            showPrompt = true;
                            instructionIndex = INDEX_CONSTANT;
                        }
                    } while (this.instructionIndex < 1 || this.instructionIndex > 3);
                } /*else {
                    Logger.println("\nLa partita è finita!");
                }*/
            }
        } catch (IOException | InterruptedException e) {
            Logger.println("");
            System.exit(Constants.EXIT_ERROR);
        } finally {
            Logger.println("");
            try {
                getNetwork().teardown();
                AnsiConsole.systemUninstall();
            } catch (IOException e) {
                Logger.error("Exception raised while tearing down");
                e.printStackTrace();
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
        String line = stdin.readLine();
        return line;
    }

    private String asyncInput(String methodName) throws IOException {
        stopAsyncInput = false;
        String bufferString = "";
        try {
            setTerminalToCBreak();
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
            e.printStackTrace();
        } finally {
            try {
                stty(ttyConfig.trim());
            }
            catch (Exception e) {
                Logger.error("Exception restoring tty config");
            }
        }
        if (!stopAsyncInput && !methodName.equals("timerPrompt")) {
            try {
                Method method = Class.forName(ClientCLI.class.getName()).getDeclaredMethod(methodName);
                Logger.print(method.invoke(this).toString());
            } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
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
    private void setTerminalToCBreak() throws IOException, InterruptedException {
        ttyConfig = stty("-g");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                stty(ttyConfig.trim());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }));
        stty("-icanon min 1");      // set the console to be character-buffered instead of line-buffered
        stty("-echo");              // disable character echoing
    }

    /**
     *  This method execute the stty command with the specified arguments
     *  against the current active terminal.
     */
    private static String stty(final String args) throws IOException, InterruptedException {
        String cmd = "stty " + args + " < /dev/tty";
        return exec(new String[] {"sh", "-c", cmd});
    }

    /**
     *  Execute the specified command and return the output
     *  (both stdout and stderr).
     */
    private static String exec(final String[] cmd) throws IOException, InterruptedException {
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
     * this method handles log in of a player
     *
     * @throws IOException socket error
     */
    private void addPlayer() throws IOException {
        String nickname = this.input("Nickname >>>");
        this.setNickname(nickname);
        setUUID(this.getNetwork().addPlayer(this.getNickname()));
        setLogged(this.getUUID() != null);
        //if (isLogged()) Logger.println("Login riuscito!");
        if (!isLogged()) {
            if(nickname.equals(""))
                Logger.println("Login fallito! I nickname non possono essere vuoti");
            else if(nickname.contains(" "))
                Logger.println("Login fallito! I nickname non possono contenere spazi");
            else if(nickname.length() > MAX_NICKNAME_LENGTH)
                Logger.println("Login fallito! I nickname non possono essere più lunghi di 20 caratteri");
        }
    }

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

    private static String windowPatternsMessage(List<String> patterns) {
        String message = concatWindowPatterns(patterns.get(0).split("\n"), patterns.get(1).split("\n"));
        if (patterns.size() == 3)
            message += "\n\n" + patterns.get(2);
        else if (patterns.size() == 4)
            message += "\n\n" + concatWindowPatterns(patterns.get(2).split("\n"), patterns.get(3).split("\n"));
        return message;
    }

    private String waitingRoomMessage() {
        synchronized (stdinBufferLock) {
            return ansi().eraseLine().cursorUpLine().eraseLine().cursorUpLine().eraseLine()
                    .a("Giocatori in attesa: ")
                    .a(this.wrPlayers)
                    .a("\n")
                    .a("Tempo rimasto: ")
                    .a(this.wrTimeout != null ? this.wrTimeout : "∞")
                    .a("\n")
                    .a(">>> ")
                    .a(stdinBuffer.toString()).toString();
        }
    }

    private String timerPrompt() {
        synchronized (stdinBufferLock) {
            String prompt = String.format("[%s] >>> %s", gameTimeout, stdinBuffer.toString());
            return updateLine(prompt);
        }
    }

    private String updateLine(String line) {
        return ansi().cursorToColumn(0)
                .eraseLine(Erase.FORWARD)
                .a(line)
                .toString();
    }

    private String reconnectionPrompt() {
        synchronized (stdinBufferLock) {
            String prompt = "Per riconnetterti, inserisci il tuo nickname >>> " + stdinBuffer.toString();
            return updateLine(prompt);
        }
    }

    // OBSERVER METHODS

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ClientNetwork && arg instanceof JsonObject) {
            JsonObject jsonArg = (JsonObject) arg;
            Methods method = Methods.getAsMethods(jsonArg.get(JsonFields.METHOD).getAsString());
            switch (method) {
                case ADD_PLAYER:
                    addPlayerUpdateHandle(jsonArg);
                    break;
                case UPDATE_WAITING_PLAYERS:
                    updateWaitingPlayersUpdateHandle(jsonArg);
                    break;
                case WR_TIMER_TICK:
                    this.wrTimeout = jsonArg.get(JsonFields.TICK).getAsString();
                    Logger.print(waitingRoomMessage());
                    break;
                case GAME_TIMER_TICK:
                    gameTimerTickUpdateHandle(jsonArg);
                    break;
                case GAME_SETUP:
                    privateObjectiveCardUpdateHandle(jsonArg);
                    selectableWindowPatternsUpdateHandle(jsonArg);
                    break;
                case WINDOW_PATTERNS:
                    windowPatternsUpdateHandle(jsonArg);
                    break;
                case TOOL_CARDS:
                    toolCardsUpdateHandle(jsonArg);
                    break;
                case PUBLIC_OBJECTIVE_CARDS:
                    publicObjectiveCardsUpdateHandle(jsonArg);
                    break;
                case DRAFT_POOL:
                    draftPoolUpdateHandle(jsonArg);
                    break;
                case TURN_MANAGEMENT:
                    turnManagementUpdateHandle(jsonArg);
                    break;
                case ROUND_TRACK:
                    String roundTrack = jsonArg.get(JsonFields.CLI_STRING).getAsString();
                    roundTrackLength = roundTrack.chars().filter(ch -> ch == ']').count();
                    Logger.println("Tracciato del Round\n" + roundTrack + "\n");
                    break;
                case PLAYERS:
                    Logger.print(ansi().eraseScreen().cursor(0, 0).toString());
                    break;
                case FAVOR_TOKENS:
                    favorTokensUpdateHandle(jsonArg);
                    break;
                case FINAL_SCORES:
                    finalScoresUpdateHandle(jsonArg);
                    break;
                default:
                    throw new IllegalStateException("This was not supposed to happen! " + method.toString());
            }
        }
    }

    private void addPlayerUpdateHandle(JsonObject jsonArg) {
        if (jsonArg.get(JsonFields.RECONNECTED).getAsBoolean()) {
            bypassWaitingRoom = true;
            stopAsyncInput = true;
            this.setPatternChosen(true);
            this.setSuspended(this.getSuspendedPlayers().stream()
                    .filter(s -> !s.equals(this.getNickname()))
                    .collect(Collectors.toList()));
        }
    }

    private void updateWaitingPlayersUpdateHandle(JsonObject jsonArg) {
        if (!this.isPatternChosen()) {
            JsonArray playersArray = jsonArg.get(JsonFields.PLAYERS).getAsJsonArray();
            this.wrPlayers = StreamSupport.stream(playersArray.spliterator(), false)
                    .map(JsonElement::getAsString)
                    .reduce((s, r) -> s + ", " + r)
                    .orElse(null);
            Logger.print(waitingRoomMessage());
        }
    }

    private void gameTimerTickUpdateHandle(JsonObject jsonArg) {
        this.gameTimeout = jsonArg.get(JsonFields.TICK).getAsString();
        if (isActive())
            Logger.print(timerPrompt());
        else {
            String timerString = "È il turno di " + this.getActiveNickname() + " [" + gameTimeout + "]";
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

    private void selectableWindowPatternsUpdateHandle(JsonObject jsonArg) {
        stopAsyncInput = true;
        JsonArray windowPatternsArray = jsonArg.getAsJsonArray(JsonFields.WINDOW_PATTERNS);
        List<String> windowPatterns = new ArrayList<>();
        for (int i = 0; i < windowPatternsArray.size(); i++) {
            JsonObject windowPattern = windowPatternsArray.get(i).getAsJsonObject();
            StringBuilder indexString = new StringBuilder();
            indexString.append(i+1);
            for (int k = 0; k < Constants.MAX_NICKNAME_LENGTH; k++) indexString.append(" ");
            windowPatterns.add(indexString.toString() + "\n" + windowPattern.get(JsonFields.CLI_STRING).getAsString());
        }
        Logger.println();
        Logger.println(windowPatternsMessage(windowPatterns));
    }

    private void windowPatternsUpdateHandle(JsonObject jsonArg) {
        JsonObject windowPatternsJson = jsonArg.getAsJsonObject(JsonFields.WINDOW_PATTERNS);
        List<String> windowPatterns = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : windowPatternsJson.entrySet()) {
            StringBuilder patternString = new StringBuilder(entry.getKey());
            while (patternString.length() < MAX_NICKNAME_LENGTH)
                patternString.append(' ');
            patternString.append('\n')
                    .append(entry.getValue().getAsJsonObject().get(JsonFields.CLI_STRING).getAsString());
            windowPatterns.add(patternString.toString());
        }
        Logger.println(windowPatternsMessage(windowPatterns));
    }

    private void toolCardsUpdateHandle(JsonObject jsonArg) {
        JsonArray toolCards = jsonArg.getAsJsonArray(JsonFields.TOOL_CARDS);
        StringBuilder toolCardsString = new StringBuilder();
        for (int i = 0; i < toolCards.size(); i++) {
            JsonObject toolCard = toolCards.get(i).getAsJsonObject();
            toolCardsString
                    .append("Carta strumento ")
                    .append(i)
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

    private void publicObjectiveCardsUpdateHandle(JsonObject jsonArg) {
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
                    .append(obj.get(JsonFields.VICTORY_POINTS).isJsonNull() ? '#' : obj.get(JsonFields.VICTORY_POINTS).getAsInt())
                    .append("\n\n");
        }
        objectiveCardsString.append(privateObjectiveCard).append("\n\n");
        Logger.println(objectiveCardsString.toString());
    }

    private void privateObjectiveCardUpdateHandle(JsonObject jsonArg) {
        Logger.print(ansi().eraseScreen().cursor(0, 0).toString());
        JsonObject cardJson = jsonArg.getAsJsonObject(JsonFields.PRIVATE_OBJECTIVE_CARD);
        privateObjectiveCard = "Obiettivo privato: " +
                cardJson.get(JsonFields.NAME).getAsString() +
                " - " +
                cardJson.get(JsonFields.DESCRIPTION).getAsString();
        Logger.println(privateObjectiveCard);
    }

    private void draftPoolUpdateHandle(JsonObject jsonArg) {
        JsonArray draftPoolDice = jsonArg.getAsJsonArray(JsonFields.DICE);
        StringBuilder draftPoolString = new StringBuilder("Riserva: ");
        this.draftPoolLength = 0;
        for (JsonElement element : draftPoolDice){
            draftPoolString.append(element.getAsJsonObject().get(JsonFields.CLI_STRING).getAsString());
            this.draftPoolLength++;
        }
        Logger.println("\n" + draftPoolString.toString() + "\n");
    }

    private void turnManagementUpdateHandle(JsonObject jsonArg) {
        if (!this.isGameStarted()) this.setGameStarted(true);
        this.setGameOver(jsonArg.get(JsonFields.GAME_OVER).getAsBoolean());
        List<String> suspendedPlayers = StreamSupport.stream(jsonArg.getAsJsonArray(JsonFields.SUSPENDED_PLAYERS).spliterator(), false)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
                    /*if (suspendedPlayers.size() == 1 && suspendedPlayers.get(0).equals(""))
                        suspendedPlayers = new ArrayList<>();*/
        this.setSuspended(suspendedPlayers);
        this.setActive(jsonArg.get(JsonFields.ACTIVE_PLAYER).getAsString());
        if (!isActive() && !isSuspended() && !isGameOver()) Logger.println("Aspetta il tuo turno.");
        //Logger.println("Suspended: " + argAsList.get(3));
        stopAsyncInput = true;
    }

    private void favorTokensUpdateHandle(JsonObject jsonArg) {
        StringBuilder favorTokenString = new StringBuilder("\nSegnalini Favore");
        Set<Map.Entry<String, JsonElement>> entrySet = jsonArg.get(JsonFields.FAVOR_TOKENS).getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            if (entry.getKey().equals(this.getNickname()))
                this.setFavorTokens(entry.getValue().getAsInt());
            favorTokenString.append("\n").append(entry.getKey()).append(": ").append(entry.getValue().getAsInt());
        }
        Logger.println(favorTokenString.toString());
    }

    private void finalScoresUpdateHandle(JsonObject jsonArg) {
        StringBuilder finalScoresString = new StringBuilder("\nLa partita è finita!\nRisultati finali");
        Set<Map.Entry<String, JsonElement>> entrySet = jsonArg.get(JsonFields.FINAL_SCORES).getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet)
            finalScoresString.append("\n").append(entry.getKey()).append(": ").append(entry.getValue().getAsInt());
        Logger.println(finalScoresString.toString());
        Logger.println("");
        AnsiConsole.systemUninstall();
        System.exit(Constants.EXIT_STATUS);
    }

}
