#include <Arduino.h>

int XAXIS = A0;
int YAXIS = A1;
int BUTTON = 8;


void setup() {
    Serial.begin(9600);
    pinMode(BUTTON, INPUT);
    pinMode(XAXIS, INPUT);
    pinMode(YAXIS, INPUT);
}

void loop() {
    /* for (int i = 3; i < 12; i++){
        Serial.print("Port: ");
        Serial.print(i);
        Serial.print(" :: ");
        Serial.println(digitalRead(i));
    }*/
    Serial.print(1 - digitalRead(BUTTON));
    Serial.print(" ");
    Serial.print(analogRead(XAXIS));
    Serial.print(" ");
    Serial.println(analogRead(YAXIS));
    delay(1000);
}
