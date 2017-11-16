package com.mustafaergan.springbatch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.mustafaergan.springbatch.model.Person;
import com.mustafaergan.springbatch.model.Task;

public class TaskItemProcessor implements ItemProcessor<Task, Person> {

    private static final Logger log = LoggerFactory.getLogger(TaskItemProcessor.class);

	@Override
	public Person process(Task item) throws Exception {
		final String firstName = item.getTaskName().toUpperCase();
        final String lastName = item.getTaskName().toUpperCase();
        final Person transformedPerson = new Person(firstName, lastName);
        log.info("Converting (" + item + ") into (" + transformedPerson + ")");
        return transformedPerson;
	}

}
