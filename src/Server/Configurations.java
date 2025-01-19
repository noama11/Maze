package Server;

import java.io.*;
import java.util.Properties;

public class Configurations {  // Singleton class

    private Properties prop;
    private static Configurations instance = null;

    private static String configPath = "resources/config.properties";

    public static synchronized Configurations getInstance() {
        if (instance == null) {
            instance = new Configurations();
        }
        return instance;
    }

     private Configurations (){
         prop = new Properties();
         try (InputStream input = new FileInputStream(configPath)) {
             // load a properties file
             prop.load(input);

         } catch (IOException io) {
             io.printStackTrace();
         }
     }

    public int getThreadPoolSize() {
        String threadPoolSize = prop.getProperty("threadPoolSize");
        return Integer.parseInt(threadPoolSize);
    }


    public String getMazeGeneratingAlgorithm() {
        return prop.getProperty("mazeGeneratingAlgorithm");
    }

    public String getMazeSearchingAlgorithm() {
        return prop.getProperty("mazeSearchingAlgorithm");
    }

}
