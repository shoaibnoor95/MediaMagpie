package de.wehner.mediamagpie.common.simplenio.file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.simplenio.file.spi.MMFileSystemProvider;

@Service
public class MMFileSystemProviderFactory {

    private final List<MMFileSystemProvider> _providers = new ArrayList<MMFileSystemProvider>();

    @Autowired
    public MMFileSystemProviderFactory(List<? extends MMFileSystemProvider> providers) {
        _providers.addAll(providers);
    }

    public List<MMFileSystemProvider> getProviders() {
        return _providers;
    }
}
