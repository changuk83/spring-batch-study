package com.jden.batchstudy2;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing // job builder, step builder 를 사용할 수 있게 함.
@SpringBootApplication
public class BatchStudy2Application {



	public static void main(String[] args) {
		SpringApplication.run(BatchStudy2Application.class, args);
	}

}
