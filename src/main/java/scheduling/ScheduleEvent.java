package scheduling;

public class ScheduleEvent {

    // either sma1 or sma2
    private String smaTypeTag;

    public ScheduleEvent(String smaTypeTag){
        this.smaTypeTag = smaTypeTag;
    }

    public String getSmaTypeTag(){
        return smaTypeTag;
    }
}
