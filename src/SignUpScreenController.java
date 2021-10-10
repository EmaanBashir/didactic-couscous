import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;

public class SignUpScreenController {
    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();
    @FXML
    Label errorLabel;
    @FXML
    TextField firstNameTextField, lastNameTextField, usernameTextField, instituteNameTextField, emailTextField, passwordTextField, confirmPasswordTextField;
    @FXML
    RadioButton schoolRadioButton, universityRadioButton, collegeRadioButton;

    public SignUpScreenController() throws SQLException {
    }

    public void initialize() {
        errorLabel.setText("");
        clearFields();
    }

    public void clearFields() {
        firstNameTextField.clear();
        lastNameTextField.clear();
        usernameTextField.clear();
        instituteNameTextField.clear();
        emailTextField.clear();
        passwordTextField.clear();
        confirmPasswordTextField.clear();
        schoolRadioButton.setSelected(false);
        collegeRadioButton.setSelected(false);
        universityRadioButton.setSelected(false);
    }

    public void create() throws SQLException, IOException {
        try {
            String username = usernameTextField.getText();
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String institute = "";
            if (schoolRadioButton.isSelected()) {
                institute = "school";
            } else if (collegeRadioButton.isSelected()) {
                institute = "college";
            } else if (universityRadioButton.isSelected()) {
                institute = "university";
            }
            String instituteName = instituteNameTextField.getText();
            String email = emailTextField.getText();
            String password = passwordTextField.getText();
            String confirmPassword = confirmPasswordTextField.getText();
            if (isEmpty(usernameTextField) || isEmpty(firstNameTextField) || isEmpty(lastNameTextField)
                    || isEmpty(instituteNameTextField) || isEmpty(emailTextField) || isEmpty(passwordTextField)
                    || isEmpty(confirmPasswordTextField) || institute.equals("") ||
                    !(schoolRadioButton.isSelected() || collegeRadioButton.isSelected() || universityRadioButton.isSelected())) {
                throw new NullPointerException();
            }
            PreparedStatement pst = connection.prepareStatement("SELECT Username FROM UserInfo WHERE Username = ?");
            pst.setString(1, username);
            ResultSet result = pst.executeQuery();
            while (result.next()) {

                throw new IllegalArgumentException();

            }
            if (!password.equals(confirmPassword)) {
                throw new InvalidObjectException("");
            }
            if (password.length() < 8) {
                throw new IndexOutOfBoundsException();
            }
            createAccount(username, firstName, lastName, institute, instituteName, email, password);
            MainScreenController.username = username;
            database.setTimings();
            moveToScreen("MainScreen.fxml");
        } catch (IllegalArgumentException e) {
            errorLabel.setText("Username not available!");
        } catch (InvalidObjectException e) {
            errorLabel.setText("Passwords do not match!");
        } catch (NullPointerException e) {
            errorLabel.setText("Required fields empty!");
        } catch (IndexOutOfBoundsException e) {
            errorLabel.setText("Password less than 8 characters");
        }
    }

    public void createAccount(String username, String firstName, String lastName, String institute, String instituteName, String email, String password) throws SQLException {
        PreparedStatement pst = connection.prepareStatement("INSERT INTO UserInfo(Username, FirstName, LastName, Institute, InstituteName, Email, Password, RememberMe) VALUES(?,?,?,?,?,?,?,?)");
        pst.setString(1, username);
        pst.setString(2, firstName);
        pst.setString(3, lastName);
        pst.setString(4, institute);
        pst.setString(5, instituteName);
        pst.setString(6, email);
        pst.setString(7, password);
        pst.setBoolean(8, false);
        pst.executeUpdate();
    }

    public boolean isEmpty(TextField textField) {
        return textField.getText().replaceAll("\\s+", "").equals("");
    }

    public void schoolSelected() {
        collegeRadioButton.setSelected(false);
        universityRadioButton.setSelected(false);
    }

    public void collegeSelected() {
        schoolRadioButton.setSelected(false);
        universityRadioButton.setSelected(false);
    }

    public void universitySelected() {
        schoolRadioButton.setSelected(false);
        collegeRadioButton.setSelected(false);
    }

    public void back() throws SQLException, IOException {
        moveToScreen("LoginScreen.fxml");
    }

    public void moveToScreen(String screenName) throws IOException, SQLException {
        statement.close();
        connection.close();
        Scene scene = Main.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource(screenName)));
        Main.getPrimaryStage().setScene(scene);
    }
}
