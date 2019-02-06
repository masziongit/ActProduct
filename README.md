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

#BeanIO
mapping.file=mapping.xml
stream.name=paymenthub

#SFTP server
sftp.host=192.168.99.100
sftp.port=22
sftp.user=foo
sftp.password=pass
sftp.upload.path=/upload/
sftp.download.path=/upload/
sftp.ssh.keyfile=id_rsa
sftp.ssh.passphrase=qwerty

#File
file.format=OACSYN[0-9]{6}(\\.)R01

```

Usage command
---------------
```sh
java -Dconfig.file=${config.properties} -jar ${PaymentHub.jar} ${mode}
```
  Use -Dconfig.file=${config.properties} to get your config
	
  Use -jar ${PaymentHub.jar} to get your jarfile to run
	
  Use ${mode} to set your mode to run
		use "write" to Write data from database to file
		use "read" to Read data from to file to database

