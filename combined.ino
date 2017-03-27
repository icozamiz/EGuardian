#include <CurieBLE.h>
#include "CurieIMU.h"

BLEPeripheral blePeripheral;       // BLE Peripheral Device (the board you're programming)
BLEService heartRateService("180D"); // BLE Heart Rate Service
// BLE Heart Rate Measurement Characteristic"
BLECharacteristic heartRateChar("2A37",  // standard 16-bit characteristic UUID
    BLERead | BLENotify, 2); 
BLECharacteristic fallChar("2B37",  // standard 16-bit characteristic UUID
    BLERead | BLENotify, 2);    
int oldHeartRate = 0;  // last heart rate reading from analog input
long previousMillis = 0;  // last time the heart rate was checked, in ms
float x, y, z;
float px =0;
float py =0;
float pz =0;

void setup() {
  Serial.begin(9600);    // initialize serial communication
  CurieIMU.begin(); 
  CurieIMU.setGyroRange(2000);
  pinMode(13, OUTPUT);   // initialize the LED on pin 13 to indicate when a central is connected
  blePeripheral.setLocalName("EGuardian");
//  blePeripheral.setDescriptor("EGuardian");
  blePeripheral.setAdvertisedServiceUuid(heartRateService.uuid());  // add the service UUID
  blePeripheral.addAttribute(heartRateService);   // Add the BLE Heart Rate service
  blePeripheral.addAttribute(heartRateChar); // add the Heart Rate Measurement characteristic
  blePeripheral.addAttribute(fallChar);
  blePeripheral.begin();
  Serial.println("Bluetooth device active, waiting for connections...");
}

void loop() {
  // listen for BLE peripherals to connect:
  BLECentral central = blePeripheral.central();
  CurieIMU.readGyroScaled(x, y, z);

  // if a central is connected to peripheral:
  if (central) {
    Serial.print("Connected to central: ");
    // print the central's MAC address:
    Serial.println(central.address());
    // turn on the LED to indicate the connection:
    digitalWrite(13, HIGH);

    // check the heart rate measurement every 200ms
    // as long as the central is still connected:
    while (central.connected()) {
      long currentMillis = millis();
      // if 200ms have passed, check the heart rate measurement:
      if (currentMillis - previousMillis >= 1000) {
        previousMillis = currentMillis;
        updateHeartRate();
        CurieIMU.readGyroScaled(x, y, z);
        
      if ((x > 375  || x < -375)&&(px > 375  || px < -375) || ((y > 375 || y < -375)&& (py > 375 || py < -375))|| ((z > 375 || z < -375)&&(pz > 375 || pz < -375))){
        Serial.print("Fall Detected");
         const unsigned char fallCharArray[2] = { 0, char(px) };
         fallChar.setValue(fallCharArray, 1);  // and update a fall may have occurred
        Serial.print("\n");
      }
      else if((x > 100  || x < -100)&&(px > 100  || px < -100) || ((y > 100 || y < -100)&& (py > 100 || py < -100))|| ((z > 100 || z < -100)&&(pz > 100 || pz < -100))){
        Serial.print("Active");
        Serial.print("\n");
      }
  //    else{
   //     Serial.print("At rest");
      //  Serial.print("\n");
    //  }
      px = x;
      py = y;
      pz = z;
     
      }
    }
    // when the central disconnects, turn off the LED:
    digitalWrite(13, LOW);
    Serial.print("Disconnected from central: ");
    Serial.println(central.address());
  }
}

void updateHeartRate() {
  /* Read the current voltage level on the A0 analog input pin.
     This is used here to simulate the heart rate's measurement.
  */
  int heartRateMeasurement = analogRead(A0);
  int heartRate = map(heartRateMeasurement, 0, 1023, 0, 100);
  if (heartRate != oldHeartRate) {      // if the heart rate has changed
    Serial.print("Heart Rate is now: "); // print it
    Serial.println(heartRate);
    const unsigned char heartRateCharArray[2] = { 0, (char)heartRate };
    heartRateChar.setValue(heartRateCharArray, 2);  // and update the heart rate measurement characteristic
    oldHeartRate = heartRate;           // save the level for next comparison
  }
}
