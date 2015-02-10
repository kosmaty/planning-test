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
	private static final LocalTime WORK_DAY_END = WORK_DAY_START.plusHours(8);

	private Attendee createAttendee() {
		return new Attendee("john", WORK_DAY_START, WORK_DAY_END);
	}

	@Test
	public void findFreeTimeSlots_oneWorkingDayPeriodNoPlannedMeetings_retunsWholeDayTimeSlot() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		ResultTimeSlot wholeWorkingDayTimeSlot = new ResultTimeSlot(LocalDateTime.of(date,
				WORK_DAY_START), LocalDateTime.of(date, WORK_DAY_END), attendee);
		Assertions.assertThat(timeSlots).containsExactly(wholeWorkingDayTimeSlot);

	}

	@Test
	public void findFreeTimeSlots_oneWorkingDayPeriodWholeDayMeeting_retunsEmptyList() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);
		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		Assertions.assertThat(timeSlots).isEmpty();

	}

	@Test
	public void findFreeTimeSlots_oneWorkingDayPeriodFirstHalfDayMeeting_retunsSecondHalfDayTimeSlot() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		Duration scheduledMeetingDuration = Duration.ofHours(4);
		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_START).plus(
						scheduledMeetingDuration));

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		ResultTimeSlot secondHalfDayTimeSlot = new ResultTimeSlot(LocalDateTime.of(date,
				WORK_DAY_START).plus(scheduledMeetingDuration),
				LocalDateTime.of(date, WORK_DAY_END), attendee);
		Assertions.assertThat(timeSlots).containsExactly(secondHalfDayTimeSlot);

	}

	@Test
	public void findFreeTimeSlots_oneWorkingDayPeriodSecondHalfDayMeeting_retunsFirstHalfDayTimeSlot() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		Duration scheduledMeetingDuration = Duration.ofHours(4);
		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_END).minus(
						scheduledMeetingDuration),
				LocalDateTime.of(date, WORK_DAY_END));

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		ResultTimeSlot firstHalfDayTimeSlot = new ResultTimeSlot(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END).minus(
						scheduledMeetingDuration), attendee);
		Assertions.assertThat(timeSlots).containsExactly(firstHalfDayTimeSlot);

	}

	@Test
	public void findFreeTimeSlots_oneWorkingDayPeriodMiddleDayMeeting_retunsTwoTimeSlots() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START).plusHours(2),
				LocalDateTime.of(date, WORK_DAY_END).minusHours(2));

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		ResultTimeSlot firstTimeSlot = new ResultTimeSlot(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_START).plusHours(2), attendee);

		ResultTimeSlot secondTimeSlot = new ResultTimeSlot(
				LocalDateTime.of(date, WORK_DAY_END).minusHours(2),
				LocalDateTime.of(date, WORK_DAY_END), attendee);
		Assertions.assertThat(timeSlots).containsOnly(firstTimeSlot,
				secondTimeSlot);

	}
	
	@Test
	public void findFreeTimeSlots_oneWorkingDayPeriodBeginingOfDayAndEndOfDayMeetings_retunsMiddleDayTimeSlot() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_START).plusHours(2));

		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_END).minusHours(2),
				LocalDateTime.of(date, WORK_DAY_END));
		
		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		ResultTimeSlot middleDayTimeSlot = new ResultTimeSlot(
				LocalDateTime.of(date, WORK_DAY_START).plusHours(2),
				LocalDateTime.of(date, WORK_DAY_END).minusHours(2), attendee);

		
		Assertions.assertThat(timeSlots).containsOnly(middleDayTimeSlot);

	}
	
	@Test
	public void findFreeTimeSlots_oneWorkingDayPeriodBeginingOfDayMeetingToLittleFreeTime_retunsEmptyList() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END).minusHours(1));
		
		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(2),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));
		
		Assertions.assertThat(timeSlots).isEmpty();
	}
	
	@Test
	public void findFreeTimeSlots_oneWorkingDayPeriodEndOfDayMeetingToLittleFreeTime_retunsEmptyList() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START).plusHours(1),
				LocalDateTime.of(date, WORK_DAY_END));
		
		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(2),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));
		
		Assertions.assertThat(timeSlots).isEmpty();
	}
	
	@Test
	public void findFreeTimeSlots_oneWorkingDayPeriodBeginingOfDayAndEndOfDayMeetingsToLittleFreeTime_retunsEmptyList() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_START).plusHours(4));

		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_END).minusHours(3),
				LocalDateTime.of(date, WORK_DAY_END));
		
		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(2),
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		Assertions.assertThat(timeSlots).isEmpty();

	}

	@Test
	public void findFreeTimeSlots_oneFullDayPeriodWholeWorkingDayMeeting_retunsEmptyList() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		attendee.addMeeting(
				LocalDateTime.of(date, WORK_DAY_START),
				LocalDateTime.of(date, WORK_DAY_END));

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, LocalTime.MIN),
				LocalDateTime.of(date, LocalTime.MAX));

		Assertions.assertThat(timeSlots).isEmpty();

	}
	
	@Test
	public void findFreeTimeSlots_oneFullDayPeriodNoMeeting_retunsTimeSlotForWholeWorkingDay() {
		Attendee attendee = createAttendee();
		LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(date, LocalTime.MIN),
				LocalDateTime.of(date, LocalTime.MAX));
		
		ResultTimeSlot wholeWorkingDayTimeSlot = new ResultTimeSlot(LocalDateTime.of(date,
				WORK_DAY_START), LocalDateTime.of(date, WORK_DAY_END), attendee);

		Assertions.assertThat(timeSlots).containsOnly(wholeWorkingDayTimeSlot);

	}
	
	@Test
	public void findFreeTimeSlots_twoFullDaysPeriodNoMeeting_retunsTimeSlotsForEachWholeWorkingDay() {
		Attendee attendee = createAttendee();
		LocalDate beginDate = LocalDate.of(2000, Month.JANUARY, 1);
		LocalDate endDate = beginDate.plusDays(1);

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(beginDate, LocalTime.MIN),
				LocalDateTime.of(endDate, LocalTime.MAX));

		ResultTimeSlot firstWorkingDayTimeSlot = new ResultTimeSlot(
				LocalDateTime.of(beginDate, WORK_DAY_START),
				LocalDateTime.of(beginDate, WORK_DAY_END), attendee);
		ResultTimeSlot secondWorkingDayTimeSlot = new ResultTimeSlot(
				LocalDateTime.of(endDate, WORK_DAY_START),
				LocalDateTime.of(endDate, WORK_DAY_END), attendee);

		Assertions.assertThat(timeSlots).containsOnly(
				firstWorkingDayTimeSlot,
				secondWorkingDayTimeSlot);

	}
	
	@Test
	public void findFreeTimeSlots_twoFullDaysPeriodFirstWholeDayMeeting_retunsTimeSlotForWholeSecondWorkingDay() {
		Attendee attendee = createAttendee();
		LocalDate beginDate = LocalDate.of(2000, Month.JANUARY, 1);
		LocalDate endDate = beginDate.plusDays(1);
		
		attendee.addMeeting(
				LocalDateTime.of(beginDate, WORK_DAY_START), 
				LocalDateTime.of(beginDate, WORK_DAY_END));

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(beginDate, LocalTime.MIN),
				LocalDateTime.of(endDate, LocalTime.MAX));
		
		ResultTimeSlot secondWorkingDayTimeSlot = new ResultTimeSlot(
				LocalDateTime.of(endDate, WORK_DAY_START),
				LocalDateTime.of(endDate, WORK_DAY_END), attendee);

		Assertions.assertThat(timeSlots).containsOnly(
				secondWorkingDayTimeSlot);

	}
	
	@Test
	public void findFreeTimeSlots_twoFullDaysPeriodSecondWholeDayMeeting_retunsTimeSlotForWholeFirstWorkingDay() {
		Attendee attendee = createAttendee();
		LocalDate beginDate = LocalDate.of(2000, Month.JANUARY, 1);
		LocalDate endDate = beginDate.plusDays(1);
		
		attendee.addMeeting(
				LocalDateTime.of(endDate, WORK_DAY_START), 
				LocalDateTime.of(endDate, WORK_DAY_END));

		List<ResultTimeSlot> timeSlots = attendee.findFreeTimeSlots(
				Duration.ofHours(1),
				LocalDateTime.of(beginDate, LocalTime.MIN),
				LocalDateTime.of(endDate, LocalTime.MAX));
		
		ResultTimeSlot firstWorkingDayTimeSlot = new ResultTimeSlot(
				LocalDateTime.of(beginDate, WORK_DAY_START),
				LocalDateTime.of(beginDate, WORK_DAY_END), attendee);

		Assertions.assertThat(timeSlots).containsOnly(
				firstWorkingDayTimeSlot);

	}
	
	
	// TODO: working hours at night
	// TODO: period starts or ends in the middle of working hours
	
}
