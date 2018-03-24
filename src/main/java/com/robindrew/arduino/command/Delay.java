package com.robindrew.arduino.command;

import com.robindrew.arduino.connection.InputBuffer;
import com.robindrew.arduino.connection.OutputBuffer;

public class Delay extends Command<Void> {

	private final int delay;

	public Delay(int delay) {
		if (delay < 1) {
			throw new IllegalArgumentException("delay=" + delay);
		}
		this.delay = delay;
	}

	@Override
	public void sendRequest(OutputBuffer output) {
		output.writeByte(COMMAND_DELAY);
		output.writeNumber(delay);
	}

	@Override
	public void receiveResponse(InputBuffer input) {
		input.readByte(COMMAND_DELAY);
	}

	@Override
	public String toString() {
		return "delay(" + delay + ")";
	}
}
