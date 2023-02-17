package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface StudentDao extends CrudRepository<CollegeStudent,Integer> {
    public CollegeStudent findByEmailAddress(String emailAddress);
}
