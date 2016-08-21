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


List tx2 = Util.list (Util.map (":db/id", 1, ":files/data", data));
tx_Result = conn.transact(tx2).get();



println "===================================== Retrieving files from the datomic";

db = conn.db();
query = "[:find ?d :where [ _ :files/data ?d]]";
results = Peer.q(query, db);
println "    ===> there is " + results.size() + " results: "

for (result in results) {
  string_data = result[0]
  try{
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
