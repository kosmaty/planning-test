package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Scheduler {

	private Set<Attendee> attendees = new HashSet<>();

	private List<ResultTimeSlot> findPossibleTimeSlots(Duration duration,
			LocalDateTime begin,
			LocalDateTime end) {
		Iterator<Attendee> iterator = attendees.iterator();
		Attendee firstAttendee = iterator.next();
		List<ResultTimeSlot> currentTimeSlots = firstAttendee
				.findFreeTimeSlots(duration, begin, end)
				.stream()
				.map((timeSlot) -> (new ResultTimeSlot(timeSlot, firstAttendee)))
				.collect(Collectors.toList());
		
		while (iterator.hasNext()) {
			List<ResultTimeSlot> result = new ArrayList<>();
			Attendee nextAtendee = iterator.next();
			List<TimeSlot> nextTimeSlots = nextAtendee.findFreeTimeSlots(
					duration, begin, end);
			for (ResultTimeSlot currentTimeSlot : currentTimeSlots) {
				for (TimeSlot nextTimeSlot : nextTimeSlots) {
					Optional<TimeSlot> intersection = currentTimeSlot
							.getTimeSlot()
							.intersectionWith(nextTimeSlot);
					if (intersection.isPresent()) {			
						Set<Attendee> newAttendees = new HashSet<>(currentTimeSlot.getAttendees());
						newAttendees.add(nextAtendee);
						result.add(new ResultTimeSlot(intersection.get(), newAttendees));
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

	public SchedulerResult findPossibleTimeSlots1(Duration duration,
			LocalDateTime begin, LocalDateTime end) {
		List<ResultTimeSlot> possibleTimeSlots = findPossibleTimeSlots(duration, begin, end);
		return new SchedulerResult(possibleTimeSlots,
				SchedulerResultStatus.OK);
	}

}
