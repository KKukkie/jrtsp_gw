/**
 * Code derived and adapted from the Jitsi client side SRTP framework.
 * <p>
 * Distributed under LGPL license.
 * See terms of license at gnu.org.
 */
package org.kkukie.jrtsp_gw.media.core.stream.rtp.crypto;

/**
 * SRTPPolicy holds the SRTP encryption / authentication policy of a SRTP
 * session.
 *
 * @author Bing SU (nova.su@gmail.com)
 */
public class SRTPPolicy {
    /**
     * Null Cipher, does not change the content of RTP payload
     */
    public static final int NULL_ENCRYPTION = 0;

    /**
     * Counter Mode AES Cipher, defined in Section 4.1.1, RFC3711
     */
    public static final int AESCM_ENCRYPTION = 1;

    /**
     * Counter Mode TwoFish Cipher
     */
    public static final int TWOFISH_ENCRYPTION = 3;

    /**
     * F8 mode AES Cipher, defined in Section 4.1.2, RFC 3711
     */
    public static final int AESF8_ENCRYPTION = 2;

    /**
     * F8 Mode TwoFish Cipher
     */
    public static final int TWOFISHF8_ENCRYPTION = 4;
    /**
     * Null Authentication, no authentication
     */
    public static final int NULL_AUTHENTICATION = 0;

    /**
     * HAMC SHA1 Authentication, defined in Section 4.2.1, RFC3711
     */
    public static final int HMACSHA1_AUTHENTICATION = 1;

    /**
     * Skein Authentication
     */
    public static final int SKEIN_AUTHENTICATION = 2;

    /**
     * SRTP encryption type
     */
    private int encType;

    /**
     * SRTP encryption key length
     */
    private int encKeyLength;

    /**
     * SRTP authentication type
     */
    private int authType;

    /**
     * SRTP authentication key length
     */
    private int authKeyLength;

    /**
     * SRTP authentication tag length
     */
    private int authTagLength;

    /**
     * SRTP salt key length
     */
    private int saltKeyLength;

    /**
     * Construct a SRTPPolicy object based on given parameters.
     * This class acts as a storage class, so all the parameters are passed in
     * through this constructor.
     *
     * @param encType       SRTP encryption type
     * @param encKeyLength  SRTP encryption key length
     * @param authType      SRTP authentication type
     * @param authKeyLength SRTP authentication key length
     * @param authTagLength SRTP authentication tag length
     * @param saltKeyLength SRTP salt key length
     */
    public SRTPPolicy (int encType,
                       int encKeyLength,
                       int authType,
                       int authKeyLength,
                       int authTagLength,
                       int saltKeyLength) {
        this.encType = encType;
        this.encKeyLength = encKeyLength;
        this.authType = authType;
        this.authKeyLength = authKeyLength;
        this.authTagLength = authTagLength;
        this.saltKeyLength = saltKeyLength;
    }

    /**
     * Get the authentication key length
     *
     * @return the authentication key length
     */
    public int getAuthKeyLength () {
        return this.authKeyLength;
    }

    /**
     * Set the authentication key length
     *
     * @param authKeyLength the authentication key length
     */
    public void setAuthKeyLength (int authKeyLength) {
        this.authKeyLength = authKeyLength;
    }

    /**
     * Get the authentication tag length
     *
     * @return the authentication tag length
     */
    public int getAuthTagLength () {
        return this.authTagLength;
    }

    /**
     * Set the authentication tag length
     *
     * @param authTagLength the authentication tag length
     */
    public void setAuthTagLength (int authTagLength) {
        this.authTagLength = authTagLength;
    }

    /**
     * Get the authentication type
     *
     * @return the authentication type
     */
    public int getAuthType () {
        return this.authType;
    }

    /**
     * Set the authentication type
     *
     * @param authType the authentication type
     */
    public void setAuthType (int authType) {
        this.authType = authType;
    }

    /**
     * Get the encryption key length
     *
     * @return the encryption key length
     */
    public int getEncKeyLength () {
        return this.encKeyLength;
    }

    /**
     * Set the encryption key length
     *
     * @param encKeyLength the encryption key length
     */
    public void setEncKeyLength (int encKeyLength) {
        this.encKeyLength = encKeyLength;
    }

    /**
     * Get the encryption type
     *
     * @return the encryption type
     */
    public int getEncType () {
        return this.encType;
    }

    /**
     * Set the encryption type
     *
     * @param encType encryption type
     */
    public void setEncType (int encType) {
        this.encType = encType;
    }

    /**
     * Get the salt key length
     *
     * @return the salt key length
     */
    public int getSaltKeyLength () {
        return this.saltKeyLength;
    }

    /**
     * Set the salt key length
     *
     * @param keyLength the salt key length
     */
    public void setSaltKeyLength (int keyLength) {
        this.saltKeyLength = keyLength;
    }

    public String getAuthTypeStr () {
        switch (this.authType) {
//            case NULL_ENCRYPTION:
//                return "NULL_ENCRYPTION";
//            case AESCM_ENCRYPTION:
//                return "AESCM_ENCRYPTION";
            case TWOFISH_ENCRYPTION:
                return "TWOFISH_ENCRYPTION";
//            case AESF8_ENCRYPTION:
//                return "AESF8_ENCRYPTION";
            case TWOFISHF8_ENCRYPTION:
                return "TWOFISHF8_ENCRYPTION";
            case NULL_AUTHENTICATION:
                return "NULL_AUTHENTICATION";
            case HMACSHA1_AUTHENTICATION:
                return "HMACSHA1_AUTHENTICATION";
            case SKEIN_AUTHENTICATION:
                return "SKEIN_AUTHENTICATION";
            default:
                return "Unknown";
        }
    }
}
