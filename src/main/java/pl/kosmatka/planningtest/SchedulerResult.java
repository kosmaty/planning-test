package pl.kosmatka.planningtest;

import java.util.List;

public class SchedulerResult {

	private SchedulerResultStatus status;
	private List<ResultTimeSlot> resultTimeSlots;

	public SchedulerResult(List<ResultTimeSlot> resultTimeSlots,
			SchedulerResultStatus status) {
		this.resultTimeSlots = resultTimeSlots;
		this.status = status;
	}

	public SchedulerResultStatus getStatus() {
		return status;
	}

	public List<ResultTimeSlot> getResultTimeSlots() {
		return resultTimeSlots;
	}

}
