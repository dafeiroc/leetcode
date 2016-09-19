#!/usr/bin/perl
use IO::Socket::INET;
use IO::Socket::UNIX;
use strict;
use warnings;

my($A, $Z) = (0, 25);
my @NWS;
for(1 .. 3){
    my %nw = map{ ($_ + 1) => rand } $A .. $Z;
    push @NWS, [sort{ $nw{$a} <=> $nw{$b} } keys %nw];
}
&main();

sub main {
    my %args = @ARGV;
    my($l, $n, $p) = ($args{-l} || 8001, $args{-n} || 100000, $args{-p} || 10000);
    
    my $ss = ($l =~ /^\d+$/ ?
        IO::Socket::INET->new(Listen => 1, LocalPort => $l, Reuse => 1) :
        IO::Socket::UNIX->new(Listen => 1, Local => $l)) || die $!;
    print "listening: $l\n";
    while(my $s = $ss->accept){
        my($req, $sum);
        while(my $buf = <$s>){
            my $c = &req($buf);
            $s->print("$c\r\n");
            $sum += $c;
            printf "req: %d, avg: %.2f\n", $req, $sum / $req if !(++$req % $p);
            last if $req >= $n;
        }
        $s->close;
    }
    $ss->close;
}

sub req {
    my $buf = shift;
    my($c, $l, $r) = (0, $A, $Z);
    for(my $i = 0; $i < @NWS; $i++){
        my $j = ord(substr($buf, $i, 1)) - ord('A');
        die $buf if $j < $l || $j > $r;
        $l = $A if ($l = $j - 1) < $A;
        $r = $Z if ($r = $j + 1) > $Z;
        my $nw = $NWS[$i];
        $c += $nw->[$j];
        &swap($nw);
    }
    $c;
}

sub swap {
    my $nw = shift;
    my $i = int(($Z + 1) * rand);
    my $j = $i;
    $j = int(($Z + 1) * rand) while $j == $i;
    @$nw[$i, $j] = @$nw[$j, $i];
}
