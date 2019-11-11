package game.quest;

public class Quest {

    private String title;
    private Objective objective;
    private boolean terminated;
    private boolean accepted;

    public Quest(String title, Objective objective)
    {
        this.title = title;
        this.objective = objective;
        this.terminated = false;
        this.accepted = false;
    }

    public String getTitle() {return title;}

    public Objective getObjective() {return objective;}

    public String objectiveToString() {return objective.objectiveToString();}

    public boolean isTerminated() {return terminated;}

    public void setTerminated() {this.terminated = true;}

    public boolean isAccepted() {return accepted;}

    public void setAccepted() {this.accepted = true;}
}
