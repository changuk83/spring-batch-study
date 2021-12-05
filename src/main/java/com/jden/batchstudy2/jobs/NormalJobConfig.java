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
public class NormalJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job normalJob() {
        return this.jobBuilderFactory.get("normalJob")
                .incrementer(new RunIdIncrementer())
                .start(stepNormal())
                .build();
    }

    @Bean
    public Step stepNormal() {
        return this.stepBuilderFactory.get("stepNormal")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("step Normal run!");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
