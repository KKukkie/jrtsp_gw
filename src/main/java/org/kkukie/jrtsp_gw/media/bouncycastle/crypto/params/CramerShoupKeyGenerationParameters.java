package org.kkukie.jrtsp_gw.media.bouncycastle.crypto.params;

import org.kkukie.jrtsp_gw.media.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

public class CramerShoupKeyGenerationParameters
	extends KeyGenerationParameters
{

	private CramerShoupParameters params;

	public CramerShoupKeyGenerationParameters(SecureRandom random, CramerShoupParameters params)
	{
		super(random, getStrength(params));

		this.params = params;
	}

	public CramerShoupParameters getParameters()
	{
		return params;
	}

	static int getStrength(CramerShoupParameters params)
	{
		return params.getP().bitLength();
	}
}
