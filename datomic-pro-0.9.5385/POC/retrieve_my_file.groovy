#!/usr/local/bin/groovy

import datomic.Peer
import datomic.Connection
import datomic.Util
import org.apache.commons.io.IOUtils

// ./bin/groovysh ./POC/retrieve_my_file.groovy -tag=thetag -version=myversion -satelite=satelitename

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
    if(FILE == "" && NAME == "" && VERSION == ""){
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
          ||                                                                         ||   //
          ||    - satelite name          =>  -satelite=name                          ||  //
          ||    - the version of record  =>  -version=A                              || //
          ||    - (optional) tag         =>  -tag=1.0                                ||//
          ============================================================================//

          """
System.exit(0)
}
else{
  println "Execution the script for the satelite : " + NAME + ", and the version : " + VERSION + " : " + TAG
}
//  ============================================================ checking done



System.exit(0)

