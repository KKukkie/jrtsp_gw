package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.pkcs;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;

import java.math.BigInteger;

public class RC2CBCParameter
    extends ASN1Object
{
    ASN1Integer version;
    ASN1OctetString iv;

    public static RC2CBCParameter getInstance(
        Object  o)
    {
        if (o instanceof RC2CBCParameter)
        {
            return (RC2CBCParameter)o;
        }
        if (o != null)
        {
            return new RC2CBCParameter(ASN1Sequence.getInstance(o));
        }

        return null;
    }

    public RC2CBCParameter(
        byte[]  iv)
    {
        this.version = null;
        this.iv = new DEROctetString(iv);
    }

    public RC2CBCParameter(
        int     parameterVersion,
        byte[]  iv)
    {
        this.version = new ASN1Integer(parameterVersion);
        this.iv = new DEROctetString(iv);
    }

    private RC2CBCParameter(
        ASN1Sequence seq)
    {
        if (seq.size() == 1)
        {
            version = null;
            iv = (ASN1OctetString)seq.getObjectAt(0);
        }
        else
        {
            version = (ASN1Integer)seq.getObjectAt(0);
            iv = (ASN1OctetString)seq.getObjectAt(1);
        }
    }

    public BigInteger getRC2ParameterVersion()
    {
        if (version == null)
        {
            return null;
        }

        return version.getValue();
    }

    public byte[] getIV()
    {
        return iv.getOctets();
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        if (version != null)
        {
            v.add(version);
        }

        v.add(iv);

        return new DERSequence(v);
    }
}
