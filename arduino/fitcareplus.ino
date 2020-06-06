#include <Wire.h>
#include <Adafruit_MLX90614.h>

const int upperThreshold = 700;
const int lowerThreshold = 250;
String command = "";

Adafruit_MLX90614 mlx = Adafruit_MLX90614();

void setup() {
  Serial.begin(9600);
  mlx.begin();
}

void loop(){
  while(Serial.available() > 0){
    command += (char)Serial.read();
  }
  
  if(command != "") {
    
    if(command == "T") {
      Serial.print("T: ");
      Serial.println(mlx.readObjectTempC() + 3);
    }
    
    if(command == "P") {
      printBPM();
    }
    
    command = "";
  }
  
  delay(100);
}

void printBPM() {
    
  boolean ignoreUpper = false;
  boolean startPulseReached = false;
  int completePulseStartTimeStamp = 0;
  int completePulseEndTimeStamp = 0;
  int completePulseCount = 0;
  
  for(int i = 0; i < 500; i++) {
     
     const int output = analogRead(A0);
          
     if(output > upperThreshold && !ignoreUpper) {       
       if(!startPulseReached) {
         startPulseReached = true;
         completePulseStartTimeStamp = millis();
         Serial.print("Start Pulse detected at: ");
         Serial.println(completePulseStartTimeStamp);
       } else {
         completePulseEndTimeStamp =  millis();
         completePulseCount++;
         Serial.print("Count = ");
         Serial.println(completePulseCount);
         Serial.print("End Pulse detected at: ");
         Serial.println(completePulseEndTimeStamp);
       }
       
       ignoreUpper = true;
     }
     
     if(output < lowerThreshold && ignoreUpper) {
       ignoreUpper = false;
       Serial.println("Lower detected!");
     }
     
     delay(10);
  }
   
  Serial.println("");
  Serial.println("");
  const float averagePulseInterval = (completePulseEndTimeStamp - completePulseStartTimeStamp)/ completePulseCount;
  Serial.print("Average Interval: ");
  Serial.println(averagePulseInterval);

  const float bpm = 60.0 / (averagePulseInterval / 1000.0;
  Serial.print("P: ");
  Serial.println(bpm > 200 ? 0 : bpm));
}
