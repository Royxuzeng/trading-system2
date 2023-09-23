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

//  responsible for scheduling the job to run at specific intervals
//  and publishing ScheduleEvent to the EventManager.
public class SchedulerManager {
    private final EventManager eventManager;

    //Represents a Scheduler instance used for scheduling Timer jobs.
    private final Scheduler scheduler;

    public SchedulerManager(EventManager eventManager) throws SchedulerException {
        this.eventManager = eventManager;
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        this.scheduler = schedulerFactory.getScheduler();
    }

    // ScheduledEventPublisherJob defines how often computeSMA is called
    // Schedules the Timer jobs to run at specified intervals
    // and sets up the tag and EventManager instance for each job.
    public void periodicCallBack(int intervalMillis, String tag) throws SchedulerException {
        // A Trigger object is used to define the schedule at which a job will be executed.
        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()  //Specifies that the trigger should start immediately.
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()    // Defines the scheduling strategy
                        .withIntervalInMilliseconds(intervalMillis)    // Specifies the interval between job executions in milliseconds
                        .repeatForever())
                .build();

        // JobDetail instances define instances of Jobs.
        // In this case, ScheduledEventPublisherJob.class is the job to be executed.
        // .build(): Builds and returns the configured JobDetail instance.
        JobDetail timerJob = JobBuilder.newJob(ScheduledEventPublisherJob.class)
                .build();
        timerJob.getJobDataMap().put("tag",tag); // Stores the value of tag in the JobDataMap.

        //  Stores the reference to eventManager in the JobDataMap. The eventManager
        //  will be used by the EventPublishingJob when it gets executed
        timerJob.getJobDataMap().put("em", eventManager);
        scheduler.scheduleJob(timerJob,trigger);
        scheduler.start();
    }
}
