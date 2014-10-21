package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;

public abstract class AbstractJobCallable implements JobCallable {

    private Long _realJobStart;
    protected String _name;

    abstract protected URI internalCall() throws Exception;;

    @Override
    public URI call() throws Exception {
        _realJobStart = System.currentTimeMillis();
        return internalCall();
    }

    @Override
    public Long getRealJobStart() {
        return _realJobStart;
    }

    @Override
    public String getName() {
        return _name;
    }

}
