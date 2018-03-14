
const long ZERO = 0;
const long NEGATIVE = 1;
const long POSITIVE = 2;
const long POWER = 10;

// The ready flag
boolean ready = false;

int bufferLength = 0;
char buffer[512];

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

  // Test numbers
  for (int i = 0; i < 19; i++) {
    testReadNumber();
  }

  for (int i = 0; i < 3; i++) {
    testReadBytes();
  }

  delay(10000);
}

void testReadBytes() {
  readBytes();
  writeBytes();
}

void testReadNumber() {
  long number = readNumber();
  writeNumber(number);
}

int readUnsignedByte() {
  int value = readByte();
  if (value < 0) {
    value += 256;
  }
  return value;
}

void readBytes() {
  bufferLength = readNumber();
  Serial.readBytes(buffer, bufferLength);
}

char readByte() {
  while (Serial.available() < 1) {
    delay(10);
  }
  return Serial.read();
}

void writeByte(long value) {
  Serial.write((char) value);
}

void writeBytes() {
  writeNumber(bufferLength);
  Serial.write(buffer, bufferLength);
}

void handshake() {
  if (!ready) {

    // Write bytes until a response is received
    while (true) {
      Serial.write(0);
      delay(100);
      int byteRead = Serial.read();
      if (byteRead == 1) {
        break;
      }
    }

    // Acknowledge the response
    Serial.write(2);

    // We only handshake once
    ready = true;
  }
}


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
