package com.javabi.arduino.pin;

import com.javabi.arduino.Arduino;

public enum Level {

	/** LOW. */
	LOW(Arduino.LOW),
	/** HIGH. */
	HIGH(Arduino.HIGH);

	public static Level valueOf(int level) {
		for (Level element : values()) {
			if (element.get() == level) {
				return element;
			}
		}
		throw new IllegalArgumentException("level=" + level);
	}

	private final int level;

	private Level(int level) {
		this.level = level;
	}

	public int get() {
		return level;
	}

}
