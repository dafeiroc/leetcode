use strict;
use warnings;

package Card;
my(%MAP, @NAMES);
my @NCHRS = (3 .. 10, qw/J Q K A 2/);
my @SUITS = qw/s c d h/;
{
    my %n = map{ $NCHRS[$_] => $_ + 3 } 0 .. $#NCHRS;
    foreach my $nc (@NCHRS){
        my $n = $n{$nc};
        Card->new($_.$nc, $n, $_) for @SUITS;
    }
    Card->new(Card->JKR, 16);
}
use constant {
    COLOR => {s => "\x1B[44m", c => "\x1B[42m", d => "\x1B[41m", h => "\x1B[45m"},
    D3    => 'd3',
    JKR   => 'JKR',
    NAMES => \@NAMES,
    NCHRS => \@NCHRS,
    SUITS => \@SUITS,
};

sub load { $MAP{$_[1]} || die $_[1] } #ClassMethod

sub new {
    my $self = bless [], shift;
    @$self = @_;
    $MAP{$_[0]} = $self;
    push @NAMES, $_[0];
}

sub name { $_[0]->[0] }

sub num { $_[0]->[1] }

sub suit { $_[0]->[2] }

package CardGroup;

sub create { #ClassMethod
    my $self = bless {}, shift;
    die if !@_;
    $self->{cns} = \@_;
    if(@_ == 1){
        my $c = Card->load($_[0]);
        @$self{qw/num suits type/} = ($c->num, $c->suit, 1);
        return $self;
    }
    my %c = map{ $_ => Card->load($_) } @_;
    $self->{has_jkr} = 1 if delete $c{Card->JKR};
    my %n = map{ $_->num => 1 } values %c;
    my @n = sort{ $a <=> $b } keys %n;
    my %s = map{ $_->suit => 1 } values %c;
    my @s = sort keys %s;
    if(@n == 1){
        @$self{qw/num suits type/} = ($n[0], join(',', @s), 'p'.@_);
    }else{
        return undef if @s > 1;
        my $n = $n[-1] - $n[0] + 1;
        return undef if @n != $n && (!$self->{has_jkr} || @n != $n - 1);
        @$self{qw/num suits type/} = ($n[0], $s[0], 's'.@_);
    }
    $self;
}

sub sort { #ClassMethod
    my($class, $cgs, $rev) = @_;
    @$cgs = sort{
        $a->{num} == $b->{num} ? $a->{type} cmp $b->{type} :
        (!$rev || $a->{num} == 16 || $b->{num} == 16) ? $a->{num} <=> $b->{num} :
        $b->{num} <=> $a->{num}
    } @$cgs;
}

sub is_bound { #しばり判定
    my($self, $bind) = @_;
    index($bind, $_) < 0 && return 1 for split /,/, ($self->{suits} || '');
    !1;
}

sub is_fin_ok { #上がりOK
    my($self, $rev) = @_;
    return 1 if $self->is_seq;
    my $n = $self->{num};
    !($n == 16 || $n == 8 || ($rev ? $n == 3 : $n == 15));
}

sub is_jkr { $_[0]->{num} == 16 }

sub is_s3 { $_[0]->{num} == 3 && $_[0]->{type} eq 1 && $_[0]->{suits} eq 's' }

sub is_seq { substr($_[0]->{type}, 0, 1) eq 's' }

sub is_valid_for {
    my($self, $pre, $rev) = @_;
    $self->{type} ne $pre->{type} ? !1 :
    $self->is_jkr ? !$pre->is_s3 :
    $pre->is_jkr  ? $self->is_s3 :
    $rev ? $self->{num} < $pre->{num} :
    $self->{num} > $pre->{num};
}

1;
