#!/usr/local/bin/groovy

import datomic.Peer
import datomic.Connection
import datomic.Util
import org.apache.commons.io.IOUtils


FILE = ""
NAME = ""
VERSION = ""
TAG = ""

//  ============================================================ checking for params

quit = true;

for (arg in this.args ) {
  try {
    switch(arg.substring(0 , arg.indexOf("="))) {
    case "-file":
      FILE = arg.substring(arg.indexOf("=") + 1)
    break
    case "-satelite":
      NAME = arg.substring(arg.indexOf("=") + 1)
    break
    case "-version":
      VERSION = arg.substring(arg.indexOf("=") + 1)
    break
    case "-tag":
      TAG = arg.substring(arg.indexOf("=") + 1)
    break
    }
  }
  catch(Exception e) {
    quit = true
  }
  finally{
    if(FILE == "" || NAME == "" || VERSION == ""){
      quit = true
    }
    else{
      quit = false
    }
  }
}

if (quit) {
  println """
              /============================================================================/|
             //                                                                          //||
            //                         DATOMIC - SAVE MY FILE                           // ||
           //                                                                          //  ||
          //==========================================================================//   ||
          || Welcome to save_my_file.groovy, you have to provide some options:       ||    //
          ||    - file to save           =>  -file=data.xml                          ||   //
          ||    - satelite name          =>  -satelite=name                          ||  //
          ||    - the version of record  =>  -version=A                              || //
          ||    - (optional) tag         =>  -tag=1.0                                ||//
          ============================================================================//

          """
System.exit(0)
}
//  ============================================================ checking done







// =====================DEV
SCHEMA_PATH = "./POC/test-schema.edn";
uri = "datomic:mem://coucou";
Peer.createDatabase(uri);
conn = Peer.connect(uri);
reader = new FileReader("./POC/test-schema.edn");
List tx = Util.readAll(reader).get(0);
txResult = conn.transact(tx).get();

println "===================================== Making a partition";


partition_tx = [["db/id": Peer.tempid(":db.part/db"),
                 "db/ident": ":files",
                 "db.install/_partition": "db.part/db"]];
txResult = conn.transact(partition_tx).get()
//  =================== DEV





println FILE

println "===================================== Storing files to the datomic";
try {
  File file = new File( FILE);
  BufferedReader reader = new BufferedReader(new FileReader (file));
    String         line = null;
    StringBuilder  stringBuilder = new StringBuilder();
    String         ls = System.getProperty("line.separator");

      while((line = reader.readLine()) != null) {
          stringBuilder.append(line);
          stringBuilder.append(ls);
      }

      data = stringBuilder.toString();
  }
  catch(e){
    println "error in file reading"
    System.exit(0)
  }
  finally {
      reader.close();
  }

d = Peer.tempid(":files")
println d

List tx2 = Util.list (Util.map (
  ":db/id", d,
  ":files/data", data,
  ":files/satelite", NAME,
  ":files/version", VERSION,
  ":files/tag", TAG
));

tx_Result = conn.transact(tx2).get();

//  ==================== dev

query = "[:find ?id ?data ?name ?version ?tag :where [ ?id :files/data ?data] [ ?id :files/version ?version] [ ?id :files/satelite ?name] [ ?id :files/tag ?tag] ]";
results = Peer.q(query, conn.db());
println "    ===> there are " + results.size() + " results: "

for (result in results) {
  println  result
}
System.exit(0)

