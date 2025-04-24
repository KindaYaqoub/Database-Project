package application;

public class Employee {
	private int empId;
	private String contactInfo;
	private double salary;
	private int branchId;
	private String position;
	private String name;

	public Employee(int empId, String contactInfo, double salary, int branchId, String position, String name) {
	    this.empId = empId;
	    this.contactInfo = contactInfo;
	    this.salary = salary;
	    this.branchId = branchId;
	    this.position = position;
	    this.name = name;
	}

	public Employee(String empId, String contactInfo, String position, String name, double salary, String branchId) {
		this.empId = Integer.parseInt(empId);
		this.contactInfo = contactInfo;
		this.salary = salary;
		this.branchId = Integer.parseInt(branchId);
		this.position = position;
		this.name = name;
	}

	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
