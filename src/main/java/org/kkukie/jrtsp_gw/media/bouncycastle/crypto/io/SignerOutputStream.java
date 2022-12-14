package org.kkukie.jrtsp_gw.media.bouncycastle.crypto.io;

import org.kkukie.jrtsp_gw.media.bouncycastle.crypto.Signer;

import java.io.IOException;
import java.io.OutputStream;

public class SignerOutputStream
    extends OutputStream
{
    protected Signer signer;

    public SignerOutputStream(
        Signer          Signer)
    {
        this.signer = Signer;
    }

    public void write(int b)
        throws IOException
    {
        signer.update((byte)b);
    }

    public void write(
        byte[] b,
        int off,
        int len)
        throws IOException
    {
        signer.update(b, off, len);
    }

    public Signer getSigner()
    {
        return signer;
    }
}
