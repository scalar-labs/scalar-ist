# IST schema loader

The image contains the schema used in the IST Scalar DL templates. Based on the `scalardb-schema-loader` image it loads
the schema in your database of choice.

## Deployment

### Build

```
docker build -t <tag> .
```

Example:
```
docker build -t ghcr.io/scalar-labs/scalar-ist-schema-loader .
```

### Push

```
docker push <tag>
```

Example:
```
docker push ghcr.io/scalar-labs/scalar-ist-schema-loader
```

## How to use

Check out the Schema tool documentation for the correct usage.
[link](https://github.com/scalar-labs/scalardb/blob/master/docs/schema-loader.md)

docker-compose.yaml

```dockerfile
  ist-schema-loader:
    image: ghcr.io/scalar-labs/scalar-ist-schema-loader
    volumes:
      - ./scalardb.properties:/scalardb.properties
    command:
      - "-c"
      - "/scalardb.properties"
      - "--coordinator"
      - "--replication-factor"
      - "1"
    networks:
      - scalar-network
    restart: on-failure
```
