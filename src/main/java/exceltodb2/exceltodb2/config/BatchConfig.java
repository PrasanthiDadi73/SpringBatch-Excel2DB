package exceltodb2.exceltodb2.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import exceltodb2.exceltodb2.entity.Employee;
import exceltodb2.exceltodb2.step.DatabaseItemWriter;
import exceltodb2.exceltodb2.step.ExcelItemProcessor;
import exceltodb2.exceltodb2.step.ExcelItemReader;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ExcelItemReader excelItemReader;

    @Autowired
    private ExcelItemProcessor excelItemProcessor;

    @Autowired
    private DatabaseItemWriter databaseItemWriter;

    @Autowired
    private JobLauncher jobLauncher;

    @Bean
    public Step excelToDatabaseStep() {
        return stepBuilderFactory.get("excelToDatabaseStep")
                .<Employee, Employee>chunk(10)
                .reader(excelItemReader.flatFileItemReader())
                .processor(excelItemProcessor)
                .writer(databaseItemWriter)
                .build();
    }

    @Bean
    public Step sendBirthdayEmailStep() {
        return stepBuilderFactory.get("sendBirthdayEmailStep")
                .<Employee, Employee>chunk(10)
                .reader(excelItemReader.flatFileItemReader())
                .processor(excelItemProcessor)
                .writer(databaseItemWriter)
                .build();
    }

    @Bean
    public Job excelToDatabaseJob(Step excelToDatabaseStep) {
        return jobBuilderFactory.get("excelToDatabaseJob")
                .incrementer(new RunIdIncrementer())
                .flow(excelToDatabaseStep)
                .end()
                .build();
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run the job every day at midnight
    public void perform() throws Exception {
        jobLauncher.run(excelToDatabaseJob(excelToDatabaseStep()), new JobParametersBuilder().toJobParameters());
    }
}
