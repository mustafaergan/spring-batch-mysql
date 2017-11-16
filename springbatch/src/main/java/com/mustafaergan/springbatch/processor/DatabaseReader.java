package com.mustafaergan.springbatch.processor;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mustafaergan.springbatch.model.Task;

@Configuration
public class DatabaseReader implements ItemReader<Task> {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseReader.class);
	
	private int nextIndex;
	private List<Task> data;
	
	public DatabaseReader() {
		TaskReader reader = new TaskReader();
		this.data = reader.readTask();
	}
	
	@Override
	public Task read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        LOGGER.info("Reading the information of the next Task");
        Task nextData= null;
        if (nextIndex < data.size()) {
        	nextData = data.get(nextIndex);
            nextIndex++;
        }
        LOGGER.info("Found student: {}", nextData);

        return nextData;
	}

}
