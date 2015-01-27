package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class SchedulerTest {

	private Attendee john;

	@Before
	public void setup() {
		john = new Attendee("john", LocalTime.of(7, 0), LocalTime.of(15, 0));
	}

	@Test
	public void findPossibleTimeSlots_oneAtendeeWithoutMeetings_returnsOkStatus() {
		Scheduler scheduler = new Scheduler();
		List<Attendee> attendees = new ArrayList<>();
		attendees.add(john);
		Duration meettingLength = Duration.ofHours(1);
		LocalDate date = LocalDate.of(2015, Month.JANUARY, 1);
		LocalDateTime timeFrameStart = LocalDateTime.of(date, LocalTime.of(0, 0));
		LocalDateTime timeFrameEnd = LocalDateTime.of(date, LocalTime.of(23,59));

		TimeSlots possibleTimeSlots = scheduler.findPossibleTimeSlots(
				attendees, meettingLength, timeFrameStart, timeFrameEnd);

		Assertions.assertThat(possibleTimeSlots.getStatus()).isEqualTo(
				OperationStatus.OK);

	}

	@Test
	public void findPossibleTimeSlots_oneAtendeeWithWholeDayMeeting_returnsNotFoundStatus() {
		Scheduler scheduler = new Scheduler();
		List<Attendee> attendees = new ArrayList<>();
		LocalDate date = LocalDate.of(2015, Month.JANUARY, 1);

		john.addMeeting(LocalDateTime.of(date,john.getWorkDayStart()),
				LocalDateTime.of(date,john.getWorkDayEnd()));
		attendees.add(john);
		Duration meettingLength = Duration.ofHours(1);
		LocalDateTime timeFrameStart = LocalDateTime.of(date, LocalTime.of(0, 0));
		LocalDateTime timeFrameEnd = LocalDateTime.of(date, LocalTime.of(23,59));

		TimeSlots possibleTimeSlots = scheduler.findPossibleTimeSlots(
				attendees, meettingLength, timeFrameStart, timeFrameEnd);

		Assertions.assertThat(possibleTimeSlots.getStatus()).isEqualTo(
				OperationStatus.NOT_FOUND);

	}
	
	@Test
	public void findPossibleTimeSlots_oneAtendeeWithHalfDayMeeting_returnsOkStatus() {
		Scheduler scheduler = new Scheduler();
		List<Attendee> attendees = new ArrayList<>();
		LocalDate date = LocalDate.of(2015, Month.JANUARY, 1);

		john.addMeeting(LocalDateTime.of(date,john.getWorkDayStart()),
				LocalDateTime.of(date,john.getWorkDayStart().plusHours(4)));
		attendees.add(john);
		Duration meettingLength = Duration.ofHours(1);
		LocalDateTime timeFrameStart = LocalDateTime.of(date, LocalTime.of(0, 0));
		LocalDateTime timeFrameEnd = LocalDateTime.of(date, LocalTime.of(23,59));

		TimeSlots possibleTimeSlots = scheduler.findPossibleTimeSlots(
				attendees, meettingLength, timeFrameStart, timeFrameEnd);

		Assertions.assertThat(possibleTimeSlots.getStatus()).isEqualTo(
				OperationStatus.OK);

	}

}
