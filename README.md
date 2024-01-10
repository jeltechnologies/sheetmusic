# Get the address from coordinates
This project is a web service that provides the address from coordinates.

## Why would I need this?
With this service you can use geolocation information for free, without limits.
You can run this service self-hosted, on your own server, or in the cloud.

Cloud services that provide the same service require signing up for API keys, throttle requests to 1 per second or cost easily 30 € per month. The cheaper cloud services most likely use the same data sources as this service.

## How does it work?
The address service is provided as REST interface with a JSON payload.

Use the method `GET geoservices/address` with following parameters:
- `latitude` for the latutide and `longitude` for longitude or
- `latlon` for a comma seperated latitude and longitude

Then web service responds in JSON
![geoservice-rest](https://github.com/jeltechnologies/geoservices/assets/153366704/3ae5b373-c117-4831-9b8b-911c72258397)
Change the URL to where the web service is deployed, for example `http://localhost:8080/geoservices`.

The service also comes with a simple web page, for testing puproses. Here you fill out coordinates
![geoservice-ui](https://github.com/jeltechnologies/geoservices/assets/153366704/d64a41e8-b9ae-4841-bfa3-060d8a43c5c6)

## Accuracy
The accuracy of the service varies per country. The accuracy is excellent in North America, China and  most European countries. There are exceptions. For example the accuracy is less good in Sweden, because there is less quality open data available.

# Installation and configuration
To run the geoservice you will need:
- A Java web container, preferably Apache Tomcat because this has been tested in Tomcat.
- Clone the repository and use Maven to build the projects `geoservices-datamodel` and `geoservices`.
- Copy the complied WAR file to tomcat/webapps.

## Configuration
- Create a YAML file with the following contents, change `dataFolder` to the location where you store the data files.
- ```
  dataFolder: C:\Projects\Geoservices\data
  useDatabase: false
  refreshOpenStreetDataCSV: false
  cache:
    useCache: true
    maxCacheSize: 150000
    expiryTimeMinutes: 2
    scheduleCacheCleanMinutes: 5
  ```
- Create an environment variable called `GEOSERVICES_CONFIG` which points to the YAML configuration file.
- After changing the YAML file, you must redeploy the service (WAR file) or restart Tomcat.

# Data files
The service uses data from both Opendatasoft and Openstreetdata. The data from Opendatasoft is mandatory, while Openstreetdata is optional. More data means more accurate results.

## Opendatasoft (mandatory)
The files from Opendatasoft are mandatory. Without these files the geoservice does not work. Download the following files from Opendatasoft and place them in the `dataFolder` configured in the YAML file. 
- 142 thousand cities from https://public.opendatasoft.com/explore/dataset/geonames-all-cities-with-a-population-1000/table/?disjunctive.cou_name_en&sort=name (download the CSV file)
- 1.5 million postal codes from https://public.opendatasoft.com/explore/dataset/geonames-postal-code/export/ (download the CSV file)
The files from Opendatasoft will find the city and country of each coordinate globally. This is a good starting point, but it does not provide the street information.

## Openstreetdata (optional)
The files from Openstreetdata are used to find the street name and house numbers in the address. Using Openstreetdata files is optional.
- Download the houses files from https://openstreetdata.org/#addresses. The streets and addresses files are not needed.
- Unzip the `*-houses.tsv.gz` to `*.houses.tsv` and copy them to `dataFolder` configured in the YAML file.

More houses files means better accuracy, but larger memory consumption.

The geoservice has been tested with all houses files from Openstreetdata, which are 109 million house addresses. 

### Database (optional)
By default all streets are loaded in memory. To reduce the memory consumption it is possible to use PostgreSQL database to store house address information.

Should you use the database or not? It depends on the countries used. Here are some examples:
- United States (31 million addresses): Without PostgreSQL: 13 GB memory. With PostgreSQL: 2 GB memory
- The Netherlands (10 million address). Without PostgreSQL:  4 GB memory. With PostgreSQL: 1 GB memory
- All countries (109 million addresses) will take around 8 GB when PostgreSQL is used.

Follow these steps to install and configure PostgrSQL:
- Set `useDatabase: true` in the YAML configuration.
- Install PostgreSQL.
- Create an empty database with the name "geoservices". There is no need to create database tables, the tables will be created automatically when the geoservice is started.
- Download the PostgrSQL JDBC driver from https://jdbc.postgresql.org/. Copy this driver (JAR file) to `tomcat/lib`.
- Add a JNDI resource with name `jdbc/geoservices` to the file `tomcat/conf/context.xml`. See the below example:
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

# For the nerds

## Performance
The performance depends on the CPU used. The system is designed to supports multiple requests in parallel. Threading is handled by the web container and/or cloud infrastructure.  
A built-in cache is used to improve performance for lookups of series of pictures taken at the same location. This cache is configured in the YAML file.

## Startup performance
When the service starts, all .tsv files are loaded. If the database is used then they will be inserted into the PostgreSQL database. This can take around 30 minutes to complete. The next time the service is started within a few minutes when the database is used. To force a reload of the .tsv files, you may set `refreshOpenStreetDataCSV` to `true` in the YAML file, or simply drop all database tables and restart the web service.

## Memory usage
To reduce memory usage, simply remove houses files you do not needed, or use PostgreSQL. Don't forget to redeploy the WAR file, or restart Tomcat after adding or removing files.







  



