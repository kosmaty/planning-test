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

public class AttendeeTest {

	private static final LocalTime WORK_DAY_START = LocalTime.of(7, 0);
	private static final LocalTime WORK_DAY_END = LocalTime.of(15, 0);

	private Attendee createAttendee() {
		return new Attendee("john", WORK_DAY_START, WORK_DAY_END);
	}

	@Test
	public void findFreeTimeSlots_oneDayPeriodNoPlannedMeetings_retunsWholeDayTimeSlot() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		List<TimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		TimeSlot wholeDayTimeSlot = new TimeSlot(LocalDateTime.of(date,
				WORK_DAY_START), LocalDateTime.of(date, WORK_DAY_END));
		Assertions.assertThat(timeSlots).containsExactly(wholeDayTimeSlot);

	}

	@Test
	public void findFreeTimeSlots_oneDayPeriodWholeDayMeeting_retunsEmptyList() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);
		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		List<TimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		Assertions.assertThat(timeSlots).isEmpty();

	}

	@Test
	public void findFreeTimeSlots_oneDayPeriodFirstHalfDayMeeting_retunsSecondHalfDayTimeSlot() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		Duration scheduledMeetingDuration = Duration.ofHours(4);
		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_START).plus(
						scheduledMeetingDuration));

		List<TimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		TimeSlot secondHalfDayTimeSlot = new TimeSlot(LocalDateTime.of(date,
				WORK_DAY_START).plus(scheduledMeetingDuration),
				LocalDateTime.of(date, WORK_DAY_END));
		Assertions.assertThat(timeSlots).containsExactly(secondHalfDayTimeSlot);

	}

	@Test
	public void findFreeTimeSlots_oneDayPeriodSecondHalfDayMeeting_retunsFirstHalfDayTimeSlot() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		Duration scheduledMeetingDuration = Duration.ofHours(4);
		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_END).minus(
						scheduledMeetingDuration),
				LocalDateTime.of(date, WORK_DAY_END));

		List<TimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		TimeSlot firstHalfDayTimeSlot = new TimeSlot(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END).minus(
						scheduledMeetingDuration));
		Assertions.assertThat(timeSlots).containsExactly(firstHalfDayTimeSlot);

	}

	@Test
	public void findFreeTimeSlots_oneDayPeriodMiddleDayMeeting_retunsTwoTimeSlots() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START).plusHours(2),
				LocalDateTime.of(date, WORK_DAY_END).minusHours(2));

		List<TimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		TimeSlot firstTimeSlot = new TimeSlot(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_START).plusHours(2));

		TimeSlot secondTimeSlot = new TimeSlot(
				LocalDateTime.of(date, WORK_DAY_END).minusHours(2),
				LocalDateTime.of(date, WORK_DAY_END));
		Assertions.assertThat(timeSlots).containsOnly(firstTimeSlot,
				secondTimeSlot);

	}

}
