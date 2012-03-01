#!/bin/bash

DBPATH=/nonbackupdata/mongodb/

# Check, mongoDBs database directory exists
if [ ! -d ${DBPATH} ]; then 
	echo "Creating missing database directory $DBPATH"
	mkdir -p ${DBPATH}
fi

echo "You can open your web interface with url: http://localhost:28017/ "
echo "--"
~/programs/mongodb/bin/mongod --dbpath ${DBPATH} 
