package client;

public final class ConsoleUtils {

    private static final Object CONSOLE_LOCK = new Object();

    private ConsoleUtils() {
    }

    public static void println(String text) {
        synchronized (CONSOLE_LOCK) {
            System.out.println(text);
        }
    }

    public static void print(String text) {
        synchronized (CONSOLE_LOCK) {
            System.out.print(text);
        }
    }

    public static void printPrompt() {
        synchronized (CONSOLE_LOCK) {
            System.out.print("\nScegli comando: create | join | state | start | ping | leave | exit\n");
        }
    }

    public static void printGameStateBlock(String text) {
        synchronized (CONSOLE_LOCK) {
            System.out.print(text);
        }
    }
}