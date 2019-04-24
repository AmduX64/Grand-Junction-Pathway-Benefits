FROM openjdk:8-jdk
ENV JAVA_OPTS "-server -Xconcurrentio -Xmx4g -Xms4g -XX:+UseG1GC -XX:MetaspaceSize=100M -Ddw.server.applicationConnectors[0].bindHost=0.0.0.0 -Ddw.server.applicationConnectors[0].port=6969 -Djava.net.preferIPv4Stack=true -server -Djava.awt.headless=true -Xconcurrentio"
COPY sysctl.conf /etc/sysctl.conf
COPY limits.conf /etc/security/limits.conf
RUN mkdir -p /graphhopper
COPY graphhopper/. /graphhopper/
COPY b6969.config.yml /graphhopper/config.yml
# COPY ./latest.osm-gh/. /latest.osm-gh
# COPY entry-point.sh ./entry-point.sh
# COPY ./multielevationprovider/. ./multielevationprovider
# COPY latest.osm.pbf ./latest.osm.pbf
WORKDIR /graphhopper
RUN ./graphhopper.sh -a clean
RUN ./graphhopper.sh -a build -i ./latest.osm.pbf -o ./latest.osm-gh --port 6969
RUN ./graphhopper.sh -a import -i ./latest.osm.pbf -o ./latest.osm-gh --port 6969
# RUN ["chmod", "+x", "/webapp/start.sh"]
EXPOSE 6969
