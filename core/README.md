[![Tests](https://github.com/nickft/mipams-jumbf/actions/workflows/maven.yml/badge.svg)](https://github.com/nickft/mipams-jumbf/actions/workflows/maven.yml)
[![coverage](../.github/badges/jacoco-core.svg)](https://github.com/nickft/mipams-jumbf/actions/workflows/maven.yml)
[![branches coverage](../.github/badges/branches-core.svg)](https://github.com/nickft/mipams-jumbf/actions/workflows/maven.yml)

# JUMBF Reference software: Core submodule

## Table of Contents

1. [Introduction](#intro)
2. [How to Deploy](#deployment)
3. [Demo](#demo)


## Introduction <a name="intro"></a>

This submodule implements the JUMBF boxes as defined in JPEG systems — Part 5: JPEG Universal Metadata Box Format (JUMBF) standard.

## Deploying <a name="deployment"></a>

In the src/main/resources/application.parameters you may find the parameters that can be configured for the core application. It is important to specify the path where the images and jumbf files (files with extension .jumbf) should be stored. For documentation purposes below you may find all the parameters used for the demo:

``` 
spring.main.allow-circular-references=true

logging.level.org.mipams.jumbf.core=DEBUG 

# Maximum size per file uploaded: 50 MB
org.mipams.core.max_file_size_in_bytes=52428800

org.mipams.core.image_folder=/home/nikos/Desktop

```
To Launch the application execute the following command:

```
java -jar target/jumbf-core-0.0.1-SNAPSHOT-exec.jar
```

Providing that all the steps were executed with no error, the following message should be displayed in the terminal:

```
org.mipams.jumbf.core.JumbfApplication       : JUMBF core module is up and running
```

The application runs on port 8080.

## Demo <a name="demo"></a>

Now that the application is up and running, let's use the Rest API to test our functionality. 

### Generate a JUMBF file
Firstly, let's request the generation of a JUMBF file containing our metadata which is a test.json file. 
For this example we need to specify a test.json file with information in JSON format.

We can use the Rest Client of our preference and perform the following POST request:

```
http://localhost:8080/core/v1/generateBox
```

We can optionally specify the name of the file that we want the jumbf file to be stored:

```
http://localhost:8080/core/v1/generateBox?targetFile=test1.jumbf
```

The body of this request should be the following JSON document describing the JUMBF structure that we want to generate. Remember that in the "fileUrl" we need to specify the absolute path of our test.json file.

```
{
  "type": "jumb",
  "description": { 
    "type": "jumd", 
    "contentType": "json", 
    "label": "JSON Content Type JUMBF box" 
  },
  "content": { 
    "type": "json", 
    "fileUrl":"/home/nikos/test.json" 
  }	
}
```

The above JSON format describes a jumbf box with one description box (by definition) and one content box of type JSON. 

Provided that the request is well-formed, the POST request is a string corresponding to the path where the .jumbf file is stored.

Let's see a more complicated example where we can specify metadata consisting of three type of content boxes: A JSON, a XML and a Contiguous Codestream (Image file) Box. For this we need to define three files: test.xml, test.json and test.jpeg. Below you can see the Request body that needs to be sent in the same endpoint.

```
[
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentType": "xml", "label": "XML Content Type JUMBF box" },
    "content": { "type": "xml", "fileUrl":"/home/nikos/test.xml" }	
  },
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentType": "json", "label": "JSON Content Type JUMBF box" },
    "content": { "type": "json", "fileUrl":"/home/nikos/test.json" }	
  },
  {
    "type": "jumb",
    "description": { "type": "jumd", "contentType": "jp2c", "label": "Contiguous Codestream Content Type JUMBF box" },
    "content": { "type": "jp2c", "fileUrl":"/home/nikos/test.jpeg" }	
  }
]
```

Example to define a UUID JUMBF Box is shown below (We need to provide the vendor-specific.obj file that contains the bytes that we want to include in the JUMBF box):

```
{
  "type": "jumb",
  "description": { 
    "type": "jumd", 
    "contentType": "uuid", 
    "label": "UUID Content Type JUMBF box" 
  },
  "content": { 
    "type": "uuid", 
    "uuid": "645ba7a8-b7f4-11ec-b909-0242ac120002", 
    "fileUrl": "/home/nikos/vendor-specific.obj" 
  }
}
```

Example to define an Embedded File JUMBF Box is shown below:

```
{
  "type": "jumb",
  "description": { "type": "jumd", "contentType": "bfbd" },
  "content": {
    "embeddedFileDescription": {
      "type": "bfdb",
      "mediaType": "image/jpeg",
      "fileName": "test.jpeg",
      "fileExternallyReferenced": "true"
    },
    "content": {
      "type": "bidb",
      "fileUrl": "http://example.org/test.jpeg"
    }
  }
}
```

The result of the aforementioned example requests is similar to the following:

```
Jumbf file is stored in location /home/nikos/Desktop/test.jumbf
The JUMBF content is the following:

XmlBox Content Type JUMBF box
```

### Parse a JUBMF file
Now that we have generated JUMBF files, let's parse on of them and see its contents. For this, we need to execute the following GET request:

```
http://localhost:8080/core/v1/parseMetadata?path=/home/nikos/Desktop/test.jumbf
```

Provided that the JUMBF file is well-formed, the GET response shall be a brief string describing the structure of the parsed file. An example of this description is depicted below:

```
[
XmlBox Content Type JUMBF box 
]
```