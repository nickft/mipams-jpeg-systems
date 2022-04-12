![coverage](../.github/badges/jacoco-privsec.svg)
![branches coverage](../.github/badges/branches-privsec.svg)

# JUMBF Reference software: Privacy & Security submodule

## Table of Contents

1. [Introduction](#intro)
2. [How to Deploy](#deployment)
3. [Demo](#demo)


## Introduction <a name="intro"></a>

This submodule implements the JUMBF boxes as defined in JPEG systems — Part 4: Privacy and security standard

## Deploying <a name="deployment"></a>

In the src/main/resources/application.parameters you may find the parameters that can be configured for the core application. It is important to specify the path where the images and jumbf files (files with extension .jumbf) should be stored. For documentation purposes below you may find all the parameters used for the demo:

``` 
spring.main.allow-circular-references=true

logging.level.org.mipams.jumbf.core=INFO
logging.level.org.mipams.jumbf.privacy_security=DEBUG 

# Maximum size per file uploaded: 50 MB
org.mipams.core.max_file_size_in_bytes=52428800

org.mipams.core.image_folder=/home/nikos/Desktop

```
To Launch the application execute the following command:

```
java -jar target/jumbf-privsec-0.0.1-SNAPSHOT.jar
```

Providing that all the steps were executed with no error, the following message should be displayed in the terminal:

```
org.mipams.jumbf.core.JumbfApplication       : Privacy & Security module is up and running
```

The application runs on port 8080.

## Demo <a name="demo"></a>

Now that the application is up and running, let's use the Rest API to test our functionality. 

### Generate a JUMBF file

#### Protection boxes
Firstly, let's generate a Protection Content Type JUMBF box containing the encrypted data that we have stored in the file "file.enc" which we assume that we have already created.

We can use the Rest Client of our preference and perform the following POST request:

```
http://localhost:8080/core/v1/generateBox
```

We can optionally specify the name of the file that we want the jumbf file to be stored:

```
http://localhost:8080/core/v1/generateBox?targetFile=test1.jumbf
```

The body of this request should be the following JSON document describing the JUMBF structure that we want to generate. Remember that in the "fileUrl" we need to specify the absolute path of our file.enc file.

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

Additionally, we present a similar example where we include access rules (which are referenced externally) along with the encryption method aes-256-cbc-iv.

The access rules information is assumed to be stored in the policy.xml file. An example structure of this file could be the following:


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
```

The result of such requests is similar to the following:

```
Jumbf file is stored in location /home/nikos/Desktop/test.jumbf
The JUMBF content is the following:

ReplacementBox Content Type JUMBF box
```

#### Replacement boxes

For this example we distinguish four cases, one for each type of replacement.

1. JUMBF Box Replacement

In this case we want to define the replacement of the referenced box specified in the Replacement Description Box with all the JUMBF boxes that we define in its contents.

In the first example we specify the referenced box using the offset.

```
{ 
  "type": "jumb", 
  "description": { "type": "jumd", "contentType": "rplc", "label": "test" }, 
  "content": { 
    "replacementDescription": {
      "type": "psrd", 
      "replacementType": "box", 
      "auto-apply": false, 
      "offset": 123123123123 
    }, 
    "content": { 
      "type": "jumb", 
      "description": { "type": "jumd", "contentType": "jp2c", "label": "Content which replaces the referenced box" }, 
      "content": { "type": "jp2c", "fileUrl":"/home/nikos/file.enc" } 
    } 
  } 
}
```

In the second example we specify the referenced box using the label instead of the offset option.

```
{ 
  "type": "jumb", 
  "description": { "type": "jumd", "contentType": "rplc", "label": "test" }, 
  "content": { 
    "replacementDescription": { 
      "type": "psrd", 
      "replacementType": "box", 
      "auto-apply": false, 
      "label": "reference-box" 
    }, 
    "content": { 
      "type": "jumb", 
      "description": { "type": "jumd", "contentType": "jp2c", "label": "Content which replaces the referenced box" }, 
      "content": { "type": "jp2c", "fileUrl":"/home/nikos/file.enc" } 
    } 
  } 
}
```

In the third example we specify the referenced box using the label and we provide multiple boxes to replace it.

```
{ 
  "type": "jumb", 
  "description": { "type": "jumd", "contentType": "rplc", "label": "test" }, 
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
        "description": { "type": "jumd", "contentType": "jp2c", "label": "One of the content which replaces the referenced box" }, 
        "content": { "type": "jp2c", "fileUrl":"/home/nikos/file.enc" } 
      },
      { 
        "type": "jumb", 
        "description": { "type": "jumd", "contentType": "xml", "label": "One of the content which replaces the referenced box" }, 
        "content": { "type": "xml", "fileUrl":"/home/nikos/test.xml" } 
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

3. ROI Replacement

In this case we want to define the replacement of a image's Region Of Interest (ROI) segment using the contents of a Codestream Box.

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

4. File Replacement

In this case we want to define the replacement of a whole image with the contents of a Codestream Box.

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


### Parse a JUBMF file

The way to parse a JUMBF box from a .jumbf file is identical to the one presented in the code module.