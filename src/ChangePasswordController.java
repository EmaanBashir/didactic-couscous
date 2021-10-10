import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;

public class ChangePasswordController {
    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();
    private String username = MainScreenController.username;
    @FXML
    Label stateLabel, usernameLabel, currentPasswordLabel, newPasswordLabel, verifyPasswordLabel;
    @FXML
    TextField currentPasswordTextField, verifyPasswordTextField, newPasswordTextField;

    public ChangePasswordController() throws SQLException {
    }

    public void initialize() {
        clearLabels();
    }

    public void changePassword() throws SQLException, IOException {
        try {
            String current_password = currentPasswordTextField.getText();
            String newPassword = newPasswordTextField.getText();
            String verifyPassword = verifyPasswordTextField.getText();
            if (isEmpty(current_password) || isEmpty(newPassword) || isEmpty(verifyPassword)) {
                throw new NullPointerException();
            }
            PreparedStatement pst = connection.prepareStatement("SELECT Password FROM UserInfo WHERE UserName = ?");
            pst.setString(1, username);
            ResultSet result = pst.executeQuery();
            if (result.next()) {
                if (!current_password.equals(result.getString("Password"))) {
                    throw new IllegalArgumentException();
                }
            }
            if (newPassword.length() < 8) {
                throw new IndexOutOfBoundsException();
            }
            if (!newPassword.equals(verifyPassword)) {
                throw new InvalidObjectException("");
            }
            setPassword(newPassword);
            moveToScreen("MainScreen.fxml");
        } catch (NullPointerException e) {
            clearLabels();
            stateLabel.setText("Required fields Empty!");
        } catch (IllegalArgumentException e) {
            clearLabels();
            currentPasswordLabel.setText("Incorrect Password!");
        } catch (InvalidObjectException e) {
            clearLabels();
            verifyPasswordLabel.setText("Passwords do not match!");
        } catch (IndexOutOfBoundsException e) {
            clearLabels();
            newPasswordLabel.setText("Password must be 8 characters long!");
        }
    }

    public void setPassword(String password) throws SQLException {
        PreparedStatement pst = connection.prepareStatement("UPDATE UserInfo SET Password = ?, RememberMe = false WHERE Username = ?");
        pst.setString(1, password);
        pst.setString(2, username);
        pst.executeUpdate();
    }

    public boolean isEmpty(String string) {
        return string.replaceAll("\\s+", "").equals("");
    }

    public void clearLabels() {
        stateLabel.setText("");
        usernameLabel.setText("USERNAME:   " + username);
        currentPasswordLabel.setText("");
        newPasswordLabel.setText("");
        verifyPasswordLabel.setText("");
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
