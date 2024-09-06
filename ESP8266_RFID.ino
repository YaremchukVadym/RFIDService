#include <SPI.h>
#include <MFRC522.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

//************************************************************************
#define SS_PIN  4  // GPIO 4
#define RST_PIN 5  // GPIO 5
MFRC522 mfrc522(SS_PIN, RST_PIN); // Create MFRC522 instance.

#define SCREEN_WIDTH 128 // OLED display width, in pixels
#define SCREEN_HEIGHT 64 // OLED display height, in pixels
#define OLED_SDA 0 // GPIO0 for SDA
#define OLED_SCL 2 // GPIO2 for SCL
#define OLED_RESET -1 // Reset pin (not used)

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

//************************************************************************
/* Set these to your desired credentials. */
const char *ssid = "Your_SSID"; // Insert your Wi-Fi network SSID here
const char *password = "Your_Password"; // Insert your Wi-Fi network password here
const char* auth_url = "http://example.com/api/auth/signin"; // Insert your API URL for authentication here
const char* status_assign = "Assigned by ESP8266";
const char* status_update = "Updated by ESP8266";
const char* device_token  = "your_device_token"; // Insert your device token here
const char* username = "your_email@example.com"; // Insert your username or email here
const char* user_password = "your_password"; // Insert your password here


String OldCardID = "";
unsigned long previousMillis = 0;
String jwt_token = "";

//************************************************************************
void displayStatus(String message) {
  display.clearDisplay();
  display.setCursor(0, 0);
  display.println(message);
  display.display();
  Serial.println(message); // Log status to serial
}

void setup() {
  delay(1000);
  Serial.begin(115200);
  SPI.begin();  // Init SPI bus
  mfrc522.PCD_Init(); // Init MFRC522 card

  // Initialize OLED display
  Wire.begin(OLED_SDA, OLED_SCL);
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;); // halt
  }
  display.clearDisplay();
  display.setTextColor(WHITE);
  display.setTextSize(1);
  display.setCursor(0,0);
  display.println("Initializing...");
  display.display();

  connectToWiFi();
  authenticate();
}
//************************************************************************
void loop() {
  // check if there's a connection to Wi-Fi or not
  if (!WiFi.isConnected()) {
    connectToWiFi();    // Retry to connect to Wi-Fi
    authenticate();     // Retry authentication
  }
  displayStatus("Attach the Rfid tag"); // Inserted line
  delay(500);
  //---------------------------------------------
  if (millis() - previousMillis >= 4000) {
    previousMillis = millis();
    OldCardID = "";
  }
  delay(50);
  //---------------------------------------------
  // look for new card
  if (!mfrc522.PICC_IsNewCardPresent()) {
    return; // go to start of loop if there is no card present
  }
  // Select one of the cards
  if (!mfrc522.PICC_ReadCardSerial()) {
    return; // if read card serial(0) returns 1, the uid struct contains the ID of the read card.
  }
  String CardID = "";
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    CardID += String(mfrc522.uid.uidByte[i], HEX);
  }
  //---------------------------------------------
  if (CardID == OldCardID) {
    return;
  } else {
    OldCardID = CardID;
  }
  //---------------------------------------------
  Serial.print("Card detected, ID: ");
  displayStatus("Card ID: " + CardID);
  delay(1500);
  Serial.println(CardID);
  SendCardID(CardID);
  delay(1000);
}
//************************************************************************
void SendCardID(String Card_uid) {
  Serial.println("Sending the Card ID");
  if (WiFi.isConnected() && jwt_token != "") {
    WiFiClient client;
    HTTPClient http;
    http.begin(client, "http://192.168.0.107:8080/api/item/update"); // Update to new API endpoint
    http.addHeader("Content-Type", "application/json");
    http.addHeader("Authorization", jwt_token);

    DynamicJsonDocument doc(256);
    doc["rfTag"] = Card_uid;
    doc["rfReaderIdToken"] = device_token;
    doc["status"] = status_assign;

    String requestBody;
    serializeJson(doc, requestBody);

    int httpCode = http.POST(requestBody); // Send the request
    String payload = http.getString(); // Get the response payload

    Serial.print("HTTP Response code: ");
    Serial.println(httpCode);
    Serial.print("Response payload: ");
    Serial.println(payload);

    if (httpCode == 200) {
      displayStatus("RFid assigned");
      delay(2000);
      displayStatus("Information: " + payload);
      delay(4500);
    }
    
    if (httpCode == 400 || httpCode == 500 || httpCode == 401 || httpCode == 501) {
      // Handling error, retrying with different endpoint
      http.end();
      displayStatus("Status updating...");
      delay(2000);
      retrySendCardID(Card_uid);
    }

    http.end(); // Close connection
  } else {
    Serial.println("Failed to send card ID. No Wi-Fi or missing token.");
    displayStatus("Failed to send card ID. No Wi-Fi or missing token.");
  }
}
//************************************************************************
void retrySendCardID(String Card_uid) {
  Serial.println("Retrying to send Card ID with different endpoint");
  if (WiFi.isConnected() && jwt_token != "") {
    WiFiClient client;
    HTTPClient http;
    String retryUrl = "http://192.168.0.107:8080/api/item/" + Card_uid + "/update";
    http.begin(client, retryUrl);
    http.addHeader("Content-Type", "application/json");
    http.addHeader("Authorization", jwt_token);

    DynamicJsonDocument doc(256);
    doc["status"] = status_update;

    String requestBody;
    serializeJson(doc, requestBody);

    int httpCode = http.POST(requestBody); // Send the request
    String payload = http.getString(); // Get the response payload

    Serial.print("HTTP Response code on retry: ");
    Serial.println(httpCode);
    Serial.print("Response payload on retry: ");
    Serial.println(payload);
  
    if (httpCode == 200) {
      displayStatus("Status updated");
      delay(1000);
      displayStatus("Information: " + payload);
      delay(4500);
    }
        if (httpCode == 400 || httpCode == 500 || httpCode == 401 || httpCode == 501) {
      displayStatus("Unidentified");
      delay(1500);
      displayStatus("Try another tag");
      delay(1000);
    }

    http.end(); // Close connection
  } else {
    Serial.println("Failed to send card ID on retry. No Wi-Fi or missing token.");
    displayStatus("Failed to send");
    delay(1000);
  }
}
//************************************************************************
void connectToWiFi() {
  WiFi.mode(WIFI_OFF); // Prevents reconnection issue
  delay(1000);
  WiFi.mode(WIFI_STA);
  Serial.print("Connecting to WiFi...");
  displayStatus("Connecting to WiFi...");
  delay(1000);
  Serial.println(ssid);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("Connected to WiFi: " + String(ssid));
  displayStatus("Connected to WiFi"); 
  delay(1000);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}
