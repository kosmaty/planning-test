package pl.kosmatka.planningtest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SchedulerResult {

	private List<TimeSlot> timeSlots;
	private SchedulerResultStatus status;
	private List<ResultTimeSlot> resultTimeSlots;

	public SchedulerResult(List<ResultTimeSlot> resultTimeSlots,
			SchedulerResultStatus status) {
		this.resultTimeSlots = resultTimeSlots;
		this.status = status;
		timeSlots = new ArrayList<>();
		for (ResultTimeSlot resultTimeSlot : resultTimeSlots) {
			timeSlots.add(resultTimeSlot.getTimeSlot());
		}
	}

	public List<TimeSlot> getTimeSlots() {
		return timeSlots;
	}

	public SchedulerResultStatus getStatus() {
		return status;
	}

	public List<ResultTimeSlot> getResultTimeSlots() {
		return resultTimeSlots;
	}

}
