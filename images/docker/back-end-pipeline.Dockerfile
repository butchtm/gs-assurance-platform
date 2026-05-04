FROM bellsoft/liberica-openjdk-alpine:21

# Environment variables
ENV MAVEN_VERSION=3.9.4

# Install unzip and wget
RUN apk add --no-cache unzip wget

# Install Maven
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt \
    && ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven

# Install AWS CLI
RUN wget https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip \
    && unzip awscli-exe-linux-x86_64 \
    && ./aws/install \
    && rm -rf aws awscli-exe-linux-x86_64.zip

# Install Docker
ARG DOCKER_VERSION
RUN wget https://download.docker.com/linux/static/stable/x86_64/docker-${DOCKER_VERSION}.tgz \
    && tar -xvf docker-${DOCKER_VERSION}.tgz \
    && rm docker-${DOCKER_VERSION}.tgz

# Add Maven and Docker to PATH
ENV MAVEN_HOME=/opt/maven
ENV PATH="${MAVEN_HOME}/bin:/docker:/usr/bin:/bin:$PATH"

# Run version commands to confirm successful install
RUN mvn -v && aws --version && docker --version
