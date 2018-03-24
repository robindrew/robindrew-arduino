package com.robindrew.arduino.command;

import com.robindrew.arduino.connection.InputBuffer;
import com.robindrew.arduino.connection.OutputBuffer;
import com.robindrew.arduino.pin.Pin;

public class AnalogRead extends PinCommand<Integer> {

	public AnalogRead(int pin) {
		super(pin);
	}

	public AnalogRead(Pin pin) {
		super(pin);
	}

	@Override
	public void sendRequest(OutputBuffer output) {
		output.writeByte(COMMAND_ANALOG_READ);
		output.writeByte(getPin().get());
	}

	@Override
	public void receiveResponse(InputBuffer input) {
		input.readByte(COMMAND_ANALOG_READ);
		int number = input.readNumber();
		setData(number);
	}

	@Override
	public String toString() {
		return "analogRead(" + getPin() + ")";
	}
}
