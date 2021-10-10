import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.*;

public class EditRoomController {
    @FXML
    TextField roomCapacityTextField;
    @FXML
    Label stateLabel, roomNumberLabel;
    private Database db;
    private Statement statement;
    private Connection connection;
    private String username = MainScreenController.username;
    public static int roomNumber;

    public void initialize() throws SQLException {
        db = new Database();
        statement = db.getStatement();
        connection = db.getConnection();
        PreparedStatement pst = connection.prepareStatement("SELECT Capacity FROM Rooms WHERE Number = ? AND Username = ?");
        pst.setInt(1, roomNumber);
        pst.setString(2, username);
        ResultSet result = pst.executeQuery();
        roomCapacityTextField.setText(Integer.toString(result.getInt("Capacity")));
        stateLabel.setText("");
        roomNumberLabel.setText(Integer.toString(roomNumber));
    }

    @FXML
    public void done() throws IllegalArgumentException, SQLException, IOException {
        try {
            int capacity = Integer.parseInt(roomCapacityTextField.getText());
            if (capacity < 0) {
                throw new IllegalArgumentException();
            }
            updateRoom(capacity);
            moveToScreen("EditingScreen.fxml");
        } catch (IllegalArgumentException e) {
            roomCapacityTextField.clear();
            stateLabel.setText("Invalid input");
        }
    }

    @FXML
    public void back() throws IOException, SQLException {
        moveToScreen("EditingScreen.fxml");
    }

    public void updateRoom(int capacity) throws SQLException {
        String sq1 = "UPDATE Rooms SET Capacity = ? WHERE Number = ? AND Username = ?";
        PreparedStatement pst = connection.prepareStatement(sq1);
        pst.setInt(1, capacity);
        pst.setInt(2, roomNumber);
        pst.setString(3, username);
        pst.executeUpdate();
        pst.close();
    }

    public void moveToScreen(String screenName) throws IOException, SQLException {
        statement.close();
        connection.close();
        Scene scene = Main.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource(screenName)));
        Main.getPrimaryStage().setScene(scene);
    }
}
