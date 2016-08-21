#!/usr/local/bin/groovy
println "===================================== Importing libs";

import datomic.Peer
import datomic.Connection
import datomic.Util
import org.apache.commons.io.IOUtils

// CONSTANTE DECLARATIONS
INPUT_DIRECTORY = "./POC/datomic_input/";
OUTPUT_DIRECTORY = "./POC/datomic_output/";
SCHEMA_PATH = "./POC/test-schema.edn";

println "===================================== Creating DB and Connecting to datomic";

uri = "datomic:mem://coucou";
Peer.createDatabase(uri);
conn = Peer.connect(uri);



println "===================================== Loading a db schema from file";

reader = new FileReader("./POC/test-schema.edn");
List tx = Util.readAll(reader).get(0);
txResult = conn.transact(tx).get();


println "===================================== Making a partition";



partition_tx = [["db/id": Peer.tempid(":db.part/db"),
                 "db/ident": ":files",
                 "db.install/_partition": "db.part/db"]];
txResult = conn.transact(partition_tx).get()


println "===================================== Storing files to the datomic";

File file = new File( INPUT_DIRECTORY + "file_to_save.txt");
BufferedReader reader = new BufferedReader(new FileReader (file));
  String         line = null;
  StringBuilder  stringBuilder = new StringBuilder();
  String         ls = System.getProperty("line.separator");

  try {
      while((line = reader.readLine()) != null) {
          stringBuilder.append(line);
          stringBuilder.append(ls);
      }

      data = stringBuilder.toString();
  } finally {
      reader.close();
  }

d = Peer.tempid(":files")
println d

List tx2 = Util.list (Util.map (
  ":db/id", d,
  ":files/data", data,
  ":files/satelite", "name1",
  ":files/version", "version",
  ":files/tag", "tag"
));
tx_Result = conn.transact(tx2).get();

// ===================================================

results = Peer.query(
  "[:find [?when ...] :where [?tx :db/txInstant ?when]]",
  conn.db())

tx_dates = new ArrayList()
for (result in results) tx_dates.add(result)
Collections.sort(tx_dates)
Collections.reverse(tx_dates)

data_tx_date = tx_dates.get(0)
schema_tx_date = tx_dates.get(1)

// ===================================================

d2 = Peer.tempid(":files")
println d2
List tx3 = Util.list (Util.map (
  ":db/id", d2,
  ":files/data", data,
  ":files/satelite", "name2",
  ":files/version", "version",
  ":files/tag", "tag"
));
tx_Result = conn.transact(tx3).get();


println "===================================== Retrieving files from the datomic";
 println schema_tx_date
query = "[:find ?id ?data ?name ?version ?tag :where [ ?id :files/data ?data] [ ?id :files/version ?version] [ ?id :files/satelite ?name] [ ?id :files/tag ?tag] ]";
results = Peer.q(query, conn.db());
println "    ===> there are " + results.size() + " results: "

for (result in results) {
  string_data = result[1]
  try{
    println result
    PrintWriter out = new PrintWriter( OUTPUT_DIRECTORY + "output.txt" );
    out.write( string_data );
    out.close();
  }
  catch(e){
    println "  /!\\ /!\\ /!\\ ERROR ON FILE WRITING /!\\ /!\\ /!\\ "
  }
}

query = "[:find ?id ?data ?name ?version ?tag :where [ ?id :files/data ?data] [ ?id :files/version ?version] [ ?id :files/satelite ?name] [ ?id :files/tag ?tag] ]";
results = Peer.q(query, conn.db().asOf(data_tx_date));
println "    ===> there are " + results.size() + " results: "

for (result in results) {
  string_data = result[1]
  try{
    println result
    PrintWriter out = new PrintWriter( OUTPUT_DIRECTORY + "output.txt" );
    out.write( string_data );
    out.close();
  }
  catch(e){
    println "  /!\\ /!\\ /!\\ ERROR ON FILE WRITING /!\\ /!\\ /!\\ "
  }
}


println "===================================== End of the POC the file retrieved is xx.txt";
System.exit(0)
