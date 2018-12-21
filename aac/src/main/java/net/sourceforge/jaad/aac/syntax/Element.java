package net.sourceforge.jaad.aac.syntax;

import net.sourceforge.jaad.aac.SampleFrequency;
import net.sourceforge.jaad.aac.sbr.SBR;

public abstract class Element implements Constants {

	private int elementInstanceTag;
	private SBR sbr;

	protected void readElementInstanceTag(BitStream in) {
		elementInstanceTag = in.readBits(4);
	}

	public int getElementInstanceTag() {
		return elementInstanceTag;
	}

	int decodeSBR(BitStream in, SampleFrequency sf, int count, boolean stereo, boolean crc, boolean downSampled,boolean smallFrames) {

		if(sbr==null) {
            /* implicit SBR signalling, see 4.6.18.2.6 */
			int fq = sf.getFrequency();
			if(fq<24000 && !downSampled)
			    sf = SampleFrequency.forFrequency(2*fq);
			sbr = new SBR(smallFrames, stereo, sf, downSampled);
		}

		return sbr.decode(in, count, crc);
	}

	boolean isSBRPresent() {
		return sbr!=null;
	}

	SBR getSBR() {
		return sbr;
	}
}
