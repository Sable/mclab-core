Octave to Natlab frontend
Written in ANTLR4
Valerie Saunders Duncan
Winter 2016

See main mclab-core readme for license

To compile:
	java -Xmx500M -cp "/usr/local/lib/antlr-4.5-complete.jar:$CLASSPATH" org.antlr.v4.Tool Octave.g4
	javac Octave*.java Main.java

To run main (currently setup to run tests from test folder):
	java Main

To run with parse tree figure output:
	grun Octave program -gui
Fill in with desired code followed by carriage return and CTRL-d. 