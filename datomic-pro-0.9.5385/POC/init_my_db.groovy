#!/usr/local/bin/groovy


import datomic.Peer
import datomic.Connection
import datomic.Util

SCHEMA_PATH = "./POC/test-schema.edn";
uri = "datomic:sql://thales?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic";

Peer.createDatabase(uri);
conn = Peer.connect(uri);

reader = new FileReader("./POC/test-schema.edn");
List tx = Util.readAll(reader).get(0);
txResult = conn.transact(tx).get();

println "===================================== Making a partition";

partition_tx = [["db/id": Peer.tempid(":db.part/db"),
                 "db/ident": ":files",
                 "db.install/_partition": "db.part/db"]];
txResult = conn.transact(partition_tx).get();
System.exit(0)