//************************************************************************
void authenticate() {
  unsigned long lastAttemptTime = 0;
  const unsigned long retryInterval = 10000; // 10 seconds in milliseconds
  bool authenticated = false;

  while (!authenticated) {
    if (WiFi.isConnected()) {
      if (millis() - lastAttemptTime > retryInterval || lastAttemptTime == 0) {
        WiFiClient client;
        HTTPClient http;
        http.begin(client, auth_url);
        http.addHeader("Content-Type", "application/json");

        DynamicJsonDocument doc(256);
        doc["username"] = username;
        doc["password"] = user_password;

        String requestBody;
        serializeJson(doc, requestBody);

        Serial.println("Authenticating...");
        displayStatus("Authenticating...");
        delay(1000);
        int httpCode = http.POST(requestBody); // Send the request
        String payload = http.getString(); // Get the response payload

        Serial.print("HTTP Response code: ");
        Serial.println(httpCode);
        displayStatus("HTTP " + String(httpCode));
        delay(1000);
        Serial.print("Response payload: ");
        Serial.println(payload);

        if (httpCode == 200) {
          DynamicJsonDocument responseDoc(512);
          deserializeJson(responseDoc, payload);
          bool success = responseDoc["success"];
          if (success) {
            jwt_token = responseDoc["token"].as<String>();
            Serial.println("Authentication successful");
            displayStatus("Authenticated");
            delay(1500);
            Serial.println("Token: " + jwt_token);
            authenticated = true; // Break the loop on successful authentication
          } else {
            Serial.println("Authentication failed");
            displayStatus("Not authenticated");
            delay(1000);
          }
        } 
        
        else {
          Serial.println("Failed to authenticate");
          displayStatus("Not authenticated");
          delay(1000);
        }

        http.end(); // Close connection
        lastAttemptTime = millis();
      }
    } else {
      Serial.println("No Wi-Fi connection");
      displayStatus("No Wi-Fi connection");
      delay(1000);
    }

    if (!authenticated) {
      delay(1000); // Delay to prevent too rapid retry attempts that don't give time for user feedback or system response
    }
  }
}
