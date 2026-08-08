#!/bin/sh

set -eu

postgres_data=/tmp/postgresql
postgres_socket=/tmp/postgresql-socket
postgres_started=false
application_pid=

cleanup() {
    if [ -n "$application_pid" ] && kill -0 "$application_pid" 2>/dev/null; then
        kill -TERM "$application_pid"
        wait "$application_pid" || true
    fi
    if [ "$postgres_started" = true ]; then
        su postgres -s /bin/sh -c "pg_ctl -D '$postgres_data' -m fast -w stop" >/dev/null
    fi
}
trap cleanup EXIT INT TERM

install -d -o postgres -g postgres "$postgres_data" "$postgres_socket"
printf 'dev\n' >/tmp/postgresql-password
chown postgres:postgres /tmp/postgresql-password
chmod 600 /tmp/postgresql-password
su postgres -s /bin/sh -c \
    "initdb -D '$postgres_data' --username=dev --pwfile=/tmp/postgresql-password --auth-local=trust --auth-host=scram-sha-256" \
    >/dev/null
su postgres -s /bin/sh -c \
    "pg_ctl -D '$postgres_data' -o '-h 127.0.0.1 -k $postgres_socket -p 5432' -w start" \
    >/dev/null
postgres_started=true
su postgres -s /bin/sh -c "createdb -h '$postgres_socket' -p 5432 -U dev spring-demo"

export SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:5432/spring-demo
export SPRING_DATASOURCE_USERNAME=dev
export SPRING_DATASOURCE_PASSWORD=dev
su spring -s /bin/sh -c \
    'exec java -XX:AOTCacheOutput=/tmp/application.aot -Xlog:aot=info -jar app.jar' &
application_pid=$!

attempt=0
until wget -q -T 2 -O /dev/null http://127.0.0.1:8080/actuator/health; do
    if ! kill -0 "$application_pid" 2>/dev/null; then
        wait "$application_pid"
    fi
    attempt=$((attempt + 1))
    if [ "$attempt" -ge 900 ]; then
        echo 'Application did not become ready during AOT training.' >&2
        exit 1
    fi
    sleep 0.1
done

for path in / /admin /table.xhtml; do
    wget -q -T 10 -O /dev/null "http://127.0.0.1:8080$path"
done

kill -TERM "$application_pid"
set +e
wait "$application_pid"
application_status=$?
set -e
application_pid=
if [ "$application_status" -ne 0 ] && [ "$application_status" -ne 143 ]; then
    echo "AOT training application exited with status $application_status." >&2
    exit "$application_status"
fi
if [ ! -s /tmp/application.aot ]; then
    echo 'AOT training did not produce /tmp/application.aot.' >&2
    exit 1
fi
install -o root -g root -m 0444 /tmp/application.aot /app/application.aot
