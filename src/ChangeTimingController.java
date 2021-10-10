import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;
import java.util.ArrayList;

public class ChangeTimingController {
    @FXML
    Spinner<Integer> spinner11, spinner12, spinner13, spinner14,
            spinner21, spinner22, spinner23, spinner24,
            spinner31, spinner32, spinner33, spinner34,
            spinner41, spinner42, spinner43, spinner44,
            spinner51, spinner52, spinner53, spinner54,
            spinner61, spinner62, spinner63, spinner64,
            spinner71, spinner72, spinner73, spinner74;
    @FXML
    Label stateLabel;

    private Database database = new Database();
    private Statement statement = database.getStatement();
    private Connection connection = database.getConnection();
    private String username = MainScreenController.username;
    private Spinner[] spinners;

    public ChangeTimingController() throws SQLException {
    }

    public void initialize() throws SQLException {
        stateLabel.setText("");
        spinners = new Spinner[]{spinner11, spinner12, spinner13, spinner14,
                spinner21, spinner22, spinner23, spinner24,
                spinner31, spinner32, spinner33, spinner34,
                spinner41, spinner42, spinner43, spinner44,
                spinner51, spinner52, spinner53, spinner54,
                spinner61, spinner62, spinner63, spinner64,
                spinner71, spinner72, spinner73, spinner74};

        ObservableList<String> hourValues = FXCollections.observableArrayList();
        ObservableList<String> minValues = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            if (i / 10 == 0) {
                hourValues.add("0" + i);
            } else {
                hourValues.add(Integer.toString(i));
            }
        }
        for (int i = 0; i < 60; i++) {
            if (i / 10 == 0) {
                minValues.add("0" + i);
            } else {
                minValues.add(Integer.toString(i));
            }
        }
        String[][] defaultTiming = getDefaultTiming();
        int i = 0, j = 0;
        for (int k = 0; k < spinners.length; k++) {
            SpinnerValueFactory<String> valueFactory;
            if (k % 2 == 0) {
                valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(hourValues);
            } else {
                valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(minValues);
            }
            valueFactory.setValue(defaultTiming[i][j]);
            spinners[k].setValueFactory(valueFactory);
            if (j == 3) {
                j = -1;
                i++;
            }
            j++;
        }
    }

    public String[][] getDefaultTiming() throws SQLException {
        PreparedStatement pst = connection.prepareStatement("SELECT Timing FROM LectureSlots WHERE id%10 = 1 AND Username = ?");
        pst.setString(1, username);
        ResultSet result = pst.executeQuery();
        ArrayList<String> timings = new ArrayList<String>();
        while (result.next()) {
            timings.add(result.getString("Timing"));
        }
        String[][] defaultTimings = new String[timings.size()][4];
        String[] time;
        for (int i = 0; i < timings.size(); i++) {
            time = timings.get(i).split(" - ");
            defaultTimings[i][0] = time[0].split(":")[0];
            defaultTimings[i][1] = time[0].split(":")[1];
            defaultTimings[i][2] = time[1].split(":")[0];
            defaultTimings[i][3] = time[1].split(":")[1];
        }
        return defaultTimings;
    }

    public void done() throws IOException, SQLException {
        try {
            String[] timing = new String[7];
            String time = "", input;
            int counter = 0, index = 0, st1 = 0, st2 = 0, et1 = 0, et2 = 0;
            for (Spinner spinner : spinners) {
                input = (String) spinner.getValue();
                time += input;
                if (counter == 0) {
                    time += ":";
                    st1 = Integer.parseInt(input);
                } else if (counter == 1) {
                    time += " - ";
                    st2 = Integer.parseInt(input);
                } else if (counter == 2) {
                    time += ":";
                    et1 = Integer.parseInt(input);
                } else if (counter == 3) {
                    et2 = Integer.parseInt(input);
                    if (((st1 - et1) > 0) || (((st1 - et1) == 0) && ((st2 - et2) >= 0))) {
                        throw new InvalidObjectException("");
                    }
                    timing[index] = time;
                    counter = -1;
                    index++;
                    time = "";
                }
                counter++;
            }
            saveTiming(timing);
            moveToScreen("MainScreen.fxml");
        } catch (InvalidObjectException e) {
            stateLabel.setText("Invalid Input!");
        }
    }

    public void saveTiming(String[] timing) throws SQLException {
        String sq1 = "UPDATE LectureSlots SET Timing = ? WHERE id/10 = ? AND Username = ?";
        PreparedStatement pst = connection.prepareStatement(sq1);
        for (int i = 1; i < 8; i++) {
            pst.setString(1, timing[i - 1]);
            pst.setInt(2, i);
            pst.setString(3, username);
            pst.execute();
        }
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
