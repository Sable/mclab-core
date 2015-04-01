def main():
    infile = open("test_master.txt","r")
    infileContent = infile.read();
    infile.close()

    testsRaw = infileContent.split("==========\n")

    for testRaw in testsRaw:
        testParts = testRaw.split("----------\n")

        name = testParts[0].strip()
        inSrc = testParts[1]
        outSrc = testParts[2]

        #print name + "-" + inSrc + "-" + outSrc + "="

        outFileForIn = open(name+".in","w")
        outFileForIn.write( inSrc )
        outFileForIn.close()

        outFileForOut = open(name+".out","w")
        outFileForOut.write( outSrc )
        outFileForOut.close()

if __name__ == "__main__":
    main()
