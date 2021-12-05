package com.jden.batchstudy2.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class QuartzTest2JobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step2() {
        return this.stepBuilderFactory.get("step2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        // job Parameter 에 접근하는 방법.
                        String name = (String) chunkContext.getStepContext().getJobParameters().get("name");
                        String fileName = (String) chunkContext.getStepContext().getJobParameters().get("fileName");

                        System.out.println(String.format("Hello, World : %s %s", name, fileName));
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    public Job job2() { // job bean 이 생성됨.
        return this.jobBuilderFactory.get("job2") // job 이라는 이름의 Job 생성
                .incrementer(new RunIdIncrementer()) // run.id 파라미터 자동 증가
                .start(step2())
                .validator(validator2())
                .build();
    }

    @Bean
    public JobParametersValidator validator2() {
        return parameters -> {
//			String fileName = parameters.getString("fileName");
//			if(StringUtils.isEmpty(fileName)) throw new JobParametersInvalidException("fileName parameter can be null");
        };
    }
}
