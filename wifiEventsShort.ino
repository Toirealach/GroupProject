#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

#ifdef ESP32
#pragma message(THIS EXAMPLE IS FOR ESP8266 ONLY!)
#error Select ESP8266 board.
#endif

ESP8266WebServer server(80); // 80 is the port number

const char* ssid = "muh phone";
const char* password = "12345678";


String redLedon, blueLedon,greenLedon, ledOff;

void RedLedon()
{ 
  digitalWrite(D3, HIGH);  
  digitalWrite(D5, LOW);  
  digitalWrite(D6, LOW);  
  
  analogWrite(D3, 255); 
  server.send(200, "text/html", redLedon);
}

void BlueLedon()
{ 
 // int blueValue; 
 // blueValue = 255;
  digitalWrite(D3, LOW);  
  digitalWrite(D5, HIGH);  
  digitalWrite(D6, LOW);
    
  analogWrite(D5, 255);
  server.send(200, "text/html", blueLedon);
}

void GreenLedon()
{ 
 // int greenValue; 
  //greenValue = 255; 
  digitalWrite(D3, LOW);  
  digitalWrite(D5, LOW);  
  digitalWrite(D6, HIGH); 
  
  analogWrite(D6, 255);
  server.send(200, "text/html", greenLedon);
}

void LedOff()
{  
  digitalWrite(D3, LOW);
  digitalWrite(D5, LOW);
  digitalWrite(D6, LOW);
  server.send(200, "text/html", ledOff);
}



void setup() {

  Serial.begin(115200);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED)delay(500);

  Serial.print(WiFi.localIP());

  server.on("/redledon", RedLedon);
  server.on("/blueledon", BlueLedon);
  server.on("/greenledon", GreenLedon); 
  
  server.on("/ledoff", LedOff);
 

  server.begin();

  pinMode(D3, OUTPUT); 
  pinMode(D5, OUTPUT); 
  pinMode(D6, OUTPUT);

  digitalWrite(D3, HIGH); 
  digitalWrite(D5, HIGH); 
  digitalWrite(D6, HIGH);
  

}

void loop()
{
  server.handleClient();
  delay(1);
}
