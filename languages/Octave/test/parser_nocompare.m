## Copyright (C) 2010-2015 John W. Eaton
##
## This file is part of Octave.
##
## Octave is free software; you can redistribute it and/or modify it
## under the terms of the GNU General Public License as published by
## the Free Software Foundation; either version 3 of the License, or (at
## your option) any later version.
##
## Octave is distributed in the hope that it will be useful, but
## WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
## General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with Octave; see the file COPYING.  If not, see
## <http://www.gnu.org/licenses/>.

## Tests for parser problems belong in this file.
## We need many more tests here!

## Test cell construction operator {}

get = {1 2 {3 4}};
expect = {1,2,{3,4}};
#{

#}
get = {1, 2 {3 4}};
expect = {1,2,{3,4}};
#{

#}
get = {1 2, {3 4}};
expect = {1,2,{3,4}};
#{

#}
get = {1 2 {3, 4}};
expect = {1,2,{3,4}};
#{

#}
get = {1, 2, {3 4}};
expect = {1,2,{3,4}};
#{

#}
get = {1 2,{3 4}};
expect = {1,2,{3,4}};
#{

#}
get = {1 2,{3,4}};
expect = {1,2,{3,4}};
#{

#}
get = {1,2,{3 4}};
expect = {1,2,{3,4}};
#{

#}

## bug #43113 using null comma-separated list in constructor
z = cell (1,2,3,0,5);
get = {1, z{:}, 2};
expect = {1, 2};
#{

#}
get = {1; z{:}; 2};
expect = {1; 2};
#{

#}
get = {1 2; z{:}; 3 4};
expect = {1, 2; 3 4};
#{

#}
get = {1 2; 5 z{:} 6; 3 4};
expect = {1, 2; 5 6; 3 4};
#{

#}
## Tests for operator precedence as documented in section 8.8 of manual
## There are 13 levels of precedence from "parentheses and indexing" (highest)
## down to "statement operators" (lowest).
##

## Level 13 (parentheses and indexing)
## Overrides all other levels
a.b = 1;
get = a. b++;
expect = 1;

get = a.b;
expect = 2;

clear a;
a.b = [0 1];
b = 2;
get = a.b';
expect = [0;1];

get = !a .b;
expect = logical ([1 0]);

get = 3*a .b;
expect = [0 3];

get = a. b-1;
expect = [-1 0];

get = a. b:3;
expect = 0:3;

get = a. b>0.5;
expect = logical ([0 1]);

get = a. b&0;
expect = logical ([0 0]);

get = a. b|0;
expect = logical ([0 1]);

a.b = [1 2];
get = a. b&&0;
expect = false;

get = a. b||0;
expect = true;

a.b += a. b*2;
get = a.b;
expect = [3 6];


## Level 12 (postfix increment and decrement)
a = [3 5];
get = 2.^a ++;
expect = [8 32];

get = a;
expect = [4 6];

get = a--';
expect = [4; 6];

get = a;
expect = [3 5];

a = 0;
get = !a --;
expect = true;

get = -a ++;
expect = 1;

get = 3*a ++;
expect = 0;

get = a++-2;
expect = -1;

get = 1:a ++;
expect = 1:2;

get = 4>a++;
expect = true;

a = [0 -1];
get = [1 1] & a++;
expect = logical ([0 1]);

get = [0 0] | a++;
expect = logical ([1 0]);

a = 0;
get = 1 && a ++;
expect = false;

get = 0 || a --;
expect = true;

a = 5; b = 2;
b +=a ++;
get = b;
expect = 7;


## Level 11 (transpose and exponentiation)
a = 2;
get = 2 ^a++;
expect = 4;

get = a;
expect = 3;

get = 2 ^--a ^2;
expect = 16;

get = a;
expect = 2;

get = 2 ^++a;
expect = 8;

get = a;
expect = 3;

get = a' ^2;
expect = 9;

get = 2 ^sin(0);
expect = 1;

get = -2 ^2;
expect = -4;

get = 2 ^+1 ^3;
expect = 8;

get = 2 ^-1 ^3;
expect = 0.125;

get = 2 ^~0 ^2;
expect = 4;

get = !0 ^0;
expect = false;

get = 2*3 ^2;
expect = 18;

get = 2+3 ^2;
expect = 11;

get = [1:10](1:2 ^2);
expect = [1 2 3 4];

get = 3>2 ^2;
expect = false;

get = 1&0 ^0;
expect = true;

get = 0|0 ^0;
expect = true;

get = 1&&0 ^0;
expect = true;

get = 0||0 ^0;
expect = true;

a = 3;
a *= 0 ^0;
get = a;
expect = 3;


## Level 10 (unary plus/minus, prefix increment/decrement, not)
a = 2;
get = ++ a*3;
expect = 9;

get = -- a-2;
expect = 0;

get = a;
expect = 2;

get = ! a-2;
expect = -2;

get = [1:10](++ a:5);
expect = 3:5;

a = [1 0];
get = ! a>=[1 0];
expect = [false true];

a = 0;
get = ++ a&1;
expect = true;

get = -- a|0;
expect = false;

get = -- a&&1;
expect = true;

get = ++ a||0;
expect = false;

a = 3;
a *= ++a;
get = a;
expect = 16;


## Level 9 (multiply, divide)
get = 3+4 * 5;
expect = 23;

get = 5 * 1:6;
expect = [5 6];

get = 3>1 * 5;
expect = false;

get = 1&1 * 0;
expect = false;

get = 1|1 * 0;
expect = true;

