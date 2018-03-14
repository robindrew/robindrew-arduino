package com.javabi.arduino.command;

import static com.javabi.arduino.board.ArduinoBoard.HIGH;
import static com.javabi.arduino.board.ArduinoBoard.LOW;
import static com.javabi.arduino.board.ArduinoBoard.OUTPUT;
import static com.javabi.arduino.board.ArduinoUno.LED_BUITLIN;

import org.junit.Test;

import com.javabi.arduino.board.ArduinoBoard;
import com.javabi.arduino.command.CommandBatch;

public class LedBuiltInTest {

	@Test
	public void flashForTwoSeconds() {
		try (ArduinoBoard board = ArduinoBoard.detectAndConnect()) {

			// Execute manually
			board.pinMode(LED_BUITLIN, OUTPUT);
			for (int i = 1; i <= 10; i++) {
				board.digitalWrite(LED_BUITLIN, HIGH);
				board.delay(100);
				board.digitalWrite(LED_BUITLIN, LOW);
				board.delay(100);
			}

			// Execute as a batch
			CommandBatch batch = board.newBatch();
			for (int i = 1; i <= 10; i++) {
				batch.digitalWrite(LED_BUITLIN, HIGH).delay(100);
				batch.digitalWrite(LED_BUITLIN, LOW).delay(100);
			}
			batch.execute();
		}
	}

}
