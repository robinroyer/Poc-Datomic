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
OUTPUT_DIRECTORY = "./POC/datomic_output/";

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


uri = "datomic:sql://thales?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic";

Peer.createDatabase(uri);
conn = Peer.connect(uri);


if(TAG == "") {
  if(VERSION == ""){
    // ================================================= tag empty version empty and date empty
    if(DATE == "") {
      query = """[
        :find ?id ?data ?name ?version ?tag
        :in \$ ?name
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]
        ]
      """;
  results = Peer.q(query, conn.db(), NAME);
    }
    // ================================================= tag empty and version empty
    else {
      query = """[
        :find ?id ?data ?name ?version ?tag
        :in \$ ?name
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]
        ]
      """;
    results = Peer.q(query, conn.db().asOf(DATE), NAME);
    }
  }
  // ================================================= tag empty and date empty
  else if (DATE == "") {
    query = """[
        :find ?id ?data ?name ?version ?tag
        :in \$ ?name ?version
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]
        ]
      """;
    results = Peer.q(query, conn.db(), NAME, VERSION);
  }
  // ================================================= tag empty
  else{
    query = """[
        :find ?id ?data ?name ?version ?tag
        :in \$ ?name ?version
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]
        ]
      """;
    results = Peer.q(query, conn.db().asOf(DATE), NAME, VERSION);
  }
}
else if (VERSION == ""){
  // ================================================= version empty and date empty
  if(DATE == "") {
    query = """[
        :find ?id ?data ?name ?version ?tag
        :in \$ ?name ?tag
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
          [ ?id :files/satelite ?name]
          [ ?id :files/tag ?tag]

        ]
      """;
    results = Peer.q(query, conn.db(), NAME, TAG);
  }
  //================================================= version empty
  else {
    query = """[
        :find ?id ?data ?name ? version ?tag
        :in \$ ?name ?tag
        :where
          [ ?id :files/data ?data]
          [ ?id :files/version ?version]
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
    PrintWriter out = new PrintWriter( OUTPUT_DIRECTORY + result[2] + "#" + result[3] + ":" + result[4] + ":" + DATE + ".txt" );
    out.write( result[1] );
    out.close();
  }
  catch(e){
    println "  /!\\ /!\\ /!\\ ERROR ON FILE WRITING /!\\ /!\\ /!\\ "
    println e
  }
}



System.exit(0)

