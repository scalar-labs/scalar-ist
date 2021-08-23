FROM openjdk:8u275-jre-slim

# Fix CVE-2021-3520, CVE-2021-33560, CVE-2021-20231, CVE-2021-20232, CVE-2020-24659, sCVE-2021-20305 and CVE-2021-23840
RUN apt-get update && apt-get install -y netcat && apt-get install -y --no-install-recommends \
    liblz4-1=1.8.3-1+deb10u1 \
    libgcrypt20=1.8.4-5+deb10u1 \
    libgnutls30=3.6.7-4+deb10u7 \
    libhogweed4=3.4.1-1+deb10u1 \
    libnettle6=3.4.1-1+deb10u1 \
    libssl1.1=1.1.1d-0+deb10u6 \
    openssl=1.1.1d-0+deb10u6 \
    && rm -rf /var/lib/apt/lists/*

# Copy all necessary files to the image
COPY docker-files/scalar-ist-loader .

# Set default ENV values for the image
ENV IST_INSTALL_CONTRACTS true
ENV IST_INSTALL_FUNCTIONS true
ENV CLIENT_PROPERTIES_PATH client.properties.sample
ENV LEDGER_HOST scalardl-samples-scalar-ledger-1

ENTRYPOINT ["./install.sh"]
