import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;
import java.util.ArrayList;

public class EditSubjectController {
    @FXML
    TextField courseCodeTextField, subjectNameTextField, lectureCreditHoursTextField, labCreditHoursTextField;
    @FXML
    Label stateLabel;
    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();
    private String username = MainScreenController.username;
    public static String subjectName;
    private int labch, lecturech;

    public EditSubjectController() throws SQLException {
    }

    public void initialize() throws SQLException {
        PreparedStatement pst = connection.prepareStatement("SELECT CourseCode, LectureCreditHours, LabCreditHours FROM Subjects WHERE SubjectName = ? AND Username = ?");
        pst.setString(1, subjectName);
        pst.setString(2, username);
        ResultSet result = pst.executeQuery();
        labch = result.getInt("LabCreditHours");
        lecturech = result.getInt("LectureCreditHours");

        courseCodeTextField.setText(result.getString("CourseCode"));
        subjectNameTextField.setText(subjectName);
        lectureCreditHoursTextField.setText(Integer.toString(lecturech));
        labCreditHoursTextField.setText(Integer.toString(labch));
        stateLabel.setText("");
        pst.close();
    }

    @FXML
    public void update() throws IllegalArgumentException, SQLException, IOException {
        try {
            String courseCode = courseCodeTextField.getText();
            String name = subjectNameTextField.getText();
            PreparedStatement pst1 = connection.prepareStatement("SELECT CourseCode,SubjectName FROM Subjects WHERE SubjectName != ? AND Username = ?");
            pst1.setString(1, subjectName);
            pst1.setString(2, username);
            ResultSet result = pst1.executeQuery();
            if ((courseCode.replaceAll("\\s+", "").equals("")) || (name.replaceAll("\\s+", "").equals(""))) {
                throw new IllegalArgumentException();
            }
            while (result.next()) {
                if (((result.getString("CourseCode").replaceAll("\\s+", "").toLowerCase()).equals(courseCode.replaceAll("\\s+", "").toLowerCase()))
                        || ((result.getString("SubjectName").replaceAll("\\s+", "").toLowerCase()).equals(name.replaceAll("\\s+", "").toLowerCase()))) {
                    throw new InvalidObjectException("Course code should be unique");
                }
            }
            int lectureCreditHours = Integer.parseInt(lectureCreditHoursTextField.getText());
            int labCreditHours = Integer.parseInt(labCreditHoursTextField.getText());
            if (lectureCreditHours < 0 || labCreditHours < 0) {
                throw new IllegalArgumentException();
            }
            updateSubject(courseCode, name, lectureCreditHours, labCreditHours);
            moveToScreen("EditingScreen.fxml");
        } catch (IllegalArgumentException e) {
            stateLabel.setText("Invalid input");
        } catch (InvalidObjectException e) {
            stateLabel.setText("This subject already exists");
        }
    }

    public void updateSubject(String courseCode, String name, int lectureCreditHours, int labCreditHours) throws SQLException {

        String sq1 = "UPDATE Subjects SET CourseCode = ?, SubjectName = ?, LectureCreditHours = ?, LabCreditHours = ? WHERE SubjectName = ? AND Username = ?";
        PreparedStatement pst1 = connection.prepareStatement(sq1);
        pst1.setString(1, courseCode);
        pst1.setString(2, name);
        pst1.setInt(3, lectureCreditHours);
        pst1.setInt(4, labCreditHours);
        pst1.setString(5, subjectName);
        pst1.setString(6, username);
        pst1.executeUpdate();
        pst1.close();
        handleChangeInLabCreditHrs(name, labCreditHours);
        handleChangeInLectureCreditHrs(name, lectureCreditHours);

        PreparedStatement pst4 = connection.prepareStatement("UPDATE Teachers SET Subject = ? WHERE Subject = ? AND Username = ?");
        pst4.setString(1, name);
        pst4.setString(2, subjectName);
        pst4.setString(3, username);
        pst4.executeUpdate();
        pst4.close();
        PreparedStatement pst5 = connection.prepareStatement("UPDATE Labs SET Subject = ? WHERE Subject = ? AND Username = ?");
        pst5.setString(1, name);
        pst5.setString(2, subjectName);
        pst5.setString(3, username);
        pst5.executeUpdate();
        pst5.close();
        PreparedStatement pst6 = connection.prepareStatement("UPDATE Lectures SET Subject = ? WHERE Subject = ? AND Username = ?");
        pst6.setString(1, name);
        pst6.setString(2, subjectName);
        pst6.setString(3, username);
        pst6.executeUpdate();
        pst6.close();
    }

