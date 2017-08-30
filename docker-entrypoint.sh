#!/bin/bash

java -jar target/GrokConstructor-0.1.0-SNAPSHOT-standalone.jar -resetExtract -extractDirectory target/.extract -httpPort 8080 2>&1 | tee target/standalone.log

