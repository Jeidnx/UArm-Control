#include <Arduino.h>

PROGMEM const int ROWS = 5;

PROGMEM const int COLUMNS = 5;

int pins[] = {
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        29,
        30,
        31,
        32,
        33,
        34,
        35,
        36,
        37,
        38,
        39,
        //40,
        41,
        //42,
        43,
        //44,
        45,
        //46,
        47,
        //48,
        49,
        //50,
        51,
        //52,
        53
};
void setup() {

    for (int pin: pins){
        pinMode(pin, INPUT);
    }
    Serial.begin(9600);
    Serial.println("Press any key to start calibration");
}

void loop(){
    if(Serial.available() > 0){
        Serial.read();
        int binding[ROWS][COLUMNS];
        for (int i = 1; i <= ROWS; ++i) {
            for (int j = 1; j <= COLUMNS; ++j) {
                Serial.print("Calibrating Row ");
                Serial.print(i);
                Serial.print(" Column ");
                Serial.println(j);
                Serial.println("Please cover the Field with something. Press any key to continue");
                while(Serial.available() < 1){
                    delay(500);
                }
                for (int pin: pins){
                    if(digitalRead(pin) == 0){
                        Serial.print("\nPin ");
                        Serial.println(pin);
                        Serial.print("\n");
                        binding[i - 1][j - 1] = pin;
                        continue;
                    }
                }
                Serial.read();
            }
            Serial.println();
        }
        Serial.println("Pin layout: ");
        for(int row = 0; row < ROWS; row++){
            for(int column = 0; column < COLUMNS; column++){
                Serial.print(binding[row][column]);
                Serial.print("  ");
            }
            Serial.println();
        }

        Serial.println("\n");
    }

}