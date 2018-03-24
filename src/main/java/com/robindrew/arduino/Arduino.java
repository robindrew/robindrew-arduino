package com.robindrew.arduino;

import static java.lang.System.nanoTime;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.robindrew.arduino.connection.Connection;
import com.robindrew.arduino.util.Log;

import gnu.io.CommPortIdentifier;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class Arduino {

	private static final Log log = new Log(Connection.class);

	public static final boolean INPUT_LOGGING = true;
	public static final boolean OUTPUT_LOGGING = true;
	public static final boolean COMMAND_LOGGING = false;

	public static final int INPUT = 0;
	public static final int OUTPUT = 1;
	public static final int INPUT_PULLUP = 2;

	public static final int LOW = 0;
	public static final int HIGH = 1;

	public static final int DEFAULT = 1;
	public static final int INTERNAL = 3;
	public static final int EXTERNAL = 0;

	/**
	 * Initialises the Ardunio.
	 */
	static {

		// Redirect system output and error streams to SLF4J
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

		// Print out library path
		String libraryPath = System.getProperty("java.library.path");
		log.info("Library Path: " + libraryPath);
	}

	/**
	 * Initialises the Ardunio.
	 */
	public static void init() {
		// Nothing to do, just ensures the static block above is called
	}

	/**
	 * Sleep for the given number of milliseconds.
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a random number between the given boundaries (inclusive).
	 */
	public static int random(int min, int max) {
		return new Random(nanoTime()).nextInt((max - min) + 1) + min;
	}

	/**
	 * Returns all available ports.
	 */
	@SuppressWarnings("unchecked")
	public static List<CommPortIdentifier> getPortIds() {
		return Collections.list(CommPortIdentifier.getPortIdentifiers());
	}

	/**
	 * Returns the named port (if available).
	 */
	public static CommPortIdentifier getPortId(String name) {
		List<CommPortIdentifier> ports = getPortIds();
		for (CommPortIdentifier port : ports) {
			log.info("Detected Serial Port: '{}'", port.getName());
		}
		for (CommPortIdentifier port : ports) {
			if (port.getName().equals(name)) {
				return port;
			}
		}
		throw new IllegalArgumentException("No port identified with name: '" + name + "'");
	}

}
