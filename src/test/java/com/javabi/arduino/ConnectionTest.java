package com.javabi.arduino;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.javabi.arduino.connection.Connection;
import com.javabi.arduino.connection.InputBuffer;
import com.javabi.arduino.connection.OutputBuffer;

public class ConnectionTest {

	private Connection connection = null;
	private OutputBuffer output;
	private InputBuffer input;

	@Before
	public void before() {
		connection = Connection.detectAndConnect();
		output = connection.getOutput();
		input = connection.getInput();
	}

	@After
	public void after() {
		connection.close();
	}

	@Test
	public void testConnection() {

		// Positive numbers

		testWriteNumber(0);
		testWriteNumber(1);

		testWriteNumber(Byte.MAX_VALUE - 1);
		testWriteNumber(Byte.MAX_VALUE + 0);
		testWriteNumber(Byte.MAX_VALUE + 1);

		testWriteNumber(Short.MAX_VALUE - 1);
		testWriteNumber(Short.MAX_VALUE + 0);
		testWriteNumber(Short.MAX_VALUE + 1);

		testWriteNumber(Integer.MAX_VALUE - 1);
		testWriteNumber(Integer.MAX_VALUE + 0);

		// Negative numbers

		testWriteNumber(-1);
		testWriteNumber(-2);

		testWriteNumber(Byte.MIN_VALUE - 1);
		testWriteNumber(Byte.MIN_VALUE + 0);
		testWriteNumber(Byte.MIN_VALUE + 1);

		testWriteNumber(Short.MIN_VALUE - 1);
		testWriteNumber(Short.MIN_VALUE + 0);
		testWriteNumber(Short.MIN_VALUE + 1);

		testWriteNumber(Integer.MIN_VALUE + 1);

		// Byte arrays
		testWriteArray(60);
		testWriteArray(120);
		testWriteArray(240);
	}

	private void testWriteArray(int length) {
		byte[] array = new byte[length];
		for (int i = 0; i < array.length; i++) {
			array[i] = (byte) i;
		}
		output.writeBytes(array);
		output.flush(false);
		Assert.assertArrayEquals(array, input.readBytes());
	}

	private void testWriteNumber(int number) {
		output.writeNumber(number);
		output.flush(false);
		Assert.assertEquals(number, input.readNumber());
	}

}
