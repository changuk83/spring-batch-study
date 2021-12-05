package com.jden.batchstudy2.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {
    @Bean
    public JobDetail quartzJobDetail() {
        return JobBuilder.newJob(BatchScheduledForQuartzTestJob.class)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger jobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(5).withRepeatCount(1);

        return TriggerBuilder.newTrigger()
                .forJob(quartzJobDetail())
                .withSchedule(scheduleBuilder)
                .build();
    }



    @Bean
    public JobDetail quartzJobDetail2() {
        return JobBuilder.newJob(BatchScheduledForQuartzTest2Job.class)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger jobTrigger2() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(5).withRepeatCount(1);

        return TriggerBuilder.newTrigger()
                .forJob(quartzJobDetail2())
                .withSchedule(scheduleBuilder)
                .build();
    }
}
