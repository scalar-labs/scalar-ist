FROM scalar-schema-loader:1.0.0
COPY ist.transaction.json schema.json
ENTRYPOINT ["java", "-jar", "app.jar", "-f", "schema.json"]