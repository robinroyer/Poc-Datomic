#!/usr/local/bin/groovy

import datomic.Peer
import datomic.Connection
import datomic.Util
import org.apache.commons.io.IOUtils
import java.util.*;
import java.text.*;


// ./bin/groovysh POC/retrieve_my_file.groovy -satelite=hello -date=2012.04.04-11:11:11 -tag=tag -version=A

NAME = ""
VERSION = ""
TAG = ""
DATE = ""

//  ============================================================ checking for params

quit = true;

for (arg in this.args ) {
  try {
    switch(arg.substring(0 , arg.indexOf("="))) {
    case "-date":
      SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd-HH:mm:ss");
      input = arg.substring(arg.indexOf("=") + 1)
      try {
         DATE = ft.parse(input, new ParsePosition(0));
      }
      catch(Exception e) {
        DATE = ""
        println "ERROR IN DATE"
      }

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
    if(NAME == "" ){
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
            //                         DATOMIC - RETRIEVE MY FILE                       // ||
           //                                                                          //  ||
          //==========================================================================//   ||
          || Welcome to retrieve_my_file.groovy, you have to provide some options:   ||    //
          ||     -(optionnal) date       => -date=2012.04.04-11:11:11                ||   //
          ||    - satelite name          =>  -satelite=name                          ||  //
          ||    - (optional version      =>  -version=A                              || //
          ||    - (optional) tag         =>  -tag=1.0                                ||//
          ============================================================================//

          """
System.exit(0)
}
else{

}
//  ============================================================ checking done

// ============================ dev
SCHEMA_PATH = "./POC/test-schema.edn";
uri = "datomic:mem://coucou";
Peer.createDatabase(uri);
conn = Peer.connect(uri);
reader = new FileReader("./POC/test-schema.edn");
List tx = Util.readAll(reader).get(0);
txResult = conn.transact(tx).get();


partition_tx = [["db/id": Peer.tempid(":db.part/db"),
                 "db/ident": ":files",
                 "db.install/_partition": "db.part/db"]];
txResult = conn.transact(partition_tx).get()
id = Peer.tempid(":files")

try {
  File file = new File( "./POC/datomic_input/file_to_save.txt");
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
  ":files/satelite", "name",
  ":files/version", "version",
  ":files/tag", "tag"
));

tx_Result = conn.transact(tx2).get();

  query = """[
        :find ?id ?data ?name ?version ?tag
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]
        ]
      """;
  results = Peer.q(query, conn.db());
  // println results

// ============================ dev


if(TAG == "") {
  if(VERSION == ""){
    // ================================================= tag empty version empty and date empty
    if(DATE == "") {
      query = """[
        :find ?id ?data ?name
        :in \$ ?name
        :where
          [ ?id :files/data ?data]
          [ ?id :files/satelite ?name]
        ]
      """;
  results = Peer.q(query, conn.db(), NAME);
    }
    // ================================================= tag empty and version empty
    else {
      query = """[
        :find ?id ?data ?name
        :in \$ ?name
        :where
          [ ?id :files/data ?data]
          [ ?id :files/satelite ?name]
        ]
      """;
    results = Peer.q(query, conn.db().asOf(DATE), NAME);
    }
  }
  // ================================================= tag empty and date empty
  else if (DATE == "") {
    query = """[
        :find ?id ?data ?name ?version
        :in \$ ?name ?version
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
        ]
      """;
    results = Peer.q(query, conn.db(), NAME, VERSION);
  }
  // ================================================= tag empty
  else{
    query = """[
        :find ?id ?data ?name ?version
        :in \$ ?name ?version
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
        ]
      """;
    results = Peer.q(query, conn.db().asOf(DATE), NAME, VERSION);
  }
}
else if (VERSION == ""){
  // ================================================= version empty and date empty
  if(DATE == "") {
    query = """[
        :find ?id ?data ?name ?tag
        :in \$ ?name ?tag
        :where
          [ ?id :files/data ?data]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]
        ]
      """;
    results = Peer.q(query, conn.db(), NAME, TAG);
  }
  //================================================= version empty
  else {
    query = """[
        :find ?id ?data ?name ?tag
        :in \$ ?name ?tag
        :where
          [ ?id :files/data ?data]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]
        ]
      """;
    results = Peer.q(query, conn.db().asOf(DATE), NAME, TAG);
  }
}
//================================================= date empty
else if (DATE == ""){
  query = """[
        :find ?id ?data ?name ?version ?tag
        :in \$ ?name ?version ?tag
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]
        ]
      """;
  results = Peer.q(query, conn.db(), NAME, VERSION, TAG);
}
// ================================================= nothing empty
else{
  query = """[
        :find ?id ?data ?name ?version ?tag
        :in \$ ?name ?version ?tag
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]
        ]
      """;
  results = Peer.q(query, conn.db().asOf(DATE), NAME, VERSION, TAG);
}

//  ===================================================== depending on the params of the script results is filled up





println "    ===> there are " + results.size() + " results: "

for (result in results) {
  // string_data = result[1]
  try{
    println result
    // PrintWriter out = new PrintWriter( OUTPUT_DIRECTORY + "output.txt" );
    // out.write( string_data );
    // out.close();
  }
  catch(e){
    println "  /!\\ /!\\ /!\\ ERROR ON FILE WRITING /!\\ /!\\ /!\\ "
  }
}



System.exit(0)

