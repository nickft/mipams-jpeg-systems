# JUMBF Reference software: Example

## Table of Contents

1. [Introduction](#intro)
2. [Run demo server as a standalon application](#deployment)
3. [Demo](#demo)


## Introduction <a name="intro"></a>

This document provides a way to run the server of this application as a standalone REST Endpoint server without using docker.** For this deployment we assume that mvn and java 11 is already installed in your machine.**

## Deploying <a name="deployment"></a>

In the src/main/resources/application.parameters you may find the parameters that can be configured for the core application. It is important to specify the path where the images and jumbf files (files with extension .jumbf) should be stored. For documentation purposes below you may find all the parameters used for the demo:

``` 
spring.main.allow-circular-references=true

logging.level.org.mipams.jumbf.core=INFO
logging.level.org.mipams.jumbf.privacy_security=INFO 

# Maximum size per file uploaded: 50 MB
org.mipams.core.max_file_size_in_bytes=52428800

org.mipams.core.image_folder=/tmp

```

It is of paramount importance to specify a valid path to the **org.mipams.core.image_folder** parameter. In this directory you should put all the metadata files (e.g. json, xml, binary files) that you want to include in a JUMBF structure. In addition, this is the directory where the application stores the generated JUMBF files. Finally, upon parsing a .jumbf file, the embedded metadata files are extracted and stored in the path specified in this parameter.

First we need to compile the example application:

```
mvn clean package
```

To Launch the application execute the following command:

```
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

Providing that all the steps were executed with no error, the following message should be displayed in the terminal:

```
org.mipams.jumbf.core.DemoApplication       : Demo application is up and running
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
Remember that for the aforementioned request we need to specify the request body which is the syntax describing a JUMBF box strucure. For more information on how to express a JUMBF structure in a way that this application understands visit the README file in the example parent subdirectory.

### Parse a JUBMF file

Now that we have generated JUMBF files, let's parse on of them and see its contents. For this, we need to execute the following GET request:

```
http://localhost:8080/core/v1/parseMetadata?fileName=test.jumbf
```

Provided that the JUMBF file is well-formed, the GET response shall be a brief string describing the structure of the parsed file.