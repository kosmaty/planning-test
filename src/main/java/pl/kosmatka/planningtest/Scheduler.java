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

		List<ResultTimeSlot> possibleTimeSlots = findBestTimeSlots(
				duration, begin, end);

		SchedulerResultStatus status = calculateStatus(possibleTimeSlots);
		return new SchedulerResult(possibleTimeSlots, status);
	}

	private SchedulerResultStatus calculateStatus(
			List<ResultTimeSlot> possibleTimeSlots) {
		SchedulerResultStatus status = SchedulerResultStatus.OK;
		if (!possibleTimeSlots.isEmpty() && 
				possibleTimeSlots.get(0).attendeesCount() == attendees.size()) {
			status = SchedulerResultStatus.OK;
		} else {
			status = SchedulerResultStatus.NOT_OK;
		}
		return status;
	}

	private List<ResultTimeSlot> findBestTimeSlots(Duration duration,
			LocalDateTime begin, LocalDateTime end) {

		List<Event> events = prepareEvents(duration, begin, end);

		List<ResultTimeSlot> baseTimeSlots = createTimeSlotsFrom(events);

		List<ResultTimeSlot> resultTimeSlots = findResultTimeSlots(duration,
				baseTimeSlots);

		return resultTimeSlots;
	}

	private List<ResultTimeSlot> findResultTimeSlots(Duration duration,
			List<ResultTimeSlot> baseTimeSlots) {
		List<ResultTimeSlot> resultTimeSlots = new ArrayList<ResultTimeSlot>();

		
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

	private List<ResultTimeSlot> createTimeSlotsFrom(List<Event> events) {
		Set<Attendee> previousAttendees = new HashSet<Attendee>();
		Event previousEvent = null;
		List<ResultTimeSlot> items = new ArrayList<ResultTimeSlot>();
		for (Event event : events) {
			Set<Attendee> attendees = new HashSet<Attendee>(previousAttendees);
			if (event.type == EventType.PERIOD_BEGIN) {
				attendees.add(event.attendee);
			} else {
				attendees.remove(event.attendee);
			}

			if (!previousAttendees.isEmpty() && previousEvent != null) {
				items.add(new ResultTimeSlot(previousEvent.time, event.time,
						previousAttendees));
			}
			previousEvent = event;
			previousAttendees = attendees;
		}
		return items;
	}

	private List<Event> prepareEvents(Duration duration, LocalDateTime begin,
			LocalDateTime end) {
		List<Event> events = new ArrayList<Event>();
		for (Attendee attendee : attendees) {
			for (ResultTimeSlot timeSlot : attendee.findFreeTimeSlots(duration,
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

	private List<ResultTimeSlot> mergeTimeSlotsWithMostAttendees(
			List<ResultTimeSlot> timeSlots) {
		int maxAttendees = maxAttendeesCountOf(timeSlots);

		List<ResultTimeSlot> resultItems = new ArrayList<>();
		for (int i = 0; i < timeSlots.size(); i++) {
			ResultTimeSlot currentItem = timeSlots.get(i);
			if (shouldMerge(maxAttendees, currentItem)
					|| isLastTimeSlot(timeSlots, i)) {
				resultItems.add(currentItem);
			} else {
				resultItems.add(mergeItems(currentItem, nextTimeSlot(timeSlots, i)));
			}
		}
		return resultItems;
	}

	private ResultTimeSlot nextTimeSlot(List<ResultTimeSlot> timeSlots, int i) {
		return timeSlots.get(i + 1);
	}

	private boolean shouldMerge(int maxAttendees, ResultTimeSlot currentItem) {
		return currentItem.attendeesCount() < maxAttendees - 1;
	}

	private boolean isLastTimeSlot(List<ResultTimeSlot> timeSlots, int i) {
		return i + 1 == timeSlots.size();
	}

	private int maxAttendeesCountOf(List<ResultTimeSlot> timeSlots) {
		return timeSlots.stream().mapToInt(x -> x.attendeesCount())
				.max().orElse(0);
	}

	private ResultTimeSlot mergeItems(ResultTimeSlot currentItem,
			ResultTimeSlot nextItem) {
		if (!currentItem.getEnd()
				.isEqual(nextItem.getBegin())) {
			return currentItem;
		}

		Set<Attendee> intersection = new HashSet<>(currentItem.getAttendees());
		intersection.retainAll(nextItem.getAttendees());
		if (intersection.size() != Math.min(currentItem.attendeesCount(),
				nextItem.attendeesCount())) {
			return currentItem;
		}

		return new ResultTimeSlot(currentItem.getBegin(),
				nextItem.getEnd(), intersection);
	}

	private List<ResultTimeSlot> findTimeSlotsOfDuration(
			List<ResultTimeSlot> items, Duration duration) {
		return items.stream()
				.filter(ts -> ts.lastsAtLeast(duration))
				.collect(Collectors.toList());
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
