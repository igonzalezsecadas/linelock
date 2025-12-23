import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.lang.reflect.Type;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        File store = new File("store.json");
        Gson gson = new Gson();

        Type tipoLista = new TypeToken<ArrayList<Entry>>(){}.getType();
        ArrayList<Entry> lista = gson.fromJson(new FileReader(store), tipoLista);

        System.out.println(lista.getFirst());
    }
}