    public void handleChangeInLabCreditHrs(String name, int labCreditHours) throws SQLException {
        if (labch != labCreditHours) {
            ArrayList<String> classes = new ArrayList<String>();
            PreparedStatement pst = connection.prepareStatement("SELECT Class FROM Labs WHERE Subject = ? AND Username = ?");
            pst.setString(1, subjectName);
            pst.setString(2, username);
            ResultSet result = pst.executeQuery();
            String _class;
            while (result.next()) {
                _class = result.getString("Class");
                if (!classes.contains(_class)) {
                    classes.add(_class);
                }
            }
            pst.close();
            int diff = labCreditHours - labch;
            if (diff > 0) {
                for (String c : classes) {
                    PreparedStatement pst2 = connection.prepareStatement("INSERT INTO Labs(Username, Class, Subject) VALUES(?,?,?)");
                    pst2.setString(1, username);
                    pst2.setString(2, c);
                    pst2.setString(3, name);
                    for (int i = 0; i < diff; i++) {
                        pst2.executeUpdate();
                    }
                }
            } else if (diff < 0) {
                PreparedStatement pst3 = connection.prepareStatement("DELETE FROM Labs WHERE Subject = ? AND Username = ?");
                pst3.setString(1, subjectName);
                pst3.setString(2, username);
                pst3.executeUpdate();
                pst3.close();
                for (String c : classes) {
                    PreparedStatement pst2 = database.getConnection().prepareStatement("INSERT INTO Labs(Username, Class, Subject) VALUES(?,?,?)");
                    pst2.setString(1, username);
                    pst2.setString(2, c);
                    pst2.setString(3, name);
                    for (int i = 0; i < labCreditHours; i++) {
                        pst2.executeUpdate();
                    }
                }
            }
        }
    }

    public void handleChangeInLectureCreditHrs(String name, int lectureCreditHours) throws SQLException {
        if (lecturech != lectureCreditHours) {
            ArrayList<String> classes = new ArrayList<String>();
            ArrayList<String> teachers = new ArrayList<String>();
            PreparedStatement pst = connection.prepareStatement("SELECT Class, Teacher FROM Lectures WHERE Subject = ? AND Username = ?");
            pst.setString(1, subjectName);
            pst.setString(2, username);
            ResultSet result = pst.executeQuery();
            String _class, teacher;
            while (result.next()) {
                _class = result.getString("Class");
                teacher = result.getString("Teacher");
                if (!classes.contains(_class)) {
                    classes.add(_class);
                    teachers.add(teacher);
                }
            }
            pst.close();
            int diff = lectureCreditHours - lecturech;
            if (diff > 0) {
                for (int c = 0; c < classes.size(); c++) {
                    PreparedStatement pst2 = connection.prepareStatement("INSERT INTO Lectures(Username, Class, Subject, Teacher) VALUES(?,?,?,?)");
                    pst2.setString(1, username);
                    pst2.setString(2, classes.get(c));
                    pst2.setString(3, name);
                    pst2.setString(4, teachers.get(c));
                    for (int i = 0; i < diff; i++) {
                        pst2.executeUpdate();
                    }
                }
            } else if (diff < 0) {
                PreparedStatement pst3 = connection.prepareStatement("DELETE FROM Lectures WHERE Subject = ? AND Username = ?");
                pst3.setString(1, subjectName);
                pst3.setString(2, username);
                pst3.executeUpdate();
                pst3.close();
                for (int c = 0; c < classes.size(); c++) {
                    PreparedStatement pst2 = connection.prepareStatement("INSERT INTO Lectures(Username, Class, Subject, Teacher) VALUES(?,?,?,?)");
                    pst2.setString(1, username);
                    pst2.setString(2, classes.get(c));
                    pst2.setString(3, name);
                    pst2.setString(4, teachers.get(c));
                    for (int i = 0; i < lectureCreditHours; i++) {
                        pst2.executeUpdate();
                    }
                }
            }
        }
    }

    public void delete() throws IOException, SQLException {
        String message = "Are you sure you want to delete the subject " + subjectName + "?";
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            alert.close();
            deleteSubject();
            moveToScreen("EditingScreen.fxml");
        }
        if (alert.getResult() == ButtonType.NO) {
            alert.close();
        }
    }

    public void deleteSubject() throws SQLException {
        PreparedStatement pst1 = connection.prepareStatement("DELETE FROM Subjects WHERE SubjectName = ? AND Username = ?");
        pst1.setString(1, subjectName);
        pst1.setString(2, username);
        pst1.executeUpdate();
        pst1.close();
        PreparedStatement pst2 = connection.prepareStatement("DELETE FROM Teachers WHERE Subject = ? AND Username = ?");
        pst2.setString(1, subjectName);
        pst2.setString(2, username);
        pst2.executeUpdate();
        pst2.close();
        PreparedStatement pst3 = connection.prepareStatement("DELETE FROM Labs WHERE Subject = ? AND Username = ?");
        pst3.setString(1, subjectName);
        pst3.setString(2, username);
        pst3.executeUpdate();
        pst3.close();
        PreparedStatement pst4 = connection.prepareStatement("DELETE FROM Lectures WHERE Subject = ? AND Username = ?");
        pst4.setString(1, subjectName);
        pst4.setString(2, username);
        pst4.executeUpdate();
        pst4.close();
    }

    public void back() throws IOException, SQLException {
        moveToScreen("EditingScreen.fxml");
    }

    public void moveToScreen(String screenName) throws IOException, SQLException {
        statement.close();
        connection.close();
        Scene scene = Main.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource(screenName)));
        Main.getPrimaryStage().setScene(scene);
    }
}
