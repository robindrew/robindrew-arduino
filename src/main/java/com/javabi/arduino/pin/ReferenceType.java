package com.javabi.arduino.pin;

import com.javabi.arduino.Arduino;

public enum ReferenceType {

	/** DEFAULT. */
	DEFAULT(Arduino.DEFAULT),
	/** INTERNAL. */
	INTERNAL(Arduino.INTERNAL),
	/** EXTERNAL. */
	EXTERNAL(Arduino.EXTERNAL);

	private final int type;

	private ReferenceType(int type) {
		this.type = type;
	}

	public int get() {
		return type;
	}

	public static ReferenceType valueOf(int type) {
		for (ReferenceType element : values()) {
			if (element.get() == type) {
				return element;
			}
		}
		throw new IllegalArgumentException("type=" + type);
	}

}
