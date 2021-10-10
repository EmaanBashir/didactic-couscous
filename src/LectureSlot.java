public class LectureSlot {
    private String day;
    private int id;
    private String timing;

    public LectureSlot(String day, int id, String timing) {
        this.day = day;
        this.id = id;
        this.timing = timing;
    }

    public String getDay() {
        return day;
    }

    public int getId() {
        return id;
    }
}
