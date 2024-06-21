package exceltodb2.exceltodb2.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import exceltodb2.exceltodb2.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee,Integer>{

	Optional<Employee> findByEmployeeId(String employeeId);

	 @Query("SELECT e FROM Employee e WHERE MONTH(e.dob) = MONTH(GETDATE()) AND DAY(e.dob) = DAY(GETDATE())")
	    List<Employee> findEmployeesWithBirthdays();
}
