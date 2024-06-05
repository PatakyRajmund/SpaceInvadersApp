package main.java.org.example;

import com.google.gson.*;

import java.io.*;

public class JSONHandler {
    String fileName;
    File managedOne;

    JSONHandler(String fileName) {

        this.fileName = fileName;
        managedOne = new File(fileName);

    }

    void write(JSONNeededData player) {

        player.doubleShoot=true;
        var jsonString = new GsonBuilder()
                .setPrettyPrinting()
                .create().toJson(player);

        try (var writer = new BufferedWriter(new FileWriter(managedOne))) {
            writer.write(jsonString);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    JSONNeededData read() {
        JSONNeededData tmp=null;


        try (Reader reader = new BufferedReader(new FileReader(managedOne))) {

            Gson gson=new Gson();
            tmp=gson.fromJson(reader,JSONNeededData.class);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return tmp;

    }


}
