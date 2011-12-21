#!/bin/bash
dbuser="$1"
dbpassword="$2"
dbname="$3"
 
# Detect paths
mysql=$(which mysql)
awk=$(which awk)
grep=$(which grep)
 
if [ $# -ne 3 ]
then
	echo "Usage: $0 <mysql-user> <mysql-password> <mysql-dbname>"
	exit 1
fi
 
tables=$(${mysql} -u ${dbuser} -p${dbpassword} ${dbname} -e 'show tables' | ${awk} '{ print $1}' | ${grep} -v '^Tables' )
 
for table in ${tables}
do
	echo "Deleting ${table} table from ${dbname} database..."
	${mysql} -u ${dbuser} -p${dbpassword} ${dbname} -e "drop table ${table}"
done