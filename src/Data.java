import java.sql.*;
import java.util.ArrayList;

public class Data {
    private Lecture[] lectures;
    private Lab[] labs;
    private Class[] classes;
    private LectureSlot[] lectureSlots;
    private Teacher[] teachers;
    private Room[] rooms;
    private Subject[] subjects;
    private LabSlot[] labSlots;
    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();
    private String username = MainScreenController.username;

    public Data() throws SQLException {
        inputClasses();
        inputSubjects();
        inputTeachers();
        inputRooms();
        inputLectures();
        inputLabs();
        inputLectureSlots();
        inputLabSlots();
        statement.close();
        connection.close();
    }

    public void inputClasses() throws SQLException {
        ArrayList<Class> classesArrayList = new ArrayList<Class>();
        int counter = 0;
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM Classes WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            String className = result.getString("ClassName");
            int noOfStudents = result.getInt("NoOfStudents");
            int noOfSubjects = result.getInt("NoOfSubjects");
            classesArrayList.add(new Class(counter, className, noOfStudents, noOfSubjects));
            counter++;
        }
        classes = classesArrayList.toArray(new Class[0]);
        pst.close();
    }

    public void inputSubjects() throws SQLException {
        ArrayList<Subject> subjectsArrayList = new ArrayList<Subject>();
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM Subjects WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            String id = result.getString("CourseCode");
            String name = result.getString("SubjectName");
            int lectureCreditHours = result.getInt("LectureCreditHours");
            int labCreditHours = result.getInt("LabCreditHours");
            subjectsArrayList.add(new Subject(id, name, labCreditHours, lectureCreditHours));
        }
        subjects = subjectsArrayList.toArray(new Subject[0]);
        pst.close();
    }

    public void inputTeachers() throws SQLException {
        ArrayList<Teacher> teachersArrayList = new ArrayList<Teacher>();
        int counter = 0;
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM Teachers WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        ArrayList<String> subjectNames = new ArrayList<String>();
        String teacherName = "";
        Subject[] subjectsArray;
        String name = "";
        String subjectName = "";
        while (result.next()) {
            name = result.getString("TeacherName");
            subjectName = result.getString("Subject");
            if (name.equals(teacherName)) {
                subjectNames.add(subjectName);
                teacherName = name;
            } else {
                if (!teacherName.equals("")) {
                    int noOfSubjects = subjectNames.size();
                    subjectsArray = new Subject[noOfSubjects];
                    for (int i = 0; i < noOfSubjects; i++) {

                        for (Subject subject : this.subjects) {
                            if (subjectNames.get(i).equals(subject.getName())) {
                                subjectsArray[i] = subject;
                            }
                        }
                    }
                    teachersArrayList.add(new Teacher(counter, teacherName, subjectsArray));
                    counter++;
                }
                subjectNames.clear();
                subjectNames.add(subjectName);
                teacherName = name;
            }
        }
        if (!teacherName.equals("")) {
            int noOfSubjects = subjectNames.size();
            subjectsArray = new Subject[noOfSubjects];
            for (int i = 0; i < noOfSubjects; i++) {

                for (Subject subject : this.subjects) {
                    if (subjectNames.get(i).equals(subject.getName())) {
                        subjectsArray[i] = subject;
                    }
                }
            }
            teachersArrayList.add(new Teacher(counter, teacherName, subjectsArray));
            counter++;
        }
        subjectNames.clear();
        subjectNames.add(subjectName);
        teacherName = name;
        teachers = teachersArrayList.toArray(new Teacher[0]);
        pst.close();

    }

    public void inputRooms() throws SQLException {
        ArrayList<Room> roomsArrayList = new ArrayList<Room>();
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM Rooms WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            int number = result.getInt("Number");
            int capacity = result.getInt("Capacity");
            roomsArrayList.add(new Room(number, capacity));
        }
        rooms = roomsArrayList.toArray(new Room[0]);
        pst.close();
    }

    public void inputLectures() throws SQLException {
        ArrayList<Lecture> lecturesArrayList = new ArrayList<Lecture>();
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM Lectures WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            String className = result.getString("Class");
            String subjectName = result.getString("Subject");
            String teacherName = result.getString("Teacher");
            Class _class = null;
            Subject subject = null;
            Teacher teacher = null;
            for (Class c : this.classes) {
                if (className.equals(c.getName())) {
                    _class = c;
                    break;
                }
            }
            for (Teacher t : this.teachers) {
                if (teacherName.equals(t.getName())) {
                    teacher = t;
                    break;
                }
            }
            for (Subject s : this.subjects) {
                if (subjectName.equals(s.getName())) {
                    subject = s;
                    break;
                }
            }
            lecturesArrayList.add(new Lecture(subject, teacher, _class));
        }
        lectures = lecturesArrayList.toArray(new Lecture[0]);
        pst.close();
    }

    public void inputLabs() throws SQLException {
        ArrayList<Lab> labsArrayList = new ArrayList<Lab>();
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM Labs WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            String className = result.getString("Class");
            String subjectName = result.getString("Subject");
            Class _class = null;
            Subject subject = null;
            for (Class c : this.classes) {
                if (className.equals(c.getName())) {
                    _class = c;
                    break;
                }
            }
            for (Subject s : this.subjects) {
                if (subjectName.equals(s.getName())) {
                    subject = s;
                    break;
                }
            }
            labsArrayList.add(new Lab(subject, _class));
        }
        labs = labsArrayList.toArray(new Lab[0]);
        pst.close();
    }

    public void inputLectureSlots() throws SQLException {
        ArrayList<LectureSlot> lectureSlotsArrayList = new ArrayList<LectureSlot>();
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM LectureSlots WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            int id = result.getInt("id");
            String day = result.getString("Day");
            String timing = result.getString("Timing");
            lectureSlotsArrayList.add(new LectureSlot(day, id, timing));
        }
        lectureSlots = lectureSlotsArrayList.toArray(new LectureSlot[0]);
        pst.close();
    }

    public void inputLabSlots() throws SQLException {
        ArrayList<LabSlot> labSlotsArrayList = new ArrayList<LabSlot>();
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM LabSlots WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            int id = result.getInt("id");
            String day = result.getString("Day");
            int lectureSlotId1 = result.getInt("LectureSlotId1");
            int lectureSlotId2 = result.getInt("LectureSlotId2");
            int lectureSlotId3 = result.getInt("LectureSlotId3");
            LectureSlot lectureSlot1 = null;
            LectureSlot lectureSlot2 = null;
            LectureSlot lectureSlot3 = null;
            for (LectureSlot lectureSlot : this.lectureSlots) {
                if (lectureSlot.getId() == lectureSlotId1) {
                    lectureSlot1 = lectureSlot;
                } else if (lectureSlot.getId() == lectureSlotId2) {
                    lectureSlot2 = lectureSlot;
                } else if (lectureSlot.getId() == lectureSlotId3) {
                    lectureSlot3 = lectureSlot;
                }
            }
            LectureSlot[] slots = {lectureSlot1, lectureSlot2, lectureSlot3};
            labSlotsArrayList.add(new LabSlot(id, slots, day));
        }
        labSlots = labSlotsArrayList.toArray(new LabSlot[0]);
        pst.close();
    }

    public Lecture[] getLectures() {
        return lectures;
    }

    public Lab[] getLabs() {
        return labs;
    }

    public Class[] getClasses() {
        return classes;
    }

    public LectureSlot[] getLectureSlots() {
        return lectureSlots;
    }
    public Room[] getRooms() {
        return rooms;
    }

    public Subject[] getSubjects() {
        return subjects;
    }

    public LabSlot[] getLabSlots() {
        return labSlots;
    }
}
