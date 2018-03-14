package com.javabi.arduino.command;

import java.util.Collection;
import java.util.List;

import com.javabi.arduino.pin.Level;
import com.javabi.arduino.pin.Mode;
import com.javabi.arduino.pin.Pin;

public interface ICommandList {

	void execute();

	boolean isEmpty();

	int size();

	ICommandList clear();

	ICommandList add(ICommand<?> command);

	<D> ICommand<D> get(int index);

	<D> D getData(int index);

	ICommandList echo(String text);

	ICommandList delay(int millis);

	ICommandList pinMode(Pin pin, Mode mode);

	ICommandList pinMode(int pin, int mode);

	ICommandList setOutputPins(Pin... pins);

	ICommandList setInputPins(Pin... pins);

	ICommandList digitalWrite(Pin pin, int value);

	ICommandList digitalWrite(int pin, int value);

	ICommandList digitalWrite(Pin pin, Level level);

	ICommandList digitalWrite(int pin, Level level);

	ICommandList analogWrite(Pin pin, int value);

	ICommandList analogWrite(int pin, int value);

	DigitalRead digitalRead(Pin pin);

	DigitalRead digitalRead(int pin);

	List<DigitalRead> digitalRead(Pin... pins);

	List<DigitalRead> digitalRead(Collection<? extends Pin> pins);

	AnalogRead analogRead(Pin pin);

	AnalogRead analogRead(int pin);

	List<AnalogRead> analogRead(Pin... pins);

	List<AnalogRead> analogRead(Collection<? extends Pin> pins);

}