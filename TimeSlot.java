import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlot 
{
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public TimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDate getDate() 
    { 
        return date; 
    }

    public LocalTime getStartTime()
    {
        return startTime;
    }

    public LocalTime getEndTime()
    {
        return endTime;
    }

    public double calculateDurationInMinutes()
    {
        if (startTime != null && endTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            return duration.toMinutes();
        }
        return 0;
    }
}