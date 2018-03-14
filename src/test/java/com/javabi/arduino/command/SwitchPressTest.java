package com.javabi.arduino.command;

import static com.javabi.arduino.board.ArduinoUno.A0;
import static com.javabi.arduino.command.DigitalReadWait.GREATER_THAN;
import static com.javabi.arduino.pin.Mode.INPUT;

import org.junit.Test;

import com.javabi.arduino.board.ArduinoBoard;

public class SwitchPressTest {

	@Test
	public void pressSwitchTwice() {
		try (ArduinoBoard board = ArduinoBoard.detectAndConnect()) {
			board.pinMode(A0, INPUT);
			board.analogReadWait(A0, GREATER_THAN, 1000);
		}
	}

}
