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
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPasswordController {
    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();
    private String username, newPassword;
    @FXML
    Label stateLabel, usernameLabel, emailLabel;
    @FXML
    TextField usernameTextField, emailTextField;

    public ForgotPasswordController() throws SQLException {
    }

    public void initialize() {
        stateLabel.setText("");
        usernameLabel.setText("");
        emailLabel.setText("");
    }

    public void submit() throws Exception {
        try {
            username = usernameTextField.getText();
            String email = emailTextField.getText();
            if (isEmpty(username) || isEmpty(email)) {
                throw new NullPointerException();
            }
            PreparedStatement pst = connection.prepareStatement("SELECT Email FROM UserInfo WHERE Username = ?");
            pst.setString(1, username);
            ResultSet result = pst.executeQuery();
            if (result.next()) {
                if (!email.equals(result.getString("Email"))) {
                    throw new InvalidObjectException("");
                }
            } else {
                throw new IllegalArgumentException();
            }
            changePassword();
            sendEmail(email);
            showAlert();
        } catch (NullPointerException e) {
            clearLabels();
            stateLabel.setText("Required Fields Empty!");
        } catch (IllegalArgumentException e) {
            clearLabels();
            usernameLabel.setText("Incorrect Username!");
        } catch (InvalidObjectException e) {
            clearLabels();
            emailLabel.setText("Incorrect Email Address");
        }
    }

    public void changePassword() throws SQLException {
        Random random = new Random();
        newPassword = "";
        for (int i = 0; i < 8; i++) {
            newPassword += (char) (random.nextInt(26) + 'a');
        }
        PreparedStatement pst = connection.prepareStatement("UPDATE UserInfo SET Password = ?, RememberME = false WHERE Username = ?");
        pst.setString(1, newPassword);
        pst.setString(2, username);
        pst.executeUpdate();
    }

    public void sendEmail(String recipient) throws Exception {
        String username = "timetablegenerator@hotmail.com";
        String password = "a@1b@2c@3";
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.outlook.com");
        properties.put("mail.smtp.port", "587");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = prepareMessage(session, username, recipient);
        Transport.send(message);
    }

    private Message prepareMessage(Session session, String username, String recipient) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress((recipient)));
            message.setSubject("New Password");
            message.setText("Your password has been reset.\nThe new pasword is:         " + newPassword + "\nYou can login using this password.");
            return message;
        } catch (Exception ex) {
            Logger.getLogger(ForgotPasswordController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void showAlert() throws SQLException, IOException {
        Alert alert = new Alert(Alert.AlertType.NONE, "Your password has been reset. Check your email to get your new password.", ButtonType.OK);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            alert.close();
            moveToScreen("LoginScreen.fxml");
        }
    }

    public void clearLabels() {
        stateLabel.setText("");
        usernameLabel.setText("");
        emailLabel.setText("");
    }

    public boolean isEmpty(String string) {
        return string.replaceAll("\\s+", "").equals("");
    }

    public void back() throws IOException, SQLException {
        moveToScreen("LoginScreen.fxml");
    }

    public void moveToScreen(String screenName) throws IOException, SQLException {
        statement.close();
        database.getConnection().close();
        Scene scene = Main.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource(screenName)));
        Main.getPrimaryStage().setScene(scene);
    }
}
