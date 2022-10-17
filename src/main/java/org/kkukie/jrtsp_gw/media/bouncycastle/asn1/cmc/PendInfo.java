package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.cmc;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;
import org.kkukie.jrtsp_gw.media.bouncycastle.util.Arrays;

/**
 * <pre>
 * PendInfo ::= SEQUENCE {
 *    pendToken        OCTET STRING,
 *    pendTime         GeneralizedTime
 * }
 * </pre>
 */
public class PendInfo
    extends ASN1Object
{
    private final byte[] pendToken;
    private final ASN1GeneralizedTime pendTime;

    public PendInfo(byte[] pendToken, ASN1GeneralizedTime pendTime)
    {
        this.pendToken = Arrays.clone(pendToken);
        this.pendTime = pendTime;
    }

    private PendInfo(ASN1Sequence seq)
    {
        if (seq.size() != 2)
        {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.pendToken = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets());
        this.pendTime = ASN1GeneralizedTime.getInstance(seq.getObjectAt(1));
    }

    public static PendInfo getInstance(Object o)
    {
        if (o instanceof PendInfo)
        {
            return (PendInfo)o;
        }

        if (o != null)
        {
            return new PendInfo(ASN1Sequence.getInstance(o));
        }

        return null;
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(new DEROctetString(pendToken));
        v.add(pendTime);

        return new DERSequence(v);
    }

    public byte[] getPendToken()
    {
        return Arrays.clone(pendToken);
    }

    public ASN1GeneralizedTime getPendTime()
    {
        return pendTime;
    }
}
