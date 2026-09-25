set -a
source ./.env
# eval "$(aws configure export-credentials --format env)"
set +a
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local,local-auth"