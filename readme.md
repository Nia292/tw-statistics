# TW Statistics Service
Java service that is able to pull statistics from an CE server via rcon. Requires Thrall Wars mod to work.

# Getting Started
Requirements:
* IDE of your choice (Recommended IntelliJ)
* (If not using IntelliJ) JDK 16
* (If not using IntelliJ) Maven
* MongoDb or Docker

Configuring servers:
1. Copy the ``src/main/resources/application.yaml`` to a ``src/main/resources/application.<name>.yaml``
2. Change the server locations
3. Add your own application.<name>.yaml to .gitignore
4. Remember <name>, e.g. Nia, it's your personal profile name

Starting the server:
1. Execute to maven goal ``sprig-boot:run`` with the config parameter ``spring.profiles.active=<name>``, e.g
``./mvnw spring-boot:run -Dspring-profiles.active=Nia``
2. Wait for the server to start
3. Go to ``http://localhost:8080/statistics/player-wallet?target=TARGETNAME``, where TARGETNAME is the name of your configured
server in application.<name>.yaml, and you should see some log output and eventually all player wallets

