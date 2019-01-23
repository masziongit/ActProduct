# PaymentHub

Usage command

	java -Dconfig.file=${config.properties} -jar ${PaymentHub.jar} ${mode} ${fileName}
	
  Use -Dconfig.file=${config.properties} to get your config
	
  Use -jar ${PaymentHub.jar} to get your jarfile to run
	
  Use ${mode} to set your mode to run
		use "write" to Write data from database to file
		use "read" to Read data from to file to database
	
  Use ${fileName} to set your file name
