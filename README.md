# Unidaplan

## What is it?

Unidaplan is a sample management software for scientists. This software is made for scientists who need to manage their samples, plan experiments and do processes on them. 

It is written in Java and Javascript with Angular.js and needs a PostgreSQL database. An old outdated video on what is about,
can be seen here (in german, sorry):

https://www.youtube.com/watch?v=gAOdfen3yqU

The software is free to use "as is". I will take no responsibility for data loss.


## How to set it up

You need an E-Mail account, an Apache Tomcat Web Application Server and an installed PostgreSQL database. Unidaplan uses the 
apache.tomcat.jdbc.pool. Make sure it is installed. 
Please enter all information in an Context.xml file which should be placed in the WebContent/META-INF directory.
The file should have the following content:

> &lt;?xml version = "1.0" encoding = "UTF-8"?&gt;
> 
>   &lt;Context <br>
>   &nbsp;&nbsp; path = "/unidaplan" <br>
>   &nbsp;&nbsp; docBase = "unidaplan" <br>
>   &nbsp;&nbsp; crossContext = "true" <br>
>   &nbsp;&nbsp; reloadable = "true" <br>
>    debug = "1"><br>
>	<br>
>  &lt;Resource name = "jdbc/postgres" <br>
>  &nbsp;&nbsp; auth = "Container" <br>
>  &nbsp;&nbsp; type = "javax.sql.DataSource" <br>
>  &nbsp;&nbsp; maxActive = "100" <br>
>  &nbsp;&nbsp; maxIdle = "30" <br>
>  &nbsp;&nbsp; maxWait = "10000" <br>
>  &nbsp;&nbsp; username = "theDatabaseUsername" <br>
>  &nbsp;&nbsp; password = "theDatabasePassword" <br>
>  &nbsp;&nbsp; driverClassName = "org.postgresql.Driver" <br>
>  &nbsp;&nbsp; url = "jdbc:postgresql://127.0.0.1:5432/your-database" <br>
>  &nbsp;&nbsp; factory = 'org.apache.tomcat.jdbc.pool.DataSourceFactory' <br>
>  &nbsp;&nbsp; minIdle = "10" <br>
>  &nbsp;&nbsp; initialSize = "10" <br>
>  &nbsp;&nbsp; testWhileIdle = "true" <br>
>  &nbsp;&nbsp; testOnBorrow = "true" <br>
>  &nbsp;&nbsp; testOnReturn = "false"<br>
>  &nbsp;&nbsp; validationQuery = "SELECT 1" <br>
>  &nbsp;&nbsp; validationInterval = "30000" <br>
>  &nbsp;&nbsp; timeBetweenEvictionRunsMillis = "30000" <br>
>  &nbsp;&nbsp; maxActive = "100" <br>
>  &nbsp;&nbsp; removeAbandonedTimeout = "60" <br>
>  &nbsp;&nbsp; removeAbandoned = "true" <br>
>  &nbsp;&nbsp; logAbandoned = "true" <br>
>  &nbsp;&nbsp; minEvictableIdleTimeMillis = "30000" <br>
>  &nbsp;&nbsp; jmxEnabled = "true" <br>
> /&gt; 
>             
>
> &lt;Environment <br>
> &nbsp;&nbsp; name = "smtpserver" <br>
> &nbsp;&nbsp; value = "your.smtp.server" > <br>
> &nbsp;&nbsp; type = "java.lang.String"/> <br>
> &nbsp;&nbsp; <br>
> &lt;Environment <br>
> &nbsp;&nbsp; name = "IPAdress" <br>
> &nbsp;&nbsp; value = "automatic" <br>
> &nbsp;&nbsp; type = "java.lang.String"/&gt; <br>
> &nbsp;&nbsp; <br>
> &lt;Environment <br>
> &nbsp;&nbsp; name = "smtpport" <br>
> &nbsp;&nbsp; value = "587" <br>
> &nbsp;&nbsp; type = "java.lang.Integer"/&gt;  <br> 
> &nbsp;&nbsp; <br>
> &lt;Environment <br>
> &nbsp;&nbsp; name = "username" <br>
> &nbsp;&nbsp; value = "your-username-for-email" <br>
> &nbsp;&nbsp; type = "java.lang.String"/&gt;  <br> 
> &nbsp;&nbsp; <br>
> &lt;Environment <br>
> &nbsp;&nbsp; name = "password" <br>
> &nbsp;&nbsp; value = "your-password-for-email" <br>
> &nbsp;&nbsp; type = "java.lang.String"/&gt;  <br>           
> &lt;/Context&gt;

Also you need to initialize the database. This can be done by running the SQL scripts in the SQL directory. Go to shell and use:
> psql -f definitions.sql <br/>
> psql -f functions.sql <br/>
> psql -f inserts.sql <br/>

You will also need to create a directory "/mnt/data-store" which is writable by the tomcat processowner for storing files.

## Changing the password

The first and only user is called ***admin*** and has the password ***admin***. 

When logged in, go to menu "Admin." and select users. Then click "action"->"edit" in the row of the user "Administrator". Double-click the e-mail field and enter your e-mail. Press the button ***back to users*** at the top of the page. Click ***action***->***resend token***. If everything is set up right, you should receive an e-mail which contains a link that allows you to change name and password of the admin account. 
