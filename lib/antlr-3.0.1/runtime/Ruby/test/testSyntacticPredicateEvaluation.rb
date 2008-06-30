# [The "BSD licence"]
# Copyright (c) 2005 Martin Traverso
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
# 3. The name of the author may not be used to endorse or promote products
#    derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
# IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
# OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
# IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
# INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
# NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
# THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

require 'test/unit'
require 'antlr'

class TestSyntacticPredicateEvaluation < Test::Unit::TestCase

    def test_two_preds_with_naked_alt
        grammar = <<-END
	        s returns [result]
	        @init { @out = "" }: (a ';')+ { result = @out };

    			a
    			options {
    			  k=1;
    			}
    			:
    			  (b '.')=> b '.' { @out << "[alt 1]" }
    			| (b)=> b { @out << "[alt 2]" }
    			| c       { @out << "[alt 3]" }
    			  ;

    			b
    			@init { @out << "[enter b]" }
    			   : '(' 'x' ')' ;

    			c
    			@init { @out << "[enter c]" }
    			   : '(' c ')' | 'x' ;

    			WS : (' '|'\\n')+ { channel = 99 }
    			   ;
	    END

		  parser = Grammar::compile(grammar, "s")

      assert_equal("[enter b][enter b][enter b][alt 2]", parser.parse('(x) ;'))
      assert_equal("[enter b][enter b][alt 1]", parser.parse('(x). ;'))
	    assert_equal("[enter b][enter b][enter c][enter c][enter c][alt 3]", parser.parse('((x)) ;'))
    end

    def test_two_preds_with_naked_alt_not_last
        grammar = <<-END
          s returns [result]
          @init { @out = "" }: (a ';')+ { result = @out };

    			a
    			options {
    			  k=1;
    			}
    			  : (b '.')=> b '.' { @out << "[alt 1]" }
    			  | c       { @out << "[alt 2]" }
    			  | (b)=> b { @out << "[alt 3]" }
    			  ;
    			b
    			@init { @out << "[enter b]" }
    			   : '(' 'x' ')' ;
    			c
    			@init { @out << "[enter c]" }
    			   : '(' c ')' | 'x' ;
    			WS : (' '|'\\n')+ {channel=99}
    			   ;
	    END

		  parser = Grammar::compile(grammar, "s")

	    assert_equal("[enter b][enter c][enter c][alt 2]", parser.parse("(x) ;"))
	    assert_equal("[enter b][enter b][alt 1]", parser.parse("(x). ;"))
	    assert_equal("[enter b][enter c][enter c][enter c][alt 2]", parser.parse("((x)) ;"))
    end

    def test_lexer_pred
        Object::const_set('OUT', [])
        grammar = <<-END
            s returns [result]
            @init { OUT.clear }: A { result = OUT.join };

			A options {k=1;}
			  : (B '.')=>B '.' { OUT << "alt1" }
			  | B { OUT << "alt2" }
			  ;

			fragment
			B : 'x'+ ;
	    END

		  parser = Grammar::compile(grammar, "s")

	    assert_equal("alt2", parser.parse('xxx'))
	    assert_equal("alt1", parser.parse('xxx.'))
    end
end
