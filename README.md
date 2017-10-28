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
$ curl -d '["Jane1", "Jane2"]' -H "Content-Type: application/json" -X POST http://localhost:9000/mix
```

### Responses
#### 200
```
{
  "status": "OK",
  "depositAddress": "e27083b0-80be-4054-b849-1324f5aacaf6"
}
```
depositAddress: Where the mixer has mapped your submitted addresses, and it will expect to see your deposit here to begin the mixing

#### 409
```
{
    "status": "ERROR",
    "error": "Address(es) have either been used or were unable to be validated",
    "invalidAddresses": [
        "Jessica1",
        "Jessica2"
    ]
}
```

#### 400
Edge cases
```
{
    "status": "ERROR",
    "error": "You must provide at least 1 non-empty address as mix recipient"
}
```

```
{
    "status": "ERROR",
    "error": "You must send a list of String addresses as mix recipients"
}
```

### Configuration
The following configuration options are available in `conf/application.conf`
```
rxu.jobcoin.mixer.fee = ".05" #5 percent. Must be a number the 1 is evenly divisible by
rxu.jobcoin.mixer.transferInterval = 10 #seconds
rxu.jobcoin.mixer.transferIncrements = ".1" #10 percent every interval
rxu.jobcoin.mixer.houseAddress = "ROGERS_HOUSE"
rxu.jobcoin.mixer.revenueAddress = "ROGERS_REVENUE_FROM_FEES"

jobcoin.api.url = "http://jobcoin.gemini.com/nuclei/api"
```
