package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.pkcs;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;

import java.math.BigInteger;

public class PBEParameter
    extends ASN1Object
{
    ASN1Integer iterations;
    ASN1OctetString salt;

    public PBEParameter(
        byte[]      salt,
        int         iterations)
    {
        if (salt.length != 8)
        {
            throw new IllegalArgumentException("salt length must be 8");
        }
        this.salt = new DEROctetString(salt);
        this.iterations = new ASN1Integer(iterations);
    }

    private PBEParameter(
        ASN1Sequence seq)
    {
        salt = (ASN1OctetString)seq.getObjectAt(0);
        iterations = (ASN1Integer)seq.getObjectAt(1);
    }

    public static PBEParameter getInstance(
        Object  obj)
    {
        if (obj instanceof PBEParameter)
        {
            return (PBEParameter)obj;
        }
        else if (obj != null)
        {
            return new PBEParameter(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    public BigInteger getIterationCount()
    {
        return iterations.getValue();
    }

    public byte[] getSalt()
    {
        return salt.getOctets();
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(salt);
        v.add(iterations);

        return new DERSequence(v);
    }
}
