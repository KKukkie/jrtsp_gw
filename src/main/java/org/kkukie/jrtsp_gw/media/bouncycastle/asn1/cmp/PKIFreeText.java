package org.kkukie.jrtsp_gw.media.bouncycastle.asn1.cmp;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.*;

import java.util.Enumeration;

public class PKIFreeText
    extends ASN1Object
{
    ASN1Sequence strings;

    public static PKIFreeText getInstance(
        ASN1TaggedObject obj,
        boolean             explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static PKIFreeText getInstance(
        Object obj)
    {
        if (obj instanceof PKIFreeText)
        {
            return (PKIFreeText)obj;
        }
        else if (obj != null)
        {
            return new PKIFreeText(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    private PKIFreeText(
        ASN1Sequence seq)
    {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements())
        {
            if (!(e.nextElement() instanceof DERUTF8String))
            {
                throw new IllegalArgumentException("attempt to insert non UTF8 STRING into PKIFreeText");
            }
        }
        
        strings = seq;
    }

    public PKIFreeText(
        DERUTF8String p)
    {
        strings = new DERSequence(p);
    }

    public PKIFreeText(
        String p)
    {
        this(new DERUTF8String(p));
    }

    public PKIFreeText(
        DERUTF8String[] strs)
    {
        strings = new DERSequence(strs);
    }

    public PKIFreeText(
        String[] strs)
    {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i < strs.length; i++)
        {
            v.add(new DERUTF8String(strs[i]));
        }
        strings = new DERSequence(v);
    }

    /**
     * Return the number of string elements present.
     * 
     * @return number of elements present.
     */
    public int size()
    {
        return strings.size();
    }
    
    /**
     * Return the UTF8STRING at index i.
     * 
     * @param i index of the string of interest
     * @return the string at index i.
     */
    public DERUTF8String getStringAt(
        int i)
    {
        return (DERUTF8String)strings.getObjectAt(i);
    }
    
    /**
     * <pre>
     * PKIFreeText ::= SEQUENCE SIZE (1..MAX) OF UTF8String
     * </pre>
     */
    public ASN1Primitive toASN1Primitive()
    {
        return strings;
    }
}
