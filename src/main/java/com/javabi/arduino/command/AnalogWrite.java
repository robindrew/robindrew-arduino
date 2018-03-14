package com.javabi.arduino.command;

import com.javabi.arduino.connection.InputBuffer;
import com.javabi.arduino.connection.OutputBuffer;
import com.javabi.arduino.pin.Pin;

public class AnalogWrite extends PinCommand<Void> {

	private final int value;

	public AnalogWrite(int pin, int value) {
		super(pin);
		this.value = value;
	}

	public AnalogWrite(Pin pin, int value) {
		super(pin);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void sendRequest(OutputBuffer output) {
		output.writeByte(COMMAND_ANALOG_WRITE);
		output.writeByte(getPin().get());
		output.writeNumber(getValue());
	}

	@Override
	public void receiveResponse(InputBuffer input) {
		input.readByte(COMMAND_ANALOG_WRITE);
	}

	@Override
	public String toString() {
		return "analogWrite(" + getPin() + ", " + getValue() + ")";
	}

}
