#include <CurieBLE.h>
#include "CurieIMU.h"
#include <SoftwareSerial.h>

BLEPeripheral blePeripheral;       // BLE Peripheral Device (the board you're programming)
SoftwareSerial mySerial(3,2); // pin 2 = TX, pin 3 = RX (unused)

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
char bpmString[10];
char inbyte; 
int temp, rpm;
char tempstring[10], rpmstring[10], alertString[10];; // create string arrays


void setup() {
  mySerial.begin(9600);
  Serial.begin(9600);
  delay(500);// initialize serial communication
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
  mySerial.write(254); // cursor to beginning of first line
  mySerial.write(128);
  mySerial.write("BPM:            "); // clear display + legends
  mySerial.write("ALERT:          ");
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

          if (Serial.available()) {
    // wait a bit for the entire message to arrive
    delay(100);
 
    //store the first character in var inbyte
    inbyte = Serial.read();
    //if it is * then we know to expect text input from Android
    if (inbyte == '*')
    {
      //clear lcd screen
  //    mySerial.clear();
      mySerial.write(254);
      mySerial.write(198);
      mySerial.write("          ");
      int i = 0;
      while (Serial.available() > 0 & i < 9)
      {
        alertString[i]=Serial.read();
        i=i+1;
      }
      mySerial.write(254);
      mySerial.write(198);
      mySerial.write(alertString);
    }
   }

   
      long currentMillis = millis();
      // if 200ms have passed, check the heart rate measurement:
      if (currentMillis - previousMillis >= 1000) {
        previousMillis = currentMillis;
        updateHeartRate();
        CurieIMU.readGyroScaled(x, y, z);
        
      if ((x > 375  || x < -375)&&(px > 375  || px < -375) || ((y > 375 || y < -375)&& (py > 375 || py < -375))|| ((z > 375 || z < -375)&&(pz > 375 || pz < -375))){
        Serial.print("Fall Detected");
        Serial.print("\n");
      }
      else if((x > 100  || x < -100)&&(px > 100  || px < -100) || ((y > 100 || y < -100)&& (py > 100 || py < -100))|| ((z > 100 || z < -100)&&(pz > 100 || pz < -100))){
        Serial.print("Active");
         const unsigned char fallCharArray[2] = { 0, char(px) };
         fallChar.setValue(fallCharArray, 2);  // and update a fall may have occurred
        Serial.print("\n");
      }
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
  int heartRateMeasurement = analogRead(A0);
  int heartRate = map(heartRateMeasurement, 0, 1023, 0, 100);
  Serial.print("Heart Rate is now: "); // print it
  Serial.println(heartRate);
  sprintf(bpmString,"%4d",heartRate); // create strings from the numbers
  mySerial.write(254); // cursor to 7th position on first line
  mySerial.write(134);
  mySerial.write(bpmString); // write out the RPM value
  const unsigned char heartRateCharArray[2] = { 0, (char)heartRate };
  heartRateChar.setValue(heartRateCharArray, 2);  // and update the heart rate measurement characteristic
}
