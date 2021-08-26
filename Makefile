SHELL := /bin/bash

.PHONY: load-schema

SCHEMA_VERSION = 3.1.0

load-schema: bin/scalar-schema-standalone-${SCHEMA_VERSION}.jar
	@java -jar bin/scalar-schema-standalone-${SCHEMA_VERSION}.jar --cassandra -h localhost -u user -p pass -f fixtures/ist.transaction.json -R 1

bin/scalar-schema-standalone-${SCHEMA_VERSION}.jar:
	curl -L -o  bin/scalar-schema-standalone-${SCHEMA_VERSION}.jar https://github.com/scalar-labs/scalardb/releases/download/v${SCHEMA_VERSION}/scalar-schema-standalone-${SCHEMA_VERSION}.jar --create-dirs
