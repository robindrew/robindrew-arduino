package com.javabi.arduino.command;

import com.javabi.arduino.connection.InputBuffer;
import com.javabi.arduino.connection.OutputBuffer;
import com.javabi.arduino.pin.Mode;
import com.javabi.arduino.pin.Pin;

public class PinMode extends PinCommand<Void> {

	private final Mode mode;

	public PinMode(int pin, int mode) {
		super(pin);
		this.mode = Mode.valueOf(mode);
	}

	public PinMode(Pin pin, Mode mode) {
		super(pin);
		if (mode == null) {
			throw new NullPointerException("mode");
		}
		this.mode = mode;
	}

	@Override
	public void sendRequest(OutputBuffer output) {
		output.writeByte(COMMAND_PIN_MODE);
		output.writeByte(getPin().get());
		output.writeByte(mode.get());
	}

	@Override
	public void receiveResponse(InputBuffer input) {
		input.readByte(COMMAND_PIN_MODE);
	}

	@Override
	public String toString() {
		return "pinMode(" + getPin() + ", " + mode + ")";
	}
}
