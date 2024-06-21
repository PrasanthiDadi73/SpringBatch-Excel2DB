package exceltodb2.exceltodb2.step;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import exceltodb2.exceltodb2.entity.Employee;
import exceltodb2.exceltodb2.repo.EmployeeRepository;
import exceltodb2.exceltodb2.service.EmployeeService;

@Component
public class ExcelItemProcessor implements ItemProcessor<Employee, Employee> {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Employee process(Employee employee) throws Exception {
        Optional<Employee> existingEmployee = employeeRepository.findByEmployeeId(employee.getEmployeeId());

        if (existingEmployee.isPresent()) {
            Employee storedEmployee = existingEmployee.get();

            if (!employee.equals(storedEmployee)) {
                return updateEmployeeData(storedEmployee, employee);
            } else {
                return null;
            }
        } else {
            return checkBirthdayAndProcess(employee);
        }
    }

    private Employee updateEmployeeData(Employee storedEmployee, Employee newEmployeeData) {
        storedEmployee.setEmployeeName(newEmployeeData.getEmployeeName());
        storedEmployee.setDob(newEmployeeData.getDob());
        storedEmployee.setEmail(newEmployeeData.getEmail());
        return storedEmployee;
    }

    private Employee checkBirthdayAndProcess(Employee employee) {
        if (isBirthdayToday(employee.getDob())) {
        	List<Employee> allEmployees = employeeRepository.findAll(); 
            employeeService.sendBirthdayEmail(employee, allEmployees);
        }

        return employee;
    }

    private boolean isBirthdayToday(Date dob) {
        LocalDate today = LocalDate.now();
        LocalDate birthday = dob.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        return today.getMonthValue() == birthday.getMonthValue() && today.getDayOfMonth() == birthday.getDayOfMonth();
    }
}
