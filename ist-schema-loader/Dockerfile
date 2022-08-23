FROM ghcr.io/scalar-labs/scalardb-schema-loader:3.6.0
COPY schema/ist.transaction.json schema.json
ENTRYPOINT ["java", "-jar", "app.jar", "-f", "schema.json"]
