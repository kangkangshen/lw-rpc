package org.archer.rpc;

import org.archer.rpc.meta.InterfaceMetaData;

import java.util.List;

public interface InterfaceDiscovery {

    InterfaceMetaData choose(Class<?> interfaceClazz, String version);

    List<InterfaceMetaData> discover(Class<?> interfaceClazz, String version);
}
