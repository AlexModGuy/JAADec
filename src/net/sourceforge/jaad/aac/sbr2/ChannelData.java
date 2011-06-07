package net.sourceforge.jaad.aac.sbr2;

import java.util.Arrays;
import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.syntax.BitStream;

class ChannelData implements SBRConstants, HuffmanTables {

	private static final int[] POW2_TABLE = {1, 2, 4, 8};
	//grid
	private int frameClass;
	private int envCount, noiseCount;
	private int[] freqRes;
	private int varBord0, varBord1;
	private int relCount0, relCount1;
	private int[] relativeBorders0, relativeBorders1;
	private int pointer;
	//dtdf
	private boolean[] dfEnv, dfNoise;
	//invf
	private int[] invfMode;
	//envelope, noise
	private int[][] envelopeData, noiseData;
	//sinusoidal
	private boolean harmonicPresent;
	private boolean[] harmonic;

	ChannelData() {
		relativeBorders0 = new int[0];
		relativeBorders1 = new int[0];
	}

	/* ======================= decoding ======================*/
	void decodeGrid(BitStream in, SBRHeader header) throws AACException {
		final int bits;

		switch(frameClass = in.readBits(2)) {
			case FIXFIX:
				envCount = POW2_TABLE[in.readBits(2)];
				if(envCount==1) header.setAmpRes(false);

				freqRes = new int[envCount];
				Arrays.fill(freqRes, in.readBit());
				break;
			case FIXVAR:
				varBord1 = in.readBits(2);
				relCount1 = in.readBits(2);
				envCount = relCount1+1;

				relativeBorders1 = new int[relCount1];
				for(int i = 0; i<relCount1; i++) {
					relativeBorders1[i] = 2*in.readBits(2)+2;
				}

				bits = (int) Math.ceil(Math.log(envCount+1)/LOG2); //TODO: replace with table
				pointer = in.readBits(bits);

				freqRes = new int[envCount];
				for(int i = 0; i<envCount; i++) {
					freqRes[envCount-1-i] = in.readBit();
				}
				break;
			case VARFIX:
				varBord0 = in.readBits(2);
				relCount0 = in.readBits(2);
				envCount = relCount0+1;

				relativeBorders0 = new int[relCount0];
				for(int i = 0; i<relCount0; i++) {
					relativeBorders0[i] = 2*in.readBits(2)+2;
				}

				bits = (int) Math.ceil(Math.log(envCount+1)/Math.log(2)); //TODO: replace with table
				pointer = in.readBits(bits);

				freqRes = new int[envCount];
				for(int i = 0; i<envCount; i++) {
					freqRes[envCount-1-i] = in.readBit();
				}
				break;
			case VARVAR:
				varBord0 = in.readBits(2);
				varBord1 = in.readBits(2);
				relCount0 = in.readBits(2);
				relCount1 = in.readBits(2);
				envCount = relCount0+relCount1+1;

				relativeBorders0 = new int[relCount0];
				for(int i = 0; i<relCount0; i++) {
					relativeBorders0[i] = 2*in.readBits(2)+2;
				}

				relativeBorders1 = new int[relCount1];
				for(int i = 0; i<relCount1; i++) {
					relativeBorders1[i] = 2*in.readBits(2)+2;
				}

				bits = (int) Math.ceil(Math.log(envCount+1)/Math.log(2)); //TODO: replace with table
				pointer = in.readBits(bits);

				freqRes = new int[envCount];
				for(int i = 0; i<envCount; i++) {
					freqRes[envCount-1-i] = in.readBit();
				}
				break;
		}
		noiseCount = (envCount>1) ? 2 : 1;
	}

	void decodeDTDF(BitStream in) throws AACException {
		dfEnv = new boolean[envCount];
		for(int i = 0; i<envCount; i++) {
			dfEnv[i] = in.readBool();
		}

		dfNoise = new boolean[noiseCount];
		for(int i = 0; i<noiseCount; i++) {
			dfNoise[i] = in.readBool();
		}
	}

	void decodeInvf(BitStream in, SBRHeader header, FrequencyTables tables) throws AACException {
		invfMode = new int[tables.getNq()];
		for(int i = 0; i<invfMode.length; i++) {
			invfMode[i] = in.readBits(2);
		}
	}

