FROM eclipse-temurin:21-jre

# Install libreoffice-writer
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libreoffice-writer \
        fonts-dejavu \
        fonts-liberation && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
