package org.gradle.wrapper;
import java.io.*;import java.net.*;import java.nio.file.*;import java.util.*;import java.util.zip.*;
public final class GradleWrapperMain {
 public static void main(String[] args) throws Exception {
  Path project=Path.of(System.getProperty("user.dir")); Path props=project.resolve("gradle/wrapper/gradle-wrapper.properties");
  Properties p=new Properties();try(InputStream in=Files.newInputStream(props)){p.load(in);}
  String url=p.getProperty("distributionUrl").replace("\\:",":");String name=url.substring(url.lastIndexOf('/')+1);String version=name.replace("-bin.zip","").replace("-all.zip","");
  Path home=Path.of(System.getProperty("user.home"),".gradle","wrapper","dists",version,"local");Path executable=home.resolve(version).resolve("bin").resolve(isWindows()?"gradle.bat":"gradle");
  if(!Files.exists(executable)){Files.createDirectories(home);Path zip=home.resolve(name);System.out.println("Downloading Gradle distribution: "+url);try(InputStream in=new URL(url).openStream()){Files.copy(in,zip,StandardCopyOption.REPLACE_EXISTING);}unzip(zip,home);Files.deleteIfExists(zip);}
  List<String> command=new ArrayList<>();if(isWindows()){command.add("cmd");command.add("/c");}command.add(executable.toString());command.addAll(Arrays.asList(args));
  Process process=new ProcessBuilder(command).directory(project.toFile()).inheritIO().start();System.exit(process.waitFor());
 }
 static boolean isWindows(){return System.getProperty("os.name").toLowerCase().contains("win");}
 static void unzip(Path zip,Path dest)throws IOException{try(ZipInputStream in=new ZipInputStream(Files.newInputStream(zip))){ZipEntry e;while((e=in.getNextEntry())!=null){Path out=dest.resolve(e.getName()).normalize();if(!out.startsWith(dest))throw new IOException("Invalid zip entry");if(e.isDirectory())Files.createDirectories(out);else{Files.createDirectories(out.getParent());Files.copy(in,out,StandardCopyOption.REPLACE_EXISTING);if(out.toString().contains("/bin/gradle")&&!isWindows())out.toFile().setExecutable(true);}}}}
}
