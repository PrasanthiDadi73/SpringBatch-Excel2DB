package exceltodb2.exceltodb2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import exceltodb2.exceltodb2.entity.Employee;
import exceltodb2.exceltodb2.service.EmployeeService;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/birthdays")
    public String sendBirthdayEmails() {
        try {
            List<Employee> allEmployees = employeeService.getAllEmployees();
            List<Employee> employeesWithBirthdays = employeeService.getEmployeesWithBirthdaysToday();

            if (!employeesWithBirthdays.isEmpty()) {
                for (Employee birthdayEmployee : employeesWithBirthdays) {
                    employeeService.sendBirthdayEmail(birthdayEmployee, allEmployees);
                }
                return "Birthday emails sent successfully!";
            } else {
                return "No employees have birthdays today.";
            }
        } catch (Exception e) {
            return "Failed to send birthday emails. Please check the logs for details.";
        }
    }
}
