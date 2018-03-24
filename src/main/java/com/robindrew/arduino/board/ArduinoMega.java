package com.robindrew.arduino.board;

import java.util.LinkedHashSet;
import java.util.Set;

import com.robindrew.arduino.connection.Connection;
import com.robindrew.arduino.pin.Pin;

/**
 * <p>
 * Digital pins D0 and D1 will NOT be available for digitalRead() or ditalWrite() when using serial communication.
 * </p>
 * <p>
 * Digital pins D3, D5, D6, D9, D10 and D11 work fully with analogWrite() using PWM (Pulse Wave Modulation). This is
 * effectively turning the digital pin on and off very fast!
 * </p>
 */
public class ArduinoMega extends ArduinoBoard {

	public static final Pin D0 = Pin.valueOf(0);
	public static final Pin D1 = Pin.valueOf(1);
	public static final Pin D2 = Pin.valueOf(2);
	public static final Pin D3 = Pin.valueOf(3);
	public static final Pin D4 = Pin.valueOf(4);
	public static final Pin D5 = Pin.valueOf(5);
	public static final Pin D6 = Pin.valueOf(6);
	public static final Pin D7 = Pin.valueOf(7);
	public static final Pin D8 = Pin.valueOf(8);
	public static final Pin D9 = Pin.valueOf(9);

	public static final Pin D10 = Pin.valueOf(10);
	public static final Pin D11 = Pin.valueOf(11);
	public static final Pin D12 = Pin.valueOf(12);
	public static final Pin D13 = Pin.valueOf(13);

	public static final Pin A0 = Pin.valueOf(14);
	public static final Pin A1 = Pin.valueOf(15);
	public static final Pin A2 = Pin.valueOf(16);
	public static final Pin A3 = Pin.valueOf(17);
	public static final Pin A4 = Pin.valueOf(18);
	public static final Pin A5 = Pin.valueOf(19);

	public static final Pin SERIAL_RX = D0;
	public static final Pin SERIAL_TX = D1;
	public static final Pin INTERRUPT_2 = D2;
	public static final Pin INTERRUPT_3 = D3;

	public static final Pin SPI_SS = D10;
	public static final Pin SPI_MOSI = D11;
	public static final Pin SPI_MISO = D12;
	public static final Pin SPI_SCK = D13;

	public static final Pin PWM_3 = D3;
	public static final Pin PWM_5 = D5;
	public static final Pin PWM_6 = D6;
	public static final Pin PWM_9 = D9;
	public static final Pin PWM_10 = D10;
	public static final Pin PWM_11 = D11;

	// LED_BUILTIN
	public static final Pin LED_BUITLIN = D13;

	public ArduinoMega(Connection connection) {
		super(connection);
	}

	@Override
	public Set<Pin> getDigitalPins() {
		Set<Pin> set = new LinkedHashSet<>();
		set.add(D0);
		set.add(D1);
		set.add(D2);
		set.add(D3);
		set.add(D4);
		set.add(D5);
		set.add(D6);
		set.add(D7);
		set.add(D8);
		set.add(D9);
		set.add(D10);
		set.add(D11);
		set.add(D12);
		set.add(D13);
		return set;
	}

	@Override
	public Set<Pin> getDigitalPwmPins() {
		Set<Pin> set = new LinkedHashSet<>();
		set.add(PWM_3);
		set.add(PWM_5);
		set.add(PWM_6);
		set.add(PWM_9);
		set.add(PWM_10);
		set.add(PWM_11);
		return set;
	}

	@Override
	public Set<Pin> getAnalogPins() {
		Set<Pin> set = new LinkedHashSet<>();
		set.add(A0);
		set.add(A1);
		set.add(A2);
		set.add(A3);
		set.add(A4);
		set.add(A5);
		return set;
	}

}
