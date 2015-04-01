# build script to generate the scanner and parser for the shape propagation language.
# This should be integrated into the natlab ant build script.
# For now we use this script - the generated parser and scanner will be in the repository
# which is ok because they are small. When the scanner or grammer changes, this script
# has to be run to update the code


# build the scanner from lexer scpec, using jflex that is included in the library
# section of the natlab project
java -jar ../../../../../../../lib/jflex-1.4.1/lib/JFlex.jar shapeprop.flex 

# build the parser from the grammer, using beaver included in the natlab project
java -jar ../../../../../../../lib/beaver-0.9.6.2/lib/beaver.jar shapeprop.grammar 



