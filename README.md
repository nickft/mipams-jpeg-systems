# Demo JUMBF application

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

## Installation <a name="requirements"></a>

The demo was developped using the following tools:

* Java 11
* Apache Maven 3.6.3
* Spring boot 2.6.4

**Note:** It is useful to have a REST Client to test the REST endpoints.

## Deploying <a name="deployment"></a>

First let's specify some application parameters. In the src/main/resources/application.parameters you may find the parameters that can be configured for the demo. It is important to specify the path where the images and jumbf files (files with extension .jumbf) should be stored. For documentation purposes below you may find all the parameters used for the demo:

``` 
 spring.main.allow-circular-references=true
logging.level.com.mipams.jumbf=DEBUG 
# Maximum size per file uploaded: 50 MB
mipams.core.max_file_size_in_bytes=52428800
mipams.core.image_folder=/home/nikos/Desktop
```

Now, let's compile the application. In the application's home directory run the following command:

```
mvn clean package
```

This will produce a tar directory with the .jar containing our compiled application. Now, we are ready to run our application:

```
java -jar target/jumbf-0.0.1-SNAPSHOT.jar
```

Providing that all the steps were executed with no error, the following message should be displayed in the terminal:

```
com.mipams.jumbf.JumbfApplication        : Application is up and running
```

The application runs on port 8080.

## Demo <a name="demo"></a>

Now that the application is up and running, let's use the Rest API to test our functionality. 

## Generate a JUMBF file
Firstly, let's request the generation of a JUMBF file containing our metadata which is the JSON {"test-label": "test-value" } . We can use the Rest Client of our reference and perform a POST request in the following URL:

```
http://localhost:8080/core/v1/generateBox
```

The body of this request should be the following JSON document describing the JUMBF structure that we want to generate:

```
{
    "type": "jumb",
    "description":
    {
        "type": "json",
        "label": "This is an example JUMBF metadata format"
    },
    "contentList":
    [
        {
            "type": "json",
            "payload": {"test":1}
        }
    ]
}
```

The above JSON format describes a jumbf box with one description box (by definition) and one content box of type JSON. 

Provided that the request is well-formed, the POST request is a string  corresponding to the path where the .jumbf file is stored.

Let's see a more complicated example where we can specify metadata consisting of two type of content boxes: A JSON and a Contiguous Codestream (Image file) Box. Below you can see the Request body that needs to be sent through the aforementioned URI:

```
{
    "type": "jumb",
    "description": {
        "type": "jumb",
        "label": "Superbox containing two children boxes"
    },
    "contentList": [
        {
            "type": "jumb",
            "description": {"type":"json","label":"Box containing the JSON metadata"},
            "contentList": [{"type":"json","payload":{"test":1}}]
        },
        {
            "type": "jumb",
            "description": {"type":"jp2c","label":"Box containing the Contiguous Codestream metadata"},
            "contentList": [{"type":"jp2c","path":"/home/nikos/Desktop/test.jpeg"}]
        }
    ]
}
```

## Parse a JUBMF file
Now that we have generated one JUMBF file, let's parse it and see its contents. For this, we need to execute the following GET request in the URL:

```
http://localhost:8080/core/v1/parseMetadata?path=/home/nikos/Desktop/test.jumbf
```

Provided that the JUMBF file is well-formed, the GET response shall be a brief string describing the structure of the parsed file. An example of this description is depicted below:

```

```

## Application structure and terminology <a name="intro"></a>

The whole JUMBF application (not only the one that appears on the demo) could be logically separated in independent layers each of whom is responsible for a specific application over JUMBF metadata. All these layers are strongly dependent on **core layer** which implements the basic JUMBF box definitions. 

In this design, the two main abstractions of a layer are the *entities* and the *services*. Each service is mapped to a specific entitity. An **entitity** describes the structure (i.e. fields) of its corresponding Box definition while a **service** defines all the necessary functionalities that need to be performed in this specific box. The following UML diagram describes the relationship of the services that need to be defined in the core module.

![Alt text](diagram.png "UML Diagram")

The *XTBoxService* contains all the functionalities related to parsing JPEG XT Box headers. Notice that it's defined as an abstract class meaning that there is no way that a box object can be solely a XTBoxService. Since each service extends the XTBoxService, these functionalities are inherited for any service in any layer which allows for code reusability.

## Implementing a BoxService
A box is considered as a plain object and all the functionalities are described in each corresponsing service. A service is implemented as a Spring Bean. This is extremely useful as it allows as to easily discover the correct BoxService class that needs to be called once the functionality at a specific level has finished. 
