import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;

public class LoginScreenController {
    @FXML
    TextField usernameTextField, passwordTextField;
    @FXML
    Label usernameError, passwordError;
    @FXML
    CheckBox rememberMeCheckBox;
    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();

    public LoginScreenController() throws SQLException {
    }

    public void initialize() throws SQLException {
        usernameError.setText("");
        passwordError.setText("");
        ResultSet result = statement.executeQuery("SELECT UserName, Password, RememberMe FROM UserInfo");
        while (result.next()) {
            if (result.getBoolean("RememberMe") == true) {
                usernameTextField.setText(result.getString("Username"));
                passwordTextField.setText(result.getString("Password"));
                rememberMeCheckBox.setSelected(true);
            }
        }
    }

    public void login() throws SQLException, IOException {
        try {
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();
            if (username.replaceAll("\\s+", "").equals("") || password.replaceAll("\\s+", "").equals("")) {
                throw new NullPointerException();
            }
            PreparedStatement pst = connection.prepareStatement("SELECT Password FROM UserInfo WHERE Username = ?");
            pst.setString(1, username);
            ResultSet result = pst.executeQuery();
            if (!result.next()) {
                throw new IllegalArgumentException();
            }
            if (!password.equals(result.getString("Password"))) {
                throw new InvalidObjectException("");
            }
            if (rememberMeCheckBox.isSelected()) {
                rememberMe(username);
            } else {
                PreparedStatement pst1 = connection.prepareStatement("UPDATE UserInfo SET RememberMe = false WHERE Username = ?");
                pst1.setString(1, username);
                pst1.executeUpdate();
            }
            MainScreenController.username = username;
            moveToScreen("MainScreen.fxml");

        } catch (IllegalArgumentException e) {
            passwordError.setText("");
            usernameError.setText("Incorrect Username!");
        } catch (InvalidObjectException e) {
            usernameError.setText("");
            passwordError.setText("Incorrect Password!");
        } catch (NullPointerException e) {
            passwordError.setText("");
            usernameError.setText("Required Fields Empty!");
        }
    }

    public void rememberMe(String username) throws SQLException {
        statement.execute("UPDATE UserInfo SET RememberMe = false");
        PreparedStatement pst = connection.prepareStatement("UPDATE UserInfo SET RememberMe = true WHERE Username = ?");
        pst.setString(1, username);
        pst.executeUpdate();
    }

    public void signUp() throws SQLException, IOException {
        moveToScreen("SignUpScreen.fxml");
    }

    public void forgotPassword() throws SQLException, IOException {
        moveToScreen("ForgotPassword.fxml");
    }

    public void moveToScreen(String screenName) throws IOException, SQLException {
        statement.close();
        connection.close();
        Scene scene = Main.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource(screenName)));
        Main.getPrimaryStage().setScene(scene);
    }

}
