# JUMBF Reference software: JLINK submodule

## Table of Contents

1. [Introduction](#intro)
2. [How to Deploy](#deployment)

## Introduction <a name="intro"></a>

This submodule implements the JUMBF boxes as defined in JPEG systems — Part 7: JLINK

## Deploying <a name="deployment"></a>

In the src/main/resources/application.parameters you may find the parameters that can be configured for the core application. It is important to specify the path where the images and jumbf files (files with extension .jumbf) should be stored. For documentation purposes below you may find all the parameters used for the demo:

``` 
spring.main.allow-circular-references=true

logging.level.org.mipams.jumbf.core=INFO
logging.level.org.mipams.jumbf.jlink=DEBUG 

# Maximum size per file uploaded: 50 MB
org.mipams.core.max_file_size_in_bytes=52428800

org.mipams.core.image_folder=/home/nikos/Desktop

```