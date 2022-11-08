public class Actor {
    private final String stageName;
    private final int dob;

    public Actor(String stageName, int dob) {
        this.stageName = stageName;
        this.dob = dob;
    }

    public String getStageName() {
        return stageName;
    }

    public int getDob() {return dob;}


    public String toString() {
        return "Stage Name:" + getStageName() + ", " +
                "DOB:" + getDob();
    }
}
