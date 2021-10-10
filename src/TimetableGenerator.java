import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;

public class TimetableGenerator {
    private String username = MainScreenController.username;

    public Schedule generateTimetable() throws SQLException {
        int POPULATION_SIZE = 9;
        Population population = new Population(POPULATION_SIZE);
        population.getSchedules().sort(Comparator.comparing(x -> x.getFitness()));
        Collections.reverse(population.getSchedules());
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        while (population.getSchedules().get(0).getFitness() != 1.0) {
            population = geneticAlgorithm.evolve(population);
            population.getSchedules().sort(Comparator.comparing(x -> x.getFitness()));
            Collections.reverse(population.getSchedules());
        }
        return population.getSchedules().get(0);
    }

    public void saveSchedule(Schedule schedule) throws SQLException {
        Database database = new Database();
        Connection connection = database.getConnection();
        Statement statement = database.getStatement();
        PreparedStatement pst1 = connection.prepareStatement("DELETE FROM LecturesSchedule WHERE Username = ?");
        pst1.setString(1, username);
        pst1.executeUpdate();
        PreparedStatement pst2 = connection.prepareStatement("DELETE FROM LabsSchedule WHERE Username = ?");
        pst2.setString(1, username);
        pst2.executeUpdate();
        String sq1 = "INSERT INTO LecturesSchedule(Username, Class, Subject, Teacher, LectureSlotId, Room) VALUES(?,?,?,?,?,?)";
        PreparedStatement pst = connection.prepareStatement(sq1);
        for (int i = 0; i < schedule.getLectures().length; i++) {
            pst.setString(1, username);
            pst.setString(2, schedule.getLectures()[i].get_class().getName());
            pst.setString(3, schedule.getLectures()[i].getSubject().getName());
            pst.setString(4, schedule.getLectures()[i].getTeacher().getName());
            pst.setInt(5, schedule.getLectures()[i].getLectureSlot().getId());
            pst.setString(6, schedule.getLectures()[i].getRoom().getId());
            pst.executeUpdate();
        }
        sq1 = "INSERT INTO LabsSchedule(Username, Class, Subject, LectureSlotId1, LectureSlotId2, LectureSlotId3) VALUES(?,?,?,?,?,?)";
        pst = connection.prepareStatement(sq1);
        for (int i = 0; i < schedule.getLabs().length; i++) {
            pst.setString(1, username);
            pst.setString(2, schedule.getLabs()[i].get_class().getName());
            pst.setString(3, schedule.getLabs()[i].getSubject().getName());
            pst.setInt(4, schedule.getLabs()[i].getLabSlot().getSlots()[0].getId());
            pst.setInt(5, schedule.getLabs()[i].getLabSlot().getSlots()[1].getId());
            pst.setInt(6, schedule.getLabs()[i].getLabSlot().getSlots()[2].getId());
            pst.executeUpdate();
        }
        pst.close();
        connection.close();
    }
}
