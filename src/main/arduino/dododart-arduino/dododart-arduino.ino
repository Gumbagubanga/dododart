#include <ArduinoJson.h>

// <PINS>
#define BUTTON_IN_PIN 52
#define BUTTON_LED_OUT_PIN 13
#define VIBRATION_IN_PIN 23
const int MATRIX_IN_PIN[16] = { 46, 44, 42, 40, 38, 36, 34, 32, 51, 49, 47, 45, 43, 41, 39, 37 };
const int MATRIX_OUT_PIN[4] = { 50, 48, 35, 33 };

// <VARIABLES>
// Matrix Values
// 120 = single 20, 220 = double 20, 320 = triple 20
// 125 = single Bull, 225 = double bull
const int MATRIX_VALUES[4][16] = {
  { 212, 112, 209, 109, 214, 114, 211, 111, 208, 108, 000, 312, 309, 314, 311, 308 },
  { 216, 116, 207, 107, 219, 119, 203, 103, 217, 117, 225, 316, 307, 319, 303, 317 },
  { 202, 102, 215, 115, 210, 110, 206, 106, 213, 113, 125, 302, 315, 310, 306, 313 },
  { 204, 104, 218, 118, 201, 101, 220, 120, 205, 105, 000, 304, 318, 301, 320, 305 }
};

int matrixValue = 0;
bool miss = false;
bool buttonPressed = false;
bool buttonBlink = false;
unsigned long previousMillis = 0;  // will store last time LED was updated
int ledState = LOW;
const long interval = 750;  // interval at which to blink (milliseconds)

int evaluateThrow() {
  for (int x = 0; x < 4; x++) {
    digitalWrite(MATRIX_OUT_PIN[0], HIGH);
    digitalWrite(MATRIX_OUT_PIN[1], HIGH);
    digitalWrite(MATRIX_OUT_PIN[2], HIGH);
    digitalWrite(MATRIX_OUT_PIN[3], HIGH);
    digitalWrite(MATRIX_OUT_PIN[x], LOW);

    for (int y = 0; y < 16; y++) {
      int val = digitalRead(MATRIX_IN_PIN[y]);
      if (val == 0) {
        return MATRIX_VALUES[x][y];
      }
    }
  }
  return 0;
}

bool checkMiss() {
  return (digitalRead(VIBRATION_IN_PIN) == HIGH);
}

bool checkButton() {
  return (digitalRead(BUTTON_IN_PIN) == LOW);
}

void processInput() {
  buttonPressed = checkButton();
  matrixValue = evaluateThrow();
  miss = checkMiss();
}

void readSerial() {
  if (Serial.available()) {
    StaticJsonDocument<100> doc;
    DeserializationError error = deserializeJson(doc, Serial);
    if (error == DeserializationError::Ok) {
      if (doc["payload"] == "ButtonStartBlink") {
        buttonBlink = true;
      } else if (doc["payload"] == "ButtonStopBlink") {
        buttonBlink = false;
      }
    } else {
      // Flush all bytes in the "link" serial port buffer
      while (Serial.available() > 0) {
        Serial.read();
      }
    }
  }
}

void render() {
  if (buttonPressed || miss || matrixValue != 0) {
    StaticJsonDocument<100> doc;

    doc["dart"] = matrixValue;
    doc["buttonPressed"] = buttonPressed;

    serializeJson(doc, Serial);
    Serial.println();

    delay(500);
  }
}

void update() {
  if (buttonBlink) {
    unsigned long currentMillis = millis();
    if (currentMillis - previousMillis >= interval) {
      // save the last time you blinked the LED
      previousMillis = currentMillis;

      // if the LED is off turn it on and vice-versa:
      if (ledState == LOW) {
        ledState = HIGH;
      } else {
        ledState = LOW;
      }

      // set the LED with the ledState of the variable:
      digitalWrite(BUTTON_LED_OUT_PIN, ledState);
    }
  }
}

void setup() {
  pinMode(BUTTON_IN_PIN, INPUT_PULLUP);
  pinMode(BUTTON_LED_OUT_PIN, OUTPUT);
  pinMode(VIBRATION_IN_PIN, INPUT);

  for (int i = 0; i < 16; i++) {
    pinMode(MATRIX_IN_PIN[i], INPUT_PULLUP);
  }

  // Matrix
  for (int i = 0; i < 4; i++) {
    pinMode(MATRIX_OUT_PIN[i], OUTPUT);
  }

  digitalWrite(BUTTON_LED_OUT_PIN, LOW);

  // Start serial
  Serial.begin(9600);
  while (!Serial) continue;
}

void loop() {
  readSerial();
  processInput();
  update();
  render();
}