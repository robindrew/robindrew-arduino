package com.javabi.arduino.command;

import com.javabi.arduino.connection.InputBuffer;
import com.javabi.arduino.connection.OutputBuffer;
import com.javabi.arduino.pin.Level;
import com.javabi.arduino.pin.Pin;

public class DigitalReadWait extends PinCommand<Integer> {

	public static final boolean GREATER_THAN = true;
	public static final boolean LESS_THAN = false;
	
	private final int value;
	private final boolean greaterThan;

	public DigitalReadWait(int pin, boolean greaterThan, int value) {
		super(pin);
		this.value = value;
		this.greaterThan = greaterThan;
	}

	public DigitalReadWait(Pin pin, boolean greaterThan, int value) {
		super(pin);
		this.value = value;
		this.greaterThan = greaterThan;
	}

	public DigitalReadWait(Pin pin, boolean greaterThan, Level level) {
		super(pin);
		this.value = level.get();
		this.greaterThan = greaterThan;
	}

	public DigitalReadWait(int pin, boolean greaterThan, Level level) {
		super(pin);
		this.value = level.get();
		this.greaterThan = greaterThan;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void sendRequest(OutputBuffer output) {
		output.writeByte(COMMAND_DIGITAL_READ_WAIT);
		output.writeByte(getPin().get());
		output.writeBoolean(greaterThan);
		output.writeNumber(getValue());
	}

	@Override
	public void receiveResponse(InputBuffer input) {
		input.readByte(COMMAND_DIGITAL_READ_WAIT);
		int number = input.readNumber();
		setData(number);
	}

	@Override
	public String toString() {
		return "digitalReadWait(" + getPin() + ", " + getValue() + ")";
	}
}
