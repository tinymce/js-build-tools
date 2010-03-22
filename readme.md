js-build-tools - A set of tools for building JS projects
==========================================================

What is js-build-tools
-----------------
This project contains a collection of Ant tasks useful for building JavaScript projects. 

What you need to build js-build-tools
--------------------------------------
* Install the Java JDK or JRE packages you can find it at: [http://java.sun.com/javase/downloads/index.jsp](http://java.sun.com/javase/downloads/index.jsp)
* Install Apache Ant you can find it at: [http://ant.apache.org/](http://ant.apache.org/)
* Add Apache Ant to your systems path environment variable, this is not required but makes it easier to issue commands to Ant without having to type the full path for it.

How to build js-build-tools
----------------------------

In the root directory of js-build-tools where the build.xml file is you can run ant against different targets.

`ant`

Will create a build directory containing the js_build_tools.jar file.

`ant release`

Will produce release packages. The release packages will be placed in the tmp directory.
