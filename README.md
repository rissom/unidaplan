# unidaplan
**Sample management software for Scientists**

This software is made for scientists who need to manage their samples, plan experiments with them and do processes on them. 
It is written in Java and Javascript with Angular.js and needs a PostgreSQL database. An old outdated video on what is about,
can be seen here (in german, sorry):

https://www.youtube.com/watch?v=gAOdfen3yqU

The software is NOT FINISHED yet. Allthough all basic functionality is there, there still are tons of bugs. The software
is free to use for public research facilities and universities. But there will be a fee for private companies, once it is
finished. 


*setting it up*

You need an E-Mail account, an Apache Tomcat Web Application Server and an installed PostgreSQL database. Please enter all
information in an Context.xml file which should be placed in the WebContent/META-INF directory. The file should have the
following content:

> <?xml version = "1.0" encoding = "UTF-8"?>
> 
> <Context 
>	  path = "/unidaplan" 
>   docBase = "unidaplan"
>   crossContext = "true"
>   reloadable = "true" 
>    debug = "1">
>
>    <Resource name = "jdbc/postgres" 
>              auth = "Container" 
>              type = "javax.sql.DataSource" 
>              maxActive = "100" 
>              maxIdle = "30" 
>              maxWait = "10000" 
>              username = "theDatabaseUsername" 
>              password = "theDatabasePassword"
>              driverClassName = "org.postgresql.Driver" 
>              url = "jdbc:postgresql://127.0.0.1:5432/your-database" /> 
>              
>
>    <Environment name = "smtpserver" value = "your.smtp.server" type = "java.lang.String"/>
>    <Environment name = "IPAdress" value = "automatic" type = "java.lang.String"/>
>    <Environment name = "smtpport"   value = "587" 		 type = "java.lang.Integer"/>          
> </Context>

Also you need to initialize the database. This can be done by running the SQL scripts in the SQL directory (which I will 
add in a minute).
