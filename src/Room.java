public class Room {
    private int number;
    private int capacity;
    private String id;

    public Room(int number, int capacity) {
        this.id = "CR" + number;
        this.number = number;
        this.capacity = capacity;
    }

    public int getNumber() {
        return number;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getId() {
        return id;
    }
}
