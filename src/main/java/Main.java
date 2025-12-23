import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        File store = new File("store.json");
        Gson gson = new Gson();

        Type tipoLista = new TypeToken<ArrayList<Entry>>(){}.getType();
        ArrayList<Entry> lista = gson.fromJson(new FileReader(store), tipoLista);

        System.out.println(lista.getFirst());
    }
}