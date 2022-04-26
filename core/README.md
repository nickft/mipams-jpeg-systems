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