import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;

public class Key {
    public static char[] promptMaster() {
        Console console = System.console();
        if (console != null) {
            return console.readPassword("Master password: ");
        }

        System.err.println("Warning: No console available. Password will be visible.");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            return br.readLine().toCharArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read master password", e);
        }
    }
}
