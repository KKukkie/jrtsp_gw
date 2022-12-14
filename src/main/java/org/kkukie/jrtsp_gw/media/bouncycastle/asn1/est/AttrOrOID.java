package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.est;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;
import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.pkcs.Attribute;

import java.io.IOException;

/**
 * <pre>
 *    AttrOrOID ::= CHOICE (oid OBJECT IDENTIFIER, attribute Attribute }
 * </pre>
 */
public class AttrOrOID
    extends ASN1Object
    implements ASN1Choice
{
    private final ASN1ObjectIdentifier oid;
    private final Attribute attribute;

    public AttrOrOID(ASN1ObjectIdentifier oid)
    {
        this.oid = oid;
        attribute = null;
    }

    public AttrOrOID(Attribute attribute)
    {
        this.oid = null;
        this.attribute = attribute;
    }

    public static AttrOrOID getInstance(
        Object obj)
    {
        if (obj instanceof AttrOrOID)
        {
            return (AttrOrOID)obj;
        }

        if (obj != null)
        {
            if (obj instanceof ASN1Encodable)
            {
                ASN1Encodable asn1Prim = ((ASN1Encodable)obj).toASN1Primitive();

                if (asn1Prim instanceof ASN1ObjectIdentifier)
                {
                    return new AttrOrOID(ASN1ObjectIdentifier.getInstance(asn1Prim));
                }
                if (asn1Prim instanceof ASN1Sequence)
                {
                    return new AttrOrOID(Attribute.getInstance(asn1Prim));
                }
            }
            if (obj instanceof byte[])
            {
                try
                {
                    return getInstance(ASN1Primitive.fromByteArray((byte[])obj));
                }
                catch (IOException e)
                {
                    throw new IllegalArgumentException("unknown encoding in getInstance()");
                }
            }
            throw new IllegalArgumentException("unknown object in getInstance(): " + obj.getClass().getName());
        }

        return null;
    }

    public boolean isOid()
    {
        return oid != null;
    }

    public ASN1ObjectIdentifier getOid()
    {
        return oid;
    }

    public Attribute getAttribute()
    {
        return attribute;
    }
    public ASN1Primitive toASN1Primitive()
    {
        if (oid != null)
        {
            return oid;
        }

        return attribute.toASN1Primitive();
    }
}
