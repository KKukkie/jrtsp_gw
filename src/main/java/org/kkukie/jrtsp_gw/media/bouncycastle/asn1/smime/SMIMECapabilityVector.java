package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.smime;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;

/**
 * Handler for creating a vector S/MIME Capabilities
 */
public class SMIMECapabilityVector
{
    private ASN1EncodableVector capabilities = new ASN1EncodableVector();

    public void addCapability(
        ASN1ObjectIdentifier capability)
    {
        capabilities.add(new DERSequence(capability));
    }

    public void addCapability(
        ASN1ObjectIdentifier capability,
        int                 value)
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(capability);
        v.add(new ASN1Integer(value));

        capabilities.add(new DERSequence(v));
    }

    public void addCapability(
        ASN1ObjectIdentifier capability,
        ASN1Encodable params)
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(capability);
        v.add(params);

        capabilities.add(new DERSequence(v));
    }

    public ASN1EncodableVector toASN1EncodableVector()
    {
        return capabilities;
    }
}
