package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class Scheduler {

	private Set<Attendee> attendees = new HashSet<>();

	public void add(Attendee attendee) {
		attendees.add(attendee);
	}

	public SchedulerResult findPossibleTimeSlots(Duration duration,
			LocalDateTime begin, LocalDateTime end) {
		
		List<ResultTimeSlot> possibleTimeSlots = findBestTimeSlots(duration, begin, end);
		
		SchedulerResultStatus status = SchedulerResultStatus.OK;
		if (!possibleTimeSlots.isEmpty()){
			if (possibleTimeSlots.get(0).attendeesCount() < attendees.size()){
				status = SchedulerResultStatus.NOT_OK;
			}
		}else {
			status = SchedulerResultStatus.NOT_OK;
		}
		return new SchedulerResult(possibleTimeSlots,
				status);
	}

	private List<ResultTimeSlot> findBestTimeSlots(Duration duration,
			LocalDateTime begin, LocalDateTime end) {

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

		List<ResultTimeSlot> resultTimeSlots = new ArrayList<ResultTimeSlot>();

		while (resultTimeSlots.isEmpty() && !items.isEmpty()) {
			resultTimeSlots = findTimeSlotsOfDuration(items, duration);
			int maxAttendees = resultTimeSlots.stream()
					.mapToInt(x -> x.attendeesCount()).max().orElse(0);
			resultTimeSlots = resultTimeSlots.stream()
					.filter(x -> x.attendeesCount() == maxAttendees)
					.collect(Collectors.toList());

			if (resultTimeSlots.isEmpty()) {
				items = mergeTimeSlotsWithMostAttendees(items);
			}
		}


		return resultTimeSlots;
	}

	private List<ResultTimeSlot> mergeTimeSlotsWithMostAttendees(
			List<ResultTimeSlot> items) {
		int maxAttendee = items.stream().mapToInt(x -> x.attendeesCount())
				.max().orElse(0);
		if (maxAttendee == 0) {
			return Collections.emptyList();
		}

		List<ResultTimeSlot> resultItems = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			ResultTimeSlot currentItem = items.get(i);
			if (currentItem.attendeesCount() < maxAttendee - 1
					|| i + 1 == items.size()) {
				resultItems.add(currentItem);
			} else {
				resultItems.add(mergeItems(currentItem, items.get(i + 1)));
			}
		}
		return resultItems;
	}

	private ResultTimeSlot mergeItems(ResultTimeSlot currentItem,
			ResultTimeSlot nextItem) {
		if (!currentItem.getTimeSlot().getEnd()
				.isEqual(nextItem.getTimeSlot().getBegin())) {
			return currentItem;
		}

		Set<Attendee> intersection = new HashSet<>(currentItem.getAttendees());
		intersection.retainAll(nextItem.getAttendees());
		if (intersection.size() != Math.min(currentItem.attendeesCount(),
				nextItem.attendeesCount())) {
			return currentItem;
		}

		return new ResultTimeSlot(currentItem.getTimeSlot().getBegin(),
				nextItem.getTimeSlot().getEnd(), intersection);
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

class Item {
	LocalDateTime start;

	LocalDateTime end;
	Set<Attendee> attendees;

	public Item(LocalDateTime start, LocalDateTime end, Set<Attendee> attendees) {
		super();
		this.start = start;
		this.end = end;
		this.attendees = attendees;
	}

}