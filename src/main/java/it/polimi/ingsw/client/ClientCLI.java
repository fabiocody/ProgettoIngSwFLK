package it.polimi.ingsw.client;

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
    private boolean printing = false;
    private int instructionIndex= 42;

    private String wrTimeout;
    private String wrPlayers;

    private String gamePlayers;
    private int draftPoolLength;

    private final Object printLock = new Object();

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
            Integer patternIndex = 42;
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

            for (int turns = 0; turns < 20; turns++) {
                boolean alreadyPlacedDie = false;
                boolean alreadyUsedToolCard = false;
                int draftPoolIndex = 42;
                int x = 42;
                int y = 42;
                if(!active){
                    log("Aspetta il tuo turno.");
                    while (!active) Thread.sleep(10);
                }
                log("È il tuo turno!");
                do {
                    while(printing) Thread.sleep(10);
                    log("Premi 1 per piazzare un dado\nPremi 2 per usare una carta strumento\nPremi 3 per " +
                                "passare il turno");
                    input = input("Scegli cosa fare[1-3] >>>");
                    stopAsyncInput = true;
                    try {
                        this.instructionIndex = Integer.valueOf(input);
                        if(instructionIndex == 1){
                            if(alreadyPlacedDie){
                                log("Hai già piazzato un dado questo turno!");
                                this.instructionIndex = 42;
                            }
                            else{
                                do {
                                    input = input("Quale dado vuoi piazzare[1-" + draftPoolLength + "]? >>>");
                                    try {
                                        draftPoolIndex = Integer.valueOf(input) - 1;
                                    } catch (NumberFormatException e) {
                                        continue;
                                    }
                                } while (draftPoolIndex < 0 || patternIndex > draftPoolLength);
                                do {
                                    input = input("In quale colonna vuoi piazzarlo[1-5]? >>>");
                                    try {
                                        x = Integer.valueOf(input) - 1;
                                    } catch (NumberFormatException e) {
                                        continue;
                                    }
                                } while (x < 0 || x > 4);
                                do {
                                    input = input("In quale riga vuoi piazzarlo[1-4]? >>>");
                                    try {
                                        y = Integer.valueOf(input) - 1;
                                    } catch (NumberFormatException e) {
                                        continue;
                                    }
                                } while (y < 0 || y > 4);
                                if(this.getNetwork().placeDie(draftPoolIndex,x,y)){
                                    log("Dado piazzato");
                                    alreadyPlacedDie = true;
                                }
                                else{
                                 log("Posizionamento invalido");
                                }
                                this.instructionIndex = 42;
                            }
                        }
                        else if(instructionIndex == 2){
                            if(alreadyUsedToolCard){
                                //log(ansi().eraseScreen().cursor(0, 0).a("Hai già usato una carta strumento questo turno!").a("\n").toString());
                                log("Hai già usato una carta strumento questo turno!");
                                this.instructionIndex = 42;
                            }
                            else{
                                //log(ansi().eraseScreen().cursor(0, 0).a("USE TOOLCARD").a("\n").toString());
                                log("USE TOOLCARD");
                                alreadyUsedToolCard = true;
                                this.instructionIndex = 42;
                            }
                        }

                        else if(instructionIndex == 3){
                            //end turn
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
        this.setNickname(this.input("Nickname >>>"));
        setUUID(this.getNetwork().addPlayer(this.getNickname()));
        setLogged(this.getUUID() != null);
        if (isLogged()) log("Login successful");
        else log("Login failed");
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
                        for (int k = 0; k < 20; k++) newWPString += " ";
                        newWPString = (i + 1) + newWPString;
                        argAsList.set(i, newWPString + "\n" + argAsList.get(i));
                    }
                    log(windowPatternsMessage(argAsList));
                    stopAsyncInput = true;
                }
                if(argAsList.get(0).equals("$$$updatedWindowPatterns")){
                    this.printing = true;
                    argAsList.remove(0);
                    List<String> patterns = new ArrayList();
                    for (String s: argAsList){
                        String spaces = "";
                        for(int i = 0; i < 21 - s.substring(0,s.indexOf("$")).length(); i++)
                            spaces += " ";
                        spaces += "\n";
                        s = s.replace("$",spaces);
                        patterns.add(s);
                    }
                    String prettyWindowPatterns = windowPatternsMessage(patterns);
                    log(prettyWindowPatterns);
                    //log(ansi().eraseScreen().cursor(0, 0).a("\n").a(prettyWindowPatterns).a("\n").toString());
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
                    //log(ansi().eraseScreen().cursor(0, 0).a("\n\n").a(cards).toString());
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
                        //log(ansi().eraseScreen().cursor(0, 0).a("\n\n").a(cards).a("\n").toString());
                    stopAsyncInput = true;
                }

                if(argAsList.get(0).startsWith("Die$")) {   //DraftPool
                    String dice = "Riserva: ";
                    this.draftPoolLength = 0;
                    for (String s : argAsList){
                        s = s.replace("Die$","");
                        dice += s;
                        draftPoolLength++;
                    }
                    log(dice);
                    //log(ansi().eraseScreen().cursor(0, 0).a("\n\n").a(dice).toString());
                        stopAsyncInput = true;
                        this.printing = false;
                }

            } else if (arg instanceof Integer) {    // Timer ticks
                this.wrTimeout = arg.toString();
                System.out.print(waitingRoomMessage());
            } else if (arg instanceof String) {
                String input = (String) arg;
                if (input.startsWith("PrivateObjectiveCard$")) {
                    input = input.replace("PrivateObjectiveCard$", "");
                    //log(ansi().eraseScreen().cursor(0, 0).a(input).a("\n").toString());
                    log(input);
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
