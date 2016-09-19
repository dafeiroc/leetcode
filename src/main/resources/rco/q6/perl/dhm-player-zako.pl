#!/usr/bin/perl
use FindBin;
use IO::Socket::INET;
use IO::Socket::UNIX;
use strict;
use warnings;
require "$FindBin::Bin/dhm-lib.pm";

package Zako;

sub main {
    my $name = shift @ARGV || die 'No name';
    my $port = shift @ARGV;
    my $S = ($port ? 
        IO::Socket::INET->new(PeerAddr => 'localhost', PeerPort => $port) :
        IO::Socket::UNIX->new(Peer => "$FindBin::Bin/dhm.sock")) || die $!;
    my $z = Zako->new($name, $S);
    <$S>; #NAME
    print $S $name."\n";
    $z->run;
}

sub new {
    my $self = bless {}, shift;
    $self->{name} = shift;
    $self->{sock} = shift;
    $self->init;
}

sub classify {
    my %cn = map{ $_ => $_ } @{shift->{cns}};
    my @cg;
    
    foreach my $s (@{Card->SUITS}){ #seq
        for(my $i = 0; $i <= 11; $i++){
            my $cn = $cn{$s.Card->NCHRS->[$i]} || next;
            my @cn = ($cn);
            push @cn, $cn while $i <= 11 && ($cn = $cn{$s.Card->NCHRS->[++$i]});
            push @cg, CardGroup->create(map{ delete $cn{$_} } @cn) if @cn >= 3;
        }
    }
    
    foreach my $nc (@{Card->NCHRS}){ #pair
        my @s = grep{ $cn{$_.$nc} } @{Card->SUITS};
        push @cg, CardGroup->create(map{ delete $cn{$_.$nc} } @s) if @s >= 2;
    }
    
    push @cg, CardGroup->create($_) for values %cn;
    \@cg;
}

sub init {
    my $self = shift;
    $self->{bind} = undef; #しばり状態
    $self->{cgs}  = undef; #CardGroups
    $self->{cns}  = [];
    $self->{fin}  = undef; #上がり札
    $self->{pre}  = undef; #直前のCard Names
    $self->{rev}  = 0;     #革命状態
    $self;
}

sub run {
    my $self = shift;
    my $S = $self->{sock};
    
    while(my $line = <$S>){
        my($ev, @args) = split /[ \r\n]/, $line;
        if($ev eq 'GAME'){
            $self->init;
        }elsif($ev eq 'DEAL'){
            push @{$self->{cns}}, @args;
        }elsif($ev eq 'EXCH'){
            <$S>; #<<
            my $n = $args[0];
            my($i, $j) = $n > 0 ? (0, $n) : ($n, -$n);
            print $S join(' ', splice @{$self->{cns}}, $i, $j)."\n";
        }elsif($ev eq 'REDY'){
            CardGroup->sort($self->{cgs} = $self->classify);
        }elsif($ev eq 'TURN'){
            if($args[0] ne $self->{name}){
                $self->{pre} = $args[1] if $args[1];
                next;
            }
            <$S>; #<<
            print $S $self->select, "\n";
        }elsif($ev eq 'BIND'){
            $self->{bind} = $args[0];
        }elsif($ev eq 'REVO'){
            CardGroup->sort($self->{cgs}, $self->{rev} = $args[0]);
            $self->set_fin;
        }elsif($ev eq 'FAIL'){
            print $line if $args[0] eq $self->{name};
        }elsif($ev eq 'TEND'){
            $self->{pre} = $self->{bind} = undef;
        }elsif($ev eq 'GEND'){
        }else{
            die $ev;
        }
    }
}

sub select {
    my $self = shift;
    my $pre = $self->{pre} && CardGroup->create(split /,/, $self->{pre});
    my($i, $cg) = (-1);
    foreach(@{$self->{cgs}}){
        $i++;
        next if $pre && !$_->is_valid_for($pre, $self->{rev})
         || $self->{bind} && $_->is_bound($self->{bind})
         || $self->{fin} && $_ eq $self->{fin};
        ($cg) = splice(@{$self->{cgs}}, $i, 1);
        last;
    }
    return '' if !$cg; #PASS
    $self->set_fin if @{$self->{cgs}} == 1 || $cg->is_fin_ok($self->{rev});
    join(' ', @{$cg->{cns}});
}

sub set_fin { #上がり札予約
    my $self = shift;
    return $self->{fin} = undef if @{$self->{cgs}} == 1;
    my $fin;
    foreach(@{$self->{cgs}}){
        next if !$_->is_fin_ok($self->{rev});
        if(!$fin){
            $fin = $_;
        }else{
            $fin = undef;
            last;
        }
    }
    $self->{fin} = $fin;
}

Zako->main;
