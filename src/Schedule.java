import java.sql.SQLException;
import java.util.Random;

public class Schedule {
    private Data data;
    private Lecture[] lectures;
    private Lab[] labs;
    private int conflicts;
    private double fitness;
    private Room[] rooms;
    private boolean isFitnessChanged;
    private LectureSlot[] lectureSlots;
    private LabSlot[] labSlots;
    private int counter;

    public Schedule() throws SQLException {
        Random random = new Random();
        data = new Data();
        fitness = -1;
        isFitnessChanged = true;
        rooms = data.getRooms();
        lectureSlots = data.getLectureSlots();
        lectures = data.getLectures();
        labs = data.getLabs();
        labSlots = data.getLabSlots();
        for (Lecture lecture : lectures) {
            lecture.setId(counter);
            lecture.setLectureSlot(lectureSlots[random.nextInt(lectureSlots.length)]);
            lecture.setRoom(rooms[random.nextInt(rooms.length)]);
            counter++;
        }
        counter = 0;
        for (Lab lab : labs) {
            lab.setId(counter);
            lab.setLabSlot(labSlots[random.nextInt(labSlots.length)]);
            counter++;
        }
    }

    public double calculateFitness() {
        conflicts = 0;
        for (int i = 0; i < lectures.length; i++) {
            if (lectures[i].getRoom().getCapacity() < lectures[i].get_class().getNoOfStudents()) {
                conflicts++;
            }
            for (int j = 0; j < lectures.length; j++) {
                if (j > i) {
                    if ((lectures[i].getId() != lectures[j].getId()) &&
                            (lectures[i].getSubject().getId().equals(lectures[j].getSubject().getId())) &&
                            (lectures[i].get_class().getId() == lectures[j].get_class().getId())) {
                        if (lectures[i].getLectureSlot().getDay().equals(lectures[j].getLectureSlot().getDay())) {
                            if ((lectures[i].getLectureSlot().getId() - lectures[j].getLectureSlot().getId() != 10) &&
                                    (lectures[i].getLectureSlot().getId() - lectures[j].getLectureSlot().getId() != -10)) {
                                conflicts++;
                            }
                        }
                    }
                    if (lectures[i].getLectureSlot().getId() == lectures[j].getLectureSlot().getId() && lectures[i].getId() != lectures[j].getId()) {
                        if (lectures[i].getRoom().getId().equals(lectures[j].getRoom().getId())) {
                            conflicts++;
                        }
                        if (lectures[i].getTeacher().getId() == lectures[j].getTeacher().getId()) {
                            conflicts++;
                        }
                        if (lectures[i].get_class().getId() == lectures[j].get_class().getId()) {
                            conflicts++;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < labs.length; i++) {
            for (int j = 0; j < labs.length; j++) {
                if (j > i) {
                    if ((labs[i].getId() != labs[j].getId()) && (labs[i].get_class().getId() == labs[j].get_class().getId()) &&
                            (labs[i].getSubject().getId().equals(labs[j].getSubject().getId())) &&
                            (labs[i].getLabSlot().getDay().equals(labs[j].getLabSlot().getDay()))) {
                        conflicts++;
                    }
                    for (LectureSlot slot1 : labs[i].getLabSlot().getSlots()) {
                        for (LectureSlot slot2 : labs[j].getLabSlot().getSlots()) {
                            if (slot1.getId() == slot2.getId() && labs[i].getId() != labs[j].getId()) {
                                if (labs[i].getSubject().getId().equals(labs[j].getSubject().getId())) {
                                    conflicts++;
                                }
                                if (labs[i].get_class().getId() == labs[j].get_class().getId()) {
                                    conflicts++;
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Lab lab : labs) {
            for (Lecture lecture : lectures) {
                for (LectureSlot slot : lab.getLabSlot().getSlots()) {
                    if (lecture.getLectureSlot().getId() == slot.getId()) {
                        if (lecture.get_class().getId() == lab.get_class().getId()) {
                            conflicts++;
                        }
                    }
                }
            }
        }


        return 1.0 / (conflicts + 1);
    }

    public Lecture[] getLectures() {
        return lectures;
    }

    public double getFitness() {
        return calculateFitness();
    }

    public Lab[] getLabs() {
        return labs;
    }
}
