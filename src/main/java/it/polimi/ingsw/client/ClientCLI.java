package it.polimi.ingsw.client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.util.Constants;
import org.fusesource.jansi.AnsiConsole;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import static org.fusesource.jansi.Ansi.*;


public class ClientCLI extends Client {

    private String ttyConfig;

    private BufferedReader stdin;
    private StringBuilder stdinBuffer = new StringBuilder();
    private final Object stdinBufferLock = new Object();

    private boolean stopAsyncInput = false;
    private boolean gameStarted = false;
    private boolean active = false;
    private boolean patternChosen = false;
    private boolean gameOver = false;
    private int instructionIndex= Constants.INDEX_CONSTANT;
    private int round = 1;

    private String wrTimeout;
    private String wrPlayers;

    private String gamePlayers;
    private int draftPoolLength;
    private long roundTrackLength = 0;

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
            Integer patternIndex = Constants.INDEX_CONSTANT;
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
            patternChosen = true;
            log("Hai scelto il pattern numero " + patternIndex + ".\nPer favore attendi che tutti i giocatori facciano la propria scelta.\n");
            while (!gameStarted) Thread.sleep(10);
            while (!gameOver) {
                boolean dieAlreadyPlaced = false;
                boolean toolCardAlreadyUsed = false;
                int draftPoolIndex = Constants.INDEX_CONSTANT;
                int x = Constants.INDEX_CONSTANT;
                int y = Constants.INDEX_CONSTANT;

                int cardIndex = Constants.INDEX_CONSTANT;
                int roundTrackIndex = Constants.INDEX_CONSTANT;
                int delta = Constants.INDEX_CONSTANT;
                int newValue = Constants.INDEX_CONSTANT;
                int fromCellX = Constants.INDEX_CONSTANT;
                int fromCellY = Constants.INDEX_CONSTANT;
                int toCellX = Constants.INDEX_CONSTANT;
                int toCellY = Constants.INDEX_CONSTANT;

                if(!active){
                    log("Aspetta il tuo turno.");
                    while (!active) Thread.sleep(10);
                }

                log("Round " + round + "\nÈ il tuo turno!");
                do {
                    log("Premi 1 per piazzare un dado\nPremi 2 per usare una carta strumento\nPremi 3 per " +
                                "passare il turno");
                    input = input("Scegli cosa fare [1-3] >>>");
                    try {
                        this.instructionIndex = Integer.valueOf(input);
                        if(instructionIndex == 1){
                            if(dieAlreadyPlaced){
                                log("Hai già piazzato un dado questo turno!");
                                this.instructionIndex = Constants.INDEX_CONSTANT;
                            }
                            else{
                                do {
                                    draftPoolIndex = draftPoolLength;
                                    input = input("Quale dado vuoi piazzare [1-" + draftPoolLength + "]?");
                                    try {
                                        draftPoolIndex = Integer.valueOf(input) - 1;
                                    } catch (NumberFormatException e) {
                                        log("Indice non valido\n");
                                    }
                                } while (draftPoolIndex < 0 || draftPoolIndex >= draftPoolLength);
                                do {
                                    input = input("In quale colonna vuoi piazzarlo [1-5]?");
                                    try {
                                        x = Integer.valueOf(input) - 1;
                                    } catch (NumberFormatException e) {
                                        log("Indice non valido\n");
                                    }
                                } while (x < 0 || x >= Constants.NUMBER_OF_PATTERN_COLUMNS);
                                do {
                                    input = input("In quale riga vuoi piazzarlo [1-4]?");
                                    try {
                                        y = Integer.valueOf(input) - 1;
                                    } catch (NumberFormatException e) {
                                        log("Indice non valido\n");
                                    }
                                } while (y < 0 || y >= Constants.NUMBER_OF_PATTERN_ROWS);
                                if(this.getNetwork().placeDie(draftPoolIndex,x,y)){
                                    log("Dado piazzato\n");
                                    dieAlreadyPlaced = true;
                                }
                                else{
                                 log("Posizionamento invalido\n");
                                }
                                this.instructionIndex = Constants.INDEX_CONSTANT;
                            }
                        }
                        else if(instructionIndex == 2) {  //Tool card
                            if (toolCardAlreadyUsed) {
                                log("Hai già usato una carta strumento questo turno!\n");
                                this.instructionIndex = Constants.INDEX_CONSTANT;
                            }
                            do {
                                input = input("Quale carta strumento vuoi usare [1-3]?");
                                try {
                                    cardIndex = Integer.valueOf(input) - 1;
                                } catch (NumberFormatException e) {
                                    log("Indice non valido\n");
                                    continue;
                                }
                            } while (cardIndex < 0 || cardIndex >= 3);
                            JsonObject requiredData = this.getNetwork().requiredData(cardIndex); //request for the data required by the tool card
                                if(requiredData.has("draftPoolIndex")) { //if the tool card requires a draftpool die
                                    do {
                                        input = input("Quale dado della riserva vuoi utilizzare [1-" + draftPoolLength + "]?");
                                        try {
                                            draftPoolIndex = Integer.valueOf(input) - 1;
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (draftPoolIndex < 0 || draftPoolIndex >= draftPoolLength);
                                }
                                if(requiredData.has("roundTrackIndex")) { //if the tool card requires a round track die
                                    do {
                                        input = input("Quale dado del round track vuoi utilizzare [1-" + roundTrackLength + "]?");
                                        try {
                                            roundTrackIndex = Integer.valueOf(input) - 1;
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (roundTrackIndex < 0 || roundTrackIndex >= roundTrackLength);
                                }
                                if(requiredData.has("delta")) { //if the tool card requires a change in the die value
                                    do {
                                        input = input("Vuoi aunmentare o diminuire il valore del dado?");
                                        try {
                                            delta = Integer.valueOf(input);
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (delta != 1 || delta != -1);
                                }
                                if(requiredData.has("newValue")) { //if the tool card requires a change in the die value
                                    do {
                                        input = input("Quale valore vuoi assegnare al dado?");
                                        try {
                                            newValue = Integer.valueOf(input);
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (newValue < 1 || newValue > 6);
                                }
                                if(requiredData.has("fromCellX")) { //if the tool card requires a change in the die value
                                    do {
                                        input = input("Da quale colonna vuoi muoverlo [1-5]?");
                                        try {
                                            fromCellX = Integer.valueOf(input) - 1;
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (fromCellX < 0 || fromCellX >= Constants.NUMBER_OF_PATTERN_COLUMNS);
                                }
                                if(requiredData.has("fromCellY")) { //if the tool card requires a change in the die value
                                    do {
                                        input = input("Da quale riga vuoi muoverlo [1-4]?");
                                        try {
                                            fromCellY = Integer.valueOf(input) - 1;
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (fromCellY < 0 || fromCellY >= Constants.NUMBER_OF_PATTERN_ROWS);
                                }
                                if(requiredData.has("toCellX")) { //if the tool card requires a change in the die value
                                    do {
                                        input = input("In quale colonna vuoi piazzarlo [1-5]?");
                                        try {
                                            toCellX= Integer.valueOf(input) - 1;
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (toCellX < 0 || toCellX >= Constants.NUMBER_OF_PATTERN_COLUMNS);
                                }
                                if(requiredData.has("toCellY")) { //if the tool card requires a change in the die value
                                    do {
                                        input = input("In quale riga vuoi piazzarlo [1-4]?");
                                        try {
                                            toCellY = Integer.valueOf(input) - 1;
                                        } catch (NumberFormatException e) {
                                            log("Indice non valido\n");
                                        }
                                    } while (toCellY < 0 || toCellY >= Constants.NUMBER_OF_PATTERN_ROWS);
                                }
                            log("USE TOOLCARD\n");
                                toolCardAlreadyUsed = true;
                                this.instructionIndex = Constants.INDEX_CONSTANT;
                        }
                        else if(instructionIndex == 3){
                            this.round = this.getNetwork().nextTurn();
                            if(this.round > Constants.NUMBER_OF_ROUNDS)
                                this.gameOver = true;
                        }
                    } catch (NumberFormatException e) {
                        continue;
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
            while (!stopAsyncInput) {
                if (System.in.available() != 0) {
                    int c = System.in.read();
                    if (c == 0x0A) {
                        synchronized (stdinBufferLock) {
                            bufferString = stdinBuffer.toString();
                            stdinBuffer = new StringBuilder();
                        }
                        Method method = Class.forName(ClientCLI.class.getName()).getDeclaredMethod(methodName);
                        System.out.print(method.invoke(this));
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
                    Method method = Class.forName(ClientCLI.class.getName()).getDeclaredMethod(methodName);
                    System.out.print(method.invoke(this));
                }
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
        if (!stopAsyncInput) {
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
        else{
            if(nickname.equals(""))
                log("Login fallito! I nickname non possono essere vuoti");
            else if(nickname.contains(" "))
                log("Login fallito! I nickname non possono contenere spazi");
            else if(nickname.length() > Constants.MAX_NICKNAME_LENGTH)
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
                if(argAsList.get(0).startsWith("---------------------")) { //Window pattern
                    for (int i = 0; i < argAsList.size(); i++) {
                        String newWPString = "";
                        for (int k = 0; k < Constants.MAX_NICKNAME_LENGTH; k++) newWPString += " ";
                        newWPString = (i + 1) + newWPString;
                        argAsList.set(i, newWPString + "\n" + argAsList.get(i));
                    }
                    log(windowPatternsMessage(argAsList));
                    stopAsyncInput = true;
                }
                if(argAsList.get(0).equals("$$$updatedWindowPatterns")){
                    argAsList.remove(0);
                    List<String> patterns = new ArrayList<>();
                    for (String s: argAsList){
                        String spaces = "";
                        for(int i = 0; i <= Constants.MAX_NICKNAME_LENGTH - s.substring(0,s.indexOf("$")).length(); i++)
                            spaces += " ";
                        spaces += "\n";
                        s = s.replace("$",spaces);
                        patterns.add(s);
                    }
                    String prettyWindowPatterns = windowPatternsMessage(patterns);
                    log(prettyWindowPatterns);
                    stopAsyncInput = true;
                }

                if(argAsList.get(0).startsWith("ToolCard$")) {   //Tool card
                    String cards = "";
                    for (String s : argAsList) {
                        s = s.replace("\n",". ");
                        s = s.replace("ToolCard$", "Carta Strumento: ");
                        s = s.replace(" - ", "\nEffetto: ");
                        s += "$NL$";
                        cards = cards + s;
                    }
                    cards = cards.replace("$NL$","\n\n");
                    log(cards);
                    stopAsyncInput = true;
                }

                if(argAsList.get(0).startsWith("PublicObjectiveCards$")) {   //Public Objective Card
                    String cards = "";
                    for (String s : argAsList) {
                        s = s.replace("PublicObjectiveCards$", "Obiettivo Pubblico: ");
                        s = s.replace(" $- ", "\nDescrizione: ");
                        s = s.replace(" $$- ", "\nPunti Vittoria (PV) per ogni set completo di questo tipo: ");
                        s += "\n\n";
                        cards = cards + s;
                        }
                    log(cards);
                    stopAsyncInput = true;
                }

                if(argAsList.get(0).startsWith("$draftPool$")) {   //DraftPool
                    String draftPool = "Riserva: ";
                    this.draftPoolLength = 0;
                    for (String s : argAsList){
                        s = s.replace("$draftPool$","");
                        draftPool += s;
                        draftPoolLength++;
                    }
                    log(draftPool + "\n");
                    stopAsyncInput = true;
                }

            } else if (arg instanceof Integer) {    // Timer ticks
                this.wrTimeout = arg.toString();
                System.out.print(waitingRoomMessage());
            } else if (arg instanceof String) {
                String input = (String) arg;
                if (input.startsWith("PrivateObjectiveCard$")) {
                    input = input.replace("PrivateObjectiveCard$", "");
                    log(input);
                }
                if (input.startsWith("$roundTrack$")) {
                    input = input.replace("$roundTrack$","");
                    roundTrackLength = input.chars().filter(ch -> ch == ']').count();
                    log("ROUND TRACK\n" + input + "\n");
                }
            } else if (arg instanceof Iterable) {   // Players
                if(patternChosen == false) {  //Players in waiting room
                    this.wrPlayers = arg.toString().replace("[", "")
                            .replace("]", "")
                            .replace("\",", ",")
                            .replace("\"", " ");
                    System.out.print(waitingRoomMessage());
                }
                else{
                    this.gamePlayers = arg.toString().replace("[", "")
                            .replace("]", "")
                            .replace("\",", ",")
                            .replace("\"", " ");
                   stopAsyncInput = true;
                }
            } else if (arg instanceof Boolean) {
                active = (boolean) arg;
                gameStarted = true;
            }
        }
    }

}
