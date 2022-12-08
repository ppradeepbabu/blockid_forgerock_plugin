# **BlockID Passwordless Login Authentication:**

The BlockID Passwordless Authentication tree let Administrators and End Users to login passwordless to ForgeRock Access manager administrative console OR any applications protected by Access manager. Passwordless login will include 
  a) Scanning a QR code rendered by 1Kosmos BlockID platform 
  b) Providing biometric authentication (FaceID, LiveID) on BlockID Mobile App and consent

## **Usage:**
To enable BlockID Passwordless authentication
* Download the code from the github (URL: https://github.com/1KBlockID/java_forgerock_plugin) 
* Please obtain a license and tenant details from 1Kosmos sales team (<sales@1kosmos.com>) 
* Update the below properties in UWL2REST/src/main/resources/application.properties with your tenant & license details
  
  uwl.dns.url=<<to_be_substituted>>
  
  uwl.license.key=<<to_be_substituted>>
  
  uwl.community.name=<<to_be_substituted>>
  
* Compile and deploy the UWL2REST springboot application in any of the application container
* Please validate the UWL2REST sprint boot application status by performing GET operation on UWL2REST/healthcheck REST endpoint through a REST Client (such as POSTMAN)
* Login to ForegeRock Access Manager administrative console
* Create a "Decision node script for authentication trees" type javascript under Realm --> Scripts section
* Modify "BlockID Passwordless Login Scripted Decision Node.txt" contents with UW2REST_HOST_NAME & UW2REST_PORT and place the updated contents in the "Decision node script for authentication trees" script
* Create a passwordless Authentication tree 
  ### **Example Flow:**

  ![image](https://user-images.githubusercontent.com/114595779/205929301-3414a1c0-38dd-4385-b4f3-dff6f5499a7a.png)

* Use the created "Decision node script for authentication trees" script for scripted decision node. Add "true" and "false" as scripted decision outcomes

  ![image](https://user-images.githubusercontent.com/114595779/206332002-7b5d3349-adad-48f4-b2c9-181ab2744148.png)

* Save the Authentication tree and invoke the authenticaion tree URL
