#!/usr/local/bin/groovy
println "===================================== importing libs";

import datomic.Peer
import datomic.Connection
import datomic.Util
import org.apache.commons.io.IOUtils


println "===================================== create and connect to the DB";

uri = "datomic:mem://coucou";
Peer.createDatabase(uri);
conn = Peer.connect(uri);

println "===================================== load a db schema from file";

reader = new FileReader("./POC/test-schema.edn");
List tx = Util.readAll(reader).get(0);
txResult = conn.transact(tx).get();
//println txResult


File file = new File("./POC/file_to_save.txt");
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


println data
List tx2 = Util.list (Util.map (":db/id", 1, ":files/data", data));


tx_Result = conn.transact(tx2).get();
db = conn.db();

query = "[:find ?d :where [ _ :files/data ?d]]";
results = Peer.q(query, db);
println " =========> there is " + results.size() + " results: "
for (result in results) {
  println result
}



//tab = result.toArray();
//db.entity.get(":files/data")
//result.iterator().next().get(0);
//byte[]cc = (byte[])data;
//FileOutputStream fos = new FileOutputStream("cc");

//fos.write(cc);
//fos.close();

println "===================================== end of the POC the file retrieved is xx.txt";
System.exit(0)
