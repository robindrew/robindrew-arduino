package com.javabi.arduino.command;

import com.javabi.arduino.connection.InputBuffer;
import com.javabi.arduino.connection.OutputBuffer;
import com.javabi.arduino.pin.ReferenceType;

/**
 * Configures the reference voltage used for analog input (i.e. the value used as the top of the input range).
 */
public class AnalogReference extends Command<Void> {

	private final ReferenceType type;

	public AnalogReference(int type) {
		this(ReferenceType.valueOf(type));
	}

	public AnalogReference(ReferenceType type) {
		if (type == null) {
			throw new NullPointerException("type");
		}
		this.type = type;
	}

	@Override
	public void sendRequest(OutputBuffer output) {
		output.writeByte(COMMAND_ANALOG_REFERENCE);
		output.writeByte(type.get());
	}

	@Override
	public void receiveResponse(InputBuffer input) {
		input.readByte(COMMAND_ANALOG_REFERENCE);
	}

	@Override
	public String toString() {
		return "analogReference(" + type + ")";
	}

}
