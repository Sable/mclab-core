
# remove all xml files
import os, fnmatch
for dirpath, dirnames, filenames in os.walk('./builtin/'):
    for f in fnmatch.filter(filenames,"*.xml"):
        if (f[:-3]+"m" in filenames):
            os.remove(os.path.join(dirpath,f))


# generate tests
import genTests

# generate results xml (in matlab)
os.system('matlab -nodisplay -r "genXml(true)"')

