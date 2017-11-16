package com.mustafaergan.springbatch.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.mustafaergan.springbatch.model.Task;

@Component
public class TaskReader {
	
    private static final String QUERY_FIND_TASK =
            "SELECT " +
                "task_id, " +
                "task_name , " +
                "task_status " +
            "FROM task " ;

	public List<Task> readTask(){
		
		List<Task> results = new ArrayList<>();
		results.add(new Task("1", "task_1", "open"));
		results.add(new Task("2", "task_2", "close"));
		results.add(new Task("3", "task_3", "result"));
		results.add(new Task("4", "task_4", "open"));

		
//		List<Task> results = jdbcTemplate.query("SELECT task_id, task_name, task_status FROM task", new RowMapper<Task>() {
//			@Override
//			public Task mapRow(ResultSet rs, int row) throws SQLException {
//				return new Task(rs.getString(1), rs.getString(2), rs.getString(3));
//			}
//		});
//		
		return results;
	}

}
