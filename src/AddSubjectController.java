import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;

public class AddSubjectController {
    @FXML
    TextField courseCodeTextField, subjectNameTextField, lectureCreditHoursTextField, labCreditHoursTextField;
    @FXML
    Label stateLabel;
    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();
    private String username = MainScreenController.username;

    public AddSubjectController() throws SQLException {
    }

    public void initialize() {
        courseCodeTextField.clear();
        subjectNameTextField.clear();
        lectureCreditHoursTextField.clear();
        labCreditHoursTextField.clear();
        stateLabel.setText("");
    }

    @FXML
    public void addSubject() throws IllegalArgumentException, SQLException {
        try {
            String courseCode = courseCodeTextField.getText();
            String subjectName = subjectNameTextField.getText();
            PreparedStatement pst = connection.prepareStatement("SELECT CourseCode,SubjectName FROM Subjects WHERE Username = ?");
            pst.setString(1, username);
            ResultSet result = pst.executeQuery();
            if ((courseCode.replaceAll("\\s+", "").equals("")) || (subjectName.replaceAll("\\s+", "").equals(""))) {
                throw new IllegalArgumentException();
            }
            while (result.next()) {
                if (((result.getString("CourseCode").replaceAll("\\s+", "").toLowerCase()).equals(courseCode.replaceAll("\\s+", "").toLowerCase()))
                        || ((result.getString("SubjectName").replaceAll("\\s+", "").toLowerCase()).equals(subjectName.replaceAll("\\s+", "").toLowerCase()))) {
                    throw new InvalidObjectException("Course code should be unique");
                }
            }
            int lectureCreditHours = Integer.parseInt(lectureCreditHoursTextField.getText());
            int labCreditHours = Integer.parseInt(labCreditHoursTextField.getText());
            if (lectureCreditHours < 0 || labCreditHours < 0) {
                throw new IllegalArgumentException();
            }
            saveSubject(courseCode, subjectName, lectureCreditHours, labCreditHours);
            initialize();
            stateLabel.setText("Subject Added");
        } catch (IllegalArgumentException e) {
            stateLabel.setText("Invalid input");
        } catch (InvalidObjectException e) {
            stateLabel.setText("This subject already exists");
        }
    }

    public void saveSubject(String courseCode, String subjectName, int lectureCreditHours, int labCreditHours) throws SQLException {

        String sq1 = "INSERT INTO Subjects(Username, CourseCode, SubjectName, LectureCreditHours, LabCreditHours) VALUES(?,?,?,?,?)";
        PreparedStatement pst = connection.prepareStatement(sq1);
        pst.setString(1, username);
        pst.setString(2, courseCode);
        pst.setString(3, subjectName);
        pst.setInt(4, lectureCreditHours);
        pst.setInt(5, labCreditHours);
        pst.executeUpdate();
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
