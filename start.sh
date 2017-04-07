#!/bin/bash

echo This is just a simple start script. Feel free to modify this according to your needs.

# we build this, so this works immediately - in a real start script you obviously wouldn't do that.
mvn clean install

# put temporary stuff into target - in a real start script you probably wouldn't do that either. :-)
java -jar target/GrokConstructor-0.1.0-SNAPSHOT-standalone.jar -resetExtract -extractDirectory target/.extract -httpPort 8080 2>&1 | tee target/standalone.log

