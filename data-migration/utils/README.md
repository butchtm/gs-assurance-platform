# Utilities for the data migration

| Utility | Description | Usage |
| ------- | ----------- | ----- |
| `extract_enums.sh` | Extract enums from the Platform Enums json and generate reference table DDL and insert DML | e.g. `./extract_enums.sh < PlatformEnums_2023-09-19.json > ref_data.sql`
| `check_documents.sh` | Cross check the SC DB extract Documents table for correspondence with the document blob storage | e.g. `source .env && ./check_documents.sh | tee document_check.txt`
