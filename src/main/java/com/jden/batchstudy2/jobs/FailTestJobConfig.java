package com.jden.batchstudy2.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Slf4j
@Configuration
public class FailTestJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job failTestJob() {
        return this.jobBuilderFactory.get("failTestJob")
                //.incrementer(new RunIdIncrementer()) // 요게 있으면 매번 재실행 되므로 실패 Step 부터 실행되지 않음.
                .start(stepFailTestFirst())
                .next(stepFailTestSecond())
                //.on("FAILED").to(failureFailTestStep())
                .on("FAILED").fail()
                //.on("FAILED").stopAndRestart(stepFailTestSecond())
                .end()
                .build();
    }

    @Bean
    public Step failureFailTestStep() {
        return this.stepBuilderFactory.get("failStep")
                .tasklet(logFailTestError())
                .build();
    }

    @Bean
    public Tasklet logFailTestError() {
        return ((contribution, chunkContext) -> {
            log.error("###### Error!!!");
            //return RepeatStatus.CONTINUABLE; // 계속 재실행된다.
            return RepeatStatus.FINISHED;
            // Step 의 상태는 ABANDONED 로 나타남.
        });
    }

    @Bean
    public Step stepFailTestFirst() {
        return this.stepBuilderFactory.get("chunkStep1")
                .tasklet(failTestFirstTasklet())
                .listener(new LoggingStepStartStopListener()) // setting step listener
                .build();
    }

    @Bean
    public Step stepFailTestSecond() {
        return this.stepBuilderFactory.get("chunkStep2")
                .tasklet(failTestSecondTasklet())
                .listener(new LoggingStepStartStopListener()) // setting step listener
                .build();
    }

    @Bean
    public Tasklet failTestFirstTasklet() {
        return (((contribution, chunkContext) -> {
            log.info("#### first Fail Test Tasklet ");
            //throw new RuntimeException("error");
            return RepeatStatus.FINISHED;
        }));
    }

    @Bean
    public Tasklet failTestSecondTasklet() {
        return (((contribution, chunkContext) -> {
            log.info("#### second Fail Test Tasklet ");
            throw new RuntimeException("error");
            //return RepeatStatus.FINISHED;
        }));
    }
}

// 똑같은 jobParameter로 실행.
// scenario 1. 첫번째 Step 에서 에러.
//2021-12-05 23:02:54.573  INFO 95976 --- [           main] c.j.b.jobs.LoggingStepStartStopListener  : #### Step start!!!
//        2021-12-05 23:02:54.647  INFO 95976 --- [eduler_Worker-3] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
//        2021-12-05 23:02:54.648  INFO 95976 --- [           main] c.j.batchstudy2.jobs.FailTestJobConfig   : #### first Tasklet
//        2021-12-05 23:02:54.674 ERROR 95976 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step chunkStep1 in job failTestJob
//
//        java.lang.RuntimeException: error
//        at com.jden.batchstudy2.jobs.FailTestJobConfig.lambda$failTestFirstTasklet$1(FailTestJobConfig.java:81) ~[classes!/:na]

// scenario 2. 첫번째 Step 성공, 두번째 Step 에서 에러.
//2021-12-05 23:06:21.706  INFO 96036 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=failTestJob]] launched with the following parameters: [{name=failTestJob, fileName=3, run.id=3}]
//        2021-12-05 23:06:22.084  INFO 96036 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [chunkStep1]
//        2021-12-05 23:06:22.233  INFO 96036 --- [           main] c.j.batchstudy2.jobs.FailTestJobConfig   : #### first Fail Test Tasklet
//        2021-12-05 23:06:22.317  INFO 96036 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [chunkStep1] executed in 232ms
//        2021-12-05 23:06:22.592  INFO 96036 --- [           main] c.j.batchstudy2.jobs.FailTestJobConfig   : #### second Fail Test Tasklet
//        2021-12-05 23:06:22.611 ERROR 96036 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step chunkStep2 in job failTestJob
//
//        java.lang.RuntimeException: error
//        at com.jden.batchstudy2.jobs.FailTestJobConfig.lambda$failTestSecondTasklet$2(FailTestJo

// scenario 3. 그대로 재실행. Step 1은 싱행 안되야 하는데?
//2021-12-05 23:25:13.114  INFO 96341 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=206, version=3, name=chunkStep1, status=COMPLETED, exitStatus=COMPLETED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=1, rollbackCount=0, exitDescription=
//        2021-12-05 23:25:13.290  INFO 96341 --- [eduler_Worker-4] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=job2]] completed with the following parameters: [{name=failTestJob, fileName=999, run.id=56}] and the following status: [COMPLETED] in 1s205ms
//        2021-12-05 23:25:13.344  INFO 96341 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [chunkStep2]
//        2021-12-05 23:25:13.428  INFO 96341 --- [           main] c.j.b.jobs.LoggingStepStartStopListener  : #### Step start!!!
//        2021-12-05 23:25:13.461  INFO 96341 --- [           main] c.j.batchstudy2.jobs.FailTestJobConfig   : #### second Fail Test Tasklet
//        2021-12-05 23:25:13.504 ERROR 96341 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step chunkStep2 in job failTestJob
