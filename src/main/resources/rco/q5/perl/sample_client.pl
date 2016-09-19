#!/usr/bin/perl
use IO::Socket::INET;
use strict;
use warnings;

my $S = IO::Socket::INET->new(PeerAddr => 'localhost', PeerPort => 8001) || die;
for(my $i = 0; $i < 10000; $i++){
    print $S "AAA\n";
    <$S>;
}
close($S);
