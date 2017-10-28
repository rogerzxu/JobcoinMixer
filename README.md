# JobcoinMixer

## Running the Application
Assuming you have SBT and Java installed, simply run the Play app through sbt or activator

`sbt run`

`activator run`

Verify the server started correctly, visit `http://localhost:9000` in your browser, or

```
$ curl http://localhost:9000
Jobcoin Mixer
```

## API
There a single relevant endpoint which takes a list of Strings as the JSON body:

```
POST http://localhost/mix
```
