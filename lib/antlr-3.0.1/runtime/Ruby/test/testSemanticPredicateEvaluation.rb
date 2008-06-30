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

class TestSemanticPredicateEvaluation < Test::Unit::TestCase

  def test_simple_cyclic_DFA_with_predicate
    grammar = <<-END
      s returns [result]
      @init { @out = "" }: a { result = @out };
      
      a : {false}? 'x'* 'y' { @out << "alt1"}
      | {true}?  'x'* 'y' { @out << "alt2"}
      ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("alt2", parser.parse("xxxy"));
  end


  def test_simple_cyclic_DFA_with_instance_var_predicate
    grammar = <<-END
      @init {
        @v = true
      }

      s returns [result]
      @init { @out = "" }: a { result = @out };

      a : {false}? 'x'* 'y' { @out << "alt1"}
        | {@v}?  'x'* 'y'   { @out << "alt2"}
      ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("alt2", parser.parse("xxxy"));
  end


  def test_predicate_validation
    grammar = <<-END
      s : {false}? 'x';
    END

    parser = Grammar::compile(grammar, "s")
    assert_raise RuntimeError do 
      parser.parse("x")
    end
  end

  def test_lexer_preds
    grammar = <<-END
      @lexer::members {
        def out
          @out
        end
      }
  
      @lexer::init {
        @p = false
        @out = ""
      }

      s returns [result]
        : a { result = lexer.out };


      a : (A|B)+ ;
      A : {@p}? 'a'  { @out << "token 1" } ;
      B : {!@p}? 'a' { @out << "token 2" } ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("token 2", parser.parse("a"));
  end

  def test_lexer_preds2
    grammar = <<-END
      @lexer::members {
        def out
          @out
        end
      }
    
      @lexer::init {
        @p = true
        @out = ""
      }

      s returns [result]
        : a { result = lexer.out };


      a : (A|B)+ ;
      A : {@p}? 'a' { @out << "token 1" } ;
      B : ('a'|'b')+ { @out << "token 2"} ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("token 1", parser.parse("a"));
  end

  def test_lexer_pred_in_exit_branch
    grammar = <<-END
      @lexer::members {
        def out
          @out
        end
      }
    
      @lexer::init {
        @p = true
        @out = ""
      }

      s returns [result]
        : a { result = lexer.out };


      a : (A|B)+ ;
      A : ('a' { @out << "1" })*
          {@p}? ('a' { @out << "2" })* ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("222", parser.parse("aaa"));
  end


  def test_lexer_pred_in_exit_branch2
    grammar = <<-END
      @lexer::members {
        def out
          @out
        end
      }
    
      @lexer::init {
        @p = true
        @out = ""
      }

      s returns [result]
        : a { result = lexer.out };

      a : (A|B)+ ;
      A : ({@p}? 'a' { @out << "1" })*
          ('a' { @out <<"2" })* ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("111", parser.parse("aaa"));
  end


  def test_lexer_pred_in_exit_branch3
    grammar = <<-END
      @lexer::members {
        def out
          @out
        end
      }
      
      @lexer::init {
        @p = true
        @out = ""
      }

      s returns [result]
        : a { result = lexer.out };

      a : (A|B)+ ;
      A : ({@p}? 'a' { @out << "1" } | )
          ('a' { @out << "2" })* ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("122", parser.parse("aaa"));
  end

  def test_lexer_pred_in_exit_branch4
    grammar = <<-END
      @lexer::members {
        def out
          @out
        end
      }
  
      @lexer::init {
        @out = ""
      }

      s returns [result]
        : a { result = lexer.out };

      a : (A|B)+ ;
      A @init { n = 0 } : ({ n < 2 }? 'a' { @out << n.to_s; n = n + 1})+
                          ('a' { @out << "x" })* ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("01xxx", parser.parse("aaaaa"));
  end

  def test_lexer_pred_in_cyclic_DFA
    grammar = <<-END
      @lexer::members {
        def out
          @out
        end
      }
  
      @lexer::init {
        @p = false
        @out = ""
      }

      s returns [result]
        : a { result = lexer.out };

      
      a : (A|B)+ ;
      A : {@p}? ('a')+ 'x'  { @out << "token 1" } ;
      B :      ('a')+ 'x' { @out << "token 2" };
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("token 2", parser.parse("aax"));
  end

  def test_lexer_pred_in_cyclic_DFA2
    grammar = <<-END
      @lexer::members {
        def out
          @out
        end
      }
  
      @lexer::init {
        @p = false
        @out = ""
      }

      s returns [result]
        : a { result = lexer.out };

      
      a : (A|B)+ ;
      A : {@p}? ('a')+ 'x' ('y')? { @out << "token 1" } ;
      B :      ('a')+ 'x' { @out << "token 2" } ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("token 2", parser.parse("aax"));
  end

  def test_gated_pred
    grammar = <<-END
      @lexer::members {
        def out
          @out
        end
      }
  
      @lexer::init {
        @out = ""
      }

      s returns [result]
        : a { result = lexer.out };

    
      a : (A|B)+ ;
      A : {true}?=> 'a' { @out << "token 1" } ;
      B : {false}?=>('a'|'b')+ { @out << "token 2" } ;
    END

    parser = Grammar::compile(grammar, "s")
    assert_equal("token 1token 1", parser.parse("aa"));
  end
end
