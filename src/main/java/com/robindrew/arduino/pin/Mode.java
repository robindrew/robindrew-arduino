package com.robindrew.arduino.pin;

import com.robindrew.arduino.Arduino;

public enum Mode {

	/** INPUT. */
	INPUT(Arduino.INPUT),
	/** OUTPUT. */
	OUTPUT(Arduino.OUTPUT),
	/** INPUT_PULLUP. */
	INPUT_PULLUP(Arduino.INPUT_PULLUP);

	public static Mode valueOf(int mode) {
		for (Mode element : values()) {
			if (element.get() == mode) {
				return element;
			}
		}
		throw new IllegalArgumentException("mode=" + mode);
	}

	private final int level;

	private Mode(int level) {
		this.level = level;
	}

	public int get() {
		return level;
	}

}
