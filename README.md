# Poc-Datomic
Quick and dirty groovy proof of concept using datomic immutable database system to store files
***


##### we can found in POC folder the code relative to the groovy scripts:
- **init_db.groovy** to create the db with the schema.
- **save_my_file.groovy** to save the file in datomic with name, version and tag.
- **retrieve_my_file.groovy** to retrieve from datomic with name, version, tag and date of records.
- **script-poc.groovy** to play with memory DB.
- **test-schema.edn** the datomic table schema.
- **output** folder: where the files are retrieve from the DB.
- **input** folder: folder to hold resources.
