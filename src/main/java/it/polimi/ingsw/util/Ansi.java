package it.polimi.ingsw.util;


public class Ansi {

    private final StringBuilder builder;

    private Ansi() {
        this.builder = new StringBuilder();
    }

    public static Ansi ansi() {
        return new Ansi();
    }

    // TERMINAL

    public Ansi clearLine() {
        this.builder.append("\r\033[K");
        return this;
    }

    public Ansi clear() {
        if (System.console() != null)
            this.builder.append("\033[H\033[2J");
        else
            for (int i = 0; i < 100; i++)
                this.builder.append("\n");
        return this;
    }

    public Ansi moveCursor(int line, int column) {
        this.builder.append("\033[");
        this.builder.append(line);
        this.builder.append(";");
        this.builder.append(column);
        this.builder.append("f");
        return this;
    }

    public Ansi cursorUp(int value) {
        this.builder.append("\033[");
        this.builder.append(value);
        this.builder.append("A");
        return this;
    }

    public Ansi cursorDown(int value) {
        this.builder.append("\033[");
        this.builder.append(value);
        this.builder.append("B");
        return this;
    }

    public Ansi cursorForward(int value) {
        this.builder.append("\033[");
        this.builder.append(value);
        this.builder.append("C");
        return this;
    }

    public Ansi cursorBackward(int value) {
        this.builder.append("\033[");
        this.builder.append(value);
        this.builder.append("D");
        return this;
    }

    public Ansi saveCursorPosition() {
        this.builder.append("\033[s");
        return this;
    }

    public Ansi restoreCursorPosition() {
        this.builder.append("\033[u");
        return this;
    }

    // FG COLORS

    public Ansi fg(Colors color) {
        this.builder.append(color.escape());
        return this;
    }

    public Ansi reset() {
        return this.fg(Colors.RESET);
    }

    // BG COLORS

    public Ansi bg(Colors color) {
        this.builder.append(color.escape().replaceAll("\\[3", "[4"));
        return this;
    }

    public Ansi block(Colors color) {
        return this.bg(color).a(" ").reset();
    }

    // APPEND

    public Ansi a(String string) {
        this.builder.append(string);
        return this;
    }

    public Ansi a(int value) {
        this.builder.append(value);
        return this;
    }

    // MISC

    public String toString() {
        return this.reset().builder.toString();
    }

}
