# Scalar IST loader

This docker images loads the necessary functions and/or contracts to use IST with Scalar DL.

## Build

The IST loader image can be build via docker

```
docker build --tag ghcr.io/scalar-labs/scalar-ist-loader:2.0.0 .
```

## Run

Once the image is created you can start it directly via `docker run` or `docker-compose`.

Example:

```
scalar-ist-loader:
    container_name: scalar-ist-loader
    image: ghcr.io/scalar-labs/scalar-ist-loader:2.0.0
    depends_on:
      - cassandra
      - scalar-ledger
    volumes:
      - ./client.properties:/config/client.properties
      - ./initializer_cert.pem:./certs/initializer_cert.pem
      - ./initializer-cert-key.pem:./certs/initializer-cert-key.pem
    environment:
      - IST_INSTALL_FUNCTIONS=false 
      - IST_INSTALL_CONTRACTS=true
      - CLIENT_PROPERTIES_PATH=/config/client.properties
      - LEDGER_HOST=scalardl-scalar-ledger-1
    networks:
      - scalar-ist-network
```

Notes:

- restart: on-failure is not necessary to add. The `scalar-ist-loader` checks and waits for the
  Scalar Ledger to be up and running before starting the installation.
- The client properties and certificates can be specified and overridden via docker volumes.

## Configuration

### Environment variables

Both `CLIENT_PROPERTIES_PATH` and `LEDGER_HOST` are required environment variables that need to be
set. Do not forget to also add the key files via docker volumes. The files should be mounted to the
path that is mentioned in the `client.properties` file.

`IST_INSTALL_FUNCTIONS` (default true) and `IST_INSTALL_CONTRACTS` (default false) are optional.

`IST_INSTALL_CONTRACTS` should only be used for development and testing as all the contracts will be
installed under the same holder id and keys provided in the `client.properties` file.

### Client properties

Example:

```
scalar.dl.client.server.host=scalardl-scalar-ledger-1
scalar.dl.client.cert_holder_id=Initializer
scalar.dl.client.cert_version=1
scalar.dl.client.cert_path=./certs/initializer_cert.pem
scalar.dl.client.private_key_path=./certs/initializer_cert-key.pem
scalar.dl.client.server.port=50051
```

The specified holder id and keys determine the owner of the deployed functions and contracts. In a
real world application, each contract will be installed for each holder. There will be no need to
pre-install all contracts under one holder.

Therefore, it is recommended to only use this `scalar-ist-loader` for development and testing
purposes.
