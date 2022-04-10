# Demo Privacy & Security application

## Table of Contents

1. [Introduction](#intro)
2. [Requirements](#requirements)
3. [How to Deploy](#deployment)
3. [Demo](#demo)
4. [Application structure and terminology](#spring)


## Introduction <a name="intro"></a>

This demo application proposes a design on how to parse metadata that are stored/structured following the JPEG Universal Metadata Box Format (JUMBF) defined in Part 5 JPEG Standard. Ideally, the goal of this design is to support the maintenance effort of the JUMBF functionalities as well as to provide an interface for additional modules(i.e. JLINK, Privacy and Security, Provenance) to extend these functionalities according to their application needs.

Specifically, the application provides two Rest endpoints that demonstrate the following operations:

1. Parse a file that stores JUMBF metadata wrapped with the binary structure (XTBox) proposed by JPEG-1 / JPEG XT. The GET response is a string containing information about the JUMBF structure.

2. Generate JUMBF file based on information provided in the POST request body. The POST response is a string showing the location where the JUMBF file is stored.

The goal of this demo is to create a proof of concept application which shows that this design could support the effort of creating a modular ecosystem as well as show how an additional application - i.e. provenance module - can extend the functionality implemented in the core JUMBF module. Regarding the provenance module, we show how a new box definition called "AssertionBox" can extend the functionality of a JUMBF box as described in the core layer. 

## Installation <a name="requirements"></a>

The demo was developped using the following tools:

* Java 11
* Apache Maven 3.6.3
* Spring boot 2.6.4

**Note:** It is useful to have a REST Client to test the REST endpoints.

## Deploying <a name="deployment"></a>

In the home directory of the project there are two directories core and provenance each one corresponding to a separate application. Provenance application is dependent on core application. Let's first focus on the core module. In the core/src/main/resources/application.parameters you may find the parameters that can be configured for the core application. It is important to specify the path where the images and jumbf files (files with extension .jumbf) should be stored. For documentation purposes below you may find all the parameters used for the demo:

``` 
spring.main.allow-circular-references=true

logging.level.org.mipams.jumbf.core=DEBUG 

# Maximum size per file uploaded: 50 MB
org.mipams.core.max_file_size_in_bytes=52428800

org.mipams.core.image_folder=/home/nikos/Desktop

```

Now, let's compile the application. In the application's home directory run the following command:

```
mvn clean package
```

This will produce the respective jars in each of the projects. In core/target directory we shall find two jar files. The first one is marked as exec which allows us to run the core module as a standalone application. The second jar is not executable and can be placed as a dependency to other modules. In provenance/target directory we shall find one executable jar file to run the provenance application. For the first part of the demo we will show the core functionalities. Thus, we launch the core module as a standalone application using the following command:

```
java -jar target/jumbf-privsec-0.0.1-SNAPSHOT.jar
```

Providing that all the steps were executed with no error, the following message should be displayed in the terminal:

```
org.mipams.jumbf.core.JumbfApplication       : PrivSec is up and running
```

The application runs on port 8080.

## Demo <a name="demo"></a>

Now that the application is up and running, let's use the Rest API to test our functionality. 

### Generate a JUMBF file
Firstly, let's request the generation of a JUMBF file containing our metadata which is the JSON {"test-label": "test-value" } . We can use the Rest Client of our reference and perform a POST request in the following URL:

```
http://localhost:8080/core/v1/generateBox
```

The body of this request should be the following JSON document describing the Protection box structure that we want to generate:

```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentType": "prtc" },
  "content": {
    "protectionDescription": {
      "type": "pspd",
      "method": "aes-256-cbc-iv",
      "ivHexString": "D9BBA15016D876F67532FAFB8B851D24"
    },
    "content": { "type": "bidb", "fileUrl": "/home/nikos/file.enc" }     
  }
}
```

Provided that the request is well-formed, the POST request is a string corresponding to the path where the .jumbf file is stored.

Let's see a more complicated example where the encryption is defined externally to the protection box - using the "external" method which is supported. Notice that the protection box references the external JSON box using the label attribute.

```
[
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentType": "prtc" },
    "content": {
      "protectionDescription": {
        "type": "pspd",
        "method": "external",
        "external-label": "json-encryption"
      },
      "content": { "type": "bidb", "fileUrl": "/home/nikos/file.enc" }     
    }
  },
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentType": "json", "label": "json-encryption" },
    "content": { "type": "json", "fileUrl":"/home/nikos/encryption-info.json" }
  }
]
```

Finally we shall see a similar example where we include access rules (which are referenced externally) along with the encryption method aes-256-cbc-iv.

```
[
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentType": "prtc" },
    "content": {
      "protectionDescription": {
        "type": "pspd",
        "method": "aes-256-cbc-iv",
        "ivHexString": "D9BBA15016D876F67532FAFB8B851D24",
        "access-rules-label": "xaml-rules-box"
      },
      "content": { "type": "bidb", "fileUrl": "/home/nikos/file.enc" }     
    }
  },
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentType": "xml", "label": "xacml-rules-box" },
    "content":{ "type": "xml", "fileUrl":"/home/nikos/policy.xml" }
  }
]

Examples using the Replacement Boxes

1. App Replacement Type

```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentType": "rplc", "label": "test" },
  "content": {
    "replacementDescription": 
    {
      "type": "psrd",
      "replacementType": "app",
      "auto-apply": false,
      "offset": 123123123123
  	},
    "content": { "type": "bidb", "fileUrl":"/home/nikos/file.dec" }
  }
}
```

2. Roi Replacement Type
```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentType": "rplc", "label": "test" },
  "content": {
    "replacementDescription": 
    {
      "type": "psrd",
      "replacementType": "roi",
      "auto-apply": false,
      "offset-X": 12232,
      "offset-Y": 21312
  	},
    "content": { "type": "jp2c", "fileUrl":"/home/nikos/file.dec" }
  }
}
```

3. File Replacement Type
```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentType": "rplc", "label": "test" },
  "content": {
    "replacementDescription": 
    {
      "type": "psrd",
      "replacementType": "file",
      "auto-apply": false
  	},
    "content": { "type": "jp2c", "fileUrl":"/home/nikos/file.dec" }
  }
}
```

4. Box Replacement Type
```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentType": "rplc", "label": "test" },
  "content": {
    "replacementDescription": 
    {
      "type": "psrd",
      "replacementType": "box",
      "auto-apply": false,
      "offset": 123123123123
  	},
    "content": {
      "type": "jumb",
      "description": { "type": "jumd", "contentType": "jp2c" },
      "content": { "type": "jp2c", "fileUrl":"/home/nikos/file.enc" }	
    }
  }
}
```

5. Box Replacement Type with label
```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentType": "rplc", "label": "test" },
  "content": {
    "replacementDescription": 
    {
      "type": "psrd",
      "replacementType": "box",
      "auto-apply": false,
      "offset": 18446744073709551615,
      "label": "reference-box"
  	},
    "content": {
      "type": "jumb",
      "description": { "type": "jumd", "contentType": "jp2c", "label": "The final reference" },
      "content": { "type": "jp2c", "fileUrl":"/home/nikos/file.enc" }	
    }
  }
}
```

6. Box Replacement Type with label and multiple Replacement Boxes

```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentType": "rplc", "label": "test" },
  "content": {
    "replacementDescription": 
    {
      "type": "psrd",
      "replacementType": "box",
      "auto-apply": false,
      "offset": 18446744073709551615,
      "label": "reference-box"
  	},
    "content": [
      {
        "type": "jumb",
        "description": { "type": "jumd", "contentType": "jp2c", "label": "The final reference" },
        "content": { "type": "jp2c", "fileUrl":"/home/nikos/file.enc"} 
      },
      {
        "type": "jumb",
        "description": { "type": "jumd", "contentType": "xml", "label": "The final reference" },
      	"content": { "type": "xml", "fileUrl":"/home/nikos/file.enc" }
      }	
    ]
  }
}
```
