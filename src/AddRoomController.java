import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;

public class AddRoomController {
    @FXML
    TextField roomCapacityTextField;
    @FXML
    Label stateLabel, roomNumberLabel;
    private Database db;
    private Statement statement;
    private Connection connection;
    private String username = MainScreenController.username;

    public void initialize() throws SQLException {
        db = new Database();
        statement = db.getStatement();
        connection = db.getConnection();
        PreparedStatement pst = connection.prepareStatement("SELECT Number FROM Rooms WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        int roomNumber = 0;
        while (result.next()) {
            roomNumber = result.getInt("Number");
        }
        roomCapacityTextField.clear();
        stateLabel.setText("");
        roomNumberLabel.setText(Integer.toString(roomNumber + 1));
    }

    @FXML
    public void addRoom() throws IllegalArgumentException, SQLException {
        try {
            int capacity = Integer.parseInt(roomCapacityTextField.getText());
            if (capacity < 0) {
                throw new IllegalArgumentException();
            }
            int roomNumber = Integer.parseInt(roomNumberLabel.getText());
            saveRoom(roomNumber, capacity);
            roomCapacityTextField.clear();
            stateLabel.setText("Room added!");
            roomNumberLabel.setText(Integer.toString(roomNumber + 1));
        } catch (IllegalArgumentException e) {
            roomCapacityTextField.clear();
            stateLabel.setText("Invalid input");
        }
    }

    @FXML
    public void back() throws IOException, SQLException {
        moveToScreen("MainScreen.fxml");
    }

    public void saveRoom(int roomNumber, int capacity) throws SQLException {
        String sq1 = "INSERT INTO Rooms(Username, Number, Capacity) VALUES(?,?,?)";
        PreparedStatement pst = connection.prepareStatement(sq1);
        pst.setString(1, username);
        pst.setInt(2, roomNumber);
        pst.setInt(3, capacity);
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
