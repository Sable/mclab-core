all: 
	java -Xmx500M -cp "/usr/local/lib/antlr-4.5-complete.jar:$CLASSPATH" org.antlr.v4.Tool Octave.g4
	javac Octave*.java Main.java
	mv *.class gen/
	mv OctaveBaseListener.java gen/
	mv OctaveLexer.* gen/
	mv OctaveParser.java gen/
	mv Octave.tokens gen/
	mv OctaveListener.java gen/

test: gen/Main.class
	cd gen
	java Main

tree:
	grun Octave program -gui

clean: 
	rm *~ gen/*
