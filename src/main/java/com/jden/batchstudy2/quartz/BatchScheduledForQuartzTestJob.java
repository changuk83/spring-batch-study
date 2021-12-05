package com.jden.batchstudy2.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class BatchScheduledForQuartzTestJob extends QuartzJobBean {
    @Autowired
    private Job job; // name 이 job 인 Bean을 찾음.

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobLauncher jobLauncher;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder(this.jobExplorer)
                .getNextJobParameters(this.job)
                .addString("fileName", "777")
                .toJobParameters();

        try {
            // 사용자가 shell 에서 실행할때는 Runner 로 실행되지만 컨테이너내에서 실행하는 경우 JobLauncher 로 job들을 실행시킴.
            // 이 경우는 Quartz 를 통해서 실행되기 떄문에 jobLauncher로 jobParameter와 함께 실행하는 것임.
            this.jobLauncher.run(this.job, jobParameters);
        } catch(Exception e) {
            log.error("Error", e);
        }
    }
}
