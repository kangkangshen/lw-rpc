package org.archer.rpc;

import java.util.List;

public interface MultiRegistrar<ID, TYPE> extends Registrar<ID, List<TYPE>> {

    boolean registerInstance(ID id, TYPE instance);

    boolean removeInstance(ID id, TYPE oldInstance);

}
