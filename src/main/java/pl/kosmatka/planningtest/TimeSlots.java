package pl.kosmatka.planningtest;

import java.math.BigDecimal;

public class TimeSlots {

	private OperationStatus status;

	public TimeSlots(OperationStatus status) {
		this.status = status;
	}

	public OperationStatus getStatus() {
		return status;
	}

}
