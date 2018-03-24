package com.robindrew.arduino.command;

import com.robindrew.arduino.connection.InputBuffer;
import com.robindrew.arduino.connection.OutputBuffer;

public class Echo extends Command<String> {

	private final String text;

	public Echo(String text) {
		if (text == null) {
			throw new NullPointerException("text");
		}
		this.text = text;
	}

	@Override
	public void sendRequest(OutputBuffer output) {
		output.writeByte(COMMAND_ECHO);
		output.writeString(text);
	}

	@Override
	public void receiveResponse(InputBuffer input) {
		input.readByte(COMMAND_ECHO);
		String response = input.readString();
		setData(response);
	}

	@Override
	public String toString() {
		return "echo('" + text + "')";
	}
}
