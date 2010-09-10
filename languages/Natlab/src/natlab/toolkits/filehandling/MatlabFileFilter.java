package natlab.toolkits.filehandling;

import java.io.*;

/**
 * A file filter for matlab files ending with .m
 */
public class MatlabFileFilter extends AbstractExtensionFileFilter
{

    private final String[] okFileExtensions = new String[] {"m"};

}
