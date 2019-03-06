# Artificial-Intelligence-DD2380

---> DUCK HUNT

To run on computer labs do the following:

1) COMPILE: javac *.java

2) CREATE PIPE: mkfifo /tmp/player2server /tmp/server2player

3) RUN: java Main server < /tmp/player2server | java Main verbose > /tmp/player2server
