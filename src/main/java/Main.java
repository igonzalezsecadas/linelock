import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final File STORE = new File("store.json");
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<ArrayList<Entry>>() {}.getType();

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("  java Main add <service> <user> <password>");
            System.out.println("  java Main list");
            return;
        }

        char[] master = Key.promptMaster();
        try {
            switch (args[0]) {
                case "add" -> addEntry(args, master);
                case "list" -> listEntries(master);
                default -> System.out.println("Unknown command: " + args[0]);
            }
        } finally {
            java.util.Arrays.fill(master, '\0');
        }
    }

    private static void addEntry(String[] args, char[] master) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: java Main add <service> <user> <password>");
            return;
        }
        List<Entry> entries = loadEntries();
        String encrypted = CryptoService.encrypt(master, args[3]);
        entries.add(new Entry(args[1], args[2], encrypted));
        saveEntries(entries);
        System.out.println("Entry added.");
    }

    private static void listEntries(char[] master) throws IOException {
        List<Entry> entries = loadEntries();
        if (entries.isEmpty()) {
            System.out.println("No entries found.");
            return;
        }
        for (Entry e : entries) {
            try {
                String decrypted = CryptoService.decrypt(master, e.getPassword());
                System.out.println(e + " -> password: " + decrypted);
            } catch (CryptoException ex) {
                System.out.println(e + " -> [decryption failed: wrong password or tampered data]");
            }
        }
    }

    private static List<Entry> loadEntries() throws IOException {
        if (!STORE.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(STORE, StandardCharsets.UTF_8)) {
            List<Entry> list = GSON.fromJson(reader, LIST_TYPE);
            return list != null ? list : new ArrayList<>();
        }
    }

    private static void saveEntries(List<Entry> entries) throws IOException {
        try (Writer writer = new FileWriter(STORE, StandardCharsets.UTF_8)) {
            GSON.toJson(entries, writer);
        }
    }
}
