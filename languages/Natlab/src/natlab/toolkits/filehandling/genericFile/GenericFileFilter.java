package natlab.toolkits.filehandling.genericFile;
/**
 * a filter for files, analaguous to java.io.FileFilter
 */
public interface GenericFileFilter {
    public boolean accept(GenericFile file);
}
