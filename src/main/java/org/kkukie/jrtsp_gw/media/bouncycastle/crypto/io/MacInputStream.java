package org.kkukie.jrtsp_gw.media.bouncycastle.crypto.io;

import org.kkukie.jrtsp_gw.media.bouncycastle.crypto.Mac;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MacInputStream
    extends FilterInputStream
{
    protected Mac mac;

    public MacInputStream(
        InputStream stream,
        Mac         mac)
    {
        super(stream);
        this.mac = mac;
    }

    public int read()
        throws IOException
    {
        int b = in.read();

        if (b >= 0)
        {
            mac.update((byte)b);
        }
        return b;
    }

    public int read(
        byte[] b,
        int off,
        int len)
        throws IOException
    {
        int n = in.read(b, off, len);
        if (n >= 0)
        {
            mac.update(b, off, n);
        }
        return n;
    }

    public Mac getMac()
    {
        return mac;
    }
}
