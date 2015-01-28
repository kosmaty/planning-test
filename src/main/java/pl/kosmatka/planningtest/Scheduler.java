package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Scheduler {
	
	private Set<Attendee> attendees = new HashSet<>();

	public List<TimeSlot> findPossibleTimeSlots(Duration duration, LocalDateTime begin,
			LocalDateTime end) {
		Iterator<Attendee> iterator = attendees.iterator();
		List<TimeSlot> currentTimeSlots = iterator.next().findFreeTimeSlots(duration, begin, end);
		while (iterator.hasNext()){
			List<TimeSlot> result = new ArrayList<TimeSlot>();
			List<TimeSlot> nextTimeSlots = iterator.next().findFreeTimeSlots(duration, begin, end);
			for (TimeSlot currentTimeSlot : currentTimeSlots) {
				for (TimeSlot nextTimeSlot : nextTimeSlots) {
					 Optional<TimeSlot> intersection = currentTimeSlot.intersectionWith(nextTimeSlot);
					 if (intersection.isPresent()){
						 result.add(intersection.get());
					 }
				}
			}
			currentTimeSlots = result;
		}
		
		
		return currentTimeSlots;
	}

	public void add(Attendee attendee) {
		attendees.add(attendee);
	}


}
