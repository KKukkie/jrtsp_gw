package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.pkcs;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;
import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.Enumeration;

public class EncryptedPrivateKeyInfo
    extends ASN1Object
{
    private AlgorithmIdentifier algId;
    private ASN1OctetString data;

    private EncryptedPrivateKeyInfo(
        ASN1Sequence seq)
    {
        Enumeration e = seq.getObjects();

        algId = AlgorithmIdentifier.getInstance(e.nextElement());
        data = ASN1OctetString.getInstance(e.nextElement());
    }

    public EncryptedPrivateKeyInfo(
        AlgorithmIdentifier algId,
        byte[]              encoding)
    {
        this.algId = algId;
        this.data = new DEROctetString(encoding);
    }

    public static EncryptedPrivateKeyInfo getInstance(
        Object  obj)
    {
        if (obj instanceof EncryptedPrivateKeyInfo)
        {
            return (EncryptedPrivateKeyInfo)obj;
        }
        else if (obj != null)
        { 
            return new EncryptedPrivateKeyInfo(ASN1Sequence.getInstance(obj));
        }

        return null;
    }
    
    public AlgorithmIdentifier getEncryptionAlgorithm()
    {
        return algId;
    }

    public byte[] getEncryptedData()
    {
        return data.getOctets();
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * EncryptedPrivateKeyInfo ::= SEQUENCE {
     *      encryptionAlgorithm AlgorithmIdentifier {{KeyEncryptionAlgorithms}},
     *      encryptedData EncryptedData
     * }
     *
     * EncryptedData ::= OCTET STRING
     *
     * KeyEncryptionAlgorithms ALGORITHM-IDENTIFIER ::= {
     *          ... -- For local profiles
     * }
     * </pre>
     */
    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(algId);
        v.add(data);

        return new DERSequence(v);
    }
}
