### Word Count

## Descripton

This service implements simple counting of words from events grouped by event type, data is read from standard input.
Counting is performed using tumbling window of 10 seconds based on event time obtained from `timestamp` field in input events.
Counts from current window is exposed in json format over http under path `/current`.
Service binds to `127.0.0.1:8080` by default and you can call endpoint using `curl 127.0.0.1:8080/current`.
Additionally all closed windows are printed to stdout.

## Running

To run service using provided `blackbox` executable, run following in project root directory.
```
/path/to/blackbox.macosx | sbt run
```
If blackbox is located on remote machine you can use `ssh` and pipe results to local machine
```
ssh user@remote-machine /path/to/on/remote/blackbox.amd64 | sbt run
```

## Configuration

Following environment variables are supported to control behaviour of service
- `HTTP_HOST` for host to bind to
- `HTTP_PORT` for port to listen
- `WORD_COUNT_WINDOW_DURATION` for changing window duration

## Tests

Run following to run tests
```
sbt test
```
