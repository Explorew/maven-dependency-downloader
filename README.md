
Maven Dependency Downloader
========================================= 

![version](https://img.shields.io/badge/version-1.1.2-brightgreen)
![coverage](https://img.shields.io/badge/coverage-95%25-brightgreen)
![building](https://img.shields.io/badge/build-passing-brightgreen)
![testinging](https://img.shields.io/badge/testing-passing-brightgreen)
![okhttp3](https://img.shields.io/badge/OkHttp3-4.9.1-blue)
![tinylog](https://img.shields.io/badge/TinyLog-1.3.6-blue)
![jdom](https://img.shields.io/badge/JDom-2.0.2-blue)
![mockito](https://img.shields.io/badge/Mockito-3.8.0-blue)

About 
----- 
The *Maven Dependency Downloader* is a simple tool which allows users to download all 
transitive dependencies of a given artifact. 

Algorithm
----- 
This program utilizes a BFS traversal algorithm with the help of an extra queue.
In each iteration, it removes the first node from the queue, and appends all the transitive
dependencies to the queue.

Prerequisite 
----- 
Make sure Java (version 8 and higher) is installed on your machine.


Build Instructions 
----- 

Run the following command from the this project's root directory to build a self contained jar:
 
```bash
mvn clean install
```

 > Note: Using ```install``` instead of ```package``` creates an artifact entry in your local maven repository at ```~/.m2```.

Usage
-----

 * [Build this library](#quick-start) 
 * Add the library to your classpath.  
Either:
   * Use the self-contained JAR in ```target/MavenDependencyDownloader.jar```
   * Add the following dependency block to your project's ```pom.xml```:
```xml
<dependency>
    <groupId>yizhong.ding</groupId>
    <artifactId>mavendependencydownloader</artifactId>
    <version>1.1.2</version>
</dependency>
```
 * Invoke this library from your program:

```java
    // Artifact for which we want to collect all dependencies:
    String groupId = "eu.kartoffelquadrat";
    String artifactId = "asyncrestlib";
    String version = "1.6.2";
    
    // Where to store the dependencies (as JARs)
    // OS tmpdir will be used if targetLocation is null or empty
    String targetLocation = "/Users/schieder/Desktop/myDependencies";
    
    // Call library to collect all dependencies
    DependencyResolver.resolveArtifact(groupId, artifactId, version, targetLocation);
```

 > Check out the minimal [demo project](https://github.com/kartoffelquadrat/MavenDependencyDownloaderDemo).


Documentation 
----- 

 * [Please consult the JavaDoc for method details.](https://explorew.github.io/maven-dependency-downloader/)

Notice
----- 
The Equals method of Artifact Class only check if groupId and artifactId are equal.
Because maven does not pull the same artifact twice. If already collected (in a different version) it should
be considered covered, and not get re-downloaded.

 > Known issue: Currently the MDD can not properly handle artefacts with property controlled version overrides (with exception to spring boot, which is hard coded in [```Util.java```](src/main/java/yizhong/ding/mavendependencydownloader/Util.java)
