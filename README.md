# RealTimeChat-App

A simple multi-client console chat written in Java (TCP sockets).

## Run
```bash
mvn -q -e -DskipTests package
java -cp target/RealTimeChat-App-1.0.jar com.realtime.chat.Server 5000
# New terminal(s):
java -cp target/RealTimeChat-App-1.0.jar com.realtime.chat.Client localhost 5000
```

Type `/quit` to exit a client.
