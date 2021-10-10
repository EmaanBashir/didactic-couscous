import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;


public class AddTeacherController {

    @FXML
    TextField teacherNameTextField;
    @FXML
    Label stateLabel;
    @FXML
    CheckComboBox subjectsCheckComboBox;
    private Database database;
    private Statement statement;
    private Connection connection;
    private String username = MainScreenController.username;

    public void initialize() throws SQLException {
        database = new Database();
        statement = database.getStatement();
        connection = database.getConnection();
        teacherNameTextField.clear();
        stateLabel.setText("");
        ObservableList<String> subjectNames = FXCollections.observableArrayList();
        PreparedStatement pst = connection.prepareStatement("SELECT SubjectName FROM Subjects WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        while (result.next()) {
            subjectNames.add(result.getString("SubjectName"));
        }
        FXCollections.sort(subjectNames, (f1, f2) -> f1.compareTo(f2));
        subjectsCheckComboBox.getItems().setAll(subjectNames);
    }

    public void addTeacher() throws SQLException, IllegalArgumentException {
        try {
            String name = teacherNameTextField.getText();
            ObservableList<String> subjects = subjectsCheckComboBox.getCheckModel().getCheckedItems();
            if (((name.replaceAll("\\s+", "")).equals("")) || (subjects.isEmpty())) {
                throw new IllegalArgumentException();
            }
            PreparedStatement pst = connection.prepareStatement("SELECT TeacherName FROM Teachers WHERE Username = ?");
            pst.setString(1, username);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                if ((result.getString("TeacherName").replaceAll("\\s+", "").toLowerCase()).equals(name.replaceAll("\\s+", "").toLowerCase())) {
                    throw new InvalidObjectException("Teacher name should be unique");
                }
            }
            saveTeacher(name, subjects);
            teacherNameTextField.clear();
            subjectsCheckComboBox.getCheckModel().clearChecks();
            stateLabel.setText("Instructor Added!");
        } catch (InvalidObjectException e) {
            stateLabel.setText("Instructor already exists!");
            teacherNameTextField.clear();
            subjectsCheckComboBox.getCheckModel().clearChecks();
        } catch (IllegalArgumentException e) {
            stateLabel.setText("Invalid input!");
            teacherNameTextField.clear();
            subjectsCheckComboBox.getCheckModel().clearChecks();
        }
    }

    public void saveTeacher(String name, ObservableList<String> subjects) throws SQLException {
        String sq1 = "INSERT INTO Teachers(Username, TeacherName, Subject) VALUES(?,?,?)";
        PreparedStatement pst = connection.prepareStatement(sq1);
        for (String subject : subjects) {
            pst.setString(1, username);
            pst.setString(2, name);
            pst.setString(3, subject);
            pst.executeUpdate();
        }
        pst.close();
    }


    public void back() throws IOException, SQLException {
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
