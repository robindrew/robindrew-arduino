package com.javabi.arduino.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import com.javabi.arduino.Arduino;
import com.javabi.arduino.ArduinoException;
import com.javabi.arduino.util.Log;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class InputBuffer implements SerialPortEventListener {

	public static final Log log = new Log(InputBuffer.class).enable(Arduino.INPUT_LOGGING);

	private static final int CAPACITY = 10000000;

	private final InputStream input;
	private final byte[] buffer;
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private int readOffset = 0;
	private int readLength = 0;

	public InputBuffer(InputStream input, int capacity) {
		this.buffer = new byte[capacity];
		this.input = input;
	}

	public InputBuffer(InputStream input) {
		this(input, CAPACITY);
	}

	public synchronized boolean isEmpty() {
		return readLength == 0;
	}

	public synchronized int available() {
		return readLength;
	}

	@Override
	public String toString() {
		return Arrays.toString(toByteArray());
	}

	private synchronized byte[] toByteArray() {
		byte[] array = new byte[readLength];
		System.arraycopy(buffer, readOffset, array, 0, readLength);
		return array;
	}

	public synchronized void clear() {
		readOffset = 0;
		readLength = 0;
	}

	private int blockingRead() {
		while (true) {
			if (isClosed()) {
				throw new ArduinoException("Connection closed");
			}
			synchronized (this) {
				if (readLength > 0) {
					int byteRead = buffer[readOffset];
					readOffset++;
					readLength--;
					if (readLength == 0) {
						readOffset = 0;
					}
					return byteRead;
				}
			}
			Arduino.sleep(10);
		}
	}

	public boolean isClosed() {
		return closed.get();
	}

	public synchronized int populate2() {
		log.info("Populating Input Buffer");
		try {
			int totalRead = 0;
			while (input.available() > 0) {
				int offset = readOffset + readLength;

				int bytesRead = input.read(buffer, offset, buffer.length - offset);
				log.info("Bytes Read: " + bytesRead);
				if (bytesRead > 1) {
					totalRead += bytesRead;
				}
				if (bytesRead == -1) {
					closed.set(true);
					throw new ArduinoException("Connection closed");
				}
				if (bytesRead == 0) {
					break;
				}

				readLength += bytesRead;
				compact();
			}

			return totalRead;
		} catch (IOException e) {
			throw new ArduinoException(e);
		}
	}

	public synchronized int populate() {
		log.info("Populating Input Buffer");
		try {
			int available = input.available();
			byte[] bytesRead = new byte[available];
			int bytesReadOffset = 0;
			log.info("Bytes Available: " + available);
			for (int i = 0; i < available; i++) {
				int byteRead = input.read();
				if (byteRead == -1) {
					log.info("Connection Closed");
					closed.set(true);
					throw new ArduinoException("Connection closed");
				}
				buffer[readOffset + readLength] = (byte) byteRead;
				bytesRead[bytesReadOffset++] = (byte) byteRead;
				readLength++;
				compact();
			}

			log.info("Populated Bytes: " + Arrays.toString(bytesRead));

			return available;
		} catch (IOException e) {
			throw new ArduinoException(e);
		}
	}

	private void compact() {

		// Buffer overflow?
		if (readOffset == 0 && readLength == buffer.length) {
			throw new IllegalStateException("buffer overflow, buffer is full!");
		}

		// Buffer not full?
		if (readOffset + readLength < buffer.length) {
			return;
		}

		// Compact the buffer
		System.arraycopy(buffer, readOffset, buffer, 0, readLength);
		readOffset = 0;
	}

	/**
	 * Read a single expected byte.
	 */
	public void readByte(int expectedByte) {
		int actualByte = blockingRead();
		log.info("readByte() -> {}", actualByte);
		if (actualByte != expectedByte) {
			throw new IllegalStateException("expected byte: " + expectedByte + ", actual byte: " + actualByte);
		}
	}

	/**
	 * Read a single byte.
	 */
	public byte readByte() {
		int b1 = blockingRead();
		log.info("readByte() -> {}", b1);
		return (byte) b1;
	}

	public boolean readBoolean() {
		boolean value = readByte() != Connection.FALSE;
		log.info("readBoolean() -> {}", value);
		return value;
	}

	public byte[] readBytes() {
		int length = readNumber();
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = readByte();
		}
		log.info("readBytes() -> {}", Arrays.toString(bytes));
		return bytes;
	}

	public String readString() {
		return new String(readBytes());
	}

	public String readLine() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		while (true) {
			byte next = readByte();
			if (next == 13) {
				continue;
			}
			if (next == 10) {
				break;
			}
			bytes.write(next);
		}
		String line = new String(bytes.toByteArray());
		log.info("readLine() -> '{}'", line);
		return line;
	}

	/**
	 * Skip all bytes that have the given value.
	 */
	public void skipBytes(int value) {
		while (readLength > 0) {
			int readValue = buffer[readOffset];
			if (readValue != value) {
				return;
			}
			log.info("Skipping byte: " + value);
			readOffset++;
			readLength--;
		}
	}

	public int readNumber() {
		int first = readByte();
		if (first == Connection.ZERO) {
			log.info("readNumber() -> {}", 0);
			return 0;
		}

		// Is the number negative
		boolean negative = (first == Connection.NEGATIVE);

		int length = readByte();
		int number = 0;
		int multiplier = 1;

		for (int i = 0; i < length; i++) {
			int decimal = readByte();
			number += (decimal * multiplier);
			multiplier *= Connection.POWER;
		}

		if (negative) {
			number = -number;
		}

		log.info("readNumber() -> {}", number);
		return number;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				dataAvailable(event);
				break;
			default:
				log.warn("Unhandled Serial Port Event: " + event.getEventType());
		}
	}

	private void dataAvailable(SerialPortEvent event) {

		// Read all available bytes
		int read = populate();
		if (read == -1) {
			throw new ArduinoException("Connection to port lost");
		}
	}

}
