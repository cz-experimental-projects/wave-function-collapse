package wfc;

import java.io.InputStream;

public class Resources {
    public static final String RESOURCE_PATH;
    
    static {
        RESOURCE_PATH = Resources.class.getClassLoader().getResource("META-INF/").getPath().split("META-INF/")[0];
    }
    
    public static InputStream getStream(String filename) {
        return Resources.class.getClassLoader().getResourceAsStream(filename);
    }
}
