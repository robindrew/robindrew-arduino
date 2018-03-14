# robindrew-arduino
Controller API for the Arduino

### Dependencies
This library is dependent on the [RXTX](http://rxtx.qbang.org/wiki/index.php/Main_Page) library for Java. This provides the means with which you can communicate with devices like the Arduino across the COM port.

### Sketch File
The sketch used by this API is simple and well documented. It is intended to be loaded on to the Arduino once and then all subsequent interaction uses the commands built in to the sketch. View the sketch source [here](https://github.com/robindrew/robindrew-arduino/blob/master/src/main/arduino/jrl/jrl.ino).

### Commands
There are a number of commands available. All the basic commands for reading and writing to pins are included, but also some more advanced commands are available too:

* PinMode
* AnalogRead
* AnalogReadWait
* AnalogWrite
* DigitalRead
* DigitalReadWait
* DigitalWrite
* Delay
* Echo

You can execute any of these commands on the board. The command is sent over the COM port and executed, before a response is received by the Java so it can continue. It is important to note that there is a noticable lag in communicating over the COM port. Sending commands one-by-one is fine in some situations, but often you are likely to need a number of commands to execute without the COM port lag between them.

To resolve this issue, I have provided a way to create a set of commands and send them all over the COM port as a single batch. In this way all the commands are executed in sequence natively on the Ardunio without any COM port delay.

## Example
The following example demostrates how to connect to the board (it can be an UNO or MEGA), and then issue a few simple commands to get the on board LED flashing. It includes the same sequence of commands, both sent individually to the board, and all in one go as a batch.

```java
try (ArduinoBoard board = ArduinoBoard.detectAndConnect()) {

	// Execute manually
	board.pinMode(ArduinoUno.LED_BUITLIN, ArduinoBoard.OUTPUT);
	for (int i = 1; i <= 10; i++) {
		board.digitalWrite(ArduinoUno.LED_BUITLIN, ArduinoBoard.HIGH);
		board.delay(100);
		board.digitalWrite(ArduinoUno.LED_BUITLIN, ArduinoBoard.LOW);
		board.delay(100);
	}

	// Execute as a batch
	CommandBatch batch = board.newBatch();
	for (int i = 1; i <= 10; i++) {
		batch.digitalWrite(ArduinoUno.LED_BUITLIN, ArduinoBoard.HIGH).delay(100);
		batch.digitalWrite(ArduinoUno.LED_BUITLIN, ArduinoBoard.LOW).delay(100);
	}
	batch.execute();
}
```
