# What is this?

This repository contains a C/C++ parser based on the Eclipse CDT core library.
The parser uses the same indexer provided by the CDT project but in a stand-alone fashion and does not require the Eclipse IDE.

This library is meant as a building block for a code analysis library and a [SonarQube](http://www.sonarqube.org/) plugin.

# Special Dependency

This library requires at least version 5.11.0 of the [org.eclipse.cdt.core](http://eclipse.mirror.triple-it.nl/tools/cdt/releases/8.8.1/r/plugins/org.eclipse.cdt.core_5.11.0.201602051005.jar) library.

To install this library in your local maven repository:

```
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file \
	-Dfile=org.eclipse.cdt.core_5.11.0.201602051005.jar \
    -DgroupId=org.eclipse.cdt \
    -DartifactId=org.eclipse.cdt.core \
    -Dversion=5.11.0 \
    -Dpackaging=jar
```

# Contact

cpp-sensor@googlegroups.com
