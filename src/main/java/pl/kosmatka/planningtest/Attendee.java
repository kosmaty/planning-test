package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
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
		if (!hasMeetings()) {
			return Collections.singletonList(new TimeSlot(begin, end));
		}
		List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();

		if (scheduledMeetings.last().getEnd().isBefore(end)) {
			timeSlots.add(new TimeSlot(scheduledMeetings
					.last().getEnd(), end));
		}

		if (scheduledMeetings.first().getBegin().isAfter(begin)) {
			timeSlots.add(new TimeSlot(begin,
					scheduledMeetings.first().getBegin()));
		}

		return timeSlots;

	}

	public LocalTime getWorkDayStart() {
		return workDayStart;
	}

	public LocalTime getWorkDayEnd() {
		return workDayEnd;
	}

}
