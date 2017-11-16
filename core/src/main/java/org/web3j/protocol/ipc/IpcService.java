package org.web3j.protocol.ipc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.web3j.protocol.Service;

/**
 * Ipc service implementation.
 */
public class IpcService extends Service {

    private static final Logger log = LoggerFactory.getLogger(IpcService.class);

    private final IOFacade ioFacade;

    public IpcService(IOFacade ioFacade, boolean includeRawResponses) {
        super(includeRawResponses);
        this.ioFacade = ioFacade;
    }

    public IpcService(IOFacade ioFacade) {
        this(ioFacade, false);
    }

    private ReentrantLock ioLock = new ReentrantLock();

    @Override
    protected InputStream performIO(String payload) throws IOException {
        log.debug(">> " + payload);
        String result;
        ioLock.lock();
        try {
            ioFacade.write(payload);
            result = ioFacade.read();
        } finally {
            ioLock.unlock();
        }
        log.debug("<< " + result);

        // It's not ideal converting back into an inputStream, but we want
        // to be consistent with the HTTPService API.
        return new ByteArrayInputStream(result.getBytes());
    }
}
