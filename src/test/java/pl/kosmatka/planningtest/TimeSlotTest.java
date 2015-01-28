package pl.kosmatka.planningtest;

import java.time.LocalDateTime;
import java.time.Month;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TimeSlotTest {
	
	@Test
	public void intersectWith_givenEarlierIntersectingTimeSlot_returnsValidIntersection(){
		
		TimeSlot timeSlot = new TimeSlot(
				LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), 
				LocalDateTime.of(2000, Month.JANUARY, 1, 12, 0));
		
		TimeSlot other = new TimeSlot(
				LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), 
				LocalDateTime.of(2000, Month.JANUARY, 1, 6, 0));	
		
		TimeSlot intersetion = timeSlot.intersectionWith(other).get();
		
		Assertions.assertThat(intersetion).isEqualTo(other);
	}

	
	@Test
	public void intersectWith_givenLaterIntersectingTimeSlot_returnsValidIntersection(){
		
		TimeSlot timeSlot = new TimeSlot(
				LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), 
				LocalDateTime.of(2000, Month.JANUARY, 1, 6, 0));
		
		TimeSlot other = new TimeSlot(
				LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), 
				LocalDateTime.of(2000, Month.JANUARY, 1, 12, 0));	
		
		TimeSlot intersetion = timeSlot.intersectionWith(other).get();
		
		Assertions.assertThat(intersetion).isEqualTo(timeSlot);
	}
}
