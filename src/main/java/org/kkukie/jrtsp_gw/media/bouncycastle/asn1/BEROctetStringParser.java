package org.kkukie.jrtsp_gw.media.bouncycastle.asn1;

import org.kkukie.jrtsp_gw.media.bouncycastle.util.io.Streams;

import java.io.IOException;
import java.io.InputStream;

/**
 * A parser for indefinite-length OCTET STRINGs.
 */
public class BEROctetStringParser
    implements ASN1OctetStringParser
{
    private ASN1StreamParser _parser;

    BEROctetStringParser(
        ASN1StreamParser parser)
    {
        _parser = parser;
    }

    /**
     * Return an InputStream representing the contents of the OCTET STRING.
     *
     * @return an InputStream with its source as the OCTET STRING content.
     */
    public InputStream getOctetStream()
    {
        return new ConstructedOctetStream(_parser);
    }

    /**
     * Return an in-memory, encodable, representation of the OCTET STRING.
     *
     * @return a BEROctetString.
     * @throws IOException if there is an issue loading the data.
     */
    public ASN1Primitive getLoadedObject()
        throws IOException
    {
        return new BEROctetString(Streams.readAll(getOctetStream()));
    }

    /**
     * Return an BEROctetString representing this parser and its contents.
     *
     * @return an BEROctetString
     */
    public ASN1Primitive toASN1Primitive()
    {
        try
        {
            return getLoadedObject();
        }
        catch (IOException e)
        {
            throw new ASN1ParsingException("IOException converting stream to byte array: " + e.getMessage(), e);
        }
    }
}
