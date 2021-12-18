
Maven Dependency Downloader
========================================= 

![version](https://img.shields.io/badge/version-1.1.2-brightgreen)
![coverage](https://img.shields.io/badge/coverage-96%25-brightgreen)
![building](https://img.shields.io/badge/build-passing-brightgreen)
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
Move to the project directory.
 
Build a self-contained jar for this library:

      $ mvn clean package

To inject into your local maven repository (```~/.m2```):

      $ mvn install:install-file -Dfile=target/MavenDependencyDownloader.jar -DgroupId=yizhong.ding -DartifactId=maven-dependency-downloader -Dversion=1.1.2 -Dpackaging=jar -DcreateChecksum=true

Usage
-----

 * [Build this library](#quick-start) 
 * Add the library to your classpath
 * Call this library from your program:  
 
```java
// Artifact for which we want to collect all dependencies:
String groupId = "eu.kartoffelquadrat";
String artifactId = "asyncrestlib";
String version = "1.6.2";

// Where to store the dependencies (as JARs)
// tmpdir will be used if null or empty
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
