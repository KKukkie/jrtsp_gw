package org.kkukie.jrtsp_gw.media.bouncycastle.crypto.tls;

import org.kkukie.jrtsp_gw.media.bouncycastle.util.Arrays;

class TlsSessionImpl implements TlsSession
{
    final byte[] sessionID;
    final SessionParameters sessionParameters;
    boolean resumable;

    TlsSessionImpl(byte[] sessionID, SessionParameters sessionParameters)
    {
        if (sessionID == null)
        {
            throw new IllegalArgumentException("'sessionID' cannot be null");
        }
        if (sessionID.length > 32)
        {
            throw new IllegalArgumentException("'sessionID' cannot be longer than 32 bytes");
        }

        this.sessionID = Arrays.clone(sessionID);
        this.sessionParameters = sessionParameters;
        this.resumable = sessionID.length > 0
            && null != sessionParameters
            && sessionParameters.isExtendedMasterSecret();
    }

    public synchronized SessionParameters exportSessionParameters()
    {
        return this.sessionParameters == null ? null : this.sessionParameters.copy();
    }

    public synchronized byte[] getSessionID()
    {
        return sessionID;
    }

    public synchronized void invalidate()
    {
        this.resumable = false;
    }

    public synchronized boolean isResumable()
    {
        return resumable;
    }
}
