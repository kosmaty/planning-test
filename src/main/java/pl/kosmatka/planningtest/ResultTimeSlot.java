package pl.kosmatka.planningtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResultTimeSlot {

	private Set<Attendee> attendees;
	private LocalDateTime begin;
	private LocalDateTime end;

	public ResultTimeSlot(LocalDateTime begin, LocalDateTime end,
			Set<Attendee> attendees) {
		this.attendees = attendees;
		this.begin = begin;
		this.end = end;
	}

	public ResultTimeSlot(LocalDateTime begin, LocalDateTime end,
			Attendee attendee) {
		this(begin, end, Collections.singleton(attendee));
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(attendees)
				.append(begin)
				.append(end)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ResultTimeSlot)) {
			return false;
		}
		ResultTimeSlot other = (ResultTimeSlot) obj;
		return new EqualsBuilder()
				.append(attendees, other.attendees)
				.append(begin, other.begin)
				.append(end, other.end)
				.isEquals();
	}

	public Set<Attendee> getAttendees() {
		return attendees;
	}

	public int attendeesCount() {
		return attendees.size();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append(begin)
				.append(end)
				.append(attendees)
				.toString();
	}

	public boolean lastsAtLeast(Duration duration) {
		return duration.compareTo(Duration.between(begin, end)) <= 0;
	}
	
	LocalDateTime getBegin(){
		return begin;
	}
	
	LocalDateTime getEnd(){
		return end;
	}
	
	
	ResultTimeSlot join(ResultTimeSlot other){
		if (!end.isEqual(other.begin)) {
			return this;
		}

		Set<Attendee> commonAttendees = new HashSet<>(attendees);
		commonAttendees.retainAll(other.attendees);
		if (commonAttendees.size() != Math.min(this.attendeesCount(),
				other.attendeesCount())) {
			return this;
		}

		return new ResultTimeSlot(this.begin,
				other.end, commonAttendees);
	}
	
}
