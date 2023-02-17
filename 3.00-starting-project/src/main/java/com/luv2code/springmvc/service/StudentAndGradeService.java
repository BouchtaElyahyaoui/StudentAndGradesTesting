package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {
    @Autowired
    private StudentDao studentDao;
    public void createStudent(String firstName,String lastName,String emailAddress){
        CollegeStudent collegeStudent=new CollegeStudent(firstName,lastName,emailAddress);
        collegeStudent.setId(0);
        studentDao.save(collegeStudent);
    }

    public Boolean checkIfStudentIsNull(Integer id){
        Optional<CollegeStudent> student=studentDao.findById(id);
        if(student.isPresent()){
            return true;
        }
        return false;

    }

    public void deleteStudent(Integer id) {
        if(checkIfStudentIsNull(id)){
            studentDao.deleteById(id);
        }
    }

    public Iterable<CollegeStudent> getAllStudents() {
        Iterable<CollegeStudent> students=studentDao.findAll();
        return students;
    }
}
