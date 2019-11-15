package com.game.quest;

import com.badlogic.gdx.utils.Array;

public abstract class Objective {
    private String description;
    private String type;
    private Array<String> areas;

    public Objective(String description, String type, Array<String> areas) {
        this.description = description;
        this.type = type;
        this.areas = areas;
    }

    public abstract String objectiveToString();

    public abstract void updateObjective();

    public abstract boolean isCompleted();

    public String getType() {
        return type;
    }

    public Array<String> getAreas() {
        return areas;
    }

    public String getDescription() {
        return description;
    }
}
