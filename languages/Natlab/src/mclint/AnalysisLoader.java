package mclint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.google.common.collect.Lists;

/**
 * Loads and instantiates analyses. All the ugly ClassLoader/JAR hell code goes here.
 * Right now we have a simplistic approach to plugins; we load each class in each jar
 * in the plugins directory, and add those that implement LintAnalysis to our list.
 * @author ismail
 *
 * TODO I am 100% sure this can be a lot less ugly.
 */
public class AnalysisLoader {
  public static List<LintAnalysis> loadAnalyses(Project project, File pluginDirectory) {
    AnalysisLoader loader = new AnalysisLoader();
    try {
      loader.loadAnalysisClasses(pluginDirectory);
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
    loader.instantiateAnalysisClasses(project);
    return loader.analyses;
  }

  private List<Class<LintAnalysis>> analysisClasses = Lists.newArrayList();
  private List<LintAnalysis> analyses = Lists.newArrayList();

  /**
   * Inspects the given jar files and retrieves an array of fully qualified class names.
   * @param jars an array of paths to jar files
   * @return an array of class names
   * @throws IOException
   */
  private String[] getAllClasses(String[] jars) throws IOException {
    List<String> classes = Lists.newArrayList();
    for (String jar : jars) {
      JarInputStream jarFile = null;
      try {
        jarFile = new JarInputStream(new FileInputStream(jar));
        JarEntry jarEntry = null;
        while ((jarEntry = jarFile.getNextJarEntry()) != null) {
          String name = jarEntry.getName();
          if (name.endsWith(".class")) {
            classes.add(name.replace("/", ".").substring(0, name.length() - ".class".length()));
          }
        }
      } finally {
        jarFile.close();
      }
    }
    return classes.toArray(new String[0]);
  }

  @SuppressWarnings("deprecation")
  private URLClassLoader getClassLoader(String[] jars) throws MalformedURLException {
    URL[] urls = new URL[jars.length];
    for (int i = 0; i < jars.length; ++i) {
      urls[i] = new URL("jar", "", new File(jars[i]).toURL() + "!/");
    }
    return new URLClassLoader(urls);
  }

  /**
   * Get all interfaces, including those implemented by superclasses.
   * @param c a class
   * @return its interfaces
   */
  private List<Class<?>> getInterfaces(Class<?> clazz) {
    List<Class<?>> interfaces = Lists.newArrayList();
    Class<?> current = clazz;
    while (current != null && current != Object.class) {  
      interfaces.addAll(Arrays.asList(current.getInterfaces()));
      current = current.getSuperclass();
    }
    return interfaces;
  }

  @SuppressWarnings("unchecked")
  private void loadAnalysisClasses(File pluginDirectory) throws MalformedURLException, IOException {
    String[] jars = pluginDirectory.list(new FilenameFilter() {
      @Override
      public boolean accept(File file, String name) {
        return name.endsWith(".jar");
      }
    });
    for (int i = 0; i < jars.length; ++i) {
      jars[i] = new File(pluginDirectory, jars[i]).getAbsolutePath();
    }
    String[] classes = getAllClasses(jars);
    URLClassLoader loader = getClassLoader(jars);
    for (String className : classes) {
      Class<?> clazz = null;
      try {
        clazz = loader.loadClass(className);
      } catch (ClassNotFoundException e) {
        continue;
      }
      if (getInterfaces(clazz).contains(LintAnalysis.class)) {
        analysisClasses.add((Class<LintAnalysis>)clazz);
      }
    }
  }

  /**
   * This method takes care of instantiating all the analysis classes. If any
   * exceptions occur while doing this, an appropriate warning message is
   * printed and we keep going, ignoring the offending class. This should be
   * configurable, so that a failed instantiation can be made to stop the whole process.
   */
  private void instantiateAnalysisClasses(Project project) {
    for (Class<LintAnalysis> clazz : analysisClasses) {
      String name = clazz.getName();
      Constructor<LintAnalysis> constructor = null;
      try {
        constructor = clazz.getConstructor(Project.class);
      } catch (SecurityException e) {
        System.err.println("Couldn't instantiate " + name + ". Is a security manager installed?");
      } catch (NoSuchMethodException e) {
        System.err.println(name + " does not implement a (Project) constructor.");
      }
      if (constructor == null) {
        continue;
      }
      try {
        analyses.add(constructor.newInstance(project));
      } catch (IllegalArgumentException e) {
        // This one shouldn't happen; it would be an error on our end.
        System.err.println("Caught an IllegalArgumentException while instantiating " + name + ".");
        System.err.println("This shouldn't happen. It is probably a bug.");
        e.printStackTrace();
      } catch (InstantiationException e) {
        System.err.println("Make sure " + name + " is not an abstract class!");
      } catch (IllegalAccessException e) {
        System.err.println("Make sure the (Project) constructor for " + name + " is public!");
      } catch (InvocationTargetException e) {
        System.err.println("Caught the following exception while instantiating " + name + ":");
        e.getTargetException().printStackTrace();
      }  
    }
  }
}
