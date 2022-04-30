# JUMBF Reference software: Example

## Table of Contents

1. [Introduction](#intro)
2. [How to Deploy](#deployment)

## Introduction <a name="intro"></a>

This submodule implements a demo application that demonstrates how to use the mipams-jumbf library. Specifically, this application defines endpoints that allows a user to parse and generate JUMBF boxes. In addition it provides a UI application to parse a JUMBF file.

## Deploying <a name="deployment"></a>

### Deploying demo server as standalone application

To deploy the demo server as standalone application, follow the instructions in demo-server subdirectory.

### Deploying UI application using docker

In order to develop/deploy the application you simply need to download the following tools:

  * Docker
  * docker-compose

Follow the instructions for your operation system. 

In the docker subdirectory you may find the docker-compose.yml file that specifies the two services, namely demo-server and demo-client. To launch the application you need to execute the following command in the docker subdirectory:

```
docker-compose up
```

This will download the necessary docker images, build the images for demo-server and demo-client and launch the instances. To access the UI application, open a browser and visit the following URL:

```
http://localhost:3001
```