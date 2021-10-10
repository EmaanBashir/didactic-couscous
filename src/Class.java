public class Class {
    private int id;
    private String name;
    private int noOfStudents;
    private Subject[] subjects;
    private int noOfSubjects;

    public Class(int id, String name, int noOfStudents, int noOfSubjects) {
        this.id = id;
        this.name = name;
        this.noOfStudents = noOfStudents;
        this.noOfSubjects = noOfSubjects;
    }

    public String getName() {
        return name;
    }

    public int getNoOfStudents() {
        return noOfStudents;
    }

    public Subject[] getSubjects() {
        return subjects;
    }

    public void setSubjects(Subject[] subjects) {
        this.subjects = subjects;
    }

    public int getId() {
        return id;
    }
}
