package pl.kosmatka.planningtest;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kosmatka.planningtest.Task.newTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PlannerTest {

	private static final Duration TWO_HOURS = Duration.ofHours(2);

	private static final Duration ONE_HOUR = Duration.ofHours(1);

	private static final LocalDateTime WORK_DAY_BEGINING = LocalDateTime.of(
			2015, Month.JANUARY, 1, 7, 0);

	private static final LocalDateTime WORK_DAY_END = WORK_DAY_BEGINING
			.plusHours(8);

	private Employee john;

	private Employee jane;

	private Employee jack;

	@Before
	public void setup() {
		john = new Employee("John");
		jane = new Employee("Jane");
		jack = new Employee("Jack");
	}

	@Test
	public void findPossibleTime_noTasksPlanned_returnsBegginingOfWorkDay() {
		Planner planner = createPlanner();

		LocalDateTime possibleDate = planner.findPossibleTime(TWO_HOURS);

		assertThat(possibleDate).isEqualTo(WORK_DAY_BEGINING);

	}

	private Planner createPlanner() {
		Planner planner = new Planner(WORK_DAY_BEGINING, WORK_DAY_END);
		return planner;
	}

	@Test
	public void findPossibleTime_oneEmployeeTaskOnBegining_returnsEndOfThatTask() {
		Planner planner = createPlanner();
		LocalDateTime taskBeginTime = WORK_DAY_BEGINING;
		LocalDateTime taskEndTime = taskBeginTime.plusHours(1);
		planner.addTask(john, new Task(taskBeginTime, taskEndTime));

		LocalDateTime possibleTime = planner.findPossibleTime(TWO_HOURS);

		assertThat(possibleTime).isEqualTo(taskEndTime);

	}

	@Test
	public void findPossibleTime_oneEmployeeTaskInMiddleOfDay_returnsBegginingOfWorkDay() {
		Planner planner = createPlanner();
		LocalDateTime taskBeginTime = WORK_DAY_BEGINING.plusHours(2);
		LocalDateTime taskEndTime = taskBeginTime.plusHours(1);
		planner.addTask(john, new Task(taskBeginTime, taskEndTime));

		LocalDateTime possibleTime = planner.findPossibleTime(ONE_HOUR);

		assertThat(possibleTime).isEqualTo(WORK_DAY_BEGINING);

	}

	@Test
	public void findPossibleTime_oneEmployeeTwoTaskToLittleTimeBetween_returnsEndOfSecondTask() {
		Planner planner = createPlanner();
		LocalDateTime firstTaskBeginTime = WORK_DAY_BEGINING;
		LocalDateTime firstTaskEndTime = firstTaskBeginTime.plusHours(1);
		planner.addTask(john, new Task(firstTaskBeginTime, firstTaskEndTime));

		LocalDateTime secondTaskBeginTime = firstTaskEndTime.plusHours(1);
		LocalDateTime secondTaskEndTime = secondTaskBeginTime.plusHours(1);
		planner.addTask(john, new Task(secondTaskBeginTime, secondTaskEndTime));

		LocalDateTime possibleTime = planner.findPossibleTime(TWO_HOURS);

		assertThat(possibleTime).isEqualTo(secondTaskEndTime);

	}

	@Test
	public void findPossibleTime_oneEmployeeTwoTaskToLittleTimeBetweenAddedInReverseOrder_returnsEndOfSecondTask() {
		Planner planner = createPlanner();
		LocalDateTime firstTaskBeginTime = WORK_DAY_BEGINING;
		LocalDateTime firstTaskEndTime = firstTaskBeginTime.plusHours(1);

		LocalDateTime secondTaskBeginTime = firstTaskEndTime.plusHours(1);
		LocalDateTime secondTaskEndTime = secondTaskBeginTime.plusHours(1);
		planner.addTask(john, new Task(secondTaskBeginTime, secondTaskEndTime));
		planner.addTask(john, new Task(firstTaskBeginTime, firstTaskEndTime));

		LocalDateTime possibleTime = planner.findPossibleTime(TWO_HOURS);

		assertThat(possibleTime).isEqualTo(secondTaskEndTime);

	}

	@Test
	public void findPossibleTime_noFreeTime_returnsNull() {
		Planner planner = createPlanner();
		LocalDateTime firstTaskBeginTime = WORK_DAY_BEGINING;
		LocalDateTime firstTaskEndTime = WORK_DAY_END;
		planner.addTask(john, new Task(firstTaskBeginTime, firstTaskEndTime));

		LocalDateTime possibleTime = planner.findPossibleTime(ONE_HOUR);

		assertThat(possibleTime).isNull();
	}

	@Test
	public void findPossibleTime_busyDay_returnsFirstFreeTime() {

		Planner planner = createPlanner();
		prepareBusyDaySchedule(planner);

		LocalDateTime possibleTime = planner.findPossibleTime(ONE_HOUR);

		assertThat(possibleTime).isEqualTo(WORK_DAY_BEGINING.plusHours(6));

	}

	private void prepareBusyDaySchedule(Planner planner) {
		// john x-xx----
		// jane -xx--x--
		// jack xxx-x---
		planner.addTask(john,
				newTask().startingAt(WORK_DAY_BEGINING).lasting(ONE_HOUR)
						.build());
		planner.addTask(
				john,
				newTask().startingAt(WORK_DAY_BEGINING.plusHours(2))
						.lasting(TWO_HOURS).build());
		planner.addTask(
				jane,
				newTask().startingAt(WORK_DAY_BEGINING.plusHours(1))
						.lasting(TWO_HOURS).build());
		planner.addTask(
				jane,
				newTask().startingAt(WORK_DAY_BEGINING.plusHours(5))
						.lasting(ONE_HOUR).build());
		planner.addTask(
				jack,
				newTask().startingAt(WORK_DAY_BEGINING)
						.lasting(3, ChronoUnit.HOURS).build());

		planner.addTask(
				jack,
				newTask().startingAt(WORK_DAY_BEGINING.plusHours(4))
						.lasting(ONE_HOUR).build());
	}

	@Test
	public void findPossibleTime_oneTaskDuringAnother_returnsEndOfLongerTask() {
		Planner planner = createPlanner();
		// john: xxx-----
		// jane: -x------
		Task johnsTask = newTask().startingAt(WORK_DAY_BEGINING)
				.lasting(3, ChronoUnit.HOURS).build();
		Task janesTask = newTask().startingAt(WORK_DAY_BEGINING.plusHours(1))
				.lasting(ONE_HOUR).build();
		planner.addTask(john, johnsTask);
		planner.addTask(jane, janesTask);

		LocalDateTime possibleTime = planner.findPossibleTime(ONE_HOUR);

		assertThat(possibleTime).isEqualTo(johnsTask.getTaskEndTime());
	}

	@Test
	public void findPossibleTime_oneBusyEmployAndOneFreeEmployeeOneAbsentAllowed_returnsPossibleTimeForFreeUser() {
		Planner planner = createPlanner();
		planner.addTask(john,
				newTask().startingAt(WORK_DAY_BEGINING).endingAt(WORK_DAY_END)
						.build());
		Task janesTask = newTask().startingAt(WORK_DAY_BEGINING)
				.lasting(ONE_HOUR).build();
		planner.addTask(jane, janesTask);

		LocalDateTime possibleTime = planner.findPossibleTime(ONE_HOUR, 1);

		assertThat(possibleTime).isEqualTo(janesTask.getTaskEndTime());
	}

}
