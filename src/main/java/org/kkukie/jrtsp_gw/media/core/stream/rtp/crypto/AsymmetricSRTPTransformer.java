/**
 * Code derived and adapted from the Jitsi client side SRTP framework.
 * <p>
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.kkukie.jrtsp_gw.media.core.stream.rtp.crypto;


/**
 * SRTP/SRTCP TransformEngine that uses different keys for forward and reverse
 * transformations.
 *
 * @author Ingo Bauersachs
 */
public class AsymmetricSRTPTransformer implements TransformEngine {

    private SRTPTransformEngine forwardEngine;
    private SRTPTransformEngine reverseEngine;

    public AsymmetricSRTPTransformer (SRTPTransformEngine forwardEngine,
                                      SRTPTransformEngine reverseEngine) {
        this.forwardEngine = forwardEngine;
        this.reverseEngine = reverseEngine;
    }

    /**
     * Close the transformer engine.
     * <p>
     * The close functions closes all stored default crypto contexts. This
     * deletes key data and forces a cleanup of the crypto contexts.
     */
    public void close () {
        if (forwardEngine != null)
            forwardEngine.close();
        if (reverseEngine != null)
            reverseEngine.close();

        forwardEngine = null;
        reverseEngine = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.java.sip.communicator.impl.neomedia.transform.srtp.SRTPTransformEngine
     * #getRTCPTransformer()
     */
    public PacketTransformer getRTCPTransformer () {
        return new SRTCPTransformer(forwardEngine, reverseEngine);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.java.sip.communicator.impl.neomedia.transform.srtp.SRTPTransformEngine
     * #getRTPTransformer()
     */
    public PacketTransformer getRTPTransformer () {
        return new SRTPTransformer(forwardEngine, reverseEngine);
    }

}
