package com.transtour.backend.notification.job;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CronExecution {

    @PostConstruct
    public void init() throws SchedulerException {
         //Create instance of factory
         SchedulerFactory schedulerFactory=new StdSchedulerFactory();

         Scheduler scheduler = schedulerFactory.getScheduler();
         scheduler.start();

         JobDetail job1 = JobBuilder.newJob(NotificationRetry.class)
                 .withIdentity("NotificationRetry", "group1")
                 .build();

         JobDetail job2 = JobBuilder.newJob(SendNotification.class)
                 .withIdentity("SendNotification", "group1")
                 .build();

         // Trigger the job to run now, and then repeat every 15 minute
         Trigger trigger1 = TriggerBuilder.newTrigger()
                 .startNow()
                 .withSchedule(CronScheduleBuilder.cronSchedule("0 2/20 0/1 1/1 * ?"))
                 .build();

         Trigger trigger2 = TriggerBuilder.newTrigger()
                 .startNow()
                 .withSchedule(CronScheduleBuilder.cronSchedule("0 2/30 0/1 1/1 * ?"))
                 .build();

         // Tell quartz to schedule the job using our trigger
         scheduler.scheduleJob(job1, trigger1);
         scheduler.scheduleJob(job2, trigger2);
     }
}
