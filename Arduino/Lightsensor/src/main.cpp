#include <Arduino.h>

int pins[] = { 53, 51, 49, 47, 45, 43, 41, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, };

int ROWS = 5;

int COLUMNS = 5;

void setup(){
    for (int pin: pins){
        pinMode(pin, INPUT);
    }
    Serial.begin(9600);
}

void loop(){
    while (Serial.available() <= 0){}
    String command = Serial.readString();
    if(command == "g") {
        for(int pin: pins){
            Serial.print(1 - digitalRead(pin));
            Serial.print(" ");
        }
        Serial.println();
    }
    if(command == "c"){
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
                bool hasTriggered = false;
                for (int pin: pins){
                    if(digitalRead(pin) == 0){
                        if(hasTriggered){
                            Serial.println("Mehrere pins wurden aktiviert. Bitte erneut versuchen");
                            j--;
                            break;
                        }
                        Serial.print("\nPin ");
                        Serial.println(pin);
                        Serial.print("\n");
                        binding[i - 1][j - 1] = pin;
                        hasTriggered = true;
                    }
                }
                Serial.read();
            }
            Serial.println();
        }
        Serial.println("Pin layout: ");
        String configString = "int pins[] = { ";
        for(int row = 0; row < ROWS; row++){
            for(int column = 0; column < COLUMNS; column++){
                int pin = binding[row][column];
                Serial.print(pin);
                Serial.print("  ");
                configString += pin;
                configString += ", ";
            }
            Serial.println();
        }
        configString += "};";
        Serial.println("Use this config String: ");
        Serial.println(configString);
    }
}