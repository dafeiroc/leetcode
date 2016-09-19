#!/usr/bin/perl
use FindBin;
use strict;
use warnings;
require "$FindBin::Bin/dhm-lib.pm";

package Player;

sub new {
    my $self = bless {}, shift;
    ($self->{sock}, my $opt) = @_;
    $self->{debug} = $opt->{-d};
    $self;
}

sub cns { my $h = shift->{cset}; grep{ $h->{$_} } @{Card->NAMES} }

sub flush {
    my $self = shift;
    $self->{buf} .= shift if @_;
    $self->{sock}->print($self->{buf});
    $self->{buf} = '';
    $self;
}

sub recv {
    my $self = shift;
    my $ms = &Time::HiRes::time() * 1000;
    my $s = $self->flush("<<\r\n")->{sock};
    my $cns = [split /[\r\n ,]+/, scalar <$s>];
    $self->{msec} += (&Time::HiRes::time() * 1000 - $ms);
    $cns;
}

sub send {
    my($self, $msg) = (shift, join(' ', @_)."\r\n");
    #$self->{sock}->print($msg);
    $self->{buf} .= $msg;
    print ">>$self->{name} $msg" if $self->{debug} && $_[0] ne 'TURN';
    $self;
}

package Server;
use IO::Socket::INET;
use IO::Socket::UNIX;
use Time::HiRes;

sub main {
    my %opt;
    $opt{$_} = /^-[pn]$/ ? shift @ARGV : 1 while ($_ = shift @ARGV);
    Server->new->listen(\%opt);
}

sub new { bless {}, shift }

sub bcast {
    my($self, $x, $msg) = (shift, shift, join(' ', @_)."\r\n");
    print $msg if $self->{debug};
    foreach my $p (@{$self->{players}}){
        next if $x && $p eq $x;
        #$p->{sock}->print($msg);
        $p->{buf} .= $msg;
    }
    $self;
}

sub cmove {
    my($self, $from, $to, $cns) = @_;
    delete $from->{cset}{$_}, $to->{cset}{$_}++ for @$cns;
    $to->send(DEAL => @$cns);
}

sub exch {
    my($self, $f, $h, $n) = @_;
    my $cns = $h->send(EXCH => -$n)->recv;
    my %tmp = %{$h->{cset}};
    
    $cns = [($h->cns)[$n == 2 ? (-2, -1) : -1]] if @$cns != $n
     || grep{ !delete $tmp{$_} } @$cns
     || (sort{ $b <=> $a } map{ Card->load($_)->num } keys %tmp)[0] >
        (sort{ $a <=> $b } map{ Card->load($_)->num } @$cns)[0];
    $self->cmove($h => $f, $cns);
    
    $cns = $f->send(EXCH => $n)->recv;
    $cns = [($f->cns)[$n == 2 ? (0, 1) : 0]] if @$cns != $n || grep{ !$f->{cset}{$_} } @$cns;
    $self->cmove($f => $h, $cns);
    $self;
}

sub game {
    my($self, $p1) = @_;
    my $ps = $self->{players};
    $_->{rank} = 0 for @$ps;
    $ps = $self->{game} = [@$ps];
    push @$ps, shift @$ps while !$ps->[0]{cset}{Card->D3}; #ダイヤの3からスタート
    
    $self->bcast(0, REDY => map{ "$_->{name}=".(keys %{$_->{cset}}) } @$ps);
    
    my $rev;
    while(@$ps > 1){
        my @t = @$ps;
        my($bind, $pre, $next_p);
        while(@t > 1){
            my $p = shift @t;
            $p->{turn}++;
            
            my @h;
            if($p->{help}){
                @h = ('c='.join ',', $p->cns);
                push @h, "b=$bind" if $bind;
                push @h, 'r=1' if $rev;
                if($p->{help} eq '!'){
                    $h[0] =~ s/(([scdh])[0-9A-Z]+)/Card->COLOR->{$2}."$1\x1B[0m"/eg;
                    $h[$_] = "\x1B[31m$h[$_]\x1B[0m" for 1 .. $#h;
                }
                push @h, 'p='.join(',', @{$pre->{cns}}) if $pre;
            }
            
            my $cns = $p->send(TURN => $p->{name}, @h)->recv;
            if(!@$cns){ #PASS
                $self->bcast($p, TURN => $p->{name});
                next;
            }
            my($now, $f);
            if(my @ng = grep{ !delete $p->{cset}{$_} } @$cns){
                $f = 'Not owner';
            }else{
                $now = CardGroup->create(@$cns);
                $f = !$now ? 'Broken cards' :
                    $bind && $now->is_bound($bind) ? "Bound $bind" :
                    $pre && !$now->is_valid_for($pre, $rev) ?
                        'Invalid for '.join(',', @{$pre->{cns}}) : undef;
            }
            $f = 'Invalid fin' if !$f && !%{$p->{cset}} && !$now->is_fin_ok($rev);
            if($f){
                $self->bcast(0, FAIL => $p->{name}, "$f: ".join(',', @$cns))->lose($p);
                $next_p = $t[0] if $next_p && $next_p eq $p;
                next;
            }
            $self->bcast($p, TURN => $p->{name}, join(',', @$cns));
            $self->bcast(0, REVO => $rev = int !$rev) if @$cns >= 4; #革命
            if(!%{$p->{cset}}){
                $self->win($p);
                if($p1 && !$p1->{rank} && $p ne $p1){ #都落ち
                    $self->lose($p1);
                    @t = grep{ $_ ne $p1 } @t;
                }
                $p1 = undef;
                $next_p = $t[0];
            }
            if(!$now->is_seq && $now->{num} == 8 #8切り
            || $pre && $pre->is_jkr && $now->is_s3){ #スペ3
                $next_p = $p if !$p->{rank};
                last;
            }
            if($pre && !$bind && !$now->{has_jkr}){
                my($s1, $s2) = ($pre->{suits}, $now->{suits});
                $self->bcast(0, BIND => $bind = $s1) if $s1 && $s2 && $s1 eq $s2;
            }
            $pre = $now;
            push @t, $next_p = $p if !$p->{rank};
        }
        $self->bcast(0, 'TEND');
        push @$ps, shift @$ps while $next_p && $ps->[0] ne $next_p;
    }
    $self->lose(shift @$ps) if @$ps;
    $_->flush for @$ps;
}

