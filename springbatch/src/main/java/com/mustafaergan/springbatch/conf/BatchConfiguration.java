package com.mustafaergan.springbatch.conf;

import java.util.UUID;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mustafaergan.springbatch.model.Person;
import com.mustafaergan.springbatch.model.Task;
import com.mustafaergan.springbatch.processor.DatabaseReader;
import com.mustafaergan.springbatch.processor.JobCompletionNotificationListener;
import com.mustafaergan.springbatch.processor.PersonItemProcessor;
import com.mustafaergan.springbatch.processor.TaskItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;
    
    
    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("spring_batch");
        asyncTaskExecutor.setConcurrencyLimit(2);
        return asyncTaskExecutor;
    }


    //dosya okuyucu
    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Person> reader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
        reader.setResource(new ClassPathResource("sample-data.csv"));
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "firstName", "lastName" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }});
        }});
        return reader;
    }
    
    
    @Bean
    ItemReader<Task> inMemoryTaskReader() {
        return new DatabaseReader();
    }

    @Bean
    public TaskItemProcessor taskprocessor() {
        return new TaskItemProcessor();
    }
    
    
    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer() {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
        writer.setSql("INSERT INTO people (person_id, first_name, last_name) VALUES ('"+UUID.randomUUID()+"', :firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(100)
                .reader(reader())
                .processor(processor())
                .writer(writer())
//                .startLimit(1)
                .taskExecutor(taskExecutor())
//                .faultTolerant()
//				.skipLimit(10) //default is set to 0
                .build();
    }
    
//    @Bean
//    public Step step2() {
//        return stepBuilderFactory.get("step1")
//                .<Task, Person> chunk(10)
//                .reader(inMemoryTaskReader())
//                .processor(taskprocessor())
//                .writer(writer())
//                .startLimit(1)
////                .faultTolerant()
////				.skipLimit(10) //default is set to 0
//                .build();
//    }
    // end::jobstep[]
}