package com.jden.batchstudy2.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class QuartzJobConfigration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job quartzJob() {
        return this.jobBuilderFactory.get("quartzJob")
                .incrementer(new RunIdIncrementer())
                .start(stepQuartz())
                .build();
    }

    @Bean
    public Step stepQuartz() {
        return this.stepBuilderFactory.get("quartzStep")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("step quartz run!");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
