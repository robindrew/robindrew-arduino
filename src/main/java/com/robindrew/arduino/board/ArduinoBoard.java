package com.robindrew.arduino.board;

import java.util.Set;

import com.robindrew.arduino.Arduino;
import com.robindrew.arduino.command.AnalogRead;
import com.robindrew.arduino.command.AnalogReadWait;
import com.robindrew.arduino.command.AnalogWrite;
import com.robindrew.arduino.command.CommandBatch;
import com.robindrew.arduino.command.Delay;
import com.robindrew.arduino.command.DigitalRead;
import com.robindrew.arduino.command.DigitalReadWait;
import com.robindrew.arduino.command.DigitalWrite;
import com.robindrew.arduino.command.Echo;
import com.robindrew.arduino.command.PinMode;
import com.robindrew.arduino.connection.Connection;
import com.robindrew.arduino.pin.Level;
import com.robindrew.arduino.pin.Mode;
import com.robindrew.arduino.pin.Pin;
import com.robindrew.arduino.util.Log;

public class ArduinoBoard implements IArduinoBoard {

	private static final Log log = new Log(ArduinoBoard.class);

	static {
		Arduino.init();
	}

	public static ArduinoBoard detectAndConnect() {

		// Attempt to establish a connection on each active port until
		// successful
		Connection connection = Connection.detectAndConnect();

		// Do we recognise the board name?
		String name = connection.getBoardName();
		if (name.equals("AVR_ATmega328P")) {
			return new ArduinoUno(connection);
		}
		if (name.equals("AVR_ATmega2560")) {
			return new ArduinoMega(connection);
		}

		log.warn("Unable to detect board from name: '{}'", name);
		return new ArduinoBoard(connection);
	}

	public static final Mode INPUT = Mode.INPUT;
	public static final Mode OUTPUT = Mode.OUTPUT;
	public static final Mode INPUT_PULLUP = Mode.INPUT_PULLUP;

	public static final Level LOW = Level.LOW;
	public static final Level HIGH = Level.HIGH;

	private final Connection connection;

	public ArduinoBoard(Connection connection) {
		if (connection == null) {
			throw new NullPointerException();
		}
		this.connection = connection;
	}

	public CommandBatch newBatch() {
		return new CommandBatch(getConnection());
	}

	public Connection getConnection() {
		return connection;
	}

	public String echo(String text) {
		return new Echo(text).execute(getConnection());
	}

	public void delay(int millis) {
		new Delay(millis).execute(getConnection());
	}

	public void pinMode(int pin, int mode) {
		new PinMode(pin, mode).execute(getConnection());
	}

	public void pinMode(Pin pin, Mode mode) {
		new PinMode(pin, mode).execute(getConnection());
	}

	public void digitalWrite(int pin, int value) {
		new DigitalWrite(pin, value).execute(getConnection());
	}

	public void digitalWrite(Pin pin, Level level) {
		new DigitalWrite(pin, level).execute(getConnection());
	}

	public int digitalReadWait(Pin pin, boolean greaterThan, Level level) {
		return new DigitalReadWait(pin, greaterThan, level).execute(getConnection());
	}

	public int digitalReadWait(int pin, boolean greaterThan, int value) {
		return new DigitalReadWait(pin, greaterThan, value).execute(getConnection());
	}

	public int analogReadWait(Pin pin, boolean greaterThan, int value) {
		return new AnalogReadWait(pin, greaterThan, value).execute(getConnection());
	}

	public int analogReadWait(int pin, boolean greaterThan, int value) {
		return new AnalogReadWait(pin, greaterThan, value).execute(getConnection());
	}

	public int digitalRead(int pin) {
		return new DigitalRead(pin).execute(getConnection());
	}

	public int digitalRead(Pin pin) {
		return new DigitalRead(pin).execute(getConnection());
	}

	public int analogRead(int pin) {
		return new AnalogRead(pin).execute(getConnection());
	}

	public int analogRead(Pin pin) {
		return new AnalogRead(pin).execute(getConnection());
	}

	public void analogWrite(int pin, int value) {
		new AnalogWrite(pin, value).execute(getConnection());
	}

	@Override
	public Set<Pin> getAnalogPins() {
		throw new IllegalStateException("Unknown board");
	}

	@Override
	public Set<Pin> getDigitalPins() {
		throw new IllegalStateException("Unknown board");
	}

	@Override
	public Set<Pin> getDigitalPwmPins() {
		throw new IllegalStateException("Unknown board");
	}

	@Override
	public void close() {
		connection.close();
	}

}
