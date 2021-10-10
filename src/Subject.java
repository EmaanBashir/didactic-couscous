public class Subject {
    private String id;
    private String name;
    private int labCreditHours;
    private int lectureCreditHours;

    public Subject(String id, String name, int labCreditHours, int lectureCreditHours) {
        this.id = id;
        this.name = name;
        this.labCreditHours = labCreditHours;
        this.lectureCreditHours = lectureCreditHours;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
