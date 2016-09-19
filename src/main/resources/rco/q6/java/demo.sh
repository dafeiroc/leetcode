#!/bin/sh
java -cp bin dhm.player.Zako A 8000 &
java -cp bin dhm.player.Zako B 8000 &
java -cp bin dhm.player.Zako C 8000 &
java -cp bin dhm.player.Zako D 8000 &
telnet localhost 8000
