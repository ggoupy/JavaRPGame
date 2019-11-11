package game.quest;

public abstract class Objective {
    private String description;
    private String type;

    public Objective(String description, String type)
    {
        this.description = description;
        this.type = type;
    }

    public abstract String objectiveToString();

    public abstract void updateObjective();

    public abstract boolean isCompleted();

    public String getType() {return type;}

    public String getDescription() {
        return description;
    }
}
