package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.cmc;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;
import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.x509.GeneralName;

import java.math.BigInteger;

/**
 * <pre>
 *      id-cmc-getCert OBJECT IDENTIFIER ::= {id-cmc 15}
 *
 *      GetCert ::= SEQUENCE {
 *           issuerName      GeneralName,
 *           serialNumber    INTEGER }
 * </pre>
 */
public class GetCert extends ASN1Object
{
    private final GeneralName issuerName;
    private final BigInteger serialNumber;

    private GetCert(ASN1Sequence seq)
    {
        if (seq.size() != 2)
        {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.issuerName = GeneralName.getInstance(seq.getObjectAt(0));
        this.serialNumber = ASN1Integer.getInstance(seq.getObjectAt(1)).getValue();
    }

    public GetCert(GeneralName issuerName, BigInteger serialNumber)
    {
        this.issuerName = issuerName;
        this.serialNumber = serialNumber;
    }

    public static GetCert getInstance(Object o)
    {
        if (o instanceof GetCert)
        {
            return (GetCert)o;
        }

        if (o != null)
        {
            return new GetCert(ASN1Sequence.getInstance(o));
        }

        return null;
    }

    public GeneralName getIssuerName()
    {
        return issuerName;
    }

    public BigInteger getSerialNumber()
    {
        return serialNumber;
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(issuerName);
        v.add(new ASN1Integer(serialNumber));

        return new DERSequence(v);
    }
}
