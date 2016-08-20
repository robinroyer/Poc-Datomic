#!/usr/local/bin/groovy
println "===================================== importing libs";

import datomic.Peer
import datomic.Connection
import datomic.Util

println "===================================== create and connect to the DB";

uri = "datomic:mem://coucou";
Peer.createDatabase(uri);
conn = Peer.connect(uri);

println "===================================== load a db schema from file";

reader = new FileReader("./POC/test-schema.edn");
List tx = Util.readAll(reader).get(0);
txResult = conn.transact(tx).get();
//println txResult

//File file = new File("./file_to_save.txt");
//InputStream stm = new FileInputStream(file);
//import org.apache.commons.io.IOUtils

 //Byte[]data = IOUtils.toByteArray(stm);
 //List tx = Util.list (Util.map (":db/id",1,":files/data",data));


//tx Result = conn.transact(tx).get();
db = conn.db();

query = "[:find ?d :where [ ?c :files/data ?d]]";
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
