# Scalar IST execution procedure
To run Scalar IST, you need to do the following:

Create Holder ID, private key, and certificate of the contract executor
- Register the contract executor's certificate with Scalar DLT
- Register the contract and function used by the contract executor in Scalar DLT.
- Generate a digital signature of request data at the time of contract execution using the contract executor's private key
- Send request data and digital signature to Scalar DLT and execute a contract

## Scalar IST user story
In IST, there are two types of business operators, system operation business operators and personal information handling business operators, and the system operating business operator has the authority to register only one business operator and to register the personal information handling business operator. A business operator handling personal information is a business operator that collects, uses, and provides personal information, creates consent documents for collecting personal information, and manages consent records.

In IST, register the business operator and user profile in the following order:

- Register the system operator who operates the system, and register the user profiles of the system administrator and system operator who belong to the system operator.
- Registration of personal information handling business operator using the system, registration of personal information handling business operator administrator, information manager, information processor user profile
- A user of a business operator handling personal information registers master items (purpose of use, dataset schema, data retention policy, benefits, third party providers) and consent documents.

## User story

### Register system operator
- Register the operator information and organization information of the system operator
- Register user profile information of system administrator and system operator

### Register a business operator handling personal information
- Register business information and business management organization information

### Register the user profile information of the user of the business operator handling personal information
- Register administrator user profile information
- Register user profile information of information processor

### Register the master information of the consent document

- Register and update the purpose of use
- Register and renew the expiration date
- Register and update third-party providers that use data
- Register and update dataset schema
- Register and renew benefits

### Registration and update of a consent document

- Register consent document
- Revised consent document (changes that do not require re-consent)
- Revised consent document (changes that require re-consent)

### Update of business information

- Update user's organization / role
- Addition / update of organization information

### Record of consent by the data subject

- The data subject records consent and refusal to the consent document
- Data subject gets its consent status
- The user of the business obtains the consent status for the consent document of the business.

## Run a user story with a deploy tool
The IST project that is being used is [scarlar-ist-internal](https://github.com/scalar-labs/scalar-ist-internal).  
To use IST you need to run [scalardl](https://github.com/scalar-labs/scalardl/blob/master/docs/installation-with-docker.md).  
The next steps have been tested with this docker-compose from scalardl:
``` 
version: "3.5"
services:
  cassandra:
    image: cassandra:3.11
    container_name: "scalardl-samples-cassandra-1"
    volumes:
      - cassandra-data:/var/lib/cassandra
    ports:
    #   - "7199:7199" # JMX
    #   - "7000:7000" # cluster communication
    #   - "7001:7001" # cluster communication (SSL)
       - "9042:9042" # native protocol clients
    #   - "9160:9160" # thrift clients
    environment:
      - CASSANDRA_DC=dc1
      - CASSANDRA_ENDPOINT_SNITCH=GossipingPropertyFileSnitch
    networks:
      - scalar-network
  scalardl-ledger-schema-loader-cassandra:
    image: ghcr.io/scalar-labs/scalardl-schema-loader:1.2.0
    command:
      - "--cassandra"
      - "-h"
      - "cassandra"
      - "-R"
      - "1"
    networks:
      - scalar-network
    restart: on-failure
  scalar-ledger:
    image: ghcr.io/scalar-labs/scalar-ledger:2.0.7
    container_name: "scalardl-samples-scalar-ledger-1"
    volumes:
      - ./fixture/ledger-key.pem:/scalar/ledger-key.pem
    depends_on:
      - cassandra
    environment:
      - SCALAR_DB_CONTACT_POINTS=cassandra
      - SCALAR_DB_STORAGE=cassandra
      - SCALAR_DL_LEDGER_PROOF_ENABLED=true
      - SCALAR_DL_LEDGER_PROOF_PRIVATE_KEY_PATH=/scalar/ledger-key.pem
    networks:
      - scalar-network
    # Overriding the CMD instruction in the scalar-ledger Dockerfile to add the -wait option.
    command: |
      dockerize -template ledger.properties.tmpl:ledger.properties
      -template log4j.properties.tmpl:log4j.properties
      -wait tcp://cassandra:9042 -timeout 60s
      ./bin/scalar-ledger --config ledger.properties
  ledger-envoy:
    image: envoyproxy/envoy:v1.12.7
    container_name: "scalardl-samples-ledger-envoy-1"
    ports:
      - "9901:9901"
      - "50051:50051"
      - "50052:50052"
    volumes:
      - ./envoy.yaml:/etc/envoy/envoy.yaml
    depends_on:
      - scalar-ledger
    command: /usr/local/bin/envoy -c /etc/envoy/envoy.yaml
    networks:
      - scalar-network

volumes:
  cassandra-data:
networks:
  scalar-network:
    name: scalar-network
```


### Build deploy tool
```
cd tools/deploy_tool
./gradlew installDist
```

### Register shared functions for use in IST
You will first need to build the contract and functions
```
cd ../../contracts_and_functions
./gradlew build
cd ../tools/deploy_tool
```
For now `./gradlew installDist` do not set function and contract path properly.  
So you will need to move the directories in the ist folder.

``` 
mv ../../contracts_and_functions/build/classes/java/main/com/scalar/ist/common build/classes/java/main/com/scalar/ist
mv ../../contracts_and_functions/build/classes/java/main/com/scalar/ist/contract build/classes/java/main/com/scalar/ist
mv ../../contracts_and_functions/build/classes/java/main/com/scalar/ist/function build/classes/java/main/com/scalar/ist
```

Then run

```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/functions.json
```

## Register information of system operator and personal information handling operator

### Register system operator

```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/initialize.json
```

### Registration of business operator handling personal information

You will need to register using the `schema.cql`.
After this, you can register the company.

```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_company.json
```

### Register the user profile information of the business operator handling personal information

You first need an admin profile, and then you can register the user profile.
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_user_profile_admin.json
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_user_profile_controller.json
```

### Register the master information of the consent document

Register the purpose of use
``` 
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_purpose.json
```

Update the purpose of use  
``` 
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_purpose.json
```

Register the dataset schema
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_data_set_schema.json
```

Update the dataset schema
``` 
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_data_set_schema.json
```

Register a third-party provider
``` 
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_third_party.json
```

Update third party providers
``` 
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_third_party.json
```

Register suspension of use and data deletion deadline
``` 
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_data_retention_policy.json
```

Suspension of use, update data deletion deadline
``` 
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_data_retention_policy.json
```

Register benefits
``` 
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_benefit.json
```

Update benefits
``` 
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_benefit.json
```

### Registration and update of a consent document

Register the consent document
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_consent_statement.json
```

Amend the consent document (changes that do not require re-consent)
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_consent_statement_revision.json
```

Revise the consent document (changes that require re-consent)
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_consent_statement_version.json
```

Change the status of the consent document 
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_consent_statement_status.json
```

### Update of business information
Update of the organization to which the operator user belongs
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_company.json
```

Update the role of the operator user
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_user_profile_controller_add_processor.json
```

Addition / update of organization information
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_organization.json
```

### Record of consent by the data subject

Registration of consent
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_consent_status_register.json
```

Renewal of consent
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_consent_status_update.json
```

Reference of consent status by a data subject
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/get_consent_status_data_subject.json
```

Reference of the status of consent by the business user 
```
build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/get_consent_status_controller.json
```

