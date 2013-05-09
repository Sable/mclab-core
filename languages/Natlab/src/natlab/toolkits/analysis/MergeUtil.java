package natlab.toolkits.analysis;

import java.util.Map;

import com.google.common.collect.Maps;

public class MergeUtil {
  private static <V extends Mergable<V>> Merger<V> naturalMerger() {
    return new Merger<V>() {
      @Override
      public V merge(V v1, V v2) {
        return v1.merge(v2);
      }
    };
  }

  public static <K, V extends Mergable<V>> Map<K, V> unionMerge(Map<K, V> m1, Map<K, V> m2) {
    return unionMerge(m1, m2, MergeUtil.<V>naturalMerger());
  }

  public static <K, V extends Mergable<V>> void mergePut(Map<K, V> map, K key, V value) {
    mergePut(map, key, value, MergeUtil.<V>naturalMerger());
  }
  
  public static <K, V> Map<K, V> unionMerge(Map<K, V> m1, Map<K, V> m2, Merger<V> merger) {
    Map<K, V> out = Maps.newHashMap(m1);
    for (Map.Entry<K, V> entry : m2.entrySet()) {
      mergePut(out, entry.getKey(), entry.getValue(), merger);
    }
    return out;
  }

  public static <K, V> void mergePut(Map<K, V> map, K key, V value, Merger<V> merger) {
    V currentValue = map.get(key);
    if (currentValue == null) {
      map.put(key, value);
    } else {
      map.put(key, merger.merge(currentValue, value));
    }
  }
  private MergeUtil() {}
}
