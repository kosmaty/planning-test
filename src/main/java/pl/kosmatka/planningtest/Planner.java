package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Planner {

	private LocalDateTime workDayBegining;
	private LocalDateTime workDayEnd;

	SortedSet<EmployeesTask> tasks = new TreeSet<>();

	SortedSet<Event> events = new TreeSet<>();

	public Planner(LocalDateTime workDayBegining, LocalDateTime workDayEnd) {
		this.workDayBegining = workDayBegining;
		this.workDayEnd = workDayEnd;
	}

	public LocalDateTime findPossibleTime(Duration duration) {
		return findPossibleTime2(duration, 0);
	}

	private LocalDateTime findPossibleTime2(Duration duration, int absentAllowed) {
		if (tasks.isEmpty()) {
			return workDayBegining;
		}

		Iterator<EmployeesTask> tasksIterator = tasks.iterator();
		EmployeesTask firstEmployeesTask = tasksIterator.next();
		if (workDayBegining.plus(duration).isBefore(
				firstEmployeesTask.getTask().getTaskBeginTime())) {
			return workDayBegining;
		}
		
		
		
		
		for (EmployeesTask outerTask : tasks) {
			if (outerTask.task.getTaskEndTime().plus(duration).isAfter(workDayEnd)){
				continue;
			}
			int employeeAbsentCount = 0;
			for (EmployeesTask innerTask : tasks) {
				if (innerTask.task.getTaskBeginTime().isBefore(outerTask.task.getTaskEndTime().plus(duration)) &&
						innerTask.getTask().getTaskEndTime().isAfter(outerTask.getTask().getTaskEndTime())){
					employeeAbsentCount++;
				}
			}
			if (employeeAbsentCount <= absentAllowed){
				return outerTask.task.getTaskEndTime();
			}
		}
		return null;
		
		// 
		
		



	}
	
	public LocalDateTime findPossibleTime(Duration duration, int absentAllowed) {
		return findPossibleTime2(duration, absentAllowed);
	}

	public LocalDateTime findPossibleTimeOld(Duration duration, int absentAllowed) {
		if (tasks.isEmpty()) {
			return workDayBegining;
		}

		Iterator<EmployeesTask> tasksIterator = tasks.iterator();
		EmployeesTask firstEmployeesTask = tasksIterator.next();
		if (workDayBegining.plus(duration).isBefore(
				firstEmployeesTask.getTask().getTaskBeginTime())) {
			return workDayBegining;
		}

		while (tasksIterator.hasNext()) {
			Task firstTask = firstEmployeesTask.getTask();
			EmployeesTask secondEmployeesTask = tasksIterator.next();
			Task secondTask = secondEmployeesTask.getTask();
			if (enoughTimeBetweenTasks(firstTask, secondTask, duration)) {
				return firstTask.getTaskEndTime();
			}
			if (firstTask.endsBeforeEndOf(secondTask)) {
				firstEmployeesTask = secondEmployeesTask;
			}
		}
		if (firstEmployeesTask.getTask().getTaskEndTime().plus(duration)
				.isBefore(workDayEnd)) {
			return firstEmployeesTask.getTask().getTaskEndTime();
		}

		return null;
	}

	private boolean enoughTimeBetweenTasks(Task firstTask, Task secondTask,
			Duration requiredGapBetweenTasks) {
		return firstTask.getTaskEndTime().plus(requiredGapBetweenTasks)
				.isBefore(secondTask.getTaskBeginTime());
	}

	public void addTask(Employee employee, Task task) {
		tasks.add(new EmployeesTask(employee, task));
		events.add(new Event(employee, EventType.BEGIN, task.getTaskBeginTime()));
		events.add(new Event(employee, EventType.END, task.getTaskEndTime()));

	}

}

class EmployeesTask implements Comparable<EmployeesTask> {
	Employee employee;
	Task task;

	public EmployeesTask(Employee employee, Task task) {
		this.employee = employee;
		this.task = task;
	}

	@Override
	public int compareTo(EmployeesTask other) {
		return task.compareTo(other.task);
	}

	public Employee getEmployee() {
		return employee;
	}

	public Task getTask() {
		return task;
	}

}

class Event implements Comparable<Event> {
	Employee employee;
	EventType type;

	LocalDateTime time;

	public Event(Employee employee, EventType type, LocalDateTime time) {
		super();
		this.employee = employee;
		this.type = type;
		this.time = time;
	}

	@Override
	public int compareTo(Event o) {
		int comparisonResult = time.compareTo(o.time);
		if (comparisonResult != 0) {
			return comparisonResult;
		}
		return type.compareTo(o.type);
	}

}

enum EventType {
	BEGIN,

	END;
}
