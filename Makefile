SHELL := /bin/bash

SCHEMA_VERSION = 3.1.0

#### Scalar DB
.PHONY: load-schema
load-schema:
	@if [  ! -f bin/scalar-schema-standalone-${SCHEMA_VERSION}.jar  ]; then\
		echo "Downloading Scalar DB schema loader...";\
    	curl -L -o  bin/scalar-schema-standalone-${SCHEMA_VERSION}.jar https://github.com/scalar-labs/scalardb/releases/download/v3.1.0/scalar-schema-standalone-${SCHEMA_VERSION}.jar --create-dirs;\
	fi
	@echo "Loading IST scalar DB schema ..."
	@java -jar bin/scalar-schema-standalone-${SCHEMA_VERSION}.jar --cassandra -h localhost -u user -p pass -f fixtures/ist.transaction.json -R 1



