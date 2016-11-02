/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package batooenhancer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.batoo.common.util.StringUtils;

import org.batoo.jpa.core.impl.instance.Enhancer;
import org.codehaus.plexus.util.FileUtils;
import org.objectweb.asm.ClassReader;

/**
 *
 * @author napoles
 */
public class BatooEnhancer {

    /**
     * File location for the persistence inputDirectory.
     *
     * @required
     */
    protected File inputDirectory;
    /**
     * File location for the persistence outputDirectory.
     *
     */
    protected File outputDirectory;
    /**
     * Classpath elements to use for enhancement.
     *
     * @required
     */
    protected List compileClasspathElements;
    /**
     * Comma seperated representation of excludes.
     *
     * @parameter default-value="";
     */
    private String excludes;
    /**
     * Comma seperated representation of includes.
     *
     * @parameter default-value="**\/*.class"
     */
    private String includes;

    public void execute() throws Exception {


        final URLClassLoader cl = this.extendRealmClasspath();

        final List<File> classes = this.findEntityClassFiles();

        for (final File classPath : classes) {
            try {
                String absolutePath = classPath.getAbsolutePath();



                if (absolutePath.endsWith("$Enhanced.class")) {
                    continue;
                }

                String className = this.getClassName(absolutePath);

                final Class<?> clazz = cl.loadClass(className);

                String newName = classPath.getName().replaceFirst(".class", "");

                final byte[] byteCode = Enhancer.create(clazz);
                final String outputFile = this.outputDirectory.getAbsolutePath() + File.separatorChar + newName + Enhancer.SUFFIX_ENHANCED + ".class";
                System.err.println("Writing  : " + outputFile);

                final FileOutputStream os = new FileOutputStream(outputFile);
                try {
                    os.write(byteCode);
                } finally {
                    os.close();
                }
            } catch (final Exception e) {
                e.printStackTrace();
                throw new Exception("Enhancement failed for " + classPath.getName());
            }
        }
    }

    /**
     * This will prepare the current ClassLoader and add all jars and local
     * classpaths (e.g. target/classes) needed by the OpenJPA task.
     *
     * @return the class loader
     *
     * @throws MojoExecutionException on any error inside the mojo
     */
    protected URLClassLoader extendRealmClasspath() throws Exception {
        final List urls = new ArrayList();

        for (final Iterator itor = this.compileClasspathElements.iterator(); itor.hasNext();) {
            final File pathElem = new File((String) itor.next());
            try {
                final URL url = pathElem.toURI().toURL();
                urls.add(url);
                System.err.println("Added classpathElement URL " + url);
            } catch (final MalformedURLException e) {
                throw new Exception("Error in adding the classpath " + pathElem, e);
            }
        }

        return new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), this.getClass().getClassLoader());
    }

    /**
     * Locates and returns a list of class files found under specified class
     * directory.
     *
     * @return list of class files.
     * @throws MojoExecutionException if there was an error scanning class file
     * resources.
     */
    private List findEntityClassFiles() throws Exception {
        List files = new ArrayList();

        try {
            files = FileUtils.getFiles(this.getEntityClasses(), this.includes, this.excludes);
        } catch (final IOException e) {
            throw new Exception("Error while scanning for '" + this.includes + "' in " + "'" + this.getEntityClasses().getAbsolutePath() + "'.", e);
        }

        return files;
    }

    /**
     * Returns File location for the persistence inputDirectory.
     *
     * @return File location for the persistence inputDirectory
     */
    private File getEntityClasses() {
        return this.inputDirectory;
    }

    public String getClassName(String classFile) throws Exception {
        InputStream in = new FileInputStream(classFile);
        ClassReader reader = new ClassReader(in);
        ClassPrinter cp = new ClassPrinter();
        reader.accept(cp, 0);
        return cp.classNane;

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        //FileUtils.getFiles(null, null, null);


        /*
         File file = new File(System.getProperty("user.dir") + File.separator);
        
        
         try {
         // Convert File to a URL
         URL url = file.toURL();          // file:/c:/myclasses/
         URL[] urls = new URL[]{url};

         System.err.println("Url:"+url);
         // Create a new class loader with the directory
         ClassLoader cl = new URLClassLoader(urls);

         // Load in the class; MyClass.class should be located in
         // the directory file:/c:/myclasses/com/mycompany
         Class cls = cl.loadClass("org.batoo.jpa.android.model.Address");
            
         System.err.println("--OK--");
            
         } catch (MalformedURLException e) {
         System.err.println("Error1:"+e.getMessage());
         } catch (ClassNotFoundException e) {
         e.printStackTrace();
         System.err.println("Error2:"+e.getMessage());
         }
         */

        BatooEnhancer batooEnhancer = new BatooEnhancer();


        /*
         options.addOption("v", "verbose", false, "Run with verbosity set to high")
         .addOption("h", "help", false, "Print usage")
         .addOption("c", "classpath", true, "The classpath separate by ,")
         .addOption("i","input-dir", true, "Input diretory")
         .addOption("o","out-dir", false, "Output diretory");
         */

        Option opt2 = OptionBuilder.hasArgs(1).withArgName("classpath")
                .withDescription("The classpath separate by ,").isRequired(true)
                .withLongOpt("classpath").create("c");

        Option opt3 = OptionBuilder.hasArgs(1).withArgName("output directory")
                .withDescription("This is the output directory").isRequired(false)
                .withLongOpt("output-dir").create("o");

        Option opt4 = OptionBuilder.hasArgs(1).withArgName("input directory")
                .withDescription("This is the input directory").isRequired(true)
                .withLongOpt("input-dir").create("i");

        Options options = new Options();
        options.addOption(opt2);
        options.addOption(opt3);
        options.addOption(opt4);

        CommandLine cmd = null;
        CommandLineParser parser = new PosixParser();
        HelpFormatter formatter = new HelpFormatter();

        try {

            cmd = parser.parse(options, args);


            //batooEnhancer.inputDirectory = new File("org\\batoo\\jpa\\android\\model\\");
            batooEnhancer.inputDirectory = new File(cmd.getOptionValue("i"));
            //batooEnhancer.includes = args[0];

            if (cmd.hasOption("o")) {
                batooEnhancer.outputDirectory = new File(cmd.getOptionValue("o"));
            } else {
                batooEnhancer.outputDirectory = batooEnhancer.inputDirectory;
            }


            batooEnhancer.compileClasspathElements = new ArrayList();
            
            String cp = cmd.getOptionValue("c");
            
            if(cp.indexOf(',') != -1){
                String[] splits = StringUtils.split(cp, ",");
                batooEnhancer.compileClasspathElements.addAll(Arrays.asList(splits));
            } else {
                batooEnhancer.compileClasspathElements.add(cp);
            }
            
            

        } catch (ParseException e) {
           // System.err.println("Error parsing cmdline.");
            System.err.println(e.getMessage());
            formatter.printHelp(batooEnhancer.getClass().getSimpleName() + " [OPTIONS] ", options);
            System.exit(1);
        }



        try {
            batooEnhancer.execute();
        } catch (Exception ex) {
            Logger.getLogger(BatooEnhancer.class.getName()).log(Level.SEVERE, null, ex);
             System.exit(1);
        }

    }
}
