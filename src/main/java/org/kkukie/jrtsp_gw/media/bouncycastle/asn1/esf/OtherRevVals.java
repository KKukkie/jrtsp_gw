package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.esf;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;

import java.io.IOException;

/**
 * <pre>
 * OtherRevVals ::= SEQUENCE {
 *    otherRevValType OtherRevValType,
 *    otherRevVals ANY DEFINED BY OtherRevValType
 * }
 *
 * OtherRevValType ::= OBJECT IDENTIFIER
 * </pre>
 */
public class OtherRevVals
    extends ASN1Object
{

    private ASN1ObjectIdentifier otherRevValType;

    private ASN1Encodable otherRevVals;

    public static OtherRevVals getInstance(Object obj)
    {
        if (obj instanceof OtherRevVals)
        {
            return (OtherRevVals)obj;
        }
        if (obj != null)
        {
            return new OtherRevVals(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    private OtherRevVals(ASN1Sequence seq)
    {
        if (seq.size() != 2)
        {
            throw new IllegalArgumentException("Bad sequence size: "
                + seq.size());
        }
        this.otherRevValType = (ASN1ObjectIdentifier)seq.getObjectAt(0);
        try
        {
            this.otherRevVals = ASN1Primitive.fromByteArray(seq.getObjectAt(1)
                .toASN1Primitive().getEncoded(ASN1Encoding.DER));
        }
        catch (IOException e)
        {
            throw new IllegalStateException();
        }
    }

    public OtherRevVals(ASN1ObjectIdentifier otherRevValType,
                        ASN1Encodable otherRevVals)
    {
        this.otherRevValType = otherRevValType;
        this.otherRevVals = otherRevVals;
    }

    public ASN1ObjectIdentifier getOtherRevValType()
    {
        return this.otherRevValType;
    }

    public ASN1Encodable getOtherRevVals()
    {
        return this.otherRevVals;
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.otherRevValType);
        v.add(this.otherRevVals);
        return new DERSequence(v);
    }
}
