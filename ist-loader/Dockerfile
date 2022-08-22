FROM ghcr.io/scalar-labs/jre8:1.1.6

# Copy all necessary files to the image
COPY docker-files/scalar-ist-loader .
ADD client.tar .

# Set default ENV values for the image
ENV IST_INSTALL_FUNCTIONS true
ENV IST_INSTALL_CONTRACTS false

ENTRYPOINT ["./install.sh"]
