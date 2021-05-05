/* shiva created on 4/30/21 inside the package - utilities */
package utilities;

import java.io.*;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class Utils {

    public static void dump(String path, Serializable resource) throws IOException {
        FileOutputStream f = new FileOutputStream(new File(path));
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(resource);
        o.close();
        f.close();
    }

    public static Object load(String path) throws IOException, ClassNotFoundException{
        FileInputStream fi = new FileInputStream(new File(path));
        ObjectInputStream oi = new ObjectInputStream(fi);
        Object res = oi.readObject();
        fi.close();
        oi.close();
        return res;
    }

    public static Map<String, Double> normalize(Map<String, Double> map){
        Double sum = map.values().stream().mapToDouble(f -> f.doubleValue()).sum();
        Map<String, Double> normalized = new HashMap<>();
        map.entrySet().forEach(entry -> normalized.put(entry.getKey(), entry.getValue()/ sum));
        return normalized;
    }

    public static <K,V> Map<K,V> filterTop(Map<K,V> map, int N, Comparator<V> valueComparator){
        List<Map.Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());
        Collections.sort(sortedEntries, (e1,e2) -> valueComparator.compare(e1.getValue(), e2.getValue()));
        return sortedEntries.subList(0, Math.min(N, sortedEntries.size()))
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    public static double log2(double val){
        return Math.log(val) / Math.log(2.0d);
    }
}
