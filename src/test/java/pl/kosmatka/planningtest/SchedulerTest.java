package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class SchedulerTest {

	LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

	LocalDateTime periodStart = LocalDateTime.of(date, LocalTime.MIN);
	LocalDateTime periodEnd = LocalDateTime.of(date, LocalTime.MAX);

	private Attendee john;

	private Attendee jane;
	
	private Attendee jack;

	@Before
	public void setup() {
		// john: --------
		john = new Attendee("john", LocalTime.of(7, 0), LocalTime.of(15, 0));

		// jane: ----xxxx
		jane = new Attendee("jane", LocalTime.of(7, 0), LocalTime.of(15, 0));
		jane.addMeeting(
				LocalDateTime.of(date, jane.getWorkDayStart().plusHours(4)),
				LocalDateTime.of(date, jane.getWorkDayEnd()));
		// jack: --xx----<<
		jack = new Attendee("jack", LocalTime.of(5, 0), LocalTime.of(13, 0));
		jack.addMeeting(
				LocalDateTime.of(date, LocalTime.of(7, 0)),
				LocalDateTime.of(date, LocalTime.of(9, 0)));
	}

	@Test
	public void findPossibleTimeSlots_oneAtendeeWithOneFreeSlot_returnsThatSlot() {

		Scheduler scheduler = new Scheduler();
		scheduler.add(john);

		List<TimeSlot> possibleTimeSlots = scheduler.findPossibleTimeSlots(
				Duration.ofHours(1),
				periodStart,
				periodEnd);

		LocalDateTime workDayStart = LocalDateTime.of(date,
				john.getWorkDayStart());
		LocalDateTime workDayEnd = LocalDateTime.of(date, john.getWorkDayEnd());
		TimeSlot expectedTimeSlot = new TimeSlot(workDayStart, workDayEnd);
		Assertions.assertThat(possibleTimeSlots).containsOnly(expectedTimeSlot);
	}

	@Test
	public void findPossibleTimeSlots_requiredJohnAndJane_returnsFirstHalfOfDaySlot() {

		Scheduler scheduler = new Scheduler();
		scheduler.add(jane);
		scheduler.add(john);

		List<TimeSlot> possibleTimeSlots = scheduler.findPossibleTimeSlots(
				Duration.ofHours(1),
				periodStart,
				periodEnd);

		LocalDateTime workDayStart = LocalDateTime.of(date,
				john.getWorkDayStart());
		LocalDateTime workDayEnd = LocalDateTime.of(date, john.getWorkDayEnd());
		TimeSlot expectedTimeSlot = new TimeSlot(workDayStart,
				workDayEnd.minusHours(4));
		Assertions.assertThat(possibleTimeSlots).containsOnly(expectedTimeSlot);
	}
	
	@Test
	public void findPossibleTimeSlots_requiredJohnAndJaneAndJack_returnsValidSlot() {

		Scheduler scheduler = new Scheduler();
		scheduler.add(jane);
		scheduler.add(john);
		scheduler.add(jack);

		List<TimeSlot> possibleTimeSlots = scheduler.findPossibleTimeSlots(
				Duration.ofHours(1),
				periodStart,
				periodEnd);

		TimeSlot expectedTimeSlot = new TimeSlot(
				LocalDateTime.of(date,LocalTime.of(9, 0)),
				LocalDateTime.of(date,LocalTime.of(11, 0)));
		Assertions.assertThat(possibleTimeSlots).containsOnly(expectedTimeSlot);
	}


}
