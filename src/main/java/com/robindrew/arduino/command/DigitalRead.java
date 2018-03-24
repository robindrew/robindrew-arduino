package com.robindrew.arduino.command;

import com.robindrew.arduino.connection.InputBuffer;
import com.robindrew.arduino.connection.OutputBuffer;
import com.robindrew.arduino.pin.Pin;

public class DigitalRead extends PinCommand<Integer> {

	public DigitalRead(int pin) {
		super(pin);
	}

	public DigitalRead(Pin pin) {
		super(pin);
	}

	@Override
	public void sendRequest(OutputBuffer output) {
		output.writeByte(COMMAND_DIGITAL_READ);
		output.writeByte(getPin().get());
	}

	@Override
	public void receiveResponse(InputBuffer input) {
		input.readByte(COMMAND_DIGITAL_READ);
		int number = input.readNumber();
		setData(number);
	}

	@Override
	public String toString() {
		return "digitalRead(" + getPin() + ")";
	}
}
