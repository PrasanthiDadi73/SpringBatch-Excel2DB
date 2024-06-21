package exceltodb2.exceltodb2.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import exceltodb2.exceltodb2.entity.Employee;
import exceltodb2.exceltodb2.repo.EmployeeRepository;

@EnableAsync
@Service
public interface EmployeeService {

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesWithBirthdaysToday();

    void sendBirthdayEmail(Employee birthdayEmployee, List<Employee> allEmployees);
}

@Service
class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> getEmployeesWithBirthdaysToday() {
        return employeeRepository.findEmployeesWithBirthdays();
    }

    @Value("${mail.attachment.path}")
    private String attachmentPath;

    @Override
    public void sendBirthdayEmail(Employee birthdayEmployee, List<Employee> allEmployees) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(birthdayEmployee.getEmail());
            helper.setSubject("Happy Birthday " + birthdayEmployee.getEmployeeName());

            Resource resource = new ClassPathResource("templates/birthday_template.html");

            if (!resource.exists()) {
                throw new IOException("HTML template not found.");
            }

            try (InputStream inputStream = resource.getInputStream()) {
                String htmlContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                htmlContent = htmlContent.replace("{employeeName}", birthdayEmployee.getEmployeeName());
                helper.setText(htmlContent, true);
                // File attachment = new File(attachmentPath + "/birthday.jpeg");
                // helper.addAttachment("birthday.jpeg", attachment);
                for (Employee ccEmployee : allEmployees) {
                    if (!ccEmployee.getEmail().equals(birthdayEmployee.getEmail())) {
                        helper.addCc(ccEmployee.getEmail());
                    }
                }

                javaMailSender.send(message);
                System.out.println("Email sent successfully to " + birthdayEmployee.getEmail());
            }
        } catch (IOException | MessagingException e) {
            System.err.println("Error sending email to " + birthdayEmployee.getEmail() + ": " + e.getMessage());
        }
    }

    @Async
    @Scheduled(cron = "*/5 * * * * *")
    public void sendBirthdayEmails() {
      try {
          List<Employee> allEmployees = getAllEmployees();
          List<Employee> employeesWithBirthdays = getEmployeesWithBirthdaysToday();

           if (!employeesWithBirthdays.isEmpty()) {
             for (Employee birthdayEmployee : employeesWithBirthdays) {
                  sendBirthdayEmail(birthdayEmployee, allEmployees);
              }
         }

   } catch (Exception e) {
        System.err.println("Error sending birthday emails: " + e.getMessage());
        }
    }
}
