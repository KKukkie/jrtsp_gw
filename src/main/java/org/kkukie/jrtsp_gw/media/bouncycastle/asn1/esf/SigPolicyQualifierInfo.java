package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.esf;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;

public class SigPolicyQualifierInfo
    extends ASN1Object
{
    private ASN1ObjectIdentifier sigPolicyQualifierId;
    private ASN1Encodable sigQualifier;

    public SigPolicyQualifierInfo(
        ASN1ObjectIdentifier sigPolicyQualifierId,
        ASN1Encodable sigQualifier)
    {
        this.sigPolicyQualifierId = sigPolicyQualifierId;
        this.sigQualifier = sigQualifier;
    }

    private SigPolicyQualifierInfo(
        ASN1Sequence seq)
    {
        sigPolicyQualifierId = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        sigQualifier = seq.getObjectAt(1);
    }

    public static SigPolicyQualifierInfo getInstance(
        Object obj)
    {
        if (obj instanceof SigPolicyQualifierInfo)
        {
            return (SigPolicyQualifierInfo) obj;
        }
        else if (obj != null)
        {
            return new SigPolicyQualifierInfo(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    public ASN1ObjectIdentifier getSigPolicyQualifierId()
    {
        return new ASN1ObjectIdentifier(sigPolicyQualifierId.getId());
    }

    public ASN1Encodable getSigQualifier()
    {
        return sigQualifier;
    }

    /**
     * <pre>
     * SigPolicyQualifierInfo ::= SEQUENCE {
     *    sigPolicyQualifierId SigPolicyQualifierId,
     *    sigQualifier ANY DEFINED BY sigPolicyQualifierId }
     *
     * SigPolicyQualifierId ::= OBJECT IDENTIFIER
     * </pre>
     */
    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(sigPolicyQualifierId);
        v.add(sigQualifier);

        return new DERSequence(v);
    }
}
