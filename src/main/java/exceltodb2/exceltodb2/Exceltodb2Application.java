package exceltodb2.exceltodb2;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Exceltodb2Application {

	public static void main(String[] args) {
		SpringApplication.run(Exceltodb2Application.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(JobLauncher jobLauncher, Job excelToDatabaseJob) {
	    return args -> {
	        if (args.length > 0 && args[0].equals("explicit-start")) {
	            jobLauncher.run(excelToDatabaseJob, new JobParameters());
	        }
	    };
	}
}
