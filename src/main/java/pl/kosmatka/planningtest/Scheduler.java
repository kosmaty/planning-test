package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Scheduler {

	public TimeSlots findPossibleTimeSlots(List<Attendee> attendees,
			Duration meettingLength, LocalDateTime timeFrameStart,
			LocalDateTime timeFrameEnd) {
		if (attendees.get(0).hasMeetings()){
			return new TimeSlots(OperationStatus.NOT_FOUND);
		}
		return new TimeSlots(OperationStatus.OK);
	}

}
