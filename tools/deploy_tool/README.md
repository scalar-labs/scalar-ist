# IST Contract / Function Deploy and Execute Tool

It is a tool to deploy and execute IST contracts and functions.

## Install

Execute

```bash
./gradlew installDist
```

After a successful run the `deploy_tool` executable may be found in `/build/install/deploy_tool/bin`.

## Run

To run the tool

```bash
build/install/deploy_tool/bin/deploy_tool -f /path/to/command.json 
```

## Required files to run the tool
    
- Command file (JSON Format)
- Binary file of the contract or function to be registered
- Contract Properties definition file
- Contract Argument definition file


## Compile contracts and functions

Place the contract / function source files in the following directory.

 ```
 src/main/java/
 ```

Run `./gradlew build` to compile the files.

## Create command file

Create a command file in JSON format.

The actions that can be defined in the command file are as follows

- set-holder
- register-cert
- register-functions
- register-contract
- execute-contract

Sample of command file.
```json
[
  {
    "action": "set-holder",
    "client-properties": {
      "scalar.dl.client.cert_holder_id": "Initializer",
      "scalar.dl.client.cert_version": "1",
      "scalar.dl.client.cert_path": "build/resources/main/holder/keys/initializer.pem",
      "scalar.dl.client.private_key_path": "build/resources/main/holder/keys/initializer-key.pem"
    }
  },
  {
    "action": "register-cert"
  },
  {
    "action": "register-contract",
    "id": "PutAssetRecord",
    "binary_name": "com.scalar.ist.contract.PutAssetRecord",
    "path": "build/classes/java/main/com/scalar/ist/contract/PutAssetRecord.class",
    "properties": {
      "contract_argument_schema": {
        "type": "file",
        "path": "build/resources/main/schema/argument/put_asset_record.json"
      },
      "salt": "salt",
      "holder_id": {
        "type": "scalar.dl.client.cert_holder_id"
      }
    }
  }
]
```

### `set-holder`

Information about the Holder and ScalarDL definition information.

Examples of definitions are shown below.

```json
  {
    "action": "set-holder",
    "client-properties": {
      "scalar.dl.client.cert_holder_id": "Initializer",
      "scalar.dl.client.cert_version": "1",
      "scalar.dl.client.cert_path": "build/resources/main/holder/keys/initializer.pem",
      "scalar.dl.client.private_key_path": "build/resources/main/holder/keys/initializer-key.pem"
    }
  }
```

### `register-cert`

Register your certificate in the Scalar DL.
You need to run set-holder beforehand and specify the holder information.

Examples of definitions are shown below.

```json
  {
    "action": "register-cert"
  }
```

### `register-functions`
Register the functions used in Scalar DL.
Multiple function definitions can be specified.

- id : Function ID of the function to be registered
- binary_name : Fully qualified name of the function class
- path : Path to the function's binary file

Examples of definitions are shown below.

```json
 {
    "action": "register-functions",
    "functions": [
      {
        "id": "Initialize",
        "binary_name": "com.scalar.ist.function.Initialize",
        "path": "build/classes/java/main/com/scalar/ist/function/Initialize.class"
      },
      {
        "id": "RegisterCompany",
        "binary_name": "com.scalar.ist.function.RegisterCompany",
        "path": "build/classes/java/main/com/scalar/ist/function/RegisterCompany.class"
      }
    ]
  }
```

### `register-contract`

Register the contract to be used in Scalar DL.

- id : Contract ID for the contract you are registering
- binary_name : The fully qualified name of the contract class
- path : path to the contract binary file
- properties : Contract properties definition

#### properties types

- file : Load the contents of the file specified by path
- scalar.dl.client.cert_holder_id : Overwrite it with the value of `scalar.dl.client.cert_holder_id` specified in the set-holder

Examples of definitions are shown below.

```json
  {
    "action": "register-contract",
    "id": "Initialize",
    "binary_name": "com.scalar.ist.contract.Initialize",
    "path": "build/classes/java/main/com/scalar/ist/contract/Initialize.class",
    "properties": {
      "contract_argument_schema": {
        "type": "file",
        "path": "build/resources/main/schema/argument/initialize.json"
      },
      "company_asset_name": "cp",
      "company_asset_version": "01",
      "user_profile_asset_name": "up",
      "user_profile_asset_version": "01",
      "holder_id": {
        "type": "scalar.dl.client.cert_holder_id"
      }
    }
  }
```


#### `execute-contract`

- id : Contract ID for the contract you are executiong
- contract_argument : Contract argument definition

#### contract_argument types
     
- file : Load the contents of the file specified by path
- scalar.dl.client.cert_holder_id : Overwrite it with the value of `scalar.dl.client.cert_holder_id` specified in the set-holder
- now : Overwrite the millisecond value at runtime

Examples of definitions are shown below.

```json
  {
    "action": "execute-contract",
    "id": "Initialize",
    "contract_argument": {
      "value": {
        "type": "file",
        "path": "build/resources/main/argument/initialize.json"
      },
      "optional": {
        "holder_id": {
          "type": "scalar.dl.client.cert_holder_id"
        },
        "created_at": {
          "type": "now"
        }
      }
    }
  }
```
