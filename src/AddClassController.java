import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;

public class AddClassController {

    @FXML
    TextField classNameTextField, noOfStudentsTextField, noOfSubjectsTextField;
    @FXML
    Label stateLabel;

    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();
    private String username = MainScreenController.username;

    public AddClassController() throws SQLException {
    }

    public void initialize() {
        classNameTextField.clear();
        noOfStudentsTextField.clear();
        noOfSubjectsTextField.clear();
        stateLabel.setText("");
    }

    @FXML
    public void done() throws IllegalArgumentException, SQLException, IOException {
        try {
            String name = classNameTextField.getText();
            int noOfStudents = Integer.parseInt(noOfStudentsTextField.getText());
            int noOfSubjects = Integer.parseInt(noOfSubjectsTextField.getText());
            PreparedStatement pst = connection.prepareStatement("SELECT ClassName FROM Classes WHERE Username = ?");
            pst.setString(1, username);
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
            saveClass(name, noOfStudents, noOfSubjects);
            moveToScreen("SelectClassSubjects.fxml");
        } catch (IllegalArgumentException e) {
            stateLabel.setText("Invalid Input");
        } catch (InvalidObjectException e) {
            stateLabel.setText("Class already exists");
        }
    }

    public void saveClass(String name, int noOfStudents, int noOfSubjects) {
        SelectClassSubjectsController.className = name;
        SelectClassSubjectsController.noOfStudents = noOfStudents;
        SelectClassSubjectsController.noOfSubjects = noOfSubjects;

    }

    @FXML
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
