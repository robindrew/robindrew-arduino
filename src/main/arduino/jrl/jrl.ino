
// Declare constants for all the commands we support
const int COMMAND_READY = 0;
const int COMMAND_ECHO = 1;
const int COMMAND_PIN_MODE = 2;
const int COMMAND_DIGITAL_WRITE = 3;
const int COMMAND_DIGITAL_READ = 4;
const int COMMAND_ANALOG_WRITE = 5;
const int COMMAND_ANALOG_READ = 6;
const int COMMAND_DELAY = 7;
const int COMMAND_ANALOG_REFERENCE = 8;
const int COMMAND_DIGITAL_READ_WAIT = 9;
const int COMMAND_ANALOG_READ_WAIT = 10;

// Number read/write constants
const long ZERO = 0;
const long NEGATIVE = 1;
const long POSITIVE = 2;
const long POWER = 100;

const long TRUE = 1;
const long FALSE = 0;

// The ready flag
boolean ready = false;

// Declare a output buffer
char outputBuffer[512];
int outputBufferLength = 0;

// Declare a request buffer
char inputBuffer[512];
int inputBufferLength = 0;
int inputBufferOffset = 0;

// Capture the board name
#if defined(__AVR_ATmega328P__)
#define BOARD_NAME "AVR_ATmega328P"
#elif defined(__AVR_ATmega168__)
#define BOARD_NAME "AVR_ATmega168"
#elif defined(__AVR_ATmega1280__)
#define BOARD_NAME "AVR_ATmega1280"
#elif defined(__AVR_ATmega2560__)
#define BOARD_NAME "AVR_ATmega2560"
#else
#define BOARD_NAME "Unknown"
#endif



// The SETUP function
void setup() {

  // Start serial port at 9600 bps:
  Serial.begin(9600);

  // Wait for serial port to connect. Needed for native USB port only
  while (!Serial) {
    ;
  }
}

// The LOOP function
void loop() {

  // We peform a handshake once with java
  handshake();

  // Commands are sent in a batch - we can execute any number of commands
  long commandCount = readNumber();
  if (commandCount > 0) {
    executeCommands(commandCount);
  }
}

void executeCommands(long count) {

  // Execute a number of commands
  for (long i = 0; i < count; i ++) {

    // First read a single byte which tells us which command to execute
    int commandByte = readByte();

    // We use a switch statement for all the commands
    switch (commandByte) {

      // Ready
      case COMMAND_READY:
        executeCommandReady();
        break;

      // Echo
      case COMMAND_ECHO:
        executeCommandEcho();
        break;

      // Echo
      case COMMAND_DELAY:
        executeCommandDelay();
        break;

      // pinMode()
      case COMMAND_PIN_MODE:
        executeCommandPinMode();
        break;

      // digitalWrite()
      case COMMAND_DIGITAL_WRITE:
        executeCommandDigitalWrite();
        break;

      // analogWrite()
      case COMMAND_ANALOG_WRITE:
        executeCommandAnalogWrite();
        break;

      // digitalRead()
      case COMMAND_DIGITAL_READ:
        executeCommandDigitalRead();
        break;

      // analogRead()
      case COMMAND_ANALOG_READ:
        executeCommandAnalogRead();
        break;

      // analogRead()
      case COMMAND_ANALOG_REFERENCE:
        executeCommandAnalogReference();
        break;

      // digitalReadWait()
      case COMMAND_DIGITAL_READ_WAIT:
        executeCommandDigitalReadWait();
        break;

      // analogReadWait()
      case COMMAND_ANALOG_READ_WAIT:
        executeCommandAnalogReadWait();
        break;
    }
  }

  // Flush the output once all commands are executed
  flushOutputBuffer();
}


// Flush the output buffer
void flushOutputBuffer() {
  if (outputBufferLength > 0) {
    Serial.write(outputBuffer, outputBufferLength);
    outputBufferLength = 0;
  }
}


// Perform a handshake with the java code, only executes once.
void handshake() {
  if (!ready) {

    // Write bytes until a response is received
    while (true) {
      Serial.write(0);
      delay(1000);
      int byteRead = Serial.read();
      if (byteRead == 1) {
        break;
      }
    }

    // Acknowledge the response
    Serial.write(2);

    // Send the board name
    Serial.println(BOARD_NAME);

    // We only handshake once
    ready = true;
  }
}


// WRITE BYTE (8-BIT SIGNED)
void writeByte(int value) {
  outputBuffer[outputBufferLength++] = value;
}


// WRITE BOOLEAN
void writeBoolean(boolean value) {
  if (value) {
    writeByte(TRUE);
  } else {
    writeByte(FALSE);
  }
}


// WRITE A LINE OF ASCII TEXT
void writeLine(String text) {
  for (int i = 0; i < text.length(); i++) {
    char character = text.charAt(i);
    writeByte(character);
  }
  writeByte('\r');
  writeByte('\n');
}


// WRITE A LINE CONTAINING A SINGLE NUMBER
void writeLine(long number) {
  String text = String(number);
  writeLine(text);
}


