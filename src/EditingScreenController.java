import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import java.io.IOException;
import java.sql.*;

public class EditingScreenController {
    Database database = new Database();
    Statement statement = database.getStatement();
    Connection connection = database.getConnection();
    ObservableList<String> rooms;
    ObservableList<String> subjects;
    ObservableList<String> instructors;
    ObservableList<String> classes;
    private String username = MainScreenController.username;
    @FXML
    ComboBox roomsComboBox, subjectsComboBox, instructorsComboBox, classesComboBox;

    public EditingScreenController() throws SQLException {
    }

    public void initialize() throws SQLException {
        inputRooms();
        inputSubjects();
        inputInstructors();
        inputClasses();
        initializeComboBoxes();
    }

    public void inputRooms() throws SQLException {
        rooms = FXCollections.observableArrayList();
        PreparedStatement pst = connection.prepareStatement("SELECT Number FROM Rooms WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            int number = result.getInt("Number");
            rooms.add("CR" + number);
        }
    }

    public void inputSubjects() throws SQLException {
        subjects = FXCollections.observableArrayList();
        PreparedStatement pst = connection.prepareStatement("SELECT SubjectName FROM Subjects WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            String name = result.getString("SubjectName");
            subjects.add(name);
        }
    }

    public void inputInstructors() throws SQLException {
        instructors = FXCollections.observableArrayList();
        PreparedStatement pst = connection.prepareStatement("SELECT TeacherName FROM Teachers WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            String name = result.getString("TeacherName");
            if (!instructors.contains(name)) {
                instructors.add(name);
            }
        }
    }

    public void inputClasses() throws SQLException {
        classes = FXCollections.observableArrayList();
        PreparedStatement pst = connection.prepareStatement("SELECT ClassName FROM Classes WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            String name = result.getString("ClassName");
            classes.add(name);
        }
    }

    public void initializeComboBoxes() {
        FXCollections.sort(rooms, (f1, f2) -> f1.compareTo(f2));
        FXCollections.sort(subjects, (f1, f2) -> f1.compareTo(f2));
        FXCollections.sort(instructors, (f1, f2) -> f1.compareTo(f2));
        FXCollections.sort(classes, (f1, f2) -> f1.compareTo(f2));
        roomsComboBox.getItems().setAll(rooms);
        subjectsComboBox.getItems().setAll(subjects);
        instructorsComboBox.getItems().setAll(instructors);
        classesComboBox.getItems().setAll(classes);
    }

    public void selectRoom() throws SQLException, IOException {
        String room = (String) roomsComboBox.getSelectionModel().getSelectedItem();
        int roomNumber = Integer.parseInt(room.substring(2));
        EditRoomController.roomNumber = roomNumber;
        moveToScreen("EditRoom.fxml");
    }

    public void selectSubject() throws SQLException, IOException {
        String subject = (String) subjectsComboBox.getSelectionModel().getSelectedItem();
        EditSubjectController.subjectName = subject;
        moveToScreen("EditSubject.fxml");
    }

    public void selectInstructor() throws SQLException, IOException {
        String instructor = (String) instructorsComboBox.getSelectionModel().getSelectedItem();
        EditInstructorController.instructorName = instructor;
        moveToScreen("EditInstructor.fxml");
    }

    public void selectClass() throws SQLException, IOException {
        String _class = (String) classesComboBox.getSelectionModel().getSelectedItem();
        EditClassController.className = _class;
        moveToScreen("EditClass.fxml");
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
