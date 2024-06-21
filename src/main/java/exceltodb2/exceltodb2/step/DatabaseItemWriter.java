package exceltodb2.exceltodb2.step;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import exceltodb2.exceltodb2.entity.Employee;

@Component
public class DatabaseItemWriter implements ItemWriter<Employee> {

	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional
	public void write(List<? extends Employee> employees) throws Exception {
		for (Employee employee : employees) {
			entityManager.persist(employee);
		}
	}
}
