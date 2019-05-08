# CAMBRIDGE GRAND JUNCTION - MAPC's routing scenario engines  


### large scale data trip/route modeling setup and infrastructure 
The basic idea is to use a routing engine to model a very large number of routes/trips (block to block)-- 
Alternative future scenarios are made via editing the street network data before using it as the input dataset of the routing engine, and running again for the same routes/trips (block to block).
Running the complete process included the following components:
- OpenStreetMaps data extracted as a *.osm.pbf files for a given geography.
- Graphhopper, an open-source navigation, and routing engine (extended with additional functionalities and customized vehicle types)
- JOSM, an open-source and cross-platform application for editing the OSM data.
- scripting plugin for running scripts in JOSM (needs to be installed in JOSM)
- Docker technology for containerization.
- Docker Swarm for running a computing cluster (4 servers--nodes, with cumulatively 50 cpus, 140 GB ram, and 1 TB of disk space)
- Postgres database for storing, querying, retrieving  data.
- NGINX web server, for reverse proxy, load ballancing.
![modeling diagram](d1.png)

### JOSM scripting
MAPC processed the OpenStreetMap datafiles, and for 'type:way' added tags for 'stress_level:high' vs. 'stress_level:low' based on a set of conditions, modifying the methodoly used in the Bicycle Network Analysis by [PeapleForBikes](https://bna.peopleforbikes.org/#/methodology). The `josm_script_stress_lev.js` is used in JOSM's _scripting plugin_, and each OSM file was processed before building the rouging engine (graphhopper) in each scenario.


### routing engine
#### using and extending [Graphhopper routing engine](https://github.com/graphhopper/graphhopper)

Graphhopper's repository was mirrored on version 0.12 to the folder `graphhopper'.

MAPC's changes to the Graphhopper's base code included:
- following the Graphhopper's developer's guidelines for [creating a new routing profile](https://github.com/graphhopper/graphhopper/blob/master/docs/core/create-new-flagencoder.md), MAPC extended the code base for an additional cycling profiles. The added profile: `mapcrider2`, which combines `bike2`, Graphhopper's biking profile that counts for elevation in routing, and `racingbike` profiles with additional priorities such as preferring _track_, _cycle track_ and _cycleway_ road classes (highway tags) over _trunk_, _primary_, _secondary_, and _residential_ road classes, avoiding roads without a cycling lane, preferring streets with lower max-speed, and preferring routes that are part of local or regional cycling network.
- MAPC added the features for returning additional details of the routes, including surface types and stress level (stress_level tag is added to the OSM data by MAPC)
- In additional, the web module is altered to include MAPC's logo+information about the project.

## run the four scenario routing engines with docker-compose 

This project needs to launch four graphhopper routing engine instances that each are built using an incrementally different version of the osm street network (for different scenarios). This could be done via servung to different ports:
- base scenario: port 8989
- scenario 1- only grand junction bike path is added: port 7979
- scenario 2- cambridge cycling vision added: port 6969
- scenario 3- regional cycling network vision is added: port 5959 
-- _Note: nginx web server is used as a reverse proxy to map the ports to 8000 to 8003 on MAPC's `ds-geoserver` which was used to run the analysis.



## setup for development


The easiest way to setup a development environment for Graphhopper is their own suggestion which could be found [here](https://github.com/graphhopper/graphhopper/blob/master/docs/core/quickstart-from-source.md#start-development) and is using IntelliJ IDEA-- this needs a licence, but is free if you set up a educational account with university email, or buy a licence) but it's also possible with NetBeans, Eclipse etc.



## running a stand alone routing engine with the two profiles  

Make sure  jdk8 is installed and working-- `latest.osm.obf` is the OSM data extract used for the base scenario (downloaded from OpenStreetMap Jan 2019).   

```export JAVA_OPTS="-Xmx2g -Xms2g"```

to build a routable graph in saving into a folder: `./latest.osm-gh `:  

```
./graphhopper.sh -a import -i ./latest.osm.pbf -o ./latest.osm-gh 

```

run the routing engine with:

```
./graphhopper.sh -a web -i ./latest.osm.pbf -o ./latest.osm-gh

```
