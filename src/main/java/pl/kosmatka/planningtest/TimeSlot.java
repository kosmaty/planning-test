package pl.kosmatka.planningtest;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TimeSlot implements Comparable<TimeSlot> {

	private LocalDateTime begin;

	private LocalDateTime end;

	public TimeSlot(LocalDateTime begin, LocalDateTime end) {
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
	public int compareTo(TimeSlot o) {
		return begin.compareTo(o.begin);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(begin)
				.append(end)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TimeSlot)) {
			return false;
		}
		TimeSlot other = (TimeSlot) obj;
		return new EqualsBuilder()
				.append(this.begin, other.begin)
				.append(this.end, other.end)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append(begin).append(end).toString();
	}

}
