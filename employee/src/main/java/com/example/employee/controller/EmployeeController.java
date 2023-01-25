/**
 * 
 */
package com.example.employee.controller;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.employee.model.EmployeeModel;
import com.example.employee.repository.EmployeeRepository;

/**
 * @author Narayana
 *
 */
@Controller
public class EmployeeController {

	@Autowired
	private EmployeeRepository employeeRepo;

	@RequestMapping(value = "/saveEmployee", method = RequestMethod.POST)
	public ResponseEntity addEmployee(@RequestBody EmployeeModel employee) {
		try {
			System.out.println("Inside postMethod");

			String errMsg = validation(employee);

			if (errMsg != null) {
				return ResponseEntity.badRequest().body(errMsg);
			}

			employeeRepo.save(employee);

			return ResponseEntity.ok().body("Employee Saved");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}

	}

	@RequestMapping(value = "/getEmployeeTaxDetails", method = RequestMethod.GET)
	public ResponseEntity retrieveEmployeeTaxDetails(@RequestBody String financialYear) {
		try {

			List<EmployeeModel> empList = employeeRepo.findAllByFinancialYear(financialYear);
			List<JSONObject> empJsonList = new ArrayList<JSONObject>();
			for (EmployeeModel emp : empList) {
				JSONObject entity = new JSONObject();
				entity.put("employeeCode", emp.getEmployeeId());
				entity.put("firstName", emp.getFirstName());
				entity.put("lastName", emp.getLastName());
				entity.put("yearlySalary", calculateSalary(emp.getMonthlySalary()));
				entity.put("taxAmount", taxCalculator(calculateSalary(emp.getMonthlySalary())));
				entity.put("cessAmount", cessCalculator(calculateSalary(emp.getMonthlySalary())));
				
				empJsonList.add(entity);
			}

			return new ResponseEntity<Object>(empJsonList, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}

	private String cessCalculator(String monthlySalary) {
		
		String cessAmt=null;
		
		Double salary=Double.parseDouble(monthlySalary);
		
		if(salary>=2500000) {
			cessAmt=String.valueOf((2.0*salary)/100);
		}
		
		return cessAmt;
	}

	private String calculateSalary(String monthlySalary) {
		
		Double salary=Double.parseDouble(monthlySalary);
		
		return String.valueOf(salary*12);
	}

	@SuppressWarnings({ "deprecation", "static-access" })
	private String validation(EmployeeModel employee) {
		String errMsg = "";

		if (employee.getFirstName() == null || StringUtils.isEmpty(employee.getFirstName())) {
			errMsg = errMsg + "First Name is Mandatory " + ",";
		}
		if (employee.getLastName() == null || StringUtils.isEmpty(employee.getLastName())) {
			errMsg = errMsg + "Last Name is Mandatory " + ",";
		}
		if (employee.getEmail() == null || StringUtils.isEmpty(employee.getEmail())) {
			errMsg = errMsg + "Email is Mandatory " + ",";
		}
		if (employee.getDateOfJoining() == null || StringUtils.isEmpty(employee.getDateOfJoining())) {
			errMsg = errMsg + "Date Of Joining is Mandatory " + ",";
		}
		if (employee.getPhoneNumber() == null || StringUtils.isEmpty(employee.getPhoneNumber())) {
			errMsg = errMsg + "Phone Number is Mandatory " + ",";
		}
		if (employee.getMonthlySalary() == null || StringUtils.isEmpty(employee.getMonthlySalary())) {
			errMsg = errMsg + "Monthly Salary is Mandatory";
		}

		return errMsg.join(",", errMsg);
	}

	private String taxCalculator(String salaryAmount) {

		String taxAmt=null;
		Double Salary=Double.parseDouble(salaryAmount);
		
		if(Salary<=200000)
			taxAmt="0";
		else if(Salary<=300000)
			taxAmt=String.valueOf(0.1*(Salary-200000));
		else if(Salary<=500000)
			taxAmt=String.valueOf((0.2*(Salary-300000))+(0.1*100000));
		else if(Salary<=1000000)
			taxAmt=String.valueOf((0.3*(Salary-500000))+(0.2*200000)+(0.1*100000));
		else
			taxAmt=String.valueOf((0.4*(Salary-1000000))+(0.3*500000)+(0.2*200000)+(0.1*100000));
		
		return taxAmt;
	}

}
