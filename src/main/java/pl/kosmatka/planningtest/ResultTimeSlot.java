package pl.kosmatka.planningtest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResultTimeSlot {

	private TimeSlot internalTimeSlot;
	private Set<Attendee> attendees;

	public ResultTimeSlot(LocalDateTime begin, LocalDateTime end,
			Set<Attendee> attendees) {
		this.attendees = attendees;
		this.internalTimeSlot = new TimeSlot(begin, end);
	}

	public ResultTimeSlot(LocalDateTime begin, LocalDateTime end,
			Attendee attendee) {
		this.attendees = Collections.singleton(attendee);
		this.internalTimeSlot = new TimeSlot(begin, end);
	}

	public ResultTimeSlot(TimeSlot timeSlot, Attendee attendee) {
		this.attendees = Collections.singleton(attendee);
		this.internalTimeSlot = timeSlot;
	}

	public ResultTimeSlot(TimeSlot timeSlot,
			Set<Attendee> attendees) {
		this.attendees = attendees;
		this.internalTimeSlot = timeSlot;
	}

	TimeSlot getTimeSlot() {
		return internalTimeSlot;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(attendees)
				.append(internalTimeSlot)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ResultTimeSlot)) {
			return false;
		}
		ResultTimeSlot other = (ResultTimeSlot) obj;
		return new EqualsBuilder()
				.append(attendees, other.attendees)
				.append(internalTimeSlot, other.internalTimeSlot)
				.isEquals();
	}

	public Set<Attendee> getAttendees() {
		return attendees;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append(internalTimeSlot)
				.append(attendees)
				.toString();
	}
	
}
