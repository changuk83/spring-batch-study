package com.jden.batchstudy2.controller;


import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping(path = "/run/{jobName}")
    public ExitStatus runJob(@PathVariable String jobName) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = this.applicationContext.getBean(jobName, Job.class);

        JobParameters jobParameter = new JobParametersBuilder(this.jobExplorer)
                .getNextJobParameters(job)
                .addString("test111", "233232")
                .toJobParameters();

        return this.jobLauncher.run(job, jobParameter).getExitStatus();
    }
}
