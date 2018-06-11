package it.polimi.ingsw.client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.util.*;
import org.fusesource.jansi.AnsiConsole;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import static it.polimi.ingsw.util.Constants.*;
import static org.fusesource.jansi.Ansi.*;


public class ClientCLI extends Client {

    private String ttyConfig;

    private BufferedReader stdin;
    private StringBuilder stdinBuffer = new StringBuilder();
    private final Object stdinBufferLock = new Object();

    private boolean stopAsyncInput = false;
    private boolean showPrompt = true;
    private int instructionIndex = INDEX_CONSTANT;

    private String wrTimeout;
    private String wrPlayers;
    private String gameTimeout = "00";
    private String lastPrintedLine = "";
    int cardIndex = INDEX_CONSTANT;

    private int draftPoolLength;
    private long roundTrackLength = 0;
    private String privateObjectiveCard;

    ClientCLI(ClientNetwork network, boolean debugActive) {
        super(network, debugActive);
    }

    void start() {
        AnsiConsole.systemInstall();
        System.out.print(ansi().eraseScreen().cursor(0, 0));
        try {
            this.stdin = new BufferedReader(new InputStreamReader(System.in));
            do addPlayer(); while (!this.isLogged());
            String input;
            do {
                input = asyncInput("waitingRoomMessage");
                if (input.equalsIgnoreCase("exit")) throw new InterruptedException();
            } while (!stopAsyncInput);
            Integer patternIndex = INDEX_CONSTANT;
            log("");
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
            log("Hai scelto il pattern numero " + patternIndex + ".\nPer favore attendi che tutti i giocatori facciano la propria scelta.\n");
            while (!this.isGameStarted()) Thread.sleep(10);
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

                while (!this.isActive()) Thread.sleep(10);

                log("È il tuo turno!");
                do {
                    try {
                        if (showPrompt) {
                            cardIndex = INDEX_CONSTANT;
                            log("Premi 1 per piazzare un dado\nPremi 2 per usare una carta strumento\nPremi 3 per " +
                                    "passare il turno.");
                            System.out.println("Scegli cosa fare [1-3]");
                            input = asyncInput("timerPrompt");
                        }
                        try {
                            if (showPrompt) this.instructionIndex = Integer.valueOf(input);
                            if (instructionIndex == 1) {
                                if (dieAlreadyPlaced) {
                                    log("Hai già piazzato un dado questo turno!");
                                    this.instructionIndex = INDEX_CONSTANT;
                                } else {
                                    do {
                                        draftPoolIndex = draftPoolLength;
                                        System.out.println("Quale dado vuoi piazzare [1-" + draftPoolLength + "]? " + EXIT_MESSAGE);
                                        input = asyncInput("timerPrompt");
                                        try {
                                            draftPoolIndex = Integer.valueOf(input) - 1;
                                            if (draftPoolIndex == -1) throw new CancelException();
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (draftPoolIndex < 0 || draftPoolIndex >= draftPoolLength);
                                    do {
                                        System.out.println("In quale colonna vuoi piazzarlo [1-5]? " + EXIT_MESSAGE);
                                        input = asyncInput("timerPrompt");
                                        try {
                                            x = Integer.valueOf(input) - 1;
                                            if (x == -1) throw new CancelException();
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (x < 0 || x >= NUMBER_OF_PATTERN_COLUMNS);
                                    do {
                                        System.out.println("In quale riga vuoi piazzarlo [1-4]? " + EXIT_MESSAGE);
                                        input = asyncInput("timerPrompt");
                                        try {
                                            y = Integer.valueOf(input) - 1;
                                            if (y == -1) throw new CancelException();
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (y < 0 || y >= NUMBER_OF_PATTERN_ROWS);
                                    if (this.getNetwork().placeDie(draftPoolIndex, x, y)) {
                                        log("Dado piazzato\n");
                                        dieAlreadyPlaced = true;
                                    } else {
                                        log("Posizionamento invalido\n");
                                    }
                                    this.instructionIndex = INDEX_CONSTANT;
                                }
                            } else if (instructionIndex == 2) {  //Tool card
                                if (toolCardAlreadyUsed) {
                                    log("Hai già usato una carta strumento questo turno!\n");
                                    this.instructionIndex = INDEX_CONSTANT;
                                }
                                if (showPrompt) {
                                    do {
                                        System.out.println("Quale carta strumento vuoi usare [1-3]? ");
                                        input = asyncInput("timerPrompt");
                                        try {
                                            cardIndex = Integer.valueOf(input) - 1;
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (cardIndex < 0 || cardIndex >= 3);
                                }
                                JsonObject requiredData = this.getNetwork().requiredData(cardIndex); //request for the data required by the tool card
                                requiredData.remove(JsonFields.METHOD);
                                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD)) {
                                    log("Non puoi utilizzare questa carta strumento: " + requiredData.get(JsonFields.IMPOSSIBLE_TO_USE_TOOL_CARD).getAsString());
                                } else {
                                    if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.DRAFT_POOL_INDEX)) { //if the tool card requires a draftpool die
                                        do {
                                            System.out.println("Quale dado della riserva vuoi utilizzare [1-" + draftPoolLength + "]? ");
                                            input = asyncInput("timerPrompt");
                                            try {
                                                draftPoolIndex = Integer.valueOf(input) - 1;
                                                requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.DRAFT_POOL_INDEX, draftPoolIndex);
                                            } catch (NumberFormatException e) {
                                                log("Indice non valido\n");
                                            }
                                        } while (draftPoolIndex < 0 || draftPoolIndex >= draftPoolLength);
                                    }
                                    if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.ROUND_TRACK_INDEX)) { //if the tool card requires a round track die

                                        do {
                                            System.out.println("Quale dado del round track vuoi utilizzare [1-" + roundTrackLength + "]? ");
                                            input = asyncInput("timerPrompt");
                                            try {
                                                roundTrackIndex = Integer.valueOf(input) - 1;
                                                requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.ROUND_TRACK_INDEX, roundTrackIndex);
                                            } catch (NumberFormatException e) {
                                                log("Indice non valido\n");
                                            }
                                        } while (roundTrackIndex < 0 || roundTrackIndex >= roundTrackLength);
                                    }
                                    if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.DELTA)) { //if the tool card requires a change in the die value
                                        do {
                                            System.out.println("Vuoi aumentare [1] o diminuire [0] il valore del dado? ");
                                            input = asyncInput("timerPrompt");
                                            try {
                                                delta = Integer.valueOf(input);
                                                if (delta == 0) delta = -1;
                                                requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.DELTA, delta);
                                            } catch (NumberFormatException e) {
                                                log("Indice non valido\n");
                                            }
                                        } while (!(delta == 1 || delta == -1));
                                    }
                                    if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.NEW_VALUE)) { //if the tool card requires a change in the die value
                                        do {
                                            System.out.println("Quale valore vuoi assegnare al dado [1-6]? ");
                                            input = asyncInput("timerPrompt");
                                            try {
                                                newValue = Integer.valueOf(input);
                                                requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.NEW_VALUE, newValue);
                                            } catch (NumberFormatException e) {
                                                log("Indice non valido\n");
                                            }
                                        } while (newValue < 1 || newValue > 6);
                                    }
                                    if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.FROM_CELL_X)) { //if the tool card requires a change in the die value
                                        do {
                                            System.out.println("Da quale colonna vuoi muoverlo [1-5]? ");
                                            input = asyncInput("timerPrompt");
                                            try {
                                                fromCellX = Integer.valueOf(input) - 1;
                                                requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.FROM_CELL_X, fromCellX);
                                            } catch (NumberFormatException e) {
                                                log("Indice non valido\n");
                                            }
                                        } while (fromCellX < 0 || fromCellX >= NUMBER_OF_PATTERN_COLUMNS);
                                    }
                                    if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.FROM_CELL_Y)) { //if the tool card requires a change in the die value
                                        do {
                                            System.out.println("Da quale riga vuoi muoverlo [1-4]? ");
                                            input = asyncInput("timerPrompt");
                                            try {
                                                fromCellY = Integer.valueOf(input) - 1;
                                                requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.FROM_CELL_Y, fromCellY);
                                            } catch (NumberFormatException e) {
                                                log("Indice non valido\n");
                                            }
                                        } while (fromCellY < 0 || fromCellY >= NUMBER_OF_PATTERN_ROWS);
                                    }
                                    if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.TO_CELL_X)) { //if the tool card requires a change in the die value
                                        do {
                                            System.out.println("In quale colonna vuoi piazzarlo [1-5]? ");
                                            input = asyncInput("timerPrompt");
                                            try {
                                                toCellX = Integer.valueOf(input) - 1;
                                                requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.TO_CELL_X, toCellX);
                                            } catch (NumberFormatException e) {
                                                log("Indice non valido\n");
                                            }
                                        } while (toCellX < 0 || toCellX >= NUMBER_OF_PATTERN_COLUMNS);
                                    }
                                    if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.TO_CELL_Y)) { //if the tool card requires a change in the die value
                                        do {
                                            System.out.println("In quale riga vuoi piazzarlo [1-4]?");
                                            input = asyncInput("timerPrompt");
                                            try {
                                                toCellY = Integer.valueOf(input) - 1;
                                                requiredData.get(JsonFields.DATA).getAsJsonObject().addProperty(JsonFields.TO_CELL_Y, toCellY);
                                            } catch (NumberFormatException e) {
                                                log("Indice non valido\n");
                                            }
                                        } while (toCellY < 0 || toCellY >= NUMBER_OF_PATTERN_ROWS);
                                    }

                                    if (this.getNetwork().useToolCard(cardIndex, requiredData.get(JsonFields.DATA).getAsJsonObject())) {
                                        if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.CONTINUE)) {
                                            toolCardAlreadyUsed = false;
                                            showPrompt = false;
                                        } else {
                                            log("Carta strumento usata con successo\n");
                                            toolCardAlreadyUsed = true;
                                            showPrompt = true;
                                        }
                                    } else {
                                        log("Carta strumento non usata");
                                    }
                                }
                                if (requiredData.get(JsonFields.DATA).getAsJsonObject().has(JsonFields.CONTINUE)) {
                                    this.instructionIndex = 2;
                                } else {
                                    this.instructionIndex = INDEX_CONSTANT;
                                }
                            } else if (instructionIndex == 3) {
                                this.setActive(false);
                                this.getNetwork().nextTurn();
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } catch (CancelException e){
                        log("Mossa annullata.");
                    }
                } while (this.instructionIndex < 1 || this.instructionIndex > 3);
            }
        } catch (IOException | InterruptedException e) {
            log("Quitting...");
        } finally {
            log("");
            try {
                getNetwork().teardown();
                AnsiConsole.systemUninstall();
            } catch (IOException e) {
                error("Exception raised while tearing down");
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
        System.out.print(prompt + " ");
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
            System.out.print(method.invoke(this));
            while (!stopAsyncInput) {
                if (System.in.available() != 0) {
                    int c = System.in.read();
                    if (c == 0x0A) {
                        synchronized (stdinBufferLock) {
                            bufferString = stdinBuffer.toString();
                            stdinBuffer = new StringBuilder();
                        }
                        lastPrintedLine = "";
                        /*Method method = Class.forName(ClientCLI.class.getName()).getDeclaredMethod(methodName);
                        System.out.print(method.invoke(this));*/
                        System.out.println();
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
                    System.out.print(method.invoke(this));
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
                error("Exception restoring tty config");
            }
        }
        if (!stopAsyncInput && !methodName.equals("timerPrompt")) {
            try {
                Method method = Class.forName(ClientCLI.class.getName()).getDeclaredMethod(methodName);
                System.out.print(method.invoke(this));
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
        if (isLogged()) log("Login riuscito!");
        else {
            if(nickname.equals(""))
                log("Login fallito! I nickname non possono essere vuoti");
            else if(nickname.contains(" "))
                log("Login fallito! I nickname non possono contenere spazi");
            else if(nickname.length() > MAX_NICKNAME_LENGTH)
                log("Login fallito! I nickname non possono essere più lunghi di 20 caratteri");
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
        String newLine = ansi().cursorLeft(lastPrintedLine.length())
                .eraseLine(Erase.FORWARD)
                .a(line)
                .toString();
        lastPrintedLine = line;
        return newLine;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ClientNetwork) {
            if (arg instanceof List) {      // Window Patterns
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<String> argAsList = (List) arg;
                if (argAsList.get(0).equals(NotificationsMessages.WR_TIMER_TICK)) {
                    this.wrTimeout = argAsList.get(1);
                    System.out.print(waitingRoomMessage());
                } else if (argAsList.get(0).equals(NotificationsMessages.GAME_TIMER_TICK)) {
                    this.gameTimeout = argAsList.get(1);
                    if (isActive())
                        System.out.print(timerPrompt());
                    else
                        System.out.print(updateLine("[" + gameTimeout + "]"));
                } else if (argAsList.get(0).startsWith(NotificationsMessages.SELECTABLE_WINDOW_PATTERNS)) {
                    stopAsyncInput = true;
                    argAsList.remove(0);
                    for (int i = 0; i < argAsList.size(); i++) {
                        StringBuilder newWPString = new StringBuilder();
                        newWPString.append(i+1);
                        for (int k = 1; k < Constants.MAX_NICKNAME_LENGTH; k++) newWPString.append(" ");
                        argAsList.set(i, newWPString.toString() + "\n" + argAsList.get(i));
                    }
                    System.out.println();
                    log(windowPatternsMessage(argAsList));
                } else if (argAsList.get(0).equals(NotificationsMessages.UPDATE_WINDOW_PATTERNS)){
                    argAsList.remove(0);
                    List<String> patterns = new ArrayList<>();
                    for (String s: argAsList){
                        String spaces = "";
                        for(int i = 0; i <= MAX_NICKNAME_LENGTH - s.substring(0,s.indexOf("$")).length(); i++)
                            spaces += " ";
                        spaces += "\n";
                        s = s.replace("$",spaces);
                        patterns.add(s);
                    }
                    String prettyWindowPatterns = windowPatternsMessage(patterns);
                    log(prettyWindowPatterns);
                } else if (argAsList.get(0).startsWith(NotificationsMessages.TOOL_CARDS)) {   //Tool card
                    String cards = "";
                    for (String s : argAsList) {
                        int index = argAsList.indexOf(s) + 1;
                        s = s.replace("\n",". ");
                        s = s.replace(NotificationsMessages.TOOL_CARDS, "Carta Strumento " + index + ": ");
                        s = s.replace(" - ", "\nEffetto: ");
                        s += "$NL$";
                        cards += s;
                    }
                    cards = cards.replace("$NL$","\n\n");
                    log(cards);
                } else if (argAsList.get(0).startsWith(NotificationsMessages.PUBLIC_OBJECTIVE_CARDS)) {   //Public Objective Card
                    StringBuilder cards = new StringBuilder();
                    for (String s : argAsList) {
                        s = s.replace(NotificationsMessages.PUBLIC_OBJECTIVE_CARDS, "Obiettivo Pubblico: ");
                        s = s.replace(" $- ", "\nDescrizione: ");
                        s = s.replace(" $$- ", "\nPunti Vittoria (PV) per ogni set completo di questo tipo: ");
                        s += "\n\n";
                        cards.append(s);
                    }
                    cards.append(privateObjectiveCard).append("\n\n");
                    log(cards.toString());
                } else if (argAsList.get(0).startsWith(NotificationsMessages.DRAFT_POOL)) {   //DraftPool
                    String draftPool = "Riserva: ";
                    this.draftPoolLength = 0;
                    for (String s : argAsList){
                        s = s.replace(NotificationsMessages.DRAFT_POOL,"");
                        draftPool += s;
                        draftPoolLength++;
                    }
                    log("\n" + draftPool + "\n");
                } else if (argAsList.get(0).equals(NotificationsMessages.TURN_MANAGEMENT)) {
                    argAsList.remove(0);
                    if(!this.isGameStarted()) this.setGameStarted(true);
                    this.setRound(Integer.valueOf(argAsList.get(0)));
                    this.setGameOver(Boolean.valueOf(argAsList.get(1)));
                    this.setActive(argAsList.get(2));
                    stopAsyncInput = true;
                }
            } else if (arg instanceof String) {
                String input = (String) arg;
                if (input.startsWith(NotificationsMessages.PRIVATE_OBJECTIVE_CARD)) {
                    System.out.print(ansi().eraseScreen().cursor(0, 0).toString());
                    input = input.replace(NotificationsMessages.PRIVATE_OBJECTIVE_CARD, "");
                    privateObjectiveCard = input;
                    log(privateObjectiveCard);
                }
                if (input.startsWith(NotificationsMessages.ROUND_TRACK)) {
                    input = input.replace(NotificationsMessages.ROUND_TRACK,"");
                    roundTrackLength = input.chars().filter(ch -> ch == ']').count();
                    log("Tracciato del Round\n" + input + "\n");
                }
            } else if (arg instanceof Iterable) {   // Players
                if (!this.isPatternChosen()) {  //Players in waiting room
                    this.wrPlayers = arg.toString().replace("[", "")
                            .replace("]", "")
                            .replace("\",", ",")
                            .replace("\"", " ");
                    System.out.print(waitingRoomMessage());
                } else {
                    System.out.print(ansi().eraseScreen().cursor(0, 0));
                }
                /*else{
                    this.gamePlayers = arg.toString().replace("[", "")
                            .replace("]", "")
                            .replace("\",", ",")
                            .replace("\"", " ");
                }*/
            }
        }
    }

}
