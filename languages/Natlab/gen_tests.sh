#!/bin/bash

TESTDIR="$1"
BINDIR="$2"
GENDIR="$3"

function gen() {
  java -cp $BINDIR $1 $TESTDIR/$2 $GENDIR
}

javac -d $BINDIR $(find $TESTDIR -name "*TestGenerator.java")
mkdir -p $GENDIR/natlab
gen natlab.ScannerTestGenerator scanner.testlist
gen natlab.ParserPassTestGenerator parserpass.testlist
gen natlab.ParserFailTestGenerator parserfail.testlist
gen natlab.MultiRewritePassTestGenerator simplificationspass.masterlist
