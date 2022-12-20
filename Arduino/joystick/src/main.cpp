#include <Arduino.h>

int XAXIS = A0;
int YAXIS = A1;
int BUTTON = 8;
int ulimit = 700;
int dlimit = 300;


void setup() {
    Serial.begin(9600);
    pinMode(BUTTON, INPUT);
    pinMode(XAXIS, INPUT);
    pinMode(YAXIS, INPUT);
}

void loop() {
    if(digitalRead(BUTTON) == 0){
        Serial.println("b");
        delay(250);
        while(digitalRead(BUTTON) == 0){
            delay(250);
        }
    }
    if(analogRead(XAXIS) > ulimit){
        Serial.println("r");
        delay(250);
        while(analogRead(XAXIS) > ulimit){
            delay(250);
        }
    }
    if(analogRead(XAXIS) < dlimit){
        Serial.println("l");
        delay(250);
        while(analogRead(XAXIS) < dlimit){
            delay(250);
        }
    }
    if(analogRead(YAXIS) > ulimit){
        Serial.println("u");
        delay(250);
        while(analogRead(YAXIS) > ulimit){
            delay(250);
        }
    }
    if(analogRead(YAXIS) < dlimit){
        Serial.println("d");
        delay(250);
        while(analogRead(YAXIS) < dlimit){
            delay(250);
        }
    }
}
