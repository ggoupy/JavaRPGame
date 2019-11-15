package com.game.quest;

public class Quest {

    private String title;
    private Objective objective;
    private boolean completed;
    private boolean accepted;
    private int xp;

    public Quest(String title, Objective objective, int xp) {
        this.title = title;
        this.objective = objective;
        this.completed = false;
        this.accepted = false;
        this.xp = xp;
    }

    public String getTitle() {
        return title;
    }

    public Objective getObjective() {
        return objective;
    }

    public String objectiveToString() {
        return objective.objectiveToString();
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        this.completed = true;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted() {
        this.accepted = true;
    }

    public int getXP() {
        return xp;
    }
}
