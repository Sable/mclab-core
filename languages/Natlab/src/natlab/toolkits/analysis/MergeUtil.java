package natlab.toolkits.analysis;

import java.util.HashMap;
import java.util.Map;

public class MergeUtil {
  public static <K, V extends Mergable<V>> Map<K, V> unionMerge(Map<K, V> m1, Map<K, V> m2) {
    return unionMerge(m1, m2, Mergable::merge);
  }

  public static <K, V> Map<K, V> unionMerge(Map<K, V> m1, Map<K, V> m2, Merger<V> merger) {
    Map<K, V> out = new HashMap<>(m1);
    m2.forEach((k, v) -> out.merge(k, v, merger::merge));
    return out;
  }

  private MergeUtil() {}
}
