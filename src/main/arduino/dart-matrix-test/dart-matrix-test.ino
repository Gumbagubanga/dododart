#include <ArduinoJson.h>

// <PINS>
// Piezo Input Pin
#define VIBRATION_IN_PIN 29

// Matrix Input Pins
const int MATRIX_IN_PIN[16] = { 50, 51, 48, 49, 46, 47, 44, 45, 42, 43, 40, 41, 38, 39, 36, 37 };
// Matrix Output Pins
const int MATRIX_OUT_PIN[4] = { 52, 53, 34, 35 };

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

int EvalThrow() {
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

bool CheckMiss() {
  return (digitalRead(VIBRATION_IN_PIN) == HIGH);
}


void processInput() {
  miss = CheckMiss();
  matrixValue = EvalThrow();
}

void render() {
  if (miss || matrixValue != 0) {
    StaticJsonDocument<100> doc;

    doc["dart"] = matrixValue;
    doc["miss"] = miss;

    serializeJson(doc, Serial);
    Serial.println();

    delay(300);
  }
}

/* Setup loop */
void setup() {
  // Piezo
  pinMode(VIBRATION_IN_PIN, INPUT);

  for (int i = 0; i < 16; i++) {
    pinMode(MATRIX_IN_PIN[i], INPUT_PULLUP);
  }

  // Matrix
  for (int i = 0; i < 4; i++) {
    pinMode(MATRIX_OUT_PIN[i], OUTPUT);
  }

  // Start serial
  Serial.begin(9600);
  while (!Serial) continue;
}

/* Main loop */
void loop() {
  processInput();
  render();
}