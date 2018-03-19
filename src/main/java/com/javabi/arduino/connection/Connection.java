package com.javabi.arduino.connection;

import static gnu.io.SerialPort.DATABITS_8;
import static gnu.io.SerialPort.PARITY_NONE;
import static gnu.io.SerialPort.STOPBITS_1;

import com.javabi.arduino.Arduino;
import com.javabi.arduino.util.Log;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class Connection implements AutoCloseable {

	private static final Log log = new Log(Connection.class);

	static {
		Arduino.init();
	}

	/**
	 * Detect the serial ports and attempt to connect to each one until successful.
	 */
	public static Connection detectAndConnect() {

		// Loop over the active ports
		for (CommPortIdentifier portId : Arduino.getPortIds()) {

			// Attempt to connect ...
			String name = portId.getName();
			try {
				log.info("Detected available port: {}", name);
				Connection connection = connect(portId);
				log.warn("Handshake successful on port: {}", name);
				return connection;
			} catch (ConnectException ce) {
				log.warn("Handshake failed on port: {} ({})", name, ce.getMessage());
			}
		}

		// No active ports, or all connection attempts failed!
		throw new ConnectException("Failed to connect to any ports");
	}

	/**
	 * Attempt to connect to the named serial port.
	 */
	public static Connection connect(String name) {
		CommPortIdentifier portId = Arduino.getPortId(name);
		return connect(portId);
	}

	/**
	 * Attempt to connect to the given serial port.
	 */
	public static Connection connect(CommPortIdentifier portId) {
		try {
			String name = portId.getName();

			log.info("Connecting: '{}'", name);

			// open serial port, and use class name for the appName.
			SerialPort port = (SerialPort) portId.open("Arduino" + name, DEFAULT_TIME_OUT);

			// set port parameters
			port.setSerialPortParams(DEFAULT_DATA_RATE, DATABITS_8, STOPBITS_1, PARITY_NONE);

			// open the streams
			log.info("Initialising input buffer (logging {})", Arduino.INPUT_LOGGING ? "enabled" : "disabled");
			InputBuffer input = new InputBuffer(port.getInputStream());
			log.info("Initialising output buffer (logging {})", Arduino.OUTPUT_LOGGING ? "enabled" : "disabled");
			OutputBuffer output = new OutputBuffer(port.getOutputStream());

			// We register an event listener on the port so we know when data is
			// available
			log.info("Initialising event listener");
			port.addEventListener(input);
			port.notifyOnDataAvailable(true);

			log.info("Performing handshake with Arduino ...");
			long waitStarted = System.currentTimeMillis();
			int timeout = 20;
			while (input.isEmpty()) {
				Arduino.sleep(100);
				timeout--;
				if (timeout == 0) {
					throw new ConnectException("Timeout waiting for response from serial connection: " + name);
				}
			}
			Arduino.sleep(100);
			output.write(1);
			Arduino.sleep(100);
			input.skipBytes(0);
			int response = input.readByte();
			if (response != 2) {
				throw new ConnectException("Unexpected response from serial connection: " + name + ", response=" + response);
			}

			// Read the board name
			String boardName = input.readLine();
			log.info("Board: {}", boardName);

			long waitFinished = System.currentTimeMillis();
			log.info("Ready in " + (waitFinished - waitStarted) + " millis");

			return new Connection(port, input, output, boardName);
		} catch (ConnectException ce) {
			throw ce;
		} catch (Exception e) {
			throw new ConnectException(e);
		}
	}

	public static final int ZERO = 0;
	public static final int NEGATIVE = 1;
	public static final int POSITIVE = 2;
	public static final int POWER = 100;
	public static final int TRUE = 1;
	public static final int FALSE = 0;

	/** Milliseconds to block while waiting for port open */
	private static final int DEFAULT_TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DEFAULT_DATA_RATE = 9600;

	/** The underlying port. */
	private final SerialPort port;
	/** The buffer for input from the port. */
	private final InputBuffer input;
	/** The buffer for output to the port. */
	private final OutputBuffer output;
	/** The name of the Arduino board. */
	private final String boardName;

	public Connection(SerialPort port, InputBuffer input, OutputBuffer output, String boardName) {
		if (port == null) {
			throw new NullPointerException("port");
		}
		if (input == null) {
			throw new NullPointerException("input");
		}
		if (output == null) {
			throw new NullPointerException("output");
		}
		if (boardName.isEmpty()) {
			throw new IllegalArgumentException("boardName is empty");
		}
		this.port = port;
		this.input = input;
		this.output = output;
		this.boardName = boardName;
	}

	public String getName() {
		return port.getName();
	}

	public InputBuffer getInput() {
		return input;
	}

	public OutputBuffer getOutput() {
		return output;
	}

	public String getBoardName() {
		return boardName;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void close() {
		log.info("Closing connection '{}'", this);
		port.removeEventListener();
		port.close();
	}

}