// WRITE NUMBER (32-BIT SIGNED)
void writeNumber(long value) {
  int length = 0;

  // Zero is a special case
  if (value == ZERO) {
    writeByte(0);
    return;
  }

  // Is the number negative?
  boolean negative = false;
  if (value < 0) {
    negative = true;
    value = -value;
  }

  // Calculate the number length
  long counter = value;
  while (counter > 0) {
    counter /= POWER;
    length++;
  }

  // Write the number
  if (negative) {
    writeByte(NEGATIVE);
  } else {
    writeByte(POSITIVE);
  }
  writeByte(length);
  while (value > 0) {
    int digit = (int) (value % POWER);
    writeByte(digit);
    value /= POWER;
  }
  Serial.flush();
}


// READ BYTE (8-BIT SIGNED)
int readByte() {

  // Buffer empty?
  if (inputBufferOffset >= inputBufferLength) {

    // Wait for bytes to be available
    while (Serial.available() < 2) {
      delay(10);
    }

    // Determine the length of the buffer
    int length1 = Serial.read();
    int length2 = Serial.read();
    if (length1 < 0) {
      length1 += 256;
    }
    if (length2 < 0) {
      length2 += 256;
    }

    // Populate the buffer
    Serial.readBytes(inputBuffer, length1 + length2);
    inputBufferLength = length1 + length2;
    inputBufferOffset = 0;
  }

  return inputBuffer[inputBufferOffset++];
}


// READ BOOLEAN
boolean readBoolean() {
  return readByte() != FALSE;
}


// READ NUMBER (32-BIT SIGNED)
long readNumber() {
  long first = readByte();
  if (first == ZERO) {
    return 0;
  }

  // Is the number negative
  boolean negative = (first == NEGATIVE);

  int length = readByte();

  long number = 0;
  long multiplier = 1;

  for (int i = 0; i < length; i++) {
    long decimal = readByte();
    number += (decimal * multiplier);
    multiplier *= POWER;
  }

  return negative ? -number : number;
}


// COMMAND_READY
void executeCommandReady() {
  writeByte(COMMAND_READY);
}


// COMMAND_ECHO
void executeCommandEcho() {
  writeByte(COMMAND_ECHO);

  // Request
  long length = readNumber();
  for (long i = 0; i < length; i++) {
    int echo = readByte();
    writeByte(echo);
  }
}


// COMMAND_DELAY
void executeCommandDelay() {

  // Request
  long millis = readNumber();

  // Command
  delay(millis);

  // Response
  writeByte(COMMAND_DELAY);
}


// COMMAND_PIN_MODE
void executeCommandPinMode() {

  // Request
  int pin = readByte();
  int mode = readByte();

  // Command
  pinMode(pin, mode);

  // Response
  writeByte(COMMAND_PIN_MODE);
}


// COMMAND_DIGITAL_WRITE
void executeCommandDigitalWrite() {

  // Request
  int pin = readByte();
  int value = readNumber();

  // Command
  digitalWrite(pin, value);

  // Response
  writeByte(COMMAND_DIGITAL_WRITE);
}


// COMMAND_ANALOG_WRITE
void executeCommandAnalogWrite() {

  // Request
  int pin = readByte();
  int value = readNumber();

  // Command
  analogWrite(pin, value);

  // Response
  writeByte(COMMAND_ANALOG_WRITE);
}


// COMMAND_DIGITAL_READ
void executeCommandDigitalRead() {

  // Request
  int pin = readByte();

  // Command
  int value = digitalRead(pin);

  // Response
  writeByte(COMMAND_DIGITAL_READ);
  writeNumber(value);
}


// COMMAND_ANALOG_READ
void executeCommandAnalogRead() {

  // Request
  int pin = readByte();

  // Command
  int value = analogRead(pin);

  // Response
  writeByte(COMMAND_ANALOG_READ);
  writeNumber(value);
}


// COMMAND_ANALOG_REFERENCE
void executeCommandAnalogReference() {

  // Request
  int type = readByte();

  // Command
  analogReference(type);

  // Response
  writeByte(COMMAND_ANALOG_REFERENCE);
}


// COMMAND_DIGITAL_READ_WAIT
void executeCommandDigitalReadWait() {

  // Request
  int pin = readByte();
  boolean greaterThan = readBoolean();
  int threshold = readNumber();

  // Command
  int value = 0;
  while (true) {
    value = digitalRead(pin);
    if (greaterThan && value >= threshold) {
      break;
    }
    if (!greaterThan && value <= threshold) {
      break;
    }
  }

  // Response
  writeByte(COMMAND_DIGITAL_READ_WAIT);
  writeNumber(value);
}


// COMMAND_ANALOG_READ_WAIT
void executeCommandAnalogReadWait() {

  // Request
  int pin = readByte();
  boolean greaterThan = readBoolean();
  int threshold = readNumber();

  // Command
  int value = 0;
  while (true) {
    value = analogRead(pin);
    if (greaterThan) {
      if (value >= threshold) {
        break;
      }
    } else {
      if (value <= threshold) {
        break;
      }
    }
  }

  // Response
  writeByte(COMMAND_ANALOG_READ_WAIT);
  writeNumber(value);
}

