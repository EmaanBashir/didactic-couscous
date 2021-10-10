import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class NewTimeTableGenerationController {
    @FXML
    Label label10, label20, label30, label40, label60, label70, label80,
            label11, label12, label13, label14, label15, label21, label22,
            label23, label24, label25, label31, label32, label33, label34,
            label35, label41, label42, label43, label44, label45, label61,
            label62, label63, label64, label65, label71, label72, label73,
            label74, label75, label81, label82, label83, label84, label85;
    @FXML
    private ComboBox classesComboBox, teachersComboBox;
    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();
    private Label[][] labels;
    public static Schedule schedule;
    private TimetableGenerator timetableGenerator = new TimetableGenerator();
    private String username = MainScreenController.username;

    public NewTimeTableGenerationController() throws SQLException {
    }

    public void initialize() throws SQLException {
        displayTiming();
        initializeComboBoxes();
        labels = new Label[][]{{label11, label12, label13, label14, label15},
                {label21, label22, label23, label24, label25},
                {label31, label32, label33, label34, label35},
                {label41, label42, label43, label44, label45},
                {label61, label62, label63, label64, label65},
                {label71, label72, label73, label74, label75},
                {label81, label82, label83, label84, label85}};
    }

    public void displayTiming() throws SQLException {
        PreparedStatement pst = connection.prepareStatement("SELECT Timing FROM LectureSlots WHERE id%10 = 1 AND Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        ArrayList<String> timings = new ArrayList<String>();
        while (result.next()) {
            timings.add(result.getString("Timing"));
        }
        label10.setText(timings.get(0));
        label20.setText(timings.get(1));
        label30.setText(timings.get(2));
        label40.setText(timings.get(3));
        label60.setText(timings.get(4));
        label70.setText(timings.get(5));
        label80.setText(timings.get(6));
    }

    public void initializeComboBoxes() throws SQLException {
        ObservableList<String> classes = FXCollections.observableArrayList();
        ObservableList<String> teachers = FXCollections.observableArrayList();
        String teacher, _class;
        PreparedStatement pst = connection.prepareStatement("SELECT ClassName FROM Classes WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result1 = pst.executeQuery();
        while (result1.next()) {
            _class = result1.getString("ClassName");
            if (!classes.contains(_class)) {
                classes.add(_class);
            }
        }
        PreparedStatement pst1 = connection.prepareStatement("SELECT TeacherName FROM Teachers WHERE Username = ?");
        pst1.setString(1, username);
        ResultSet result2 = pst1.executeQuery();
        while (result2.next()) {
            teacher = result2.getString("TeacherName");
            if (!teachers.contains(teacher)) {
                teachers.add(teacher);
            }
        }
        FXCollections.sort(classes, (f1, f2) -> f1.compareTo(f2));
        FXCollections.sort(teachers, (f1, f2) -> f1.compareTo(f2));
        classesComboBox.getItems().setAll(classes);
        teachersComboBox.getItems().setAll(teachers);
    }

    public void selectClass() {
        if (!classesComboBox.getSelectionModel().isEmpty()) {
            teachersComboBox.getSelectionModel().clearSelection();
        }
        inputClassTimeTable();

    }

    public void selectTeacher() {
        if (!teachersComboBox.getSelectionModel().isEmpty()) {
            classesComboBox.getSelectionModel().clearSelection();
        }
        inputTeacherTimeTable();
    }

    public void inputClassTimeTable() {
        String _class = (String) classesComboBox.getSelectionModel().getSelectedItem();
        String[][] timeTable = new String[7][5];
        Lecture[] allLectures = schedule.getLectures();
        Lab[] allLabs = schedule.getLabs();
        ArrayList<Lecture> lectures = new ArrayList<Lecture>();
        ArrayList<Lab> labs = new ArrayList<Lab>();
        for (Lecture lecture : allLectures) {
            if (lecture.get_class().getName().equals(_class)) {
                lectures.add(lecture);
            }
        }
        for (Lab lab : allLabs) {
            if (lab.get_class().getName().equals(_class)) {
                labs.add(lab);
            }
        }

        int lectureSlotId, row, column;
        for (Lecture lecture : lectures) {
            lectureSlotId = lecture.getLectureSlot().getId();
            row = lectureSlotId / 10;
            column = lectureSlotId % 10;
            timeTable[row - 1][column - 1] = lecture.getSubject().getName() + "\n" + lecture.getRoom().getId();
        }
        int lectureSlotId1;
        for (Lab lab : labs) {
            lectureSlotId1 = lab.getLabSlot().getSlots()[0].getId();
            row = lectureSlotId1 / 10;
            column = lectureSlotId1 % 10;
            for (int i = row; i < (row + 3); i++) {
                timeTable[i - 1][column - 1] = lab.getSubject().getName() + "\n(Lab)";
            }
        }
        displayTimetable(timeTable);
    }

    public void inputTeacherTimeTable() {
        String teacher = (String) teachersComboBox.getSelectionModel().getSelectedItem();
        String[][] timeTable = new String[7][5];
        Lecture[] allLectures = schedule.getLectures();
        ArrayList<Lecture> lectures = new ArrayList<Lecture>();
        for (Lecture lecture : allLectures) {
            if (lecture.getTeacher().getName().equals(teacher)) {
                lectures.add(lecture);
            }
        }
        int lectureSlotId, row, column;
        for (Lecture lecture : lectures) {
            lectureSlotId = lecture.getLectureSlot().getId();
            row = lectureSlotId / 10;
            column = lectureSlotId % 10;
            timeTable[row - 1][column - 1] = lecture.get_class().getName() + "\n" + lecture.getSubject().getName() + "(" + lecture.getRoom().getId() + ")";
        }
        displayTimetable(timeTable);
    }

    public void displayTimetable(String[][] timetable) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                labels[i][j].setText("-");
                if (timetable[i][j] != null) {
                    labels[i][j].setText(timetable[i][j]);
                }
            }
        }
    }

    public void regenerateTimeTable() throws SQLException {
        schedule = timetableGenerator.generateTimetable();
        if (!classesComboBox.getSelectionModel().isEmpty()) {
            inputClassTimeTable();
        } else if (!teachersComboBox.getSelectionModel().isEmpty()) {
            inputTeacherTimeTable();
        }
    }

    public void saveTimeTable() throws SQLException, IOException {
        String message;
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM LecturesSchedule WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        if (result.next()) {
            message = "Are you sure you want to replace the existing timetable?";
        } else {
            message = "Are you sure you want to save this timetable?";
        }
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            alert.close();
            connection.close();
            timetableGenerator.saveSchedule(schedule);
            moveToScreen("MainScreen.fxml");
        }
        if (alert.getResult() == ButtonType.NO) {
            alert.close();
        }
    }

    public void back() throws SQLException, IOException {
        moveToScreen("MainScreen.fxml");
    }

    public void moveToScreen(String screenName) throws IOException, SQLException {
        statement.close();
        connection.close();
        Scene scene = Main.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource(screenName)));
        Main.getPrimaryStage().setScene(scene);
    }
}
