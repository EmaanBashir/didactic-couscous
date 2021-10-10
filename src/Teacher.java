public class Teacher {
    private int id;
    private String name;
    private Subject[] subjects;

    public Teacher(int id, String name, Subject[] subjects) {
        this.id = id;
        this.name = name;
        this.subjects = subjects;
    }

    public String getName() {
        return name;
    }

    public Subject[] getSubjects() {
        return subjects;
    }

    public int getId() {
        return id;
    }
}
