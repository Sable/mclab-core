lexer grammar t029synpredgate;
options {
  language = Python;
}

FOO
    : A
    | B
    ;

fragment
A: ('ab')=> 'a';

fragment
B: ('ac')=> 'a';

