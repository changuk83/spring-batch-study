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
public class ChunkJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkJob() {
        return this.jobBuilderFactory.get("chunkJob")
                .incrementer(new RunIdIncrementer())
                .start(stepChunk())
                .on("FAILED").to(failureChunkStep())
                .end()
                .build();
    }

    @Bean
    public Step failureChunkStep() {
        return this.stepBuilderFactory.get("failChunkStep")
                .tasklet(logChunkError())
                .build();
    }

    @Bean
    public Tasklet logChunkError() {
        return ((contribution, chunkContext) -> {
            log.error("###### Error!!!");
            return RepeatStatus.FINISHED;
            // Step 의 상태는 ABANDONED 로 나타남.
        });
    }

    @Bean
    public Step stepChunk() {
        return this.stepBuilderFactory.get("chunkStep")
                .<String, String>chunk(10)
                .reader(itemReader(null))
                .writer(itemWriter(null))
                .listener(new LoggingStepStartStopListener()) // setting step listener
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<String> itemReader(@Value("#{jobParameters['inputFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<String>()
                .name("itemReader")
                .resource(inputFile)
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<String> itemWriter(@Value("#{jobParameters['outputFile']}") Resource outputFile) {
        return new FlatFileItemWriterBuilder<String>()
                .name("itemWriter")
                .resource(outputFile)
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }
}
