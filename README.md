docker run --name my-sql -d -e TZ=Asia/Seoul --env-file=mysql.env -p 3306:3306 -it mysql --default-time-zone=+09:00

