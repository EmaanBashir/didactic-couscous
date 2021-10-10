import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;
import java.util.ArrayList;

public class EditInstructorController {

    @FXML
    TextField teacherNameTextField;
    @FXML
    Label stateLabel;
    @FXML
    CheckComboBox subjectsCheckComboBox;
    private Database database;
    private Statement statement;
    private Connection connection;
    public static String instructorName;
    private ArrayList<String> previousSubjects;
    private String username = MainScreenController.username;

    public void initialize() throws SQLException {
        database = new Database();
        statement = database.getStatement();
        connection = database.getConnection();
        teacherNameTextField.setText(instructorName);
        stateLabel.setText("");
        ObservableList<String> subjectNames = FXCollections.observableArrayList();
        PreparedStatement pst1 = connection.prepareStatement("SELECT SubjectName FROM Subjects WHERE Username = ?");
        pst1.setString(1, username);
        ResultSet result = pst1.executeQuery();
        while (result.next()) {
            subjectNames.add(result.getString("SubjectName"));
        }
        FXCollections.sort(subjectNames, (f1, f2) -> f1.compareTo(f2));
        subjectsCheckComboBox.getItems().setAll(subjectNames);
        PreparedStatement pst = connection.prepareStatement("SELECT Subject FROM Teachers WHERE TeacherName = ? AND Username = ?");
        pst.setString(1, instructorName);
        pst.setString(2, username);
        result = pst.executeQuery();
        previousSubjects = new ArrayList<String>();
        while (result.next()) {
            previousSubjects.add(result.getString("Subject"));
        }
        for (String subject : previousSubjects) {
            subjectsCheckComboBox.getCheckModel().check(subject);
        }
        pst.close();

    }

    public void updateInstructor() throws SQLException, IllegalArgumentException, IOException {
        try {
            String name = teacherNameTextField.getText();
            ObservableList<String> subjects = subjectsCheckComboBox.getCheckModel().getCheckedItems();
            if (((name.replaceAll("\\s+", "")).equals("")) || (subjects.isEmpty())) {
                throw new IllegalArgumentException();
            }
            PreparedStatement pst = connection.prepareStatement("SELECT TeacherName FROM Teachers WHERE TeacherName != ? AND Username = ?");
            pst.setString(1, instructorName);
            pst.setString(2, username);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                if ((result.getString("TeacherName").replaceAll("\\s+", "").toLowerCase()).equals(name.replaceAll("\\s+", "").toLowerCase())) {
                    throw new InvalidObjectException("Teacher name should be unique");
                }
            }
            ArrayList<String> removedSubjects = new ArrayList<String>();
            for (String subject : previousSubjects) {
                if (!subjects.contains(subject)) {
                    removedSubjects.add(subject);
                }
            }
            boolean shouldUpdate = true;
            if (!removedSubjects.isEmpty()) {
                PreparedStatement pst1 = connection.prepareStatement("SELECT Class, Subject FROM Lectures WHERE Teacher = ? AND Username = ?");
                pst1.setString(1, instructorName);
                pst1.setString(2, username);
                result = pst1.executeQuery();
                while (result.next()) {
                    if (removedSubjects.contains(result.getString("Subject"))) {
                        shouldUpdate = false;
                        String message = "Cannot remove " + result.getString("Subject") + " from " + instructorName +
                                "'s subjects.\n" + instructorName + " teaches " + result.getString("Subject") + " to " + result.getString("Class");
                        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.OK) {
                            alert.close();
                        }
                        break;
                    }
                }
            }
            if (shouldUpdate) {
                update(name, subjects);
                moveToScreen("EditingScreen.fxml");
            }
        } catch (InvalidObjectException e) {
            stateLabel.setText("Instructor name already exists!");
            teacherNameTextField.clear();
        } catch (IllegalArgumentException e) {
            stateLabel.setText("Invalid input!");
            teacherNameTextField.clear();
        }
    }

    public void update(String name, ObservableList<String> subjects) throws SQLException {
        PreparedStatement pst1 = connection.prepareStatement("DELETE FROM Teachers WHERE TeacherName = ? AND Username = ?");
        pst1.setString(1, instructorName);
        pst1.setString(2, username);
        pst1.executeUpdate();

        for (String subject : subjects) {
            PreparedStatement pst2 = connection.prepareStatement("INSERT INTO Teachers(Username, TeacherName, Subject) VALUES(?,?,?)");
            pst2.setString(1, username);
            pst2.setString(2, name);
            pst2.setString(3, subject);
            pst2.executeUpdate();
            pst2.close();
        }
        if (!instructorName.equals(name)) {
            PreparedStatement pst3 = connection.prepareStatement("UPDATE Lectures SET Teacher = ? WHERE Teacher = ? AND Username = ?");
            pst3.setString(1, name);
            pst3.setString(2, instructorName);
            pst3.setString(3, username);
            pst3.executeUpdate();
        }
    }

    public void deleteInstructor() throws SQLException, IOException {
        PreparedStatement pst = connection.prepareStatement("SELECT Class FROM Lectures WHERE Teacher = ? AND Username = ?");
        pst.setString(1, instructorName);
        pst.setString(2, username);
        ResultSet result = pst.executeQuery();
        boolean shouldDelete = true;
        while (result.next()) {
            shouldDelete = false;
            String message = "Instructor cannot be deleted!\n" + result.getString("Class") + " is taught by the instructor.";
            Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
            break;
        }
        if (shouldDelete) {
            PreparedStatement pst1 = connection.prepareStatement("DELETE FROM Teachers WHERE TeacherName = ? AND Username = ?");
            pst1.setString(1, instructorName);
            pst1.setString(2, username);
            pst1.executeUpdate();
            moveToScreen("EditingScreen.fxml");
        }
    }

    public void back() throws IOException, SQLException {
        moveToScreen("EditingScreen.fxml");
    }

    public void moveToScreen(String screenName) throws IOException, SQLException {
        statement.close();
        database.getConnection().close();
        Scene scene = Main.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource(screenName)));
        Main.getPrimaryStage().setScene(scene);
    }
}