get = 1&&1 * 0;
expect = false;

get = 1||1 * 0;
expect = true;

a = 3;
a /= a * 2;
get = a;
expect = 0.5;


## Level 8 (add, subtract)
get = [2 + 1:6];
expect = 3:6;

get = 3>1 + 5;
expect = false;

get = 1&1 - 1;
expect = false;

get = 0|1 - 2;
expect = true;

get = 1&&1 - 1;
expect = false;

get = 0||1 - 2;
expect = true;

a = 3;
a *= 1 + 1;
get = a;
expect = 6;


## Level 7 (colon)
get = 5:-1: 3>4;
expect = [true false false];

get = 1: 3&1;
expect = [true true true];

get = 1: 3|0;
expect = [true true true];

get = -1: 3&&1;
expect = false;

get = -1: 3||0;
expect = false;

a = [1:3];
a += 3 : 5;
get = a;
expect = [4 6 8];


## Level 6 (relational)
get = 0 == -1&0;
expect = false;

get = 1 == -1|0;
expect = false;

get = 0 == -1&&0;
expect = false;

get = 1 == -1||0;
expect = false;

a = 2;
a *= 3 > 1;
get = a;
expect = 2;


## Level 5 (element-wise and)
get = 0 & 1|1;
expect = true;

get = [0 1] & 1&&1;
expect = false;

get = 0 & 1||1;
expect = true;

a = 2;
a *= 3 & 1;
get = a;
expect = 2;


## Level 4 (element-wise or)
get = [0 1] | 1&&0;
expect = false;

get = [0 1] | 1||0;
expect = true;

a = 2;
a *= 0 | 1;
get = a;
expect = 2;


## Level 3 (logical and)
get = 0 && 1||1;
expect = true;

a = 2;
a *= 3 && 1;
get = a;
expect = 2;


## Level 2 (logical or)
a = 2;
a *= 0 || 1;
get = a;
expect = 2;


## Tests for operator precedence within each level where ordering should
## be left to right except for postfix and assignment operators.
clear;
## Level 13 (parentheses and indexing)
a.b1 = 2;
get = a.(strcat('b','1'))++;
expect = 2;

get = a.b1;
expect = 3;

b = {1 2 3 4 5};
get = b{(a. b1 + 1)};
expect = 4;

b = 1:5;
get = b(a. b1 + 1);
expect = 4;

get = [2 3].^2';
expect = [4; 9];


## Level 12 (postfix increment and decrement)
## No tests possible since a++-- is not valid

## Level 11 (transpose and exponentiation)
get = 2^3**2;
expect = 64;

get = [2 3].^2.';
expect = [4;9];

get = [2 3].'.^2;
expect = [4;9];

get = 3*4i'.';
expect = 0 - 12i;

get = 3*4i.'.';
expect = 0 + 12i;

get = 2^-4^3;
expect = (1/16)^3;

get = 2^+4^3;
expect = 16^3;

get = 2^~0^2;
expect = 4;


## Level 10 (unary plus/minus, prefix increment/decrement, not)
get = +-+1;
expect = -1;

a = -1;
get = !++a;
expect = true;

get = a;
expect = 0;

get = -~a;
expect = -1;

get = !~--a;
expect = true;

get = a;
expect = -1;


## Level 9 (multiply, divide)
get = 3 * 4 / 5;
expect = 2.4;

get = 3 ./ 4 .* 5;
expect = 3.75;

get = 2 * 4 \ 6;
expect = 0.75;

get = 2 .\ 4 .* 6;
expect = 12;


## Level 8 (add, subtract)
get = -3 - 4 + 1 + 3 * 2;
expect = 0;


## Level 7 (colon)
## No tests possible because colon operator can't be combined
## with second colon operator.

## Level 6 (relational)
get = 0 < 1 <= 0.5 == 0 >= 0.5 > 0;
expect = true;

get = 1 < 1 == 0 != 0;
expect = true;

get = 1 < 1 == 0 ~= 0;
expect = true;


## Level 5 (element-wise and)
## No tests possible.  Only one operator (&) at this precedence level
## and operation is associative.

## Level 4 (element-wise or)
## No tests possible.  Only one operator (|) at this precedence level
## and operation is associative.

## Level 3 (logical and)
a = 1;
get = 1 && 0 && ++a;
expect = false;

get = a;
expect = 1;


## Level 2 (logical or)
a = 1;
get = 0 || 1 || ++a;
expect = true;

get = a;
expect = 1;


## Level 1 (assignment)
a = 2; b = 5; c = 7;
get = a += b *= c += 1;
expect = 42;

get = b == 40 && c == 8;

## Test creation of anonymous functions

af_in_cell = {@(x) [1 2]};
get = af_in_cell{1}();
expect = [1, 2];


R = @(rot) [cos(rot) -sin(rot) sin(rot) cos(rot)];
get = R(pi/2);
expect = [cos(pi/2), -sin(pi/2) sin(pi/2),cos(pi/2)];


## Check that xyz is tagged as a variable in the parser.  Both
## expressions must remain on one line for this test to work as
## intended.
xyz(1) = 1; xyz /= 1;
get = xyz;
expect = 1;


warning ("off", "Octave:num-to-str", "local");
a = [97 ... % comment
     'b'];
get = a;
expect = 'ab';



## Check that a cell array containing function handles is parsed
## correctly with or without commas.
a = {1, @sin, 2, @cos};
b = {1 @sin 2 @cos};
get = a;
expect = b;
#{
if (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}