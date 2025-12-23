import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Key {
    public static String getMaster() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(".env/master"));
        return br.readLine();
    }
}
