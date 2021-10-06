# Scalar IST loader

This docker images loads the necessary functions and/or contracts to use IST with Scalar DL.

## Build

The IST loader image can be build via docker

```
docker build --tag scalar-ist-loader .
```

## Run

Once the image is created you can start it directly via `docker run` or `docker-compose`.

Both `CLIENT_PROPERTIES_PATH` and `LEDGER_HOST` are required environment variables that need to be
set. Do not forget to also add the key files via docker volumes. The files should be mounted to the
path that is mentioned in the `client.properties` file.

`IST_INSTALL_FUNCTIONS` (default true) and `IST_INSTALL_CONTRACTS` (default false) are optional.

client.properties file example:

```
scalar.dl.client.server.host=scalardl-scalar-ledger-1
scalar.dl.client.cert_holder_id=Initializer
scalar.dl.client.cert_version=1
scalar.dl.client.cert_path=./certs/initializer.pem
scalar.dl.client.private_key_path=./certs/initializer-key.pem
scalar.dl.client.server.port=50051
```

Docker-compose.yml file example:

```
scalar-ist-loader:
    container_name: scalar-ist-loader
    image: ghcr.io/scalar-labs/scalar-ist-loader:2.0.0
    depends_on:
      - cassandra
      - scalar-ledger
    volumes:
      - ./client.properties:/client.properties
      - ./initializer.pem:/initializer.pem
      - ./initializer-key.pem:/initializer-key.pem
    environment:
      - IST_INSTALL_FUNCTIONS=false 
      - IST_INSTALL_CONTRACTS=true
      - CLIENT_PROPERTIES_PATH=/client.properties
      - LEDGER_HOST=docker-ledger-name
    networks:
      - scalar-ist-network
    restart: on-failure
```

### Configuration

#### Environment variables

- IST_INSTALL_FUNCTIONS: install the Scalar DB contract functions, default true
- IST_INSTALL_CONTRACTS: install the Scalar DL contracts, default false

`IST_INSTALL_CONTRACTS` should only be used for development and testing as all the contracts will be
installed under the same holder id and keys provided in the `client.properties` file.

#### Client properties and certificates

The client properties and certificates can be specified and overridden via docker volumes.
