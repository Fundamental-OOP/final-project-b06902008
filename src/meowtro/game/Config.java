package meowtro.game;

import java.util.Properties;
import java.util.Set;
import java.io.FileInputStream;
import java.io.IOException;

public class Config {

    String defaultConfigPath = null;
    private Properties properties = new Properties();

    public Config(String defaultConfigPath, String localConfigPath) {
        try {
            // load default config
            FileInputStream in = new FileInputStream(defaultConfigPath);
            this.properties.load(in);
            in.close();

            if (localConfigPath != null) {
                // load local config
                in = new FileInputStream(localConfigPath);
                Properties localProperties = new Properties();
                localProperties.load(in);
                in.close();
    
                // merge local config to this.properties
                for (Object key: localProperties.keySet()) {
                    String keyStr = (String) key;
                    if (this.properties.containsKey(keyStr)) {
                        this.properties.setProperty(keyStr, localProperties.getProperty(keyStr));
                    }
                }
            }

            // store config_path for writing back
            this.defaultConfigPath = defaultConfigPath;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return this.properties.getProperty(key);
    }

    public Set<String> getAllKeys() {
        return this.properties.stringPropertyNames();
    }

    public void printConfig() {
        this.properties.list(System.out);
    }
    
    /****** MAIN ******/
    public static void main(String[] args) {
        Config config = new Config("./defaultConfig.properties", "./localConfig.properties");
        config.printConfig();
    }
}
