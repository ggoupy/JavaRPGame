package game.entity.component.player;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    protected String name;
    protected double level;
    protected Bar life;
    protected Bar action; //Call it differently
    protected int speed;
    protected int damage;

    //FOR TEST
    public PlayerComponent()
    {
        this.name = "test";
        this.level = 1;
        this.life = new Bar(100);
        this.action = new Bar(100);
        this.speed = 5;
        this.damage = 10;
    }
    public PlayerComponent(String name, int l, int s, int d) {
        this.name = name;
        this.level = l;
        this.life = new Bar(100);
        this.action = new Bar(100);
        this.speed = s;
        this.damage = d;
    }

}

class Bar {
    private int max;
    private int current;

    public Bar(int max) {
        this.max = max;
        this.current = max;
    }
    public Bar(int max, int current) {
        this.max = max;
        this.current = current;
    }
    public int getMax() {return max;}
    public void setMax(int m) {max = m;}
    public int getCurrent() {return current;}
    public void setCurrent(int c) {current = c;}
    public void updateCurrent(int c) {current += c;}
}