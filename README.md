# basic-gui

A lightweight GUI framework for Java applications.

## Overview
`basic-gui` provides a set of streamlined utilities for window management and widget handling. It was developed to abstract complex GUI operations, allowing developers to focus on application logic rather than boilerplate implementation.

## History
This library originated as a core component of the currently proprietary `phantom-slicer` project. As the project grew, the need to separate the graphical interface from the core API became apparent. `basic-gui` was extracted from the main codebase to provide a stable, independent API that can be utilized across various projects, ensuring a clean separation of concerns and improved modularity.

## Features
* **Streamlined Window Management:** Initialize and configure fully customizable windows in a single line of code.
* **Intuitive Widget API:** Construct GUI instances with declarative widget registration directly in the constructor.
* **Flexible Component Architecture:** Extend custom base components by overriding lifecycle methods (render, update, input events). Support for nested child widgets enables modular UI design and code reusability.
* **Global Theming & Styling:** Manage and access application-wide fonts and color themes from a centralized configuration.
* **Dynamic Localization:** Easily manage multi-language support via external JSON files with seamless runtime switching.
* **Global Input Handling:** Define and manage application-wide key bindings.
* **Built-in Utility Suite:** Includes easy-to-trigger alert dialogs, direct window screenshot capabilities, and support for global UI overlays (e.g., loading screens).
* **Developer-Friendly Factories:** Utilize intuitive factory methods for rapid creation of complex widgets.

## Usage
To use `basic-gui` in your project via JitPack, follow the instructions for your build tool:

### Maven
Add the repository and dependency to your `pom.xml`:
```XML
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.isoyigido</groupId>
        <artifactId>basic-gui</artifactId>
        <version>RELEASE_TAG</version>
    </dependency>
</dependencies>
```

### Gradle
Add the JitPack repository to your `settings.gradle` (or `build.gradle` for older projects) and the dependency to your `build.gradle`:

In `settings.gradle`:
```Gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

In `build.gradle`:
```Gradle
dependencies {
    implementation 'com.github.isoyigido:basic-gui:RELEASE_TAG'
}
```

*Note: Replace RELEASE_TAG with a specific version (e.g., v1.0.0), a branch name followed by -SNAPSHOT (e.g., main-SNAPSHOT), or a specific commit hash.*

## Author
* **isoyigido** - [GitHub Profile](https://github.com/isoyigido)

## License
This project is licensed under the [Apache License 2.0](LICENSE).