package com.robindrew.arduino.command;

import com.robindrew.arduino.pin.Pin;

public abstract class PinCommand<D> extends Command<D> {

	private final Pin pin;

	protected PinCommand(int pin) {
		this(Pin.valueOf(pin));
	}

	protected PinCommand(Pin pin) {
		if (pin == null) {
			throw new NullPointerException("pin");
		}
		this.pin = pin;
	}

	public Pin getPin() {
		return pin;
	}

}
