package exceltodb2.exceltodb2.step;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import exceltodb2.exceltodb2.entity.Employee;

@Component
public class ExcelItemReader {
	@Bean
    public FlatFileItemReader<Employee> flatFileItemReader() {
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("/home/prasanthi/Downloads/employees_deatils.xlsx"));
        reader.setLinesToSkip(1); 

        LineTokenizer lineTokenizer = createLineTokenizer();
        reader.setLineMapper(createLineMapper(lineTokenizer));

        return reader;
    }

    private LineTokenizer createLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(","); 
        lineTokenizer.setNames("employeename", "employeeid", "dob","email"); 
        return lineTokenizer;
    }

    private LineMapper<Employee> createLineMapper(LineTokenizer lineTokenizer) {
        DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);

        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Employee.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
    
}
