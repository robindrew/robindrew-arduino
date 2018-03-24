package com.robindrew.arduino.pin.value;

import com.robindrew.arduino.pin.Pin;

public class PinValue {

	private final Pin pin;
	private int previous;
	private int current;

	public PinValue(Pin pin, int value) {
		this.pin = pin;
		this.previous = value;
		this.current = value;
	}

	public Pin getPin() {
		return pin;
	}

	public void update(int value) {
		this.previous = this.current;
		this.current = value;
	}

	public int getPrevious() {
		return previous;
	}

	public int getCurrent() {
		return current;
	}

	public boolean hasChanged() {
		return getDelta() != 0;
	}

	public int getDelta() {
		return current - previous;
	}

	@Override
	public String toString() {
		if (!hasChanged()) {
			return "PinValue[pin=" + pin + ", value=" + current + "]";
		}
		return "PinValue[pin=" + pin + ", value=" + current + ", delta=" + getDelta() + "]";
	}
}
