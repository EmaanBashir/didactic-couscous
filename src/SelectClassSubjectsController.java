import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.*;

public class SelectClassSubjectsController {
    private ComboBox[] subjectComboBoxes;
    private ComboBox[] teacherComboBoxes;
    private Database database;
    private Statement statement;
    private Connection connection;
    public static String className;
    public static int noOfSubjects;
    public static int noOfStudents;
    private String username = MainScreenController.username;
    int n;
    @FXML
    Button addSubjectsButton;
    @FXML
    AnchorPane anchorPane;
    @FXML
    Label classNameLabel, stateLabel;

    public void initialize() throws SQLException {
        database = new Database();
        statement = database.getStatement();
        connection = database.getConnection();
        stateLabel.setText("");
        classNameLabel.setText(className);
        n = noOfSubjects;
        double x = (addSubjectsButton.getLayoutY() - classNameLabel.getLayoutY()) / (n + 1);
        subjectComboBoxes = new ComboBox[n];
        teacherComboBoxes = new ComboBox[n];
        for (int i = 0; i < n; i++) {
            ComboBox comboBox1 = new ComboBox();
            ComboBox comboBox2 = new ComboBox();
            comboBox1.setLayoutX(250);
            comboBox1.setLayoutY(classNameLabel.getLayoutY() + ((i + 1) * x));
            comboBox1.setPromptText("SELECT SUBJECT");
            comboBox1.setStyle("-fx-border-color: BLACK;");
            comboBox1.setPrefHeight(35);
            comboBox1.setPrefWidth(195);
            comboBox1.setCursor(Cursor.HAND);
            comboBox2.setLayoutX(560);
            comboBox2.setLayoutY(classNameLabel.getLayoutY() + ((i + 1) * x));
            comboBox2.setPromptText("SELECT INSTRUCTOR");
            comboBox2.setStyle("-fx-border-color: BLACK;");
            comboBox2.setPrefWidth(195);
            comboBox2.setPrefHeight(35);
            comboBox2.setCursor(Cursor.HAND);
            anchorPane.getChildren().add(comboBox1);
            anchorPane.getChildren().add(comboBox2);
            subjectComboBoxes[i] = comboBox1;
            teacherComboBoxes[i] = comboBox2;
        }
        initializeComboBoxes(n);
    }

    public void initializeComboBoxes(int n) throws SQLException {
        ObservableList<String> subjectNames = FXCollections.observableArrayList();
        PreparedStatement pst = connection.prepareStatement("SELECT SubjectName FROM Subjects WHERE Username = ?");
        pst.setString(1, username);
        ResultSet result1 = pst.executeQuery();
        while (result1.next()) {
            subjectNames.add(result1.getString("SubjectName"));
        }
        ObservableList<String> teacherNames = FXCollections.observableArrayList();
        PreparedStatement pst1 = connection.prepareStatement("SELECT TeacherName FROM Teachers WHERE Username = ?");
        pst1.setString(1, username);
        ResultSet result2 = pst1.executeQuery();
        while (result2.next()) {
            String teacher = result2.getString("TeacherName");
            if (!teacherNames.contains(teacher))
                teacherNames.add(teacher);
        }

        FXCollections.sort(subjectNames, (f1, f2) -> f1.compareTo(f2));
        FXCollections.sort(teacherNames, (f1, f2) -> f1.compareTo(f2));
        for (int i = 0; i < n; i++) {
            subjectComboBoxes[i].getItems().setAll(subjectNames);
            teacherComboBoxes[i].getItems().setAll(teacherNames);
        }
    }


    public void addSubjects() throws SQLException, IOException {
        try {
            String name = classNameLabel.getText();
            String temp = "";
            for (int i = 0; i < n; i++) {
                String subject = (String) subjectComboBoxes[i].getSelectionModel().getSelectedItem();
                String teacher = (String) teacherComboBoxes[i].getSelectionModel().getSelectedItem();
                if (subject == null || teacher == null) {
                    throw new NullPointerException();
                }
                if (subject == temp) {
                    throw new IllegalArgumentException();
                } else {
                    temp = subject;
                }
                PreparedStatement pst = connection.prepareStatement("SELECT TeacherName FROM Teachers WHERE Subject = ? AND TeacherName = ? AND Username = ?");
                pst.setString(1, subject);
                pst.setString(2, teacher);
                pst.setString(3, username);
                ResultSet result = pst.executeQuery();
                if (!result.next()) {
                    throw new InvalidObjectException("");
                }
            }
            for (int i = 0; i < n; i++) {
                String subject = (String) subjectComboBoxes[i].getSelectionModel().getSelectedItem();
                String teacher = (String) teacherComboBoxes[i].getSelectionModel().getSelectedItem();
                saveLecture(name, subject, teacher);
                saveLab(name, subject);

            }
            saveClass();
            moveToScreen("AddClass.fxml");

        } catch (NullPointerException e) {
            stateLabel.setText("Required Fields are Empty");
        } catch (IllegalArgumentException e) {
            stateLabel.setText("Same subject selected more than once");
        } catch (InvalidObjectException e) {
            stateLabel.setText("Invalid Input");
        }

    }

    public void saveLecture(String className, String subject, String teacher) throws SQLException {
        PreparedStatement pst1 = connection.prepareStatement("SELECT LectureCreditHours FROM Subjects WHERE SubjectName = ? AND Username = ?");
        pst1.setString(1, subject);
        pst1.setString(2, username);
        ResultSet result = pst1.executeQuery();
        int lectureCreditHours = result.getInt("LectureCreditHours");
        for (int i = 0; i < lectureCreditHours; i++) {
            String sq1 = "INSERT INTO Lectures(Username, Class, Subject, Teacher) VALUES(?,?,?,?)";
            PreparedStatement pst = connection.prepareStatement(sq1);
            pst.setString(1, username);
            pst.setString(2, className);
            pst.setString(3, subject);
            pst.setString(4, teacher);
            pst.executeUpdate();
            pst1.close();
            pst.close();
        }
    }

    public void saveLab(String className, String subject) throws SQLException {
        PreparedStatement pst1 = connection.prepareStatement("SELECT LabCreditHours FROM Subjects WHERE SubjectName = ? AND Username = ?");
        pst1.setString(1, subject);
        pst1.setString(2, username);
        ResultSet result = pst1.executeQuery();
        int labCreditHours = result.getInt("LabCreditHours");
        for (int i = 0; i < labCreditHours; i++) {
            String sq1 = "INSERT INTO Labs(Username, Class, Subject) VALUES(?,?,?)";
            PreparedStatement pst = connection.prepareStatement(sq1);
            pst.setString(1, username);
            pst.setString(2, className);
            pst.setString(3, subject);
            pst.executeUpdate();
            pst1.close();
            pst.close();
        }
    }

    public void saveClass() throws SQLException {
        String sq2 = "INSERT INTO Classes(Username, ClassName, NoOfStudents, NoOfSubjects) VALUES(?,?,?,?)";
        PreparedStatement pst2 = connection.prepareStatement(sq2);
        pst2.setString(1, username);
        pst2.setString(2, className);
        pst2.setInt(3, noOfStudents);
        pst2.setInt(4, noOfSubjects);
        pst2.executeUpdate();
        pst2.close();
    }

    public void moveToScreen(String screenName) throws IOException, SQLException {

        statement.close();
        connection.close();
        Scene scene = Main.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource(screenName)));
        Main.getPrimaryStage().setScene(scene);
    }

    public void back() throws IOException, SQLException {
        moveToScreen("AddClass.fxml");
    }
}
