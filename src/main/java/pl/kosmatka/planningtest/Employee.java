package pl.kosmatka.planningtest;

public class Employee {

	private String name;

	public Employee(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (!(obj instanceof Employee)){
			return false;
		}
		return name.equals(((Employee)obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
}
