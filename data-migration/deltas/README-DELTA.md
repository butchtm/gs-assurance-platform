# Delta Migration
This python application exists to allow us to perform ongoing migrations 
between the legacy database in Sustain Cert and our Assurance Platform. Our initial 
migration script pulled all the data over however does not have the functionality to run 
the migrations again without completely overwriting the target db. 

## Requirement
We have been asked to migrate over requests from Sustain Cert that have entered into a 
closed state, this allows the Project Developer to finish the request on Sustain Cert and 
then open the next request in the Assurance Platform whilst being able to view all legacy 
requests in Assurance Platform too.

## Implementation
The implementation goes through 4 stages:
1. Get all request ids of:
   - requests that have moved into a closed state that the Assurance Platform already knows about.
   - requests in any state that the Assurance Platform has no knowledge of.
2. Get all data relating to the request ids found above including:
   - projects
   - request history
   - project history
   - invoices
   - documents
   - accounts
3. Transform the gathered data into the Assurance DB schema.
4. Execute insert/upsert commands against the target db.

### Transactions
All the execute commands are wrapped in a high level transaction therefor if there is a 
problem with any of the requests, it will rollback any call already performed.

## Running the application

### Locally
The application can be run locally by creating a `.env` file with the following required variables in, 
place the file in the deltas folder:
```
SC_DB_PASSWORD=
SC_DB_USER=
SC_HOST=
SC_DB_NAME=
ASSURANCE_DB_HOST=
ASSURANCE_DB_PORT=
ASSURANCE_DB_NAME=
ASSURANCE_DB_USER=
ASSURANCE_DB_PASSWORD=
```
Then you can start prepare the application by following the commands below:
```
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```
Then to finally run the deltas:
```
python main.py
```

### Gitlab Schedule

## Issues seen
The scripts do their best to not manipulate the data too much as this could break 
the integrity of the migrated data. We have found a few issues where we have had 
to manually intervene, including:
1. Account names, there no enforced join between the ProjectDeveloperName and Account_Name, 
we have found an account where these names should be the same but are one character different.
2. Document hash needed to be encoded to UTF-8.