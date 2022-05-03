# JUMBF Reference software: Example

## Table of Contents

1. [Introduction](#intro)
2. [How to Deploy](#deployment)
3. [Syntax for JUMBF generation](#demo)

## Introduction <a name="intro"></a>

This is a demo application which offers graphical user interface (GUI) that allows the user to experiment with JUMBF structures. Specifically, a user has the ability to parse an existing JUMBF structure (stored in .jumbf extension). In addition, this application defines a JSON syntax which helps the user build a JUMBF file from scratch.

## Deploying <a name="deployment"></a>

### Deploying demo server as standalone application

To deploy the demo server as standalone application, follow the instructions in demo-server subdirectory.

### Deploying demo application using docker

In order to develop/deploy the application you simply need to download the following tools:

  * Docker
  * docker-compose

Follow the instructions for your operation system. 

In the docker subdirectory you may find the docker-compose.yml file that specifies the two services, namely demo-server and demo-client. 

To launch the application you need to execute the following command in the docker subdirectory:

```
docker-compose up
```

This will download the necessary docker images, build the images for demo-server and demo-client and launch the instances. Once all the services are up and running you can access the GUI application by opening a browser and visiting the following URL:

```
http://localhost:3001
```

## Syntax for JUMBF generation <a name="demo"></a>

This section focuses on providing some useful examples on how to use the GUI in order to build the desired JUMBF box file (extension .jumbf). Specifically, you may find examples of describing a JUMBF box in a way so that the demo application understands. Remember that in the cases where a file is referenced (e.g. in a JSON JUMBF Box there is a link reference to a .json file) it is required that you, first, upload the necessary files by following the instructions in the GUI. **Make sure that the name of the uploaded file is identical to the one specified in the JUMBF structure.**

#### (JPEG Systems Part 5: JUMBF): JSON box

The body of this request should be the following JSON document describing the JUMBF structure that we want to generate. Remember that in the "fileName" we specify the name of the file that we have already selected to upload via the GUI.

```
{
  "type": "jumb",
  "description": { 
    "type": "jumd", 
    "contentTypeUuid": "6A736F6E-0011-0010-8000-00AA00389B71", 
    "label": "JSON Content Type JUMBF box" 
  },
  "content": { 
    "type": "json", 
    "fileName":"test.json" 
  }	
}
```

The above JSON format describes a jumbf box with one description box (by definition) and one content box of type JSON. 

Provided that the request is well-formed, the POST request is a string corresponding to the path where the .jumbf file is stored.

#### (JPEG Systems Part 5: JUMBF): Multiple JUMBF boxes

Let's see a more complicated example where we can specify metadata consisting of three type of content boxes: A JSON, a XML and a Contiguous Codestream (Image file) Box. For this we need to define (and select to upload through the GUI) three files: test.xml, test.json and test.jpeg. Below you can see the Request body that needs to be sent in the same endpoint.

```
[
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentTypeUuid": "786D6C20-0011-0010-8000-00AA00389B71", "label": "XML Content Type JUMBF box" },
    "content": { "type": "xml", "fileName":"test.xml" }	
  },
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentTypeUuid": "6A736F6E-0011-0010-8000-00AA00389B71", "label": "JSON Content Type JUMBF box" },
    "content": { "type": "json", "fileName":"test.json" }	
  },
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentTypeUuid": "6579D6FB-DBA2-446B-B2AC-1B82FEEB89D1", "label": "Contiguous Codestream Content Type JUMBF box" },
    "content": { "type": "jp2c", "fileName":"test.jpeg" }	
  }
]
```

#### (JPEG Systems Part 5: JUMBF): UUID box

Example to define a UUID JUMBF Box is shown below (We need to provide the vendor-specific.obj file that contains the bytes that we want to include in the JUMBF box):

```
{
  "type": "jumb",
  "description": { 
    "type": "jumd", 
    "contentTypeUuid": "75756964-0011-0010-8000-00AA00389B71", 
    "label": "UUID Content Type JUMBF box" 
  },
  "content": { 
    "type": "uuid", 
    "uuid": "645ba7a8-b7f4-11ec-b909-0242ac120002", 
    "fileName": "vendor-specific.obj" 
  }
}
```

#### (JPEG Systems Part 5: JUMBF): Embedded File box

Example to define an Embedded File JUMBF Box is shown below. Regarding the fileName occurence in embeddedFileDescription box, this is simply a String specifying the file that is about to be embedded in the JUMBF structure. In addition, this specific example does not include the file itself JUMBF structure. Instead, it provides an example URL specifying where this file is located. According to the standard, this URL needs to be included in the JUMBF structure by providing the necessary flags (i.e. toggle) in the Embedded File Description Box:

```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentTypeUuid": "40CB0C32-BB8A-489D-A70B-2AD6F47F4369" },
  "content": {
    "embeddedFileDescription": {
      "type": "bfdb",
      "mediaType": "image/jpeg",
      "fileName": "test.jpeg",
      "fileExternallyReferenced": "true"
    },
    "content": {
      "type": "bidb",
      "fileName": "http://example.org/test.jpeg"
    }
  }
}
```

#### (JPEG Systems Part 4: Privacy & Security): Protection boxes
Firstly, let's generate a Protection Content Type JUMBF box containing the encrypted data that we have stored in the file "file.enc" which we assume that we have already created.

We can use the Rest Client of our preference and perform the following POST request:

```
http://localhost:8080/core/v1/generateBox
```

We can optionally specify the name of the file that we want the jumbf file to be stored:

```
http://localhost:8080/core/v1/generateBox?targetFile=test1.jumbf
```

The body of this request should be the following JSON document describing the JUMBF structure that we want to generate.

```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentTypeUuid": "74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3" },
  "content": {
    "protectionDescription": {
      "type": "pspd",
      "method": "aes-256-cbc-iv",
      "ivHexString": "D9BBA15016D876F67532FAFB8B851D24"
    },
    "content": { "type": "bidb", "fileName": "file.enc" }     
  }
}
```

The above JSON format describes a jumbf box with one description box (by definition) and one binary data box.

Provided that the request is well-formed, the POST request is a string corresponding to the path where the .jumbf file is stored.

Let's see a more complicated example where the encryption is defined externally to the protection box - using the "external" method which is supported. Notice that the protection box references the external JSON box using the label attribute.

The encryption information is assumed to be stored in the encryption-info.json file. An example structure of this file could be the following:

```
{
  "jpeg_security": {
    "type": "protection",
    "cipher": {
      "method": "AES256-GCM",
      "nonce": "BdZbHABY/sytDTUB",
      "aad": "ZmFzb28uY29t“,
      "tag": "1dsCuZ5XuanojwM/p6EoCA==“
      }
  }
}
```

The request body is:

```
[
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentTypeUuid": "74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3" },
    "content": {
      "protectionDescription": {
        "type": "pspd",
        "method": "external",
        "external-label": "json-encryption"
      },
      "content": { "type": "bidb", "fileName": "file.enc" }     
    }
  },
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentTypeUuid": "6A736F6E-0011-0010-8000-00AA00389B71", "label": "json-encryption" },
    "content": { "type": "json", "fileName":"encryption-info.json" }
  }
]
```

Additionally, we present a similar example where we include access rules (which are referenced externally) along with the encryption method aes-256-cbc-iv.

The access rules information is assumed to be stored in the policy.xml file. An example structure of this file could be the following:


```
[
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentTypeUuid": "74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3" },
    "content": {
      "protectionDescription": {
        "type": "pspd",
        "method": "aes-256-cbc-iv",
        "ivHexString": "D9BBA15016D876F67532FAFB8B851D24",
        "access-rules-label": "xaml-rules-box"
      },
      "content": { "type": "bidb", "fileName": "file.enc" }     
    }
  },
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentTypeUuid": "786D6C20-0011-0010-8000-00AA00389B71", "label": "xacml-rules-box" },
    "content":{ "type": "xml", "fileName":"policy.xml" }
  }
]
```

The result of such requests is similar to the following:

```
Jumbf file is stored in location Desktop/test.jumbf
The JUMBF content is the following:

ReplacementBox Content Type JUMBF box
```

#### (JPEG Systems Part 4: Privacy & Security): Replacement boxes

For this example we distinguish four cases, one for each type of replacement.

1. JUMBF Box Replacement

In this case we want to define the replacement of the referenced box specified in the Replacement Description Box with all the JUMBF boxes that we define in its contents.

In the first example we specify the referenced box using the offset.

```
{ 
  "type": "jumb", 
  "description": { "type": "jumd", "contentTypeUuid": "DC28B95F-B68A-498E-8064-0FCA845D6B0E", "label": "test" }, 
  "content": { 
    "replacementDescription": {
      "type": "psrd", 
      "replacementType": "box", 
      "auto-apply": false, 
      "offset": 123123123123 
    }, 
    "content": { 
      "type": "jumb", 
      "description": { "type": "jumd", "contentTypeUuid": "6579D6FB-DBA2-446B-B2AC-1B82FEEB89D1", "label": "Content which replaces the referenced box" }, 
      "content": { "type": "jp2c", "fileName":"file.enc" } 
    } 
  } 
}
```

In the second example we specify the referenced box using the label instead of the offset option.

```
{ 
  "type": "jumb", 
  "description": { "type": "jumd", "contentTypeUuid": "DC28B95F-B68A-498E-8064-0FCA845D6B0E", "label": "test" }, 
  "content": { 
    "replacementDescription": { 
      "type": "psrd", 
      "replacementType": "box", 
      "auto-apply": false, 
      "label": "reference-box" 
    }, 
    "content": { 
      "type": "jumb", 
      "description": { "type": "jumd", "contentTypeUuid": "6579D6FB-DBA2-446B-B2AC-1B82FEEB89D1", "label": "Content which replaces the referenced box" }, 
      "content": { "type": "jp2c", "fileName":"file.enc" } 
    } 
  } 
}
```

In the third example we specify the referenced box using the label and we provide multiple boxes to replace it.

```
{ 
  "type": "jumb", 
  "description": { "type": "jumd", "contentTypeUuid": "DC28B95F-B68A-498E-8064-0FCA845D6B0E", "label": "test" }, 
  "content": { 
    "replacementDescription": { 
      "type": "psrd", 
      "replacementType": "box", 
      "auto-apply": false, 
      "label": "reference-box" 
    }, 
    "content": [
      { 
        "type": "jumb", 
        "description": { "type": "jumd", "contentTypeUuid": "6579D6FB-DBA2-446B-B2AC-1B82FEEB89D1", "label": "One of the content which replaces the referenced box" }, 
        "content": { "type": "jp2c", "fileName":"file.enc" } 
      },
      { 
        "type": "jumb", 
        "description": { "type": "jumd", "contentTypeUuid": "786D6C20-0011-0010-8000-00AA00389B71", "label": "One of the content which replaces the referenced box" }, 
        "content": { "type": "xml", "fileName":"test.xml" } 
      }
    ]  
  } 
}
```

2. APP Segment Replacement

In this case we want to define the replacement of a APP segment using the contents of a Binary Data Box.

```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentTypeUuid": "DC28B95F-B68A-498E-8064-0FCA845D6B0E", "label": "test" },
  "content": {
    "replacementDescription": 
    {
      "type": "psrd",
      "replacementType": "app",
      "auto-apply": false,
      "offset": 123123123123
  	},
    "content": { "type": "bidb", "fileName":"file.dec" }
  }
}
```

3. ROI Replacement

In this case we want to define the replacement of a image's Region Of Interest (ROI) segment using the contents of a Codestream Box.

```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentTypeUuid": "DC28B95F-B68A-498E-8064-0FCA845D6B0E", "label": "test" },
  "content": {
    "replacementDescription": 
    {
      "type": "psrd",
      "replacementType": "roi",
      "auto-apply": false,
      "offset-X": 12232,
      "offset-Y": 21312
  	},
    "content": { "type": "jp2c", "fileName":"file.dec" }
  }
}
```

4. File Replacement

In this case we want to define the replacement of a whole image with the contents of a Codestream Box.

```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentTypeUuid": "DC28B95F-B68A-498E-8064-0FCA845D6B0E", "label": "test" },
  "content": {
    "replacementDescription": 
    {
      "type": "psrd",
      "replacementType": "file",
      "auto-apply": false
  	},
    "content": { "type": "jp2c", "fileName":"file.dec" }
  }
}
```

