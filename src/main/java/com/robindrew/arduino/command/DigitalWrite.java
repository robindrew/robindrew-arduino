package com.robindrew.arduino.command;

import com.robindrew.arduino.connection.InputBuffer;
import com.robindrew.arduino.connection.OutputBuffer;
import com.robindrew.arduino.pin.Level;
import com.robindrew.arduino.pin.Pin;

public class DigitalWrite extends PinCommand<Void> {

	private final int value;

	public DigitalWrite(int pin, int value) {
		super(pin);
		this.value = value;
	}

	public DigitalWrite(Pin pin, int value) {
		super(pin);
		this.value = value;
	}

	public DigitalWrite(Pin pin, Level level) {
		super(pin);
		this.value = level.get();
	}

	public DigitalWrite(int pin, Level level) {
		super(pin);
		this.value = level.get();
	}

	public int getValue() {
		return value;
	}

	@Override
	public void sendRequest(OutputBuffer output) {
		output.writeByte(COMMAND_DIGITAL_WRITE);
		output.writeByte(getPin().get());
		output.writeNumber(getValue());
	}

	@Override
	public void receiveResponse(InputBuffer input) {
		input.readByte(COMMAND_DIGITAL_WRITE);
	}

	@Override
	public String toString() {
		return "digitalWrite(" + getPin() + ", " + getValue() + ")";
	}
}
