#!/bin/sh
set -e

[ -f /run/secrets/db_user ] && export SPRING_DATASOURCE_USERNAME="$(cat /run/secrets/db_user)"
[ -f /run/secrets/db_password ] && export SPRING_DATASOURCE_PASSWORD="$(cat /run/secrets/db_password)"
[ -f /run/secrets/mail_user ] && export SPRING_MAIL_USERNAME="$(cat /run/secrets/mail_user)"
[ -f /run/secrets/mail_pass ] && export SPRING_MAIL_PASSWORD="$(cat /run/secrets/mail_pass)"

echo "Starting Spring Boot app..."
exec java -jar /app/app.jar
