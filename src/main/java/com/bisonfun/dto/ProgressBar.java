package com.bisonfun.dto;

import java.util.Set;

public interface ProgressBar<K, V extends Number> {
    Set<Progress<K, V>> getProgressParts();
    void addProgressParts(Progress<K, V> progressPart);
    float getPercentByKey(K key);
    boolean isEmpty();
    Progress<K, V> getProgressByKey(K key);
    V getTotal();
}
