package org.archer.rpc.beans;

import org.archer.rpc.InterfaceDiscovery;
import org.archer.rpc.processor.DefaultInterfaceDiscovery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Deprecated
@Configuration
public class ArcherLwRpcAutoConfiguration {


    @Bean
    public InterfaceDiscovery interfaceDiscovery() {
        return new DefaultInterfaceDiscovery();
    }

}
