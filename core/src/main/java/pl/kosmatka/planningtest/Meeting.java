package pl.kosmatka.planningtest;

import java.time.LocalDateTime;

public class Meeting implements Comparable<Meeting>{
	
	private LocalDateTime begin;
	
	private LocalDateTime end;

	public Meeting(LocalDateTime begin, LocalDateTime end) {
		super();
		this.begin = begin;
		this.end = end;
	}

	LocalDateTime getBegin() {
		return begin;
	}

	LocalDateTime getEnd() {
		return end;
	}

	@Override
	public int compareTo(Meeting o) {
		return begin.compareTo(o.begin);
	}
	
	

}
