public class Lab {
    private int id;
    private Subject subject;
    private LabSlot labSlot;
    private Class _class;

    public Lab(Subject subject, Class _class) {
        this.subject = subject;
        this._class = _class;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLabSlot(LabSlot labSlot) {
        this.labSlot = labSlot;
    }

    public int getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public LabSlot getLabSlot() {
        return labSlot;
    }

    public Class get_class() {
        return _class;
    }
}
