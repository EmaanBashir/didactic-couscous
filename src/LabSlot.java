public class LabSlot {
    private int id;
    private LectureSlot[] slots;
    private String day;

    public LabSlot(int id, LectureSlot[] slots, String day) {
        this.id = id;
        this.slots = slots;
        this.day = day;
    }


    public int getId() {
        return id;
    }

    public LectureSlot[] getSlots() {
        return slots;
    }

    public String getDay() {
        return day;
    }
}
