package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

public class Task implements Comparable<Task> {

	private LocalDateTime taskBeginTime;
	private LocalDateTime taskEndTime;

	public LocalDateTime getTaskBeginTime() {
		return taskBeginTime;
	}

	public LocalDateTime getTaskEndTime() {
		return taskEndTime;
	}

	public Task(LocalDateTime taskBeginTime, LocalDateTime taskEndTime) {
		this.taskBeginTime = taskBeginTime;
		this.taskEndTime = taskEndTime;
	}

	public static TaskBuilder newTask() {
		return new TaskBuilder();
	}
	
	boolean endsBeforeEndOf(Task otherTask){
		return taskEndTime.isBefore(otherTask.getTaskEndTime());
	}

	@Override
	public int compareTo(Task o) {
		int beginTimeComparison = taskBeginTime.compareTo(o.taskBeginTime);
		if (beginTimeComparison != 0) {
			return beginTimeComparison;
		}
		return taskEndTime.compareTo(o.taskEndTime);
	}

}

class TaskBuilder {
	private LocalDateTime taskBeginTime;
	private LocalDateTime taskEndingTime;
	private Duration duration;

	public TaskBuilder startingAt(LocalDateTime taskBeginTime) {
		this.taskBeginTime = taskBeginTime;
		return this;
	}

	public TaskBuilder endingAt(LocalDateTime taskEndingTime) {
		duration = null;
		this.taskEndingTime = taskEndingTime;
		return this;
	}

	public TaskBuilder lasting(int amount, TemporalUnit unit) {
		return lasting(unit.getDuration().multipliedBy(amount));
	}

	public TaskBuilder lasting(Duration duration) {
		this.duration = duration;
		taskEndingTime = null;
		return this;
	}

	public Task build() {
		if (taskEndingTime != null){
			duration = Duration.between(taskBeginTime, taskEndingTime);
		}
		
		return new Task(taskBeginTime, taskBeginTime.plus(duration));
	}
}
