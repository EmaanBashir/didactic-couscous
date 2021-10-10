import java.sql.SQLException;
import java.util.ArrayList;

public class Population {
    private ArrayList<Schedule> schedules;

    public Population(int size) throws SQLException {
        this.schedules = new ArrayList<Schedule>();

        for (int i = 0; i < size; i++) {
            this.schedules.add(new Schedule());
        }
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }
}
