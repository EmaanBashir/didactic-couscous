import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.SQLException;


public class Main extends Application {
    private static Stage primaryStage;
    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
        primaryStage.setTitle("Timetable Generator");
        scene = new Scene(root, 1800, 900);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Scene getScene() {
        return scene;
    }

    public static void main(String[] args) throws SQLException {
        Database database = new Database();
        database.createTables();
        database.getConnection().close();
        launch(args);
    }
}
