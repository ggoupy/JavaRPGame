package game.quest;

public class KillingObjective extends Objective {
    private int toKill;
    private int killed;
    private String enemy;

    public KillingObjective(String description, String type, int nbToKill, String enemy)
    {
        super(description, type);
        this.toKill = nbToKill;
        this.killed = 0;
        this.enemy = enemy;
    }

    public String getEnemy() {return enemy;}

    public int getToKill() {
        return toKill;
    }

    public int getKilled() {
        return killed;
    }

    public void setKilled(int killed) {
        this.killed = killed;
    }

    @Override
    public void updateObjective() { killed++; }

    @Override
    public boolean isCompleted() { return killed >= toKill; }

    @Override
    public String objectiveToString()
    {
        if (!isCompleted())
            return killed + " / " + toKill + "  " + enemy + " killed.";

        else return "Completed. Return the quest.";
    }
}
