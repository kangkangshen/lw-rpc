package org.archer.rpc.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import lombok.Data;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
public class ArrayListMultiMap<K, V> implements Multimap<K, V> {


    private final Map<K, List<V>> container;

    public ArrayListMultiMap() {
        this.container = Maps.newHashMap();
    }

    public static <K, V> Multimap<K, V> create() {
        return new ArrayListMultiMap<>();
    }

    @Override
    public int size() {
        AtomicInteger size = new AtomicInteger();
        container.forEach((i, list) -> size.addAndGet(list.size()));
        return size.get();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object o) {
        return container.containsKey(o);
    }

    @Override
    public boolean containsValue(@Nullable Object o) {
        return container.values().stream().flatMap(Collection::stream).anyMatch(ele -> Objects.equals(ele, o));
    }

    @Override
    public boolean containsEntry(@Nullable Object o, @Nullable Object o1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean put(K o, V o2) {
        if (Objects.isNull(container.get(o))) {
            container.put(o, Lists.newArrayList());
        }
        List<V> val = container.get(o);
        val.add(o2);
        container.put(o, val);
        return true;
    }

    @Override
    public boolean remove(@Nullable Object o, @Nullable Object o1) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean putAll(@Nullable Object o, Iterable iterable) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean putAll(Multimap multimap) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Collection replaceValues(@Nullable Object o, Iterable iterable) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Collection removeAll(@Nullable Object o) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void clear() {
        container.clear();
    }

    @Override
    public Collection get(@Nullable Object o) {
        return container.get(o);
    }

    @Override
    public Set keySet() {
        return container.keySet();
    }

    @Override
    public Multiset keys() {
        throw new UnsupportedOperationException();

    }

    @Override
    public Collection values() {
        return container.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public Collection<Map.Entry<K, V>> entries() {
        throw new UnsupportedOperationException();

    }

    @Override
    public Map asMap() {
        throw new UnsupportedOperationException();
    }
}
