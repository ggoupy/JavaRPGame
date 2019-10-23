package game.utils;

public class Bar {
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
