package pl.kosmatka.planningtest;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Attendee {

	private String name;
	private LocalTime workDayStart;
	private LocalTime workDayEnd;
	
	int meetingsCount = 0;
	
	

	public Attendee(String name, LocalTime workDayStart, LocalTime workDayEnd) {
		this.name = name;
		this.workDayStart = workDayStart;
		this.workDayEnd = workDayEnd;
	}

	public void addMeeting(LocalDateTime meettingStart, LocalDateTime mittingEnd) {
		meetingsCount++;

	}

	public boolean hasMeetings() {
		return meetingsCount != 0;
	}

	public LocalTime getWorkDayStart() {
		return workDayStart;
	}

	public LocalTime getWorkDayEnd() {
		return workDayEnd;
	}

}
