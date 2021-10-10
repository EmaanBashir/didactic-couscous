import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;

public class EditClassController {

    @FXML
    TextField classNameTextField, noOfStudentsTextField, noOfSubjectsTextField;
    @FXML
    Label stateLabel;
    Database database = new Database();
    Statement statement = database.getStatement();
    Connection connection = database.getConnection();
    public static String className;
    private String username = MainScreenController.username;

    public EditClassController() throws SQLException {
    }

    public void initialize() throws SQLException {
        PreparedStatement pst = connection.prepareStatement("SELECT NoOfStudents, NoOfSubjects FROM Classes WHERE ClassName = ? AND Username = ?");
        pst.setString(1, className);
        pst.setString(2, username);
        ResultSet result = pst.executeQuery();
        classNameTextField.setText(className);
        noOfStudentsTextField.setText(Integer.toString(result.getInt("NoOfStudents")));
        noOfSubjectsTextField.setText(Integer.toString(result.getInt("NoOfSubjects")));
        stateLabel.setText("");
        pst.close();
    }

    @FXML
    public void selectSubjects() throws IllegalArgumentException, SQLException, IOException {
        try {
            String name = classNameTextField.getText();
            int noOfStudents = Integer.parseInt(noOfStudentsTextField.getText());
            int noOfSubjects = Integer.parseInt(noOfSubjectsTextField.getText());
            PreparedStatement pst = connection.prepareStatement("SELECT ClassName FROM Classes WHERE ClassName != ? AND Username = ?");
            pst.setString(1, className);
            pst.setString(2, username);
            ResultSet result = pst.executeQuery();
            if (name.replaceAll("\\s+", "").equals("")) {
                throw new IllegalArgumentException();
            }
            while (result.next()) {
                if ((result.getString("ClassName").replaceAll("\\s+", "").toLowerCase()).equals(name.replaceAll("\\s+", "").toLowerCase())) {
                    throw new InvalidObjectException("ClassName should be unique");
                }
            }
            if (noOfStudents < 0 || noOfSubjects < 0) {
                throw new IllegalArgumentException();
            }
            pst.close();
            saveClass(name, noOfStudents, noOfSubjects);
            moveToScreen("EditClassSubjects.fxml");
        } catch (IllegalArgumentException e) {
            stateLabel.setText("Invalid Input");
        } catch (InvalidObjectException e) {
            stateLabel.setText("Class name already exists");
        }
    }

    public void saveClass(String name, int noOfStudents, int noOfSubjects) {
        EditClassSubjectsController.className = name;
        EditClassSubjectsController.noOfStudents = noOfStudents;
        EditClassSubjectsController.noOfSubjects = noOfSubjects;

    }

    public void deleteClass() throws SQLException, IOException {
        String message = "Are you sure you want to delete the class \"" + className + "\" ?";
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            alert.close();
            delete();
            moveToScreen("EditingScreen.fxml");
        }
        if (alert.getResult() == ButtonType.NO) {
            alert.close();
        }
    }

    public void delete() throws SQLException {
        PreparedStatement pst1 = connection.prepareStatement("DELETE FROM Classes WHERE ClassName = ? AND Username = ?");
        pst1.setString(1, className);
        pst1.setString(2, username);
        pst1.executeUpdate();
        pst1.close();
        PreparedStatement pst2 = connection.prepareStatement("DELETE FROM Labs WHERE Class = ? AND Username = ?");
        pst2.setString(1, className);
        pst2.setString(2, username);
        pst2.executeUpdate();
        pst2.close();
        PreparedStatement pst3 = connection.prepareStatement("DELETE FROM Lectures WHERE Class = ? AND Username = ?");
        pst3.setString(1, className);
        pst3.setString(2, username);
        pst3.executeUpdate();
        pst3.close();
    }

    @FXML
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
