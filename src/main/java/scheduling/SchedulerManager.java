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
    private EventManager em;
    private Scheduler scheduler;

    public SchedulerManager() throws SchedulerException {
        this.em = em;
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
        timer.getJobDataMap().put("em",em);
        scheduler.scheduleJob(timer,trigger);
        scheduler.start();
    }
}
