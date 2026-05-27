The AnimeWatchlist web-application can be used to store Anime titles in a local DB.
It lists the entries with alphabetical order. If an entry was given a date (intended to rewatch or check out if there
is a new Movie/Season for the entry), it will switch to the status "Watching" and be listed at the top, OVER the rest
of the list.

Setup:
-Checkout the code into your lokal IDE (I used IntelliJ)
-use the application.properties to store your MySQL-Credentials

Create Docker Container:
docker run -d \
        --name animewatch-db \
        -e MYSQL_ROOT_PASSWORD=pw \
        -e MYSQL_DATABASE=animewatch \
        -p 3306:3306 \
        -v "$PWD/mysql-data-backup:/var/lib/mysql" \
        mysql:8.0

Remove Container (deletes your entries, so back your data up first!):
docker rm -f animewatch-db

Restart Container:
docker start animewatch-db

Load DB state into your docker container (Arch-Linux):
docker exec -i animewatch-db mysql -uroot -ppw animewatch < /home/user/YOUR_BACKUP_DIRECTORY/anime_watchlist_backup.sql

For a default DB state, use the Entity-Classes for reference to create a MySQL template (too lazy to upload one rn).