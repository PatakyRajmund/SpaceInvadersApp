package main.java.org.example;

public class JSONNeededData {
    public int health;
    public int points;
    public boolean doubleShoot;

    JSONNeededData(int hp, int p, boolean doubleShoot)
    {
        this.doubleShoot=doubleShoot;
        health=hp;
        points=p;
    }

}
