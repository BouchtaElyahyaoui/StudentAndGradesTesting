package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
public class GradebookController {

	@Autowired
	private Gradebook gradebook;

	@Autowired
	private StudentAndGradeService service;


	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStudents(Model m) {
		Iterable<CollegeStudent> students= service.getAllStudents();
		m.addAttribute("students",students);
		return "index";
	}

	@PostMapping(value = "/")
	public String createStudent(@ModelAttribute("student") CollegeStudent student,Model m)
	{
		service.createStudent(student.getFirstname(),student.getLastname(),student.getEmailAddress());
		Iterable<CollegeStudent> students= service.getAllStudents();
		m.addAttribute("students",students);
		return "index";
	}

	@GetMapping("/studentInformation/{id}")
		public String studentInformation(@PathVariable int id, Model m) {
		return "studentInformation";
		}

	@GetMapping("/delete/student/{id}")
	public String deleteStudent(@PathVariable int id, Model m) {
		if (!service.checkIfStudentIsNull(id)){
			return "error";
		}
		service.deleteStudent(id);
		Iterable<CollegeStudent> students= service.getAllStudents();
		m.addAttribute("students",students);
		return "index";
	}

}
