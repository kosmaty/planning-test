package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Attendee {

	private String name;
	private LocalTime workDayStart;
	private LocalTime workDayEnd;
	private SortedSet<TimeSlot> scheduledMeetings = new TreeSet<>();

	int meetingsCount = 0;

	public Attendee(String name, LocalTime workDayStart, LocalTime workDayEnd) {
		this.name = name;
		this.workDayStart = workDayStart;
		this.workDayEnd = workDayEnd;
	}

	public void addMeeting(LocalDateTime meettingStart,
			LocalDateTime meettingEnd) {
		scheduledMeetings.add(new TimeSlot(meettingStart, meettingEnd));

	}

	public boolean hasMeetings() {
		return !scheduledMeetings.isEmpty();
	}

	public List<TimeSlot> findFreeTimeSlots(Duration duration,
			LocalDateTime begin, LocalDateTime end) {
		LocalDateTime currentWorkDayStart = LocalDateTime.of(begin.toLocalDate(), workDayStart);
		LocalDateTime currentWorkDayEnd = LocalDateTime.of(end.toLocalDate(), workDayEnd);
		if (!hasMeetings()) {
			return Collections.singletonList(new TimeSlot(currentWorkDayStart, currentWorkDayEnd));
		}
		List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();

		
		if (scheduledMeetings.last().getEnd().isBefore(currentWorkDayEnd)) {
			LocalDateTime timeSlotStart = scheduledMeetings
					.last().getEnd();
			LocalDateTime timeSlotEnd = currentWorkDayEnd;
			addTimeSlotIfBigEnough(duration, timeSlots, timeSlotStart,
					timeSlotEnd);
		}

		if (scheduledMeetings.first().getBegin().isAfter(currentWorkDayStart)) {
			LocalDateTime timeSlotStart = currentWorkDayStart;
			LocalDateTime timeSlotEnd = scheduledMeetings.first().getBegin();
			addTimeSlotIfBigEnough(duration, timeSlots, timeSlotStart,
					timeSlotEnd);
		}

		Iterator<TimeSlot> iterator = scheduledMeetings.iterator();
		TimeSlot currentMeeting = iterator.next();

		while (iterator.hasNext()) {
			TimeSlot nextMeeting = iterator.next();
			LocalDateTime timeSlotStart = currentMeeting.getEnd();
			LocalDateTime timeSlotEnd = nextMeeting.getBegin();
			addTimeSlotIfBigEnough(duration, timeSlots, timeSlotStart,
					timeSlotEnd);
			currentMeeting = nextMeeting;
		}

		return timeSlots;

	}

	private void addTimeSlotIfBigEnough(Duration duration,
			List<TimeSlot> timeSlots, LocalDateTime timeSlotStart,
			LocalDateTime timeSlotEnd) {
		if (Duration.between(timeSlotStart, timeSlotEnd)
				.compareTo(duration) > 0) {
			timeSlots.add(new TimeSlot(timeSlotStart, timeSlotEnd));
		}
	}

	public LocalTime getWorkDayStart() {
		return workDayStart;
	}

	public LocalTime getWorkDayEnd() {
		return workDayEnd;
	}

}
