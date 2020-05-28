package org.archer.rpc.processor;

import org.archer.rpc.InterfaceDiscovery;
import org.archer.rpc.constants.Delimiters;
import org.archer.rpc.meta.InterfaceMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DefaultInterfaceDiscovery implements InterfaceDiscovery {

    private final AtomicInteger counter = new AtomicInteger();
    @Autowired
    private InterfaceMetaRegister interfaceMetaRegister;

    @Override
    public InterfaceMetaData choose(Class<?> interfaceClazz, String version) {
        List<InterfaceMetaData> interfaceMetaData = interfaceMetaRegister.get(interfaceClazz.getName() + Delimiters.COLON + version);
        if (!CollectionUtils.isEmpty(interfaceMetaData)) {
            return interfaceMetaData.get(counter.getAndIncrement() % interfaceMetaData.size());
        } else {
            return null;
        }
    }

    @Override
    public List<InterfaceMetaData> discover(Class<?> interfaceClazz, String version) {
        return interfaceMetaRegister.get(interfaceClazz.getName() + Delimiters.COLON + version);
    }
}
