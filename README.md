# PaymentHub

Configuration
---------------
```sh
#Database
db.oracle.url=jdbc:oracle:thin:@192.168.99.101:1521:xe
db.oracle.user=payment_hub
db.oracle.pass=qwerty

#BeanIO
mapping.file=mapping.xml
stream.name=paymenthub

#SFTP server
sftp.host=192.168.99.100
sftp.port=2222
sftp.user=foo
sftp.password=pass
sftp.upload.path=/upload/
sftp.download.path=/download/

#File
file.format=OACSYN[0-9]{6}(\\.)R01
```

Usage command
---------------
```sh
java -Dconfig.file=${config.properties} -jar ${PaymentHub.jar} ${mode} ${fileName}
```
  Use -Dconfig.file=${config.properties} to get your config
	
  Use -jar ${PaymentHub.jar} to get your jarfile to run
	
  Use ${mode} to set your mode to run
		use "write" to Write data from database to file
		use "read" to Read data from to file to database
	
  Use ${fileName} to set your file name
