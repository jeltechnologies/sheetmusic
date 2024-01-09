# About Geoservices
This project is a web service that provides the address from coordinates.

## Why would I need this?
With this service you can use geolocation information without limits and (cloud) costs, especially when running it on a home server.
Address information is also possible to fetch from paid web services, or from web services that throttle the requests to 1 request per second. 

## How does it work?
The web service comes with a simple web page, for testing puproses. Here you fill out coordinates
![geoservice-ui](https://github.com/jeltechnologies/geoservices/assets/153366704/d64a41e8-b9ae-4841-bfa3-060d8a43c5c6)

Then web service responds in JSON
![geoservice-rest](https://github.com/jeltechnologies/geoservices/assets/153366704/3ae5b373-c117-4831-9b8b-911c72258397)
Change the URL to where the web service is deployed, for example `http://localhost:8080/geoservices`.

It is of course also possible to simply call the web service directly. Use the method `GET` with following parameters for the web service: 
- `latitude` for the latutide and `longitude` for longitude or
- `latlon` for a comma seperated latitude and longitude

The project geoservice-datamodel contains Java classes for this service. This may come handy for Java clients of the web service.  

## Accuracy
The accuracy of the service varies per country. The accuracy is excellent in North America, China and  most European countries. There are exceptions, for example for Sweden, the accuracy is less good, because there is less quality open data available.

# Installation and configuration
To run the geoservice you will need:
- A Java web container, preferably Apache Tomcat
- PostgreSQL database server, which will be used to store more than 100 million addresses. Create an empty database with the name "geoservices"
- Download the PostgrSQL JDBC driver (https://jdbc.postgresql.org/), which must be placed in tomcat/lib
- Create a JNDI resource to the PostgreSQL database with the name `jdbc/geoservices`. This is configured in Tomcat in conf/context.xml. 
- Example context.xml:
 ```
  <Resource 
		name="jdbc/geoservices" 
		auth="Container"
		type="javax.sql.DataSource" 
		username="******"
		password="******" 
		url="jdbc:postgresql://<server:port>/geoservices"
		driverClassName="org.postgresql.Driver"
		initialSize="10" 
		maxTotal="25"
		maxIdle="5"
		defaultAutoCommit="false"
	/>
  ```

## Configuration
- Create a YAML file with the following contents, change the dataFolder to the location where you store the data files.
- ```
  dataFolder: C:\Projects\Geoservices\data
  refreshOpenStreetDataCSV: false
  cache:
    useCache: true
    maxCacheSize: 150000
    expiryTimeMinutes: 2
    scheduleCacheCleanMinutes: 5
  ```
- Create an environment variable called `GEOSERVICES_CONFIG` which points to the YAML configuration file.

# Data files
Download the following files from Opendatasoft and place them in your dataFolder. The files from Opendatasoft are mandatory. Without these files the geoservice does not work.
- 142 thousand cities from https://public.opendatasoft.com/explore/dataset/geonames-all-cities-with-a-population-1000/table/?disjunctive.cou_name_en&sort=name (download the CSV file)
- 1.5 million postal codes from https://public.opendatasoft.com/explore/dataset/geonames-postal-code/export/ (download the CSV file)

Optionally download houses information Openstreetdata. More houses files means better accuracy, but larger memory consumption. The geoservice has been tested with all houses files from Openstreetdata, which are 109 million house addresses.
- Download the houses files from https://openstreetdata.org/#addresses. The streets and addresses files are not needed.
- Unzip the `*-houses.tsv.gz` to `*.houses.tsv` and copy them to the dataFolder.

# For the nerds

## Performance
The performance depends on the CPU used. The system is designed to supports multiple requests in parallel. Threading is handled by the web container and/or cloud infrastructure. A built-in cache is used to improve performance for lookups of series of pictures taken at the same location. This cache is configured in the YAML file.

## Startup performance
The first time the application runs, it will read all .tsv files and insert them into the PostgreSQL database. This can take around 30 minutes to complete. 
After this the next startup will take just a few minutes. To force a reload of the .tsv files, you may set `refreshOpenStreetDataCSV` to `true` in the YAML file, or simply drop all database tables and restart the web service.

## Memory usage
Coordinates are kept in memory, 109 million house addresses will take around 8 GB of heap memory. To reduce memory usage, simply remove houses files you do not need. 







  



