package org.archer.rpc;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface ServiceFactory {
    <T> T prepareService(@NotNull Class<T> interfaceClazz, @NotBlank String version);
}
