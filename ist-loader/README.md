# Scalar IST loader

This docker images loads the necessary functions and/or contracts to use IST with Scalar DL.

## Build

The IST loader image can be build via docker
``` 
docker build --tag scalar-ist-loader .
```

## Run

Once the image is created you can start it directly via `docker run` or `docker-compose`.

An example usage for docker-compose is
```
scalar-ist-loader:
    container_name: scalar-ist-loader
    image: ghcr.io/scalar-labs/scalar-ist-loader:1.0.0
    depends_on:
      - cassandra
      - scalar-ledger
#    volumes:
#      - ./client.properties:/client.properties
#      - ./initializer.pem:/initializer.pem
#      - ./initializer-key.pem:/initializer-key.pem
    environment:
      - IST_INSTALL_FUNCTIONS=false #default=true
      - IST_INSTALL_CONTRACTS=true  #default=true
      - CLIENT_PROPERTIES_PATH=my/path #default=sample-client.properties
    networks:
      - scalar-ist-network
    restart: on-failure
```

### Configuration

#### Environment variables

- IST_INSTALL_FUNCTIONS: install the Scalar DB contract functions, default true
- IST_INSTALL_CONTRACTS: install the Scalar DL contracts, default true

#### Client properties and certificates

The client properties and certificates can be specified and overridden via docker volumes.


