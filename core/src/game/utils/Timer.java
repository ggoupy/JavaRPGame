package game.utils;

public class Timer {
    private double max;
    private double current;

    public Timer(double max)
    {
        this.max = max;
        this.current = 0;
    }

    //update the current value and return if it reached max value
    public boolean update(float deltaTime)
    {
        current += deltaTime;
        return current >= max;
    }

    public void reset()
    {
        current = 0;
    }

    public double getMax() {return max;}
    public double getCurrent() {return current;}
}
