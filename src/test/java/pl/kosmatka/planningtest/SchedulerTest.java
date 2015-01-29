package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static pl.kosmatka.planningtest.SchedulerResultAssert.*;

import org.junit.Before;
import org.junit.Test;

public class SchedulerTest {

	LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

	LocalDateTime periodStart = LocalDateTime.of(date, LocalTime.MIN);
	LocalDateTime periodEnd = LocalDateTime.of(date, LocalTime.MAX);

	private Attendee john;

	private Attendee jane;

	private Attendee jack;

	private Attendee jill;

	@Before
	public void setup() {
		// john: XX--------
		john = new Attendee("john", LocalTime.of(7, 0), LocalTime.of(15, 0));

		// jane: XX----xxxx
		jane = new Attendee("jane", LocalTime.of(7, 0), LocalTime.of(15, 0));
		jane.addMeeting(
				LocalDateTime.of(date, jane.getWorkDayStart().plusHours(4)),
				LocalDateTime.of(date, jane.getWorkDayEnd()));
		// jack: --xx----XX
		jack = new Attendee("jack", LocalTime.of(5, 0), LocalTime.of(13, 0));
		jack.addMeeting(
				LocalDateTime.of(date, LocalTime.of(7, 0)),
				LocalDateTime.of(date, LocalTime.of(9, 0)));

		// jill: XXxxxxxxxx
		jill = new Attendee("jill", LocalTime.of(7, 0), LocalTime.of(15, 0));
		jill.addMeeting(
				LocalDateTime.of(date, LocalTime.of(7, 0)),
				LocalDateTime.of(date, LocalTime.of(15, 0)));
	}

	@Test
	public void findPossibleTimeSlots_oneAtendeeWithOneFreeSlot_returnsThatSlot() {

		Scheduler scheduler = new Scheduler();
		scheduler.add(john);

		SchedulerResult result = scheduler.findPossibleTimeSlots1(
				Duration.ofHours(1),
				periodStart,
				periodEnd);

		LocalDateTime workDayStart = LocalDateTime.of(date,
				john.getWorkDayStart());
		LocalDateTime workDayEnd = LocalDateTime.of(date, john.getWorkDayEnd());
		ResultTimeSlot expectedTimeSlot = new ResultTimeSlot(workDayStart,
				workDayEnd, 
				Collections.singleton(john));
		assertThat(result).hasTimeSlots(expectedTimeSlot);
	}

	@Test
	public void findPossibleTimeSlots_requiredJohnAndJane_returnsFirstHalfOfDaySlot() {

		Scheduler scheduler = new Scheduler();
		scheduler.add(jane);
		scheduler.add(john);

		SchedulerResult result = scheduler.findPossibleTimeSlots1(
				Duration.ofHours(1),
				periodStart,
				periodEnd);

		LocalDateTime workDayStart = LocalDateTime.of(date,
				john.getWorkDayStart());
		LocalDateTime workDayEnd = LocalDateTime.of(date, john.getWorkDayEnd());
		ResultTimeSlot expectedTimeSlot = new ResultTimeSlot(workDayStart,
				workDayEnd.minusHours(4),
				new HashSet<>(Arrays.asList(john, jane)));
		assertThat(result)
				.hasTimeSlots(expectedTimeSlot)
				.hasStatus(SchedulerResultStatus.OK);
	}

	@Test
	public void findPossibleTimeSlots_requiredJohnAndJaneAndJack_returnsValidSlot() {

		Scheduler scheduler = new Scheduler();
		scheduler.add(jane);
		scheduler.add(john);
		scheduler.add(jack);

		SchedulerResult result = scheduler.findPossibleTimeSlots1(
				Duration.ofHours(1),
				periodStart,
				periodEnd);

		ResultTimeSlot expectedTimeSlot = new ResultTimeSlot(
				LocalDateTime.of(date, LocalTime.of(9, 0)),
				LocalDateTime.of(date, LocalTime.of(11, 0)),
				new HashSet<>(Arrays.asList(jane, jack, john)));
		assertThat(result).hasTimeSlots(expectedTimeSlot);
	}

}