	void decodeEnvelope(BitStream in, SBRHeader header, FrequencyTables tables, boolean secCh, boolean coupling) throws AACException {
		final boolean ampRes = header.getAmpRes();

		//select huffman codebooks
		final int[][] tHuff, fHuff;
		if(coupling&&secCh) {
			if(ampRes) {
				tHuff = T_HUFFMAN_ENV_BAL_3_0;
				fHuff = F_HUFFMAN_ENV_BAL_3_0;
			}
			else {
				tHuff = T_HUFFMAN_ENV_BAL_1_5;
				fHuff = F_HUFFMAN_ENV_BAL_1_5;
			}
		}
		else {
			if(ampRes) {
				tHuff = T_HUFFMAN_ENV_3_0;
				fHuff = F_HUFFMAN_ENV_3_0;
			}
			else {
				tHuff = T_HUFFMAN_ENV_1_5;
				fHuff = F_HUFFMAN_ENV_1_5;
			}
		}

		//read huffman data: i=envelope, j=band
		envelopeData = new int[envCount][];
		final int[] envBands = tables.getN();

		final int bits = 7-((coupling&&secCh) ? 1 : 0)-(ampRes ? 1 : 0);

		int j;
		int[][] table;
		for(int i = 0; i<envCount; i++) {
			envelopeData[i] = new int[envBands[freqRes[i]]];

			j = 0;
			if(dfEnv[i]) table = tHuff;
			else {
				table = fHuff;
				envelopeData[i][0] = in.readBits(bits);
				j = 1;
			}

			for(; j<envelopeData[i].length; j++) {
				envelopeData[i][j] = decodeHuffman(in, table);
			}
		}

		//parsing: 4.6.18.3.3
		final int absBordLead, absBordTrail, nRelLead, nRelTrail;
		switch(frameClass) {
			case FIXFIX:
				absBordLead = 0;
				absBordTrail = TIME_SLOTS;
				nRelLead = envCount-1;
				nRelTrail = 0;
				break;
			case FIXVAR:
				absBordLead = 0;
				absBordTrail = varBord1+TIME_SLOTS;
				nRelLead = 0;
				nRelTrail = relCount1;
				break;
			case VARFIX:
				absBordLead = varBord0;
				absBordTrail = TIME_SLOTS;
				nRelLead = relCount0;
				nRelTrail = 0;
				break;
			default:
				//VARVAR
				absBordLead = varBord0;
				absBordTrail = varBord1+TIME_SLOTS;
				nRelLead = relCount0;
				nRelTrail = relCount1;
				break;
		}

		final int[] relBordLead = new int[nRelLead];
		if(frameClass==FIXFIX) Arrays.fill(relBordLead, (int) Math.round((double) TIME_SLOTS/(double) envCount));
		else if(frameClass==VARFIX||frameClass==VARVAR) System.arraycopy(relativeBorders0, 0, relBordLead, 0, nRelLead);

		final int[] relBordTrail = new int[nRelTrail];
		if(frameClass==VARVAR||frameClass==FIXVAR) System.arraycopy(relativeBorders1, 0, relBordTrail, 0, nRelTrail);


	}

	void decodeNoise(BitStream in, SBRHeader header, FrequencyTables tables, boolean secCh, boolean coupling) throws AACException {
		//select huffman codebooks
		final int[][] tHuff, fHuff;
		if(coupling&&secCh) {
			tHuff = T_HUFFMAN_NOISE_BAL_3_0;
			fHuff = F_HUFFMAN_NOISE_BAL_3_0;
		}
		else {
			tHuff = T_HUFFMAN_NOISE_3_0;
			fHuff = F_HUFFMAN_NOISE_3_0;
		}

		//read huffman data: i=noise, j=band
		final int noiseBands = tables.getNq();
		noiseData = new int[noiseCount][noiseBands];

		int j;
		int[][] table;
		for(int i = 0; i<noiseCount; i++) {
			j = 0;
			if(dfNoise[i]) table = tHuff;
			else {
				table = fHuff;
				noiseData[i][0] = in.readBits(5);
				j = 1;
			}

			for(; j<noiseBands; j++) {
				noiseData[i][j] = decodeHuffman(in, table);
			}
		}
	}

	void decodeSinusoidal(BitStream in, SBRHeader header, FrequencyTables tables) throws AACException {
		if(harmonicPresent = in.readBool()) {
			harmonic = new boolean[tables.getN(HIGH)];
			for(int i = 0; i<harmonic.length; i++) {
				harmonic[i] = in.readBool();
			}
		}
	}

	private int decodeHuffman(BitStream in, int[][] table) throws AACException {
		int off = 0;
		int len = table[off][0];
		int cw = in.readBits(len);
		int j;
		while(cw!=table[off][1]) {
			off++;
			j = table[off][0]-len;
			len = table[off][0];
			cw <<= j;
			cw |= in.readBits(j);
		}
		return table[off][2];
	}

	/* ======================= gets======================*/
	public int getFrameClass() {
		return frameClass;
	}

	public int getEnvCount() {
		return envCount;
	}

	public int getNoiseCount() {
		return noiseCount;
	}

	public int[] getFrequencyResolutions() {
		return freqRes;
	}

	public int getVariableBorder(boolean second) {
		return second ? varBord1 : varBord0;
	}

	public int getRelativeCount(boolean second) {
		return second ? relCount1 : relCount0;
	}

	public int[] getRelativeBorders(boolean second) {
		return second ? relativeBorders1 : relativeBorders0;
	}

	public int getPointer() {
		return pointer;
	}

	public int[] getInvfMode() {
		return invfMode;
	}

	/* ======================= copying ======================*/
	void copyGrid(ChannelData cd) {
		frameClass = cd.getFrameClass();
		envCount = cd.getEnvCount();
		noiseCount = cd.getNoiseCount();

		int[] tmp = cd.getFrequencyResolutions();
		freqRes = new int[tmp.length];
		System.arraycopy(tmp, 0, freqRes, 0, tmp.length);

		varBord0 = cd.getVariableBorder(false);
		varBord1 = cd.getVariableBorder(true);
		relCount0 = cd.getRelativeCount(false);
		relCount1 = cd.getRelativeCount(true);

		tmp = cd.getRelativeBorders(false);
		relativeBorders0 = new int[tmp.length];
		System.arraycopy(tmp, 0, relativeBorders0, 0, tmp.length);

		tmp = cd.getRelativeBorders(true);
		relativeBorders1 = new int[tmp.length];
		System.arraycopy(tmp, 0, relativeBorders1, 0, tmp.length);

		pointer = cd.getPointer();
	}

	void copyInvf(ChannelData cd) {
		final int[] tmp = cd.getInvfMode();
		invfMode = new int[tmp.length];
		System.arraycopy(tmp, 0, invfMode, 0, tmp.length);
	}
}
