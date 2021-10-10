public class Lecture {
    private int id;
    private Subject subject;
    private Teacher teacher;
    private LectureSlot lectureSlot;
    private Room room;
    private Class _class;

    public Lecture(Subject subject, Teacher teacher, Class _class) {
        this.subject = subject;
        this.teacher = teacher;
        this._class = _class;
    }

    public void setLectureSlot(LectureSlot lectureSlot) {
        this.lectureSlot = lectureSlot;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public LectureSlot getLectureSlot() {
        return lectureSlot;
    }

    public Room getRoom() {
        return room;
    }

    public Class get_class() {
        return _class;
    }
}
