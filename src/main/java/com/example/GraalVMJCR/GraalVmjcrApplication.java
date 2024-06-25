package com.example.GraalVMJCR;

import com.example.GraalVMJCR.controller.RunnerController;
import com.example.GraalVMJCR.model.CodeSubmission;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GraalVmjcrApplication {

	static String sampleSubmission =
	"""
	public class Main {
		public static void main(String[] args) {
			System.out.println("Hello, world!");
		}
	}
	""";

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(GraalVmjcrApplication.class, args);
		System.out.println(
			context.getBean(RunnerController.class).compileAndRun(new CodeSubmission(sampleSubmission)).getBody());
	}

}
