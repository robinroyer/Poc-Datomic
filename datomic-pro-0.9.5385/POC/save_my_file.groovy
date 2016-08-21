#!/usr/local/bin/groovy

import datomic.Peer
import datomic.Connection
import datomic.Util
import org.apache.commons.io.IOUtils

// ./bin/groovysh ./POC/save_my_file.groovy -tag=thetag -file=POC/datomic_input/file_to_save.txt -version=myversion -satelite=satelitename

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
else{
  println "Execution the script for the file : " + FILE + " corresponding to the satelite : " + NAME + ", and the version : " + VERSION + " : " + TAG
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

query = "[:find ?id :in \$ ?a ?b :where  [ ?id  :files/satelite ?a ] [?id :files/version ?b] ]";
results = Peer.q(query, conn.db(), NAME, VERSION);

try {
  id = results.iterator().next().get(0)
}
catch(Exception e) {
  id = Peer.tempid(":files")
}


println "===================================== Storing files to  datomic";
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


List tx2 = Util.list (Util.map (
  ":db/id", id,
  ":files/data", data,
  ":files/satelite", NAME,
  ":files/version", VERSION,
  ":files/tag", TAG
));

tx_Result = conn.transact(tx2).get();





// ================================================= TEST

// println id
// query = "[:find ?id :in \$ ?a ?b :where  [ ?id  :files/satelite ?a ] [?id :files/version ?b] ]";
// results = Peer.q(query, conn.db(), NAME, VERSION);

// try {
//   id = results.iterator().next().get(0)
// }
// catch(Exception e) {
//   id = Peer.tempid(":files")
// }


// println "===================================== Storing files to  datomic";
// try {
//   File file = new File( FILE);
//   BufferedReader reader = new BufferedReader(new FileReader (file));
//     String         line = null;
//     StringBuilder  stringBuilder = new StringBuilder();
//     String         ls = System.getProperty("line.separator");

//       while((line = reader.readLine()) != null) {
//           stringBuilder.append(line);
//           stringBuilder.append(ls);
//       }

//       data = stringBuilder.toString();
//   }
//   catch(e){
//     println "error in file reading"
//     System.exit(0)
//   }
//   finally {
//       reader.close();
//   }


// tx2 = Util.list (Util.map (
//   ":db/id", id,
//   ":files/data", data,
//   ":files/satelite", "TEST",
//   ":files/version", VERSION,
//   ":files/tag", TAG
// ));

// tx_Result = conn.transact(tx2).get();


// //  ==================== dev
// println "===================================== looking for a record from the same satelite in the same version";
// query = "[:find ?id ?a ?b :where  [ ?id  :files/satelite ?a ] [?id :files/version ?b] ]";
// results = Peer.q(query, conn.db(), NAME, VERSION);

// println results.size()
// println results

System.exit(0)

