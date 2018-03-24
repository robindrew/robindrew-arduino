package com.robindrew.arduino.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.robindrew.arduino.Arduino;
import com.robindrew.arduino.ArduinoException;
import com.robindrew.arduino.util.Log;

public class OutputBuffer {

	public static final Log log = new Log(OutputBuffer.class).enable(Arduino.INPUT_LOGGING);

	private final OutputStream output;
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	public OutputBuffer(OutputStream output) {
		this.output = output;
	}

	public synchronized void flush(boolean includeLength) {
		try {
			byte[] bytes = buffer.toByteArray();
			log.info("Flushing Output Buffer ({} bytes): {}", bytes.length, Arrays.toString(bytes));

			// Write the length of the array?
			if (includeLength) {
				if (bytes.length > 512) {
					throw new IllegalStateException("Too much data to send: " + bytes.length + " bytes");
				}

				int length = bytes.length;
				if (length <= 255) {
					output.write(length);
					output.write(0);
				} else {
					output.write(255);
					output.write(length - 255);
				}
			}

			// Write the array next ...
			output.write(bytes, 0, bytes.length);

			// Ensure the data has been sent
			output.flush();

			// Reset the buffer for the next send
			buffer.reset();

		} catch (IOException e) {
			throw new ArduinoException(e);
		}
	}

	public synchronized void write(int value) {
		try {
			log.info("Write Byte: " + value);
			output.write(value);
			output.flush();
		} catch (IOException e) {
			throw new ArduinoException(e);
		}
	}

	public synchronized void writeByte(int value) {
		log.info("writeByte({})", value);
		buffer.write(value);
	}

	public synchronized void writeBoolean(boolean value) {
		log.info("writeBoolean({})", value);
		writeByte(value ? Connection.TRUE : Connection.FALSE);
	}

	public synchronized void writeBytes(byte[] bytes) {
		writeNumber(bytes.length);
		buffer.write(bytes, 0, bytes.length);
	}

	public synchronized void writeString(String text) {
		writeBytes(text.getBytes());
	}

	public synchronized void writeNumber(int value) {
		if (value <= Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("value=" + value);
		}
		log.info("writeNumber({})", value);
		int length = 0;

		// Zero is a special case
		if (value == 0) {
			writeByte(0);
			return;
		}

		// Is the number negative?
		boolean negative = false;
		if (value < 0) {
			negative = true;
			value = -value;
		}

		// Calculate the number length
		int counter = value;
		while (counter > 0) {
			counter /= Connection.POWER;
			length++;
		}

		// Write the number
		if (negative) {
			writeByte(Connection.NEGATIVE);
		} else {
			writeByte(Connection.POSITIVE);
		}
		writeByte(length);
		while (value > 0) {
			byte digit = (byte) (value % Connection.POWER);
			writeByte(digit);
			value /= Connection.POWER;
		}
	}

}
