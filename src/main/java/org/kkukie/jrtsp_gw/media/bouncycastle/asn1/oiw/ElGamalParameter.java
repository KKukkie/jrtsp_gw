package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.oiw;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;

import java.math.BigInteger;
import java.util.Enumeration;

public class ElGamalParameter
    extends ASN1Object
{
    ASN1Integer p, g;

    public ElGamalParameter(
        BigInteger  p,
        BigInteger  g)
    {
        this.p = new ASN1Integer(p);
        this.g = new ASN1Integer(g);
    }

    private ElGamalParameter(
        ASN1Sequence seq)
    {
        Enumeration     e = seq.getObjects();

        p = (ASN1Integer)e.nextElement();
        g = (ASN1Integer)e.nextElement();
    }

    public static ElGamalParameter getInstance(Object o)
    {
        if (o instanceof ElGamalParameter)
        {
            return (ElGamalParameter)o;
        }
        else if (o != null)
        {
            return new ElGamalParameter(ASN1Sequence.getInstance(o));
        }

        return null;
    }

    public BigInteger getP()
    {
        return p.getPositiveValue();
    }

    public BigInteger getG()
    {
        return g.getPositiveValue();
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(p);
        v.add(g);

        return new DERSequence(v);
    }
}
