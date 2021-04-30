/* shiva created on 4/30/21 inside the package - utilities */
package utilities;

import java.io.*;

public class Utils {

    public static void dump(String path, Serializable resource) throws IOException {
        FileOutputStream f = new FileOutputStream(new File(path));
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(resource);
        o.close();
        f.close();
    }

    public static double log2(double val){
        return Math.log(val) / Math.log(2.0d);
    }
}
