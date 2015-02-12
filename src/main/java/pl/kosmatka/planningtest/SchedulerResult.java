package pl.kosmatka.planningtest;

import java.util.List;

public class SchedulerResult {

	private SchedulerResultStatus status;
	private List<TimeSlot> resultTimeSlots;

	public SchedulerResult(List<TimeSlot> resultTimeSlots,
			SchedulerResultStatus status) {
		this.resultTimeSlots = resultTimeSlots;
		this.status = status;
	}

	public SchedulerResultStatus getStatus() {
		return status;
	}

	public List<TimeSlot> getResultTimeSlots() {
		return resultTimeSlots;
	}

}
