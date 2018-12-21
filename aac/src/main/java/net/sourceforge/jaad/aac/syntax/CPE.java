package net.sourceforge.jaad.aac.syntax;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.DecoderConfig;
import net.sourceforge.jaad.aac.Profile;
import net.sourceforge.jaad.aac.SampleFrequency;
import net.sourceforge.jaad.aac.tools.MSMask;

import java.util.Arrays;

/**
 * channel_pair_element: abbreviation CPE.
 *
 * Syntactic element of the bitstream payload containing data for a pair of channels.
 * A channel_pair_element consists of two individual_channel_streams and additional
 * joint channel coding information. The two channels may share common side information.
 *
 * The channel_pair_element has the same restrictions as the single channel element
 * as far as element_instance_tag, and number of occurrences.
 */

public class CPE extends Element implements Constants {

	private MSMask msMask;
	private boolean[] msUsed;
	private boolean commonWindow;
	ICStream icsL, icsR;

	CPE(int frameLength) {
		super();
		msUsed = new boolean[MAX_MS_MASK];
		icsL = new ICStream(frameLength);
		icsR = new ICStream(frameLength);
	}

	void decode(BitStream in, DecoderConfig conf) {
		final Profile profile = conf.getProfile();
		final SampleFrequency sf = conf.getSampleFrequency();
		if(sf.equals(SampleFrequency.SAMPLE_FREQUENCY_NONE))
			throw new AACException("invalid sample frequency");

		readElementInstanceTag(in);

		commonWindow = in.readBool();
		final ICSInfo info = icsL.getInfo();
		if(commonWindow) {
			info.decode(in, conf, commonWindow);
			icsR.getInfo().setData(info);

			msMask = MSMask.forInt(in.readBits(2));
			if(msMask.equals(MSMask.TYPE_USED)) {
				final int maxSFB = info.getMaxSFB();
				final int windowGroupCount = info.getWindowGroupCount();

				for(int idx = 0; idx<windowGroupCount*maxSFB; idx++) {
					msUsed[idx] = in.readBool();
				}
			}
			else if(msMask.equals(MSMask.TYPE_ALL_1))
				Arrays.fill(msUsed, true);

			else if(msMask.equals(MSMask.TYPE_ALL_0))
				Arrays.fill(msUsed, false);

			else throw new AACException("reserved MS mask type used");
		}
		else {
			msMask = MSMask.TYPE_ALL_0;
			Arrays.fill(msUsed, false);
		}

		if(profile.isErrorResilientProfile()&&(info.isLTPrediction1Present())) {
			if(info.ltpData2Present = in.readBool()) info.getLTPrediction2().decode(in, info, profile);
		}

		icsL.decode(in, commonWindow, conf);
		icsR.decode(in, commonWindow, conf);
	}

	public ICStream getLeftChannel() {
		return icsL;
	}

	public ICStream getRightChannel() {
		return icsR;
	}

	public MSMask getMSMask() {
		return msMask;
	}

	public boolean isMSUsed(int off) {
		return msUsed[off];
	}

	public boolean isMSMaskPresent() {
		return !msMask.equals(MSMask.TYPE_ALL_0);
	}

	public boolean isCommonWindow() {
		return commonWindow;
	}
}
