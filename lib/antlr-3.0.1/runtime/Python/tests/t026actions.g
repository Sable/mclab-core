grammar t026actions;
options {
  language = Python;
}

@lexer::init {
    self.foobar = "blarz"
}

prog
@init {
    print "init prog"
}
@after {
    print "after prog"
}
    :   IDENTIFIER EOF
    ;
    catch [ RecognitionException, exc ] {
        print "Caboom " + str(exc)
        raise
    }
    finally {
        print "Finally I'm home!"
    }


IDENTIFIER
    : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
        {
            # a comment
          print "foo"
            print $text, $type, $line, $pos, $index, $channel, $start, $stop
            if True:
                print "bar"
        }
    ;

WS: (' ' | '\n')+;
