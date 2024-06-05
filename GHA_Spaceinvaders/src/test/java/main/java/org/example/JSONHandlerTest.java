package main.java.org.example;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.*;

class JSONHandlerTest {

    @Test
    void testWrite() {
        JSONHandler tmp = new JSONHandler("test.json");
        JSONNeededData tmpNeeded = new JSONNeededData(10,10,true);
        JSONNeededData readData=new JSONNeededData(10,10,false);
        tmp.write(tmpNeeded);
        try (Reader reader = new BufferedReader(new FileReader("test.json"))) {

            Gson gson=new Gson();
            readData=gson.fromJson(reader,JSONNeededData.class);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        assertEquals(tmpNeeded.health,readData.health);
        assertEquals(tmpNeeded.points,readData.points);
        assertEquals(tmpNeeded.doubleShoot,readData.doubleShoot);
    }

    @Test
    void testRead() {
        JSONHandler tmp=new JSONHandler("test.json");
        JSONNeededData l=new JSONNeededData(0,0,false);
        l=tmp.read();
        assertEquals(10,l.health);
        assertEquals(10,l.points);
        assertTrue(l.doubleShoot);
    }
}