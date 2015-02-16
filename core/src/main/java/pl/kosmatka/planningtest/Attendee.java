package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Attendee {

	private String name;
	private LocalTime workDayStart;
	private LocalTime workDayEnd;
	private SortedSet<Meeting> scheduledMeetings = new TreeSet<>();

	int meetingsCount = 0;

	public Attendee(String name, LocalTime workDayStart, LocalTime workDayEnd) {
		this.name = name;
		this.workDayStart = workDayStart;
		this.workDayEnd = workDayEnd;
	}

	public void addMeeting(LocalDateTime meettingStart,
			LocalDateTime meettingEnd) {
		scheduledMeetings.add(new Meeting(meettingStart, meettingEnd));

	}

	public List<TimeSlot> findFreeTimeSlots(Duration duration,
			LocalDateTime begin, LocalDateTime end) {

		List<TimeSlot> timeSlots = new ArrayList<>();

		for (LocalDate currentDate = begin.toLocalDate(); currentDate
				.minusDays(
						1).isBefore(end.toLocalDate()); currentDate = currentDate
				.plusDays(1)) {
			LocalDateTime currentWorkDayStart = LocalDateTime.of(currentDate,
					workDayStart);
			LocalDateTime currentWorkDayEnd = LocalDateTime.of(currentDate,
					workDayEnd);
			SortedSet<Meeting> currentDayMeetings = scheduledMeetings.subSet(
					new Meeting(currentWorkDayStart, currentWorkDayStart),
					new Meeting(currentWorkDayEnd, currentWorkDayEnd));

			if (currentDayMeetings.isEmpty()) {
				timeSlots.add(
						new TimeSlot(currentWorkDayStart, currentWorkDayEnd, this));
				continue;
			}

			if (currentDayMeetings.last().getEnd().isBefore(currentWorkDayEnd)) {
				LocalDateTime timeSlotStart = currentDayMeetings
						.last().getEnd();
				LocalDateTime timeSlotEnd = currentWorkDayEnd;
				addTimeSlotIfBigEnough(duration, timeSlots, timeSlotStart,
						timeSlotEnd);
			}

			if (currentDayMeetings.first().getBegin()
					.isAfter(currentWorkDayStart)) {
				LocalDateTime timeSlotStart = currentWorkDayStart;
				LocalDateTime timeSlotEnd = currentDayMeetings.first()
						.getBegin();
				addTimeSlotIfBigEnough(duration, timeSlots, timeSlotStart,
						timeSlotEnd);
			}

			Iterator<Meeting> iterator = currentDayMeetings.iterator();
			Meeting currentMeeting = iterator.next();

			while (iterator.hasNext()) {
				Meeting nextMeeting = iterator.next();
				LocalDateTime timeSlotStart = currentMeeting.getEnd();
				LocalDateTime timeSlotEnd = nextMeeting.getBegin();
				addTimeSlotIfBigEnough(duration, timeSlots, timeSlotStart,
						timeSlotEnd);
				currentMeeting = nextMeeting;
			}
		}
		
		return timeSlots;

	}

	private void addTimeSlotIfBigEnough(Duration duration,
			List<TimeSlot> timeSlots, LocalDateTime timeSlotStart,
			LocalDateTime timeSlotEnd) {
		if (Duration.between(timeSlotStart, timeSlotEnd)
				.compareTo(duration) >= 0) {
			timeSlots.add(new TimeSlot(timeSlotStart, timeSlotEnd, this));
		}
	}

	public LocalTime getWorkDayStart() {
		return workDayStart;
	}

	public LocalTime getWorkDayEnd() {
		return workDayEnd;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(name).toString();
	}

}
