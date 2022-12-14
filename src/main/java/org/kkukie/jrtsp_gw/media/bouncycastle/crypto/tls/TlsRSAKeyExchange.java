package org.kkukie.jrtsp_gw.media.bouncycastle.crypto.tls;

import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.x509.KeyUsage;
import org.kkukie.jrtsp_gw.media.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.kkukie.jrtsp_gw.media.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.kkukie.jrtsp_gw.media.bouncycastle.crypto.params.RSAKeyParameters;
import org.kkukie.jrtsp_gw.media.bouncycastle.crypto.util.PublicKeyFactory;
import org.kkukie.jrtsp_gw.media.bouncycastle.util.io.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * (D)TLS and SSLv3 RSA key exchange.
 */
public class TlsRSAKeyExchange
    extends AbstractTlsKeyExchange
{
    protected AsymmetricKeyParameter serverPublicKey = null;

    protected RSAKeyParameters rsaServerPublicKey = null;

    protected TlsEncryptionCredentials serverCredentials = null;

    protected byte[] premasterSecret;

    public TlsRSAKeyExchange(Vector supportedSignatureAlgorithms)
    {
        super(KeyExchangeAlgorithm.RSA, supportedSignatureAlgorithms);
    }

    public void skipServerCredentials()
        throws IOException
    {
        throw new TlsFatalAlert(AlertDescription.unexpected_message);
    }

    public void processServerCredentials(TlsCredentials serverCredentials)
        throws IOException
    {
        if (!(serverCredentials instanceof TlsEncryptionCredentials))
        {
            throw new TlsFatalAlert(AlertDescription.internal_error);
        }

        processServerCertificate(serverCredentials.getCertificate());

        this.serverCredentials = (TlsEncryptionCredentials)serverCredentials;
    }

    public void processServerCertificate(TlsCertificate serverTlsCertificate)
        throws IOException
    {
        if (serverTlsCertificate.isEmpty())
        {
            throw new TlsFatalAlert(AlertDescription.bad_certificate);
        }

        org.kkukie.jrtsp_gw.media.bouncycastle.asn1.x509.Certificate x509Cert = serverTlsCertificate.getCertificateAt(0);

        SubjectPublicKeyInfo keyInfo = x509Cert.getSubjectPublicKeyInfo();
        try
        {
            this.serverPublicKey = PublicKeyFactory.createKey(keyInfo);
        }
        catch (RuntimeException e)
        {
            throw new TlsFatalAlert(AlertDescription.unsupported_certificate, e);
        }

        // Sanity check the PublicKeyFactory
        if (this.serverPublicKey.isPrivate())
        {
            throw new TlsFatalAlert(AlertDescription.internal_error);
        }

        this.rsaServerPublicKey = validateRSAPublicKey((RSAKeyParameters)this.serverPublicKey);

        TlsUtils.validateKeyUsage(x509Cert, KeyUsage.keyEncipherment);

        super.processServerCertificate(serverTlsCertificate);
    }

    public void validateCertificateRequest(CertificateRequest certificateRequest)
        throws IOException
    {
        short[] types = certificateRequest.getCertificateTypes();
        for (int i = 0; i < types.length; ++i)
        {
            switch (types[i])
            {
            case ClientCertificateType.rsa_sign:
            case ClientCertificateType.dss_sign:
            case ClientCertificateType.ecdsa_sign:
                break;
            default:
                throw new TlsFatalAlert(AlertDescription.illegal_parameter);
            }
        }
    }

    public void processClientCredentials(TlsCredentials clientCredentials)
        throws IOException
    {
        if (!(clientCredentials instanceof TlsSignerCredentials))
        {
            throw new TlsFatalAlert(AlertDescription.internal_error);
        }
    }

    public void generateClientKeyExchange(OutputStream output)
        throws IOException
    {
        this.premasterSecret = TlsRSAUtils.generateEncryptedPreMasterSecret(context, rsaServerPublicKey, output);
    }

    public void processClientKeyExchange(InputStream input)
        throws IOException
    {
        byte[] encryptedPreMasterSecret;
        if (TlsUtils.isSSL(context))
        {
            // Do any SSLv3 clients actually include the length?
            encryptedPreMasterSecret = Streams.readAll(input);
        }
        else
        {
            encryptedPreMasterSecret = TlsUtils.readOpaque16(input);
        }

        this.premasterSecret = serverCredentials.decryptPreMasterSecret(encryptedPreMasterSecret);
    }

    public byte[] generatePremasterSecret()
        throws IOException
    {
        if (this.premasterSecret == null)
        {
            throw new TlsFatalAlert(AlertDescription.internal_error);
        }

        byte[] tmp = this.premasterSecret;
        this.premasterSecret = null;
        return tmp;
    }

    // Would be needed to process RSA_EXPORT server key exchange
    // protected void processRSAServerKeyExchange(InputStream is, Signer signer) throws IOException
    // {
    // InputStream sigIn = is;
    // if (signer != null)
    // {
    // sigIn = new SignerInputStream(is, signer);
    // }
    //
    // byte[] modulusBytes = TlsUtils.readOpaque16(sigIn);
    // byte[] exponentBytes = TlsUtils.readOpaque16(sigIn);
    //
    // if (signer != null)
    // {
    // byte[] sigByte = TlsUtils.readOpaque16(is);
    //
    // if (!signer.verifySignature(sigByte))
    // {
    // handler.failWithError(AlertLevel.fatal, AlertDescription.bad_certificate);
    // }
    // }
    //
    // BigInteger modulus = new BigInteger(1, modulusBytes);
    // BigInteger exponent = new BigInteger(1, exponentBytes);
    //
    // this.rsaServerPublicKey = validateRSAPublicKey(new RSAKeyParameters(false, modulus,
    // exponent));
    // }

    protected RSAKeyParameters validateRSAPublicKey(RSAKeyParameters key)
        throws IOException
    {
        // What is the minimum bit length required?
        // key.getModulus().bitLength();

        if (!key.getExponent().isProbablePrime(2))
        {
            throw new TlsFatalAlert(AlertDescription.illegal_parameter);
        }

        return key;
    }
}
