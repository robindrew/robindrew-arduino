package com.robindrew.arduino.pin.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.robindrew.arduino.command.PinCommand;
import com.robindrew.arduino.pin.Pin;

public class PinValueMap {

	private final Map<Pin, PinValue> map = new TreeMap<>();

	public PinValue set(Pin pin, int value) {
		PinValue pinValue = map.get(pin);
		if (pinValue == null) {
			pinValue = new PinValue(pin, value);
			map.put(pin, pinValue);
		} else {
			pinValue.update(value);
		}
		return pinValue;
	}

	public PinValue set(PinCommand<Integer> read) {
		return set(read.getPin(), read.get());
	}

	public void set(Collection<? extends PinCommand<Integer>> reads) {
		for (PinCommand<Integer> read : reads) {
			set(read);
		}
	}

	public PinValue get(Pin pin) {
		PinValue value = map.get(pin);
		if (value == null) {
			throw new IllegalArgumentException("no value set for pin: " + pin);
		}
		return value;
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public List<PinValue> getChangedPins() {
		List<PinValue> list = new ArrayList<>();
		for (PinValue value : map.values()) {
			if (value.hasChanged()) {
				list.add(value);
			}
		}
		return list;
	}

}
