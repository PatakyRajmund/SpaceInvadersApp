package main.java.org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONNeededDataTest {

    @Test
    void testConstructor()
    {
        JSONNeededData tmp=new JSONNeededData(10,150,false);
        assertEquals(10,tmp.health);
        assertEquals(150,tmp.points);
        assertFalse(tmp.doubleShoot);
    }

}