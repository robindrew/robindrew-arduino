package com.robindrew.arduino.pin;

public class Pin implements Comparable<Pin> {

	public static final Pin valueOf(int pin) {
		return new Pin(pin);
	}

	private final int pin;

	private Pin(int pin) {
		if (pin < 0) {
			throw new IllegalArgumentException("pin=" + pin);
		}
		this.pin = pin;
	}

	public int get() {
		return pin;
	}

	@Override
	public int hashCode() {
		return pin;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof Pin) {
			Pin that = (Pin) object;
			return this.get() == that.get();
		}
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(pin);
	}

	@Override
	public int compareTo(Pin that) {
		return Integer.compare(this.get(), that.get());
	}
}
