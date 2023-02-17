package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@TestPropertySource("/application.properties")
@SpringBootTest
class StudentAndGradeServiceTest {
    @Autowired
    private StudentAndGradeService service;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentDao studentDao;

    @BeforeEach
    void setUp() {
        jdbc.execute("insert into student(id,firstname,lastname,email_address) values (1,'abad','ghani','abad98@gmail.com')");
    }

    @AfterEach
    void tearDown() {
        jdbc.execute("delete from student");
    }

    @Test
    @DisplayName("create student test")
    public void createStudentService(){
        service.createStudent("monaime","ENNABBALI","monaime08@gmail.com");
        CollegeStudent collegeStudent=studentDao.findByEmailAddress("monaime08@gmail.com");
        Assertions.assertEquals(collegeStudent.getEmailAddress(),"monaime08@gmail.com","create student");
    }

    @Test
    @Sql("/insertData.sql")
    @DisplayName("get all students")
    public void getAllStudents(){
        Iterable<CollegeStudent> students=service.getAllStudents();
        List<CollegeStudent> collegeStudents=new ArrayList<>();
        for (CollegeStudent collegeStudent:students
             ) {
            collegeStudents.add(collegeStudent);
        }
        Assertions.assertEquals(5,collegeStudents.size(),"list must have a single student");
    }
    @Test
    @DisplayName("delete a student")
    public void deleteStudent(){
        Optional<CollegeStudent> student=studentDao.findById(1);
        Assertions.assertTrue(student.isPresent(),"student must be present in database");
        service.deleteStudent(1);
        Optional<CollegeStudent> collegeStudent=studentDao.findById(1);
        Assertions.assertFalse(collegeStudent.isPresent(),"student must no longer exist in database");
    }



    @Test
    public void isStudentNullCheck(){
        Assertions.assertTrue(service.checkIfStudentIsNull(1));
        Assertions.assertFalse(service.checkIfStudentIsNull(0));
    }
}
