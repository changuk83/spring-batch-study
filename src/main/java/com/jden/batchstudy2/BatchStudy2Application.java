package com.jden.batchstudy2;

import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
public class BatchStudy2Application {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step() {
		return this.stepBuilderFactory.get("step1")
				.tasklet(new Tasklet() {
					@Override
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
						String name = (String) chunkContext.getStepContext().getJobParameters().get("name");

						System.out.println(String.format("Hello, World : %s", name));
						return RepeatStatus.FINISHED;
					}
				}).build();
	}

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.start(step())
				.validator(validator())
				.build();
	}

	@Bean
	public JobParametersValidator validator() {
		return parameters -> {
//			String fileName = parameters.getString("fileName");
//			if(StringUtils.isEmpty(fileName)) throw new JobParametersInvalidException("fileName parameter can be null");
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchStudy2Application.class, args);
	}

}
