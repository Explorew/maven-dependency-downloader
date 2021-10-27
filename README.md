
Dependency Downloader for Maven Artifact
========================================= 

About 
----- 
Maven-dependency-downloader is a simple tool which allows users to download all 
dependencies of a given artifact.

Prerequisite 
----- 
Make sure Java (version 8 and higher) is installed on your machine.


Quick Start 
----- 
Move to the project directory.
 
Run the java codes from terminal by typing:
    

      $ mvn exec:java


Also, you can build a self-contained jar:

      $ mvn clean package

Then you can use the following command to run the jar file:

      $ java -jar target/MavenDependencyDownloader.jar


This tool can also be used in your own project. 
This can be done by calling the static method resolveDependencies
in DependencyResolver class. The method takes a target artifact 
as an input and return a list of dependency artifacts.

Documentation 
----- 

[Read the Java Doc](https://explorew.github.io/maven-dependency-downloader/)
