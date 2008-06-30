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

require 'tmpdir'
require 'tempfile'
require 'stringio'
require 'fileutils'

include FileUtils

class Grammar
  def self.debug= value
    @debug = value
  end
  
  def self.compile(grammar, top_rule = nil)
    type = ""
    type = "lexer" if top_rule.nil?

    name = "Grammar#{Time.now.to_i}#{rand(1000)}"

    grammar = <<-GRAMMAR
      #{type} grammar #{name};
      options { language = Ruby; }

      #{grammar}
    GRAMMAR

    tempfile = Tempfile.new("antlr")
    dirname = tempfile.path + ".dir"
    
    puts dirname if @debug
    
    Dir.mkdir(dirname)

    grammar_class = nil

    cd(dirname) do
      # write the grammar to a file
      File.open("#{name}.g", "w") { |f| f.puts grammar }

      # run antlr
      `java -cp #{ENV['CLASSPATH']} org.antlr.Tool #{name}.g`

      class_name = name + (type == "lexer" ? "Lexer" : "Parser")

      load(dirname + "/#{class_name}.rb")

      grammar_class = Class::const_get(class_name)

      # delete created files
      Dir.new(dirname).each { |file|
        rm_f(file) if file != ".." && file != "."
      } unless @debug
    end

    Dir.delete(dirname) unless @debug

    if type == "lexer"
      Lexer.new(grammar_class)
    else
      Parser.new(grammar_class, top_rule)
    end
  end

  class Lexer
    def initialize(grammar)
      @grammar = grammar
    end

    def parse(input)
      parser = @grammar.new(input)

      tokens = []
      loop do
        token = parser.next_token
        break if token == :EOF
        tokens << token
      end

      tokens
    end
  end

  class Parser
    attr_reader :top_rule
    
    def initialize(grammar, top_rule)
      @grammar = grammar
      @top_rule = top_rule
    end

    def parse(input)
      @grammar.new(input).send @top_rule
    end
  end
end