sub gend {
    my($self, $p, $r) = @_;
    $self->bcast(0, GEND => $p->{name}, $p->{rank} = $r);
    my $ps = $self->{game};
    $ps->[$_] eq $p && splice(@$ps, $_, 1) && last for 0 .. $#$ps;
    $self;
}

sub listen {
    my($self, $opt) = @_;
    my $f = "$FindBin::Bin/dhm.sock";
    unlink $f if !$opt->{-p};
    my $S = ($opt->{-p} ? 
        IO::Socket::INET->new(Listen => 5, LocalPort => $opt->{-p}, Reuse => 1) :
        IO::Socket::UNIX->new(Listen => 5, Local => $f)) || die $!;
    my $ps = $self->{players} = [];
    while(@$ps < 5){
        my $s = $S->accept;
        my $p = Player->new($s, $opt);
        print $s "NAME?\r\n";
        (my $nm = <$s>) =~ tr/\r\n//d;
        die "Invalid name '$nm'" if $nm eq '' || $nm =~ /[ =]/;
        die "Existing name '$nm'" if grep{ $_->{name} eq $nm } @$ps;
        print "Accept player '$nm'\r\n";
        $p->{name} = $nm;
        $p->{help} = $nm =~ /([?!])$/ ? $1 : '';
        push @$ps, $p;
    }
    $self->{debug} = $opt->{-d};
    $self->start($opt->{-n} || 10);
    unlink $f;
}

sub lose {
    my($self, $p) = @_;
    my $ps = $self->ranking;
    !$ps->[$_ - 1] && return $self->gend($p, $_) for 5, 4, 3, 2, 1;
    die '?';
}

sub ranking {
    my @p;
    $_->{rank} && ($p[$_->{rank} - 1] = $_) for @{shift->{players}};
    \@p;
}

sub report {
    my($self, $n) = @_;
    my $t = &Time::HiRes::time() * 1000 - $self->{start_at};
    my $ms = 0;
    $ms += $_->{msec} for @{$self->{players}};
    print STDERR "#GAME $n\r\n";
    printf "name: %s, avg: %.2f (%.2fsec, %dt, %.2fms/t)\r\n",
        $_->{name}, $_->{rank_sum} / $n, $_->{msec} / 1000, $_->{turn}, $_->{msec} / $_->{turn}
        for sort{ $a->{rank_sum} <=> $b->{rank_sum} } @{$self->{players}};
    printf "total: %.2fsec, server: %.2fsec\r\n", $t / 1000, ($t - $ms) / 1000;
}

sub start {
    my($self, $n) = @_;
    $self->{start_at} = &Time::HiRes::time() * 1000;
    my $i;
    while(++$i <= $n){
        $self->{players} = $self->shuffle(@{$self->{players}}) if $i % 10 == 1; #席替え
        $self->bcast(0, GAME => $i);
        
        my $ps = $self->{players};
        $_->{cset} = {} for @$ps;
        my $cns = $self->shuffle(@{Card->NAMES});
        while(@$cns){
            foreach(@$ps){
                my $c = shift @$cns || last;
                $_->{cset}{$c}++;
            }
        }
        $_->send(DEAL => $_->cns) for @$ps;
        if($i > 1){ #EXCH
            my $ps = $self->ranking;
            $self->exch($ps->[0], $ps->[4], 2)->exch($ps->[1], $ps->[3], 1)->game($ps->[0]);
        }else{
            $self->game;
        }
        $_->{rank_sum} += $_->{rank} for @$ps;
        $self->report($i) if !($i % 100);
    }
    $self->report($n) if $n % 100;
}

sub shuffle { shift; [map{ $_->[1] } sort{ $a->[0] <=> $b->[0] } map{ [rand, $_] } @_] }

sub win {
    my($self, $p) = @_;
    my $ps = $self->ranking;
    !$ps->[$_ - 1] && return $self->gend($p, $_) for 1 .. 5;
    die '?';
}

Server->main;
