import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgorithm {
    private final int POPULATION_SIZE = 9;
    private final int NUMB_OF_ELITE_SCHEDULES = 1;
    private final int TOURNAMENT_SELECTION_SIZE = 3;
    private final double MUTATION_RATE = 0.1;
    Random random = new Random();

    public Population evolve(Population population) throws SQLException {
        return this.mutatePopulation(this.crossOverPopulation(population));
    }

    public Population crossOverPopulation(Population pop) throws SQLException {
        Population crossOverPop = new Population(0);
        for (int i = 0; i < NUMB_OF_ELITE_SCHEDULES; i++) {
            crossOverPop.getSchedules().add(pop.getSchedules().get(i));
        }
        int i = NUMB_OF_ELITE_SCHEDULES;
        while (i < POPULATION_SIZE) {
            Schedule schedule1 = this.selectTournamentPopulation(pop).getSchedules().get(0);
            Schedule schedule2 = this.selectTournamentPopulation(pop).getSchedules().get(0);
            crossOverPop.getSchedules().add(this.crossOverSchedule(schedule1, schedule2));
            i++;
        }
        return crossOverPop;
    }

    public Population mutatePopulation(Population population) throws SQLException {
        for (int i = NUMB_OF_ELITE_SCHEDULES; i < POPULATION_SIZE; i++) {
            this.mutateSchedule(population.getSchedules().get(i));
        }
        return population;
    }

    public Schedule crossOverSchedule(Schedule schedule1, Schedule schedule2) throws SQLException {
        Schedule crossOverSchedule = new Schedule();
        for (int i = 0; i < crossOverSchedule.getLectures().length; i++) {
            if (random.nextFloat() > 0.5) {
                crossOverSchedule.getLectures()[i] = schedule1.getLectures()[i];
            } else {
                crossOverSchedule.getLectures()[i] = schedule2.getLectures()[i];
            }
        }
        for (int i = 0; i < crossOverSchedule.getLabs().length; i++) {
            if (random.nextFloat() > 0.5) {
                crossOverSchedule.getLabs()[i] = schedule1.getLabs()[i];
            } else {
                crossOverSchedule.getLabs()[i] = schedule2.getLabs()[i];
            }
        }
        return crossOverSchedule;
    }

    public Schedule mutateSchedule(Schedule mutateSchedule) throws SQLException {
        Schedule schedule = new Schedule();
        for (int i = 0; i < mutateSchedule.getLectures().length; i++) {
            if (MUTATION_RATE > random.nextFloat()) {
                mutateSchedule.getLectures()[i] = schedule.getLectures()[i];
            }
        }
        for (int i = 0; i < mutateSchedule.getLabs().length; i++) {
            if (MUTATION_RATE > random.nextFloat()) {
                mutateSchedule.getLabs()[i] = schedule.getLabs()[i];
            }
        }
        return mutateSchedule;
    }

    public Population selectTournamentPopulation(Population pop) throws SQLException {
        Population tournamentPop = new Population(0);
        for (int i = 0; i < TOURNAMENT_SELECTION_SIZE; i++) {
            tournamentPop.getSchedules().add(pop.getSchedules().get(random.nextInt(POPULATION_SIZE)));
        }
        tournamentPop.getSchedules().sort(Comparator.comparing(x -> x.getFitness()));
        Collections.reverse(tournamentPop.getSchedules());
        return tournamentPop;
    }
}
