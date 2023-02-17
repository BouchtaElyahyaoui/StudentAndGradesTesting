package com.luv2code.springmvc;

import com.luv2code.springmvc.controller.GradebookController;
import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application.properties")
public class GradeBookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentDao studentDao;

    private static MockHttpServletRequest mockHttpServletRequest;

    @Mock
    private StudentAndGradeService studentAndGradeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void beforeAll() {
        mockHttpServletRequest=new MockHttpServletRequest();
        mockHttpServletRequest.addParameter("firstname","monaime");
        mockHttpServletRequest.addParameter("lastname","ENNABBALI");
        mockHttpServletRequest.addParameter("emailAddress","monaime07@gmail.com");
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("insert into student(id,firstname,lastname,email_address) values (1,'abad','ghani','abad98@gmail.com')");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("delete from student");
    }

    @Test
    void getStudentsHttpRequest() throws Exception {
        CollegeStudent collegeStudent=new CollegeStudent("ali","ali","ali@gmail.com");
        CollegeStudent collegeStudent1=new CollegeStudent("ali09","ali09","ali09@gmail.com");
        Iterable<CollegeStudent> students=new ArrayList<>(Arrays.asList(collegeStudent,collegeStudent1));
        when(studentAndGradeService.getAllStudents()).thenReturn(students);
        Assertions.assertIterableEquals(students,studentAndGradeService.getAllStudents());
        MvcResult mvcResult= mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"index");
    }

    @Test
    void saveStudent() throws Exception {
        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                .param("firstname",mockHttpServletRequest.getParameterValues("firstname"))
                .param("lastname",mockHttpServletRequest.getParameterValues("lastname"))
                .param("emailAddress",mockHttpServletRequest.getParameterValues("emailAddress"))
        ).andExpect(status().isOk())
                .andReturn();
        ModelAndView modelAndView=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView,"index");
        CollegeStudent collegeStudent=studentDao.findByEmailAddress(
                "monaime07@gmail.com"
        );
        Assertions.assertNotNull(collegeStudent,"student should be found");
    }

    @Test
    void deleteStudent() throws Exception {
        Assertions.assertTrue(studentDao.findById(1).isPresent());
        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",1))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndView modelAndView=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView,"index");
        Assertions.assertFalse(studentDao.findById(1).isPresent());
    }

    @Test
    void deleteStudentHttpRequestError() throws Exception {
        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",0))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndView modelAndView=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView,"error");
    }
}
