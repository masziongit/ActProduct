# PaymentHub

Configuration
---------------
```sh
#Database
db.oracle.url=jdbc:oracle:thin:@192.168.99.101:1521:xe
db.oracle.user=C##testsc
db.oracle.pass=qwerty

#query
db.select=SELECT branch_code, scheme_code, acct_bal_amt, acct_crncy, account_id, scheme_type FROM ext_general_acct_table WHERE bank_id = '011' AND scheme_type != 'OAB' AND LENGTH(account_id) = 10
db.update=UPDATE EXT_GENERAL_ACCT_TABLE SET acct_bal_amt=?,scheme_code=? WHERE account_id =? AND  bank_id ='011'

#SFTP server
sftp.host=192.168.99.100
sftp.port=2222

sftp.user=foo
sftp.upload.path=/upload/
sftp.download.path=/download/
sftp.ssh.keyfile=nopp
sftp.ssh.passphrase=

#File
file.name.dateformat=yyMMdd
file.name.type=R01
upload.file.format=IACSYN[0-9]{6}(\\.)R01
upload.file.name.prefix=IACSYN
download.file.format=OACSYN[0-9]{6}(\\.)R01
download.file.name.prefix=OACSYN


#BeanIO
mapping.file=mapping.xml
stream.name=paymenthub

#Log4j
log.config.file=log4j.properties
```

Usage command
---------------

Encrypt password
```sh
java -cp ActProduct.jar gen.AESCrypt ${password}
```

```sh
java -Dconfig.file=${config.properties} -jar ${PaymentHub.jar} ${mode}
```
  Use -Dconfig.file=${config.properties} to get your config
	
  Use -jar ${PaymentHub.jar} to get your jarfile to run
	
  Use ${mode} to set your mode to run
		use "write" to Write data from database to file
		use "read" to Read data from to file to database

