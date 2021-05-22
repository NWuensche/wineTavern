FROM archlinux:latest

RUN pacman -Syu --noconfirm
RUN pacman -S jdk8-openjdk maven mariadb --noconfirm #sets `java` as well

RUN mariadb-install-db --user=mysql --basedir=/usr --datadir=/var/lib/mysql
RUN sh -c ' /usr/bin/mysqld_safe --datadir=/var/lib/mysql &' && sleep 3 && mysql -u root -e "CREATE USER 'roota'@'localhost' IDENTIFIED BY 'test123';" && mysql -u root -e " GRANT ALL PRIVILEGES ON *.* TO 'roota'@'localhost';"  #no systemd, start mysql like that, have all to be on same line

ADD ./app /opt/app
WORKDIR /opt/app
RUN mvn clean install #Generate Jar, only with jar can I change port + it will be run only once and not on every startup

#EXPOSE 8080 Not supported by heroku

CMD sh -c ' /usr/bin/mysqld_safe --datadir=/var/lib/mysql &' && sleep 3 && java -Dserver.port=$PORT -jar ./target/*.jar  #Need to be on same line #Port Provided by heroku
