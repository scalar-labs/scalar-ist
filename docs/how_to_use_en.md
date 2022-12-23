
# Prerequisites

## ScalarDL
Before you can use IST you first have to deploy ScalarDL.
You can quickly start a local installation for testing purposes via the following [docker-compose.yml](https://github.com/scalar-labs/scalar-ist/blob/main/fixture/scalardl/docker-compose.yml) file.
More information on how to get up and running with ScalarDL can be found [here](https://github.com/scalar-labs/scalardl/blob/master/docs/installation-with-docker.md).

## Create Scalar IST schema

Once ScalarDL is up and running, the [IST schema](../ist-schema-loader/schema/ist.transaction.json) needs to be installed on the running ScalarDL instance.

The schema can be installed either using the [docker IST schema loader](../ist-schema-loader/README.md) or via the ScalarDL docker schema loader as described in the ScalarDL documentation.
You can build the IST schema docker image yourself or use the pre-built image from `ghcr.io/scalar-labs/scalar-ist-schema-loader:<release-number>`.

## Set up Scalar DL Java Client SDK

Download the `scalardl-java-client-sdk` zip file from the [release](https://github.com/scalar-labs/scalardl-java-client-sdk/releases/tag/v3.5.3) to `scalar-ist`.
Then unzip and rename it to `scalardl-java-client-sdk`.
```console
wget -O ./scalardl-java-client-sdk.zip https://github.com/scalar-labs/scalardl-java-client-sdk/releases/download/v3.5.3/scalardl-java-client-sdk-3.5.3.zip
unzip scalardl-java-client-sdk.zip
mv scalardl-java-client-sdk-3.5.3 scalardl-java-client-sdk
```

# How to use Scalar IST?
To run Scalar IST, you need to do the following:

Create a Holder ID, private key, and certificate for the contract executor
- Register the contract executor's certificate with Scalar DLT
- Register the contract and function used by the contract executor in Scalar DLT.
- Generate a digital signature of request data at the time of contract execution using the contract executor's private key
- Send request data and digital signature to Scalar DLT and execute a contract

## Scalar IST user stories
In IST, there are two types of business operators, system operation business operators and personal information handling business operators, and the system operating business operator has the authority to register only one business operator and to register the personal information handling business operator. A business operator handling personal information is a business operator that collects, uses, and provides personal information, creates consent documents for collecting personal information, and manages consent records.

In IST, register the business operator and user profile in the following order:

- Register the system operator who operates the system, and register the user profiles of the system administrator and system operator who belong to the system operator.
- Registration of personal information handling business operator using the system, registration of personal information handling business operator administrator, information manager, information processor user profile
- A user of a business operator handling personal information registers master items (purpose of use, dataset schema, data retention policy, benefits, third party providers) and consent documents.

### Register system operator
- Register the operator information and organization information of the system operator
- Register user profile information of the system administrator and system operator

### Register a business operator handling personal information
- Register business information and business management organization information

### Register the user profile information of the user of the business operator handling personal information
- Register administrator user profile information
- Register user profile information of information processor

### Register the master information of the consent document

- Register and update the purpose of use
- Register and renew the expiration date
- Register and update third-party providers that use data
- Register and update the Dataset schema
- Register and renew benefits

### Registration and update of the consent document

- Register consent document
- Revised consent document (changes that do not require re-consent)
- Revised consent document (changes that require re-consent)

### Update of business information

- Update the user's organization/role
- Addition/update of organization information

### Record of consent by the data subject

- The data subject records consent and refusal of the consent document
- Data subject gets its consent status
- The user of the business obtains the consent status for the consent document of the business.

## How to run a user story

### Register shared functions for use in IST

You will first need to build the contract and functions
```console
cd contracts_and_functions
./gradlew build
cd ../tools/deploy
```

Then register the functions

```console
./functions
```

## Register information of system operator and personal information handling operator

### Register system operator

```console
./initialize
```

### Registration of business operator handling personal information

```console
./register_company
```

### Register the user profile information of the business operator handling personal information

You first need an admin profile, and then you can register the user profile.
```console
./upsert_user_profile_admin
./upsert_user_profile_controller
```

### Register the master information of the consent document

Register the purpose of use
```console
./register_purpose
```

Update the purpose of use
```console
./update_purpose
```

Register the dataset schema
```console
./register_data_set_schema
```

Update the dataset schema
```console
./update_data_set_schema
```

Register a third-party provider
```console 
./register_third_party
```

Update third-party providers
```console 
./update_third_party
```

Register suspension of use and data deletion deadline
```console 
./register_data_retention_policy
```

Suspension of use, update data deletion deadline
```console 
./update_data_retention_policy
```

Register benefits
```console 
./register_benefit
```

Update benefits
```console
./update_benefit
```

### Registration and update of a consent document

Register the consent document
```console
./register_consent_statement
```

Amend the consent document (changes that do not require re-consent)
```console
./update_consent_statement_revision
```

Revise the consent document (changes that require re-consent)
```console
./update_consent_statement_version
```

Change the status of the consent document
```console
./update_consent_statement_status
```

Retrieves the history of amendments to the consent document (changes that do not require re-consent)
```console
./get_consent_statement_history
```

### Update of business information
Update of the organization to which the operator user belongs
```console
./update_company
```

Update the role of the operator user
```console
./upsert_user_profile_controller_add_processor
```

Addition/update of organization information
```console
./upsert_organization
```

### Record of consent by the data subject

Registration of consent
```console
./upsert_consent_status_register
```

Renewal of consent
```console
./upsert_consent_status_update
```

Reference of consent status by a data subject
```console
./get_consent_status_data_subject
```

Reference of the status of consent by the business user
```console
./get_consent_status_controller
```