package com.mustafaergan.springbatch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.mustafaergan.springbatch.model.Person;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {
	
	int sayac = 0;

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(final Person person) throws Exception {
        final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();
        final Person transformedPerson = new Person(firstName, lastName);
        log.info("Converting (" + person + ") into (" + transformedPerson + ")");
        log.info("****Uykudan girmeden"+sayac);
        Thread.sleep(1000000);
        log.info("Uykudan çıktık"+sayac);
        sayac++;
        return transformedPerson;
    }

}
