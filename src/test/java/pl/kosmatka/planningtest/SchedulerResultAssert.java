package pl.kosmatka.planningtest;

import java.util.List;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class SchedulerResultAssert extends
		AbstractAssert<SchedulerResultAssert, SchedulerResult> {

	protected SchedulerResultAssert(SchedulerResult actual) {
		super(actual, SchedulerResultAssert.class);
	}
	
	public static SchedulerResultAssert assertThat(SchedulerResult actual){
		return new SchedulerResultAssert(actual);
	}
	
	public SchedulerResultAssert hasTimeSlots(TimeSlot... timeSlots){
		isNotNull();
		Assertions.assertThat(actual.getTimeSlots()).containsOnly(timeSlots);
		return this;
	}
	
	public SchedulerResultAssert hasTimeSlots(ResultTimeSlot... timeSlots){
		isNotNull();
		Assertions.assertThat(actual.getResultTimeSlots()).containsOnly(timeSlots);
		return this;
	}
	
	public SchedulerResultAssert hasStatus(SchedulerResultStatus status){
		isNotNull();
		Assertions.assertThat(actual.getStatus()).isEqualTo(status);
		return this;
	}
	
	

}
 