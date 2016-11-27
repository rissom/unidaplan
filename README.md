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
>  &nbsp;&nbsp; driverClassName ="org.postgresql.Driver" <br>
>  &nbsp;&nbsp; url = "jdbc:postgresql://127.0.0.1:5432/your-database" /&gt; 
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
> &lt;/Context&gt;


Also you need to initialize the database. This can be done by running the SQL scripts in the SQL directory.
