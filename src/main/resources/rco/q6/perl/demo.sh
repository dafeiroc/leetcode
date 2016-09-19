#!/bin/sh
perl dhm-player-zako.pl A 8000 &
perl dhm-player-zako.pl B 8000 &
perl dhm-player-zako.pl C 8000 &
perl dhm-player-zako.pl D 8000 &
telnet localhost 8000
