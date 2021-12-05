package com.jden.batchstudy2.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

@Slf4j
public class LoggingStepStartStopListener {
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("#### Step start!!! ");
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("#### Step ended!!!");
        return stepExecution.getExitStatus();
    }
}
