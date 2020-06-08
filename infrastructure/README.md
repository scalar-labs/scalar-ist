#Scalar Information Banking REST API database
#### Start up the database (Cassandra) of Scalar Information Bank and install the schema
This will start a Cassandra docker container and install the database [schema](./schema.cql) file
```
./db up
```

#### Tear down the database
Stop the container and **destroy** its data
```
./db down
```

#Scalar Information Banking Vault
#### Start up the vault
This will start a Vault docker container at port :8200.
```
./vault up
```
The root token will be `vaulttoken`.

#### Tear down the vault
```
./vault down
```
