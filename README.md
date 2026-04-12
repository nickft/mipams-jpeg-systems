# MIPAMS JPEG Systems Reference Software

This repository provides a comprehensive Java reference implementation of the **JPEG Universal Metadata Box Format (JUMBF)** and all of its official extensions as defined in the **ISO/IEC 19566 series**.

This is a multi-module Maven project where each submodule implements a specific part of the JPEG Systems specification, relying on the core JUMBF data structures. 

---

## Overview

The SDK provides a complete toolkit to embed, parse, and manipulate structured metadata inside JPEG images. It is built using a **Spring-managed Java Bean architecture**, making it easy to integrate into modern Java applications via Dependency Injection.

### Key Features
* Complete coverage of all JUMBF data structures defined in the ISO/IEC 19566 series.
* Service-oriented architecture using Spring beans (`@Autowired`).
* **Thorough Test Suite**: Every submodule is backed by extensive automated tests covering generation, parsing, and schema validation.
* Extensible design for future JPEG Systems standards.

---

## Project Structure (Submodules)

Each component of the standard is isolated in its own module. Please refer to their individual README files for specific usage scenarios and code examples.

1. **[JUMBF Core (`/jumbf`)](./jumbf/README.md)** * *ISO/IEC 19566-5* - The foundational metadata box format used by all other modules.
2. **[Privacy & Security (`/privacy-security`)](./privacy-security/README.md)** * *ISO/IEC 19566-4* - Structures for metadata protection, encryption (AES-256), and replacement content.
3. **[JPEG Snack (`/jpeg-snack`)](./jpeg-snack/README.md)** * *ISO/IEC 19566-8* - Media composition, animations, and object instructions over time.
4. **[JLINK (`/jlink`)](./jlink/README.md)** * *ISO/IEC 19566-7* - RDF-backed interactive regions, navigational links, and scenes.
5. **[JPEG 360 (`/jpeg-360`)](./jpeg-360/README.md)** * *ISO/IEC 19566-6* - RDF-backed immersive 360-degree image metadata, viewports, and accelerated ROIs.

---

## Getting Started

### Requirements

* Java 21
* Apache Maven 3.9.6+
* Spring Framework 6.2.x

## Installation & Build

To use the MIPAMS JPEG Systems modules in your own Java project, you first need to build and install the repository to your local machine.

### 1. Build from Source

From within the root directory of this repository, run the following command to build all modules, run the test suite, and install the artifacts to your local Maven repository:

```bash
mvn clean install
```

### 2. Add as a Dependency

Once installed locally, you can include the core module or any specific extension in your project's build configuration. *(Note: Extension modules will automatically pull in the core `jumbf` module as a transitive dependency, so you usually only need to declare the specific extension you are using).*

#### Maven (`pom.xml`)

```xml
<dependency>
    <groupId>org.mipams</groupId>
    <artifactId>jumbf</artifactId>
    <version>YOUR_VERSION</version>
</dependency>

<dependency>
    <groupId>org.mipams</groupId>
    <artifactId>jpeg-snack</artifactId>
    <version>YOUR_VERSION</version>
</dependency>
```

#### Gradle (`build.gradle`)

*Ensure you have `mavenLocal()` included in your repositories block so Gradle can locate the locally built MIPAMS artifacts.*

```groovy
repositories {
    mavenCentral()
    mavenLocal() 
}

dependencies {
    // Core JUMBF module
    implementation 'org.mipams:jumbf:YOUR_VERSION'
    
    // Example: JLINK extension
    implementation 'org.mipams:jlink:YOUR_VERSION'
}
```

## Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch
3. Submit a pull request

---

## License

This project is licensed under the Apache License, Version 2.0. 

You may obtain a copy of the License at:
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.