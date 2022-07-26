# Demo to Test SunEC sm2p256v1 with Bouncy Castle

Generate the sm2p256v1 key pairs using patches from:
https://github.com/HollowMan6/jdk/tree/sm2

Test the key pairs by digital signature signing and verifying using Bouncy Castle.

## Build
```sh
mvn clean verify
```

## Run
```sh
java -jar target/sm2.jar
```
