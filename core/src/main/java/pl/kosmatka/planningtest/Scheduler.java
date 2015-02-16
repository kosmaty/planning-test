package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Scheduler {

	private Set<Attendee> attendees = new HashSet<>();

	public void add(Attendee attendee) {
		attendees.add(attendee);
	}

	public SchedulerResult findPossibleTimeSlots(Duration duration,
			LocalDateTime begin, LocalDateTime end) {

		List<TimeSlot> possibleTimeSlots = findBestTimeSlots(
				duration, begin, end);

		SchedulerResultStatus status = calculateStatus(possibleTimeSlots);
		return new SchedulerResult(possibleTimeSlots, status);
	}
	
	private List<TimeSlot> findBestTimeSlots(Duration duration,
			LocalDateTime begin, LocalDateTime end) {

		List<Event> events = prepareEvents(duration, begin, end);

		List<TimeSlot> baseTimeSlots = createTimeSlotsFrom(events);

		List<TimeSlot> resultTimeSlots = findResultTimeSlots(duration,
				baseTimeSlots);

		return resultTimeSlots;
	}


	private List<Event> prepareEvents(Duration duration, LocalDateTime begin,
			LocalDateTime end) {
		List<Event> events = new ArrayList<Event>();
		for (Attendee attendee : attendees) {
			for (TimeSlot timeSlot : attendee.findFreeTimeSlots(duration,
					begin, end)) {
				events.add(new Event(timeSlot.getBegin(), attendee,
						EventType.PERIOD_BEGIN));
				events.add(new Event(timeSlot.getEnd(), attendee,
						EventType.PERIOD_END));
			}
		}
	
		events.sort((x, y) -> x.time.compareTo(y.time));
		return events;
	}

	private List<TimeSlot> createTimeSlotsFrom(List<Event> events) {
		Set<Attendee> previousAttendees = new HashSet<Attendee>();
		Event previousEvent = null;
		List<TimeSlot> items = new ArrayList<TimeSlot>();
		for (Event currentEvent : events) {
			Set<Attendee> attendees = new HashSet<Attendee>(previousAttendees);
			if (currentEvent.type == EventType.PERIOD_BEGIN) {
				attendees.add(currentEvent.attendee);
			} else {
				attendees.remove(currentEvent.attendee);
			}
	
			if (!previousAttendees.isEmpty()) {
				items.add(new TimeSlot(previousEvent.time, currentEvent.time,
						previousAttendees));
			}
			previousEvent = currentEvent;
			previousAttendees = attendees;
		}
		return items;
	}

	private List<TimeSlot> findResultTimeSlots(Duration duration,
			List<TimeSlot> baseTimeSlots) {
		List<TimeSlot> resultTimeSlots = new ArrayList<TimeSlot>();

		while (resultTimeSlots.isEmpty() && !baseTimeSlots.isEmpty()) {
			resultTimeSlots = findTimeSlotsOfDuration(baseTimeSlots, duration);

			if (resultTimeSlots.isEmpty()) {
				baseTimeSlots = mergeTimeSlotsWithMostAttendees(baseTimeSlots);
			} else {
				int maxAttendees = maxAttendeesCountOf(resultTimeSlots);
				resultTimeSlots = resultTimeSlots.stream()
						.filter(x -> x.attendeesCount() == maxAttendees)
						.collect(Collectors.toList());
			}
		}
		return resultTimeSlots;
	}

	private List<TimeSlot> mergeTimeSlotsWithMostAttendees(
			List<TimeSlot> timeSlots) {
		int maxAttendees = maxAttendeesCountOf(timeSlots);

		List<TimeSlot> resultItems = mergeTimeSlotsWithAttendeesNo(
				timeSlots, maxAttendees);
		return resultItems;
	}

	private List<TimeSlot> mergeTimeSlotsWithAttendeesNo(
			List<TimeSlot> timeSlots, int attendeesNo) {
		List<TimeSlot> resultItems = new ArrayList<>();
		for (int i = 0; i < timeSlots.size(); i++) {
			TimeSlot currentItem = timeSlots.get(i);
			if (shouldMerge(attendeesNo, currentItem)
					|| isLastTimeSlot(timeSlots, i)) {
				resultItems.add(currentItem);
			} else {
				resultItems.add(currentItem.join(nextTimeSlot(timeSlots, i)));
			}
		}
		return resultItems;
	}

	private TimeSlot nextTimeSlot(List<TimeSlot> timeSlots, int i) {
		return timeSlots.get(i + 1);
	}

	private boolean shouldMerge(int maxAttendees, TimeSlot currentItem) {
		return currentItem.attendeesCount() < maxAttendees - 1;
	}

	private boolean isLastTimeSlot(List<TimeSlot> timeSlots, int i) {
		return i + 1 == timeSlots.size();
	}

	private int maxAttendeesCountOf(List<TimeSlot> timeSlots) {
		return timeSlots.stream()
				.mapToInt(timeSlot -> timeSlot.attendeesCount())
				.max()
				.orElse(0);
	}

	private List<TimeSlot> findTimeSlotsOfDuration(
			List<TimeSlot> items, Duration duration) {
		return items.stream()
				.filter(ts -> ts.lastsAtLeast(duration))
				.collect(Collectors.toList());
	}

	private SchedulerResultStatus calculateStatus(
			List<TimeSlot> possibleTimeSlots) {
		SchedulerResultStatus status = SchedulerResultStatus.OK;
		if (allAttendeePresent(possibleTimeSlots)) {
			status = SchedulerResultStatus.OK;
		} else {
			status = SchedulerResultStatus.NOT_OK;
		}
		return status;
	}

	private boolean allAttendeePresent(List<TimeSlot> possibleTimeSlots) {
		return !possibleTimeSlots.isEmpty() &&
				possibleTimeSlots.get(0).attendeesCount() == attendees.size();
	}

}

class Event {
	LocalDateTime time;

	Attendee attendee;

	EventType type;

	public Event(LocalDateTime time, Attendee attendee, EventType type) {
		super();
		this.time = time;
		this.attendee = attendee;
		this.type = type;
	}

}

enum EventType {
	PERIOD_BEGIN,

	PERIOD_END;

}
