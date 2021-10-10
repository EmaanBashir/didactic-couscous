import java.sql.*;

public class Database {
    private Connection connection;
    private Statement statement;

    public Database() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:timetable_generator_database.db");
        statement = connection.createStatement();
    }

    public void createTables() throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS UserInfo(Username STRING PRIMARY KEY, FirstName STRING, LastName STRING, Institute STRING, InstituteName STRING, Email STRING, Password STRING, RememberMe BOOLEAN)");
        statement.execute("CREATE TABLE IF NOT EXISTS Rooms(Username STRING, Number INTEGER, Capacity INTEGER)");
        statement.execute("CREATE TABLE IF NOT EXISTS Subjects(Username STRING, CourseCode STRING, SubjectName STRING, LectureCreditHours INTEGER, LabCreditHours INTEGER)");
        statement.execute("CREATE TABLE IF NOT EXISTS Teachers(Username STRING, TeacherName STRING, Subject STRING)");
        statement.execute("CREATE TABLE IF NOT EXISTS Classes(Username STRING, ClassName STRING, NoOfStudents INTEGER, NoOfSubjects INTEGER)");
        statement.execute("CREATE TABLE IF NOT EXISTS Lectures(Username STRING, Class STRING, Subject STRING, Teacher STRING)");
        statement.execute("CREATE TABLE IF NOT EXISTS Labs(Username STRING, Class STRING, Subject STRING)");
        statement.execute("CREATE TABLE IF NOT EXISTS LectureSlots(Username STRING, id INTEGER, Day STRING, Timing STRING)");
        statement.execute("CREATE TABLE IF NOT EXISTS LabSlots(Username STRING, id INTEGER, Day STRING, LectureSlotId1 INTEGER, LectureSlotId2 INTEGER, LectureSlotId3 INTEGER)");
        statement.execute("CREATE TABLE IF NOT EXISTS LecturesSchedule(Username STRING, Class STRING, Subject STRING, Teacher STRING, LectureSlotId INTEGER, Room STRING)");
        statement.execute("CREATE TABLE IF NOT EXISTS LabsSchedule(Username STRING, Class STRING, Subject STRING, LectureSlotId1 INTEGER, LectureSlotId2 INTEGER, LectureSlotId3 INTEGER)");
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setTimings() throws SQLException {
        int id;
        String day;
        String timing;
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] lectureTimings = {"09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00"};
        int[] firstLectures = {1, 2, 5};
        int lectureSlotId1;
        int lectureSlotId2;
        int lectureSlotId3;
        String sq1;
        PreparedStatement pst;
        for (int i = 1; i < 8; i++) {
            timing = lectureTimings[i - 1];
            for (int j = 1; j < 6; j++) {
                id = (i * 10) + j;
                day = days[j - 1];
                sq1 = "INSERT INTO LectureSlots(Username, id, Day, Timing) VALUES(?,?,?,?)";
                pst = connection.prepareStatement(sq1);
                pst.setString(1, MainScreenController.username);
                pst.setInt(2, id);
                pst.setString(3, day);
                pst.setString(4, timing);
                pst.executeUpdate();
                pst.close();
            }
        }
        for (int i = 1; i < 6; i++) {
            day = days[i - 1];
            for (int j = 1; j < 4; j++) {
                id = (j * 10) + i;
                lectureSlotId1 = (firstLectures[j - 1] * 10) + i;
                lectureSlotId2 = lectureSlotId1 + 10;
                lectureSlotId3 = lectureSlotId2 + 10;
                sq1 = "INSERT INTO LabSlots(Username, id, Day, LectureSlotId1, LectureSlotId2, LectureSlotId3) VALUES(?,?,?,?,?,?)";
                pst = connection.prepareStatement(sq1);
                pst.setString(1, MainScreenController.username);
                pst.setInt(2, id);
                pst.setString(3, day);
                pst.setInt(4, lectureSlotId1);
                pst.setInt(5, lectureSlotId2);
                pst.setInt(6, lectureSlotId3);
                pst.executeUpdate();
                pst.close();
            }
        }
    }
}
