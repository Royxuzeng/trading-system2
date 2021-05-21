package scheduling;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import messaging.EventManager;

public class SchedulerManager {
    private final EventManager eventManager;
    private final Scheduler scheduler;

    public SchedulerManager(EventManager eventManager) throws SchedulerException {
        this.eventManager = eventManager;
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        this.scheduler = schedulerFactory.getScheduler();
    }

    public void periodicCallBack(int intervalMillis, String tag) throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(intervalMillis)
                        .repeatForever())
                .build();

        JobDetail timer = JobBuilder.newJob(Timer.class)
                .build();
        timer.getJobDataMap().put("tag",tag);
        timer.getJobDataMap().put("em", eventManager);
        scheduler.scheduleJob(timer,trigger);
        scheduler.start();
    }
}
