FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy all necessary files to the image
COPY docker-files/scalar-ist-loader .
ADD client.tar .

# Set default ENV values for the image
ENV IST_INSTALL_FUNCTIONS true
ENV IST_INSTALL_CONTRACTS false

ENTRYPOINT ["./install.sh"]
