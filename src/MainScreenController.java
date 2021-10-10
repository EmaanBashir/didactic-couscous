import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class MainScreenController {
    @FXML
    HBox hBox;
    @FXML
    Button button;
    @FXML
    VBox vBox;
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
    public static String username;


    public MainScreenController() throws SQLException {
    }

    public void initialize() throws SQLException {
        vBox.getChildren().remove(hBox);
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

    public void displayOptions() {
        vBox.getChildren().add(0, hBox);
        button.setText("/\\");
        button.setOnAction(event -> hideOptions());
    }

    public void hideOptions() {
        vBox.getChildren().remove(hBox);
        button.setText("\\/");
        button.setOnAction(event -> displayOptions());
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
        PreparedStatement pst = connection.prepareStatement("SELECT Class, Teacher FROM LecturesSchedule WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result1 = pst.executeQuery();
        while (result1.next()) {
            _class = result1.getString("Class");
            teacher = result1.getString("Teacher");
            if (!classes.contains(_class)) {
                classes.add(_class);
            }
            if (!teachers.contains(teacher)) {
                teachers.add(teacher);
            }
        }
        PreparedStatement pst1 = connection.prepareStatement("SELECT Class FROM LabsSchedule WHERE Username = ?");
        pst1.setString(1, username);
        ResultSet result2 = pst1.executeQuery();
        while (result2.next()) {
            _class = result2.getString("Class");
            if (!classes.contains(_class)) {
                classes.add(_class);
            }
        }
        FXCollections.sort(classes, (f1, f2) -> f1.compareTo(f2));
        FXCollections.sort(teachers, (f1, f2) -> f1.compareTo(f2));
        classesComboBox.getItems().setAll(classes);
        teachersComboBox.getItems().setAll(teachers);
    }

    public void selectClass() throws SQLException {
        if (!classesComboBox.getSelectionModel().isEmpty()) {
            teachersComboBox.getSelectionModel().clearSelection();
        }
        inputClassTimeTable();

    }

    public void selectTeacher() throws SQLException {
        if (!teachersComboBox.getSelectionModel().isEmpty()) {
            classesComboBox.getSelectionModel().clearSelection();
        }
        inputTeacherTimeTable();
    }

    public void inputClassTimeTable() throws SQLException {
        String _class = (String) classesComboBox.getSelectionModel().getSelectedItem();
        String[][] timeTable = new String[7][5];
        PreparedStatement pst = connection.prepareStatement("SELECT Subject, LectureSlotId, Room FROM LecturesSchedule WHERE Class = ? AND Username = ?");
        pst.setString(1, _class);
        pst.setString(2, username);
        ResultSet result = pst.executeQuery();
        int lectureSlotId, row, column;
        while (result.next()) {
            lectureSlotId = result.getInt("LectureSlotId");
            row = lectureSlotId / 10;
            column = lectureSlotId % 10;
            timeTable[row - 1][column - 1] = result.getString("Subject") + "\n" + result.getString("Room");
        }
        PreparedStatement pst1 = connection.prepareStatement("SELECT Subject, LectureSlotId1 FROM LabsSchedule WHERE Class = ? AND Username = ?");
        pst1.setString(1, _class);
        pst1.setString(2, username);
        ResultSet result1 = pst1.executeQuery();
        int lectureSlotId1;
        while (result1.next()) {
            lectureSlotId1 = result1.getInt("LectureSlotId1");

            row = lectureSlotId1 / 10;
            column = lectureSlotId1 % 10;
            for (int i = row; i < (row + 3); i++) {
                timeTable[i - 1][column - 1] = result1.getString("Subject") + "\n(Lab)";
            }
        }
        displayTimetable(timeTable);
    }

    public void inputTeacherTimeTable() throws SQLException {
        String teacher = (String) teachersComboBox.getSelectionModel().getSelectedItem();
        String[][] timeTable = new String[7][5];
        PreparedStatement pst = connection.prepareStatement("SELECT Class, Subject, LectureSlotId, Room FROM LecturesSchedule WHERE Teacher = ? AND Username = ?");
        pst.setString(1, teacher);
        pst.setString(2, username);
        ResultSet result = pst.executeQuery();
        int lectureSlotId, row, column;
        while (result.next()) {
            lectureSlotId = result.getInt("LectureSlotId");
            row = lectureSlotId / 10;
            column = lectureSlotId % 10;
            timeTable[row - 1][column - 1] = result.getString("Class") + "\n" + result.getString("Subject") + "(" + result.getString("Room") + ")";
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

    public void addRoomScreen() throws IOException, SQLException {
        moveToScreen("AddRoom.fxml");
    }

    public void addSubjectScreen() throws IOException, SQLException {
        moveToScreen("AddSubject.fxml");
    }

    public void addInstructorScreen() throws IOException, SQLException {
        moveToScreen("AddTeacher.fxml");
    }

    public void addClassScreen() throws IOException, SQLException {
        moveToScreen("AddClass.fxml");
    }

    public void editingScreen() throws IOException, SQLException {
        moveToScreen("EditingScreen.fxml");
    }

    public void changeTimingScreen() throws IOException, SQLException {
        moveToScreen("ChangeTiming.fxml");
    }

    public void generateNewTimeTable() throws SQLException, IOException {
        TimetableGenerator timetableGenerator = new TimetableGenerator();
        NewTimeTableGenerationController.schedule = timetableGenerator.generateTimetable();
        moveToScreen("NewTimeTableGeneration.fxml");
    }

    public void changePassword() throws SQLException, IOException {
        moveToScreen("ChangePassword.fxml");
    }

    public void deleteData() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.NONE, "Are you sure you want to delete all existing data?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            delete("Classes");
            delete("Labs");
            delete("LabsSchedule");
            delete("Lectures");
            delete("LecturesSchedule");
            delete("Rooms");
            delete("Subjects");
            delete("Teachers");
            delete("LabSlots");
            delete("LectureSlots");
            database.setTimings();
            initializeComboBoxes();
            displayTiming();
            alert.close();
        }
        if (alert.getResult() == ButtonType.NO) {
            alert.close();
        }
    }

    public void logOut() throws SQLException, IOException {
        moveToScreen("LoginScreen.fxml");
    }

    public void delete(String table) throws SQLException {
        PreparedStatement pst = connection.prepareStatement("DELETE FROM " + table + " WHERE Username = ?");
        pst.setString(1, username);
        pst.executeUpdate();
    }

    public void moveToScreen(String screenName) throws IOException, SQLException {
        statement.close();
        connection.close();
        Scene scene = Main.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource(screenName)));
        Main.getPrimaryStage().setScene(scene);
    }
}
