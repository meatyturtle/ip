import java.time.LocalDateTime;

public class Event extends Task {
    private String startTime;
    private String endTime;
    public Event(String name, boolean status, String startTime, String endTime) {
        super(name, status);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString();
    }
}
