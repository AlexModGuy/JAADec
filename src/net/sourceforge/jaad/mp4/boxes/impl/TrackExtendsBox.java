package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

/**
 * This box sets up default values used by the movie fragments. By setting
 * defaults in this way, space and complexity can be saved in each Track
 * Fragment Box.
 *
 * @author in-somnia
 */
public class TrackExtendsBox extends FullBox {

	private long trackID, defaultSampleDescriptionIndex, defaultSampleDuration, defaultSampleSize;
	private int sampleDegradationPriority, samplePaddingValue;
	private int sampleDependsOn, sampleIsDependedOn, sampleHasRedundancy;
	private boolean differenceSample;

	public TrackExtendsBox() {
		super("Track Extends Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		trackID = in.readBytes(4);
		defaultSampleDescriptionIndex = in.readBytes(4);
		defaultSampleDuration = in.readBytes(4);
		defaultSampleSize = in.readBytes(4);
		final long l = in.readBytes(4);
		/* 6 bits reserved
		 * 2 bits sampleDependsOn
		 * 2 bits sampleIsDependedOn
		 * 2 bits sampleHasRedundancy
		 * 3 bits samplePaddingValue
		 * 1 bit sampleIsDifferenceSample
		 * 16 bits sampleDegradationPriority
		 */
		sampleDependsOn = (int) ((l>>24)&3);
		sampleIsDependedOn = (int) ((l>>22)&3);
		sampleHasRedundancy = (int) ((l>>20)&3);
		samplePaddingValue = (int) ((l>>17)&7);
		differenceSample = ((l>>16)&1)==1;
		sampleDegradationPriority = (int) (l&0xFFFF);

		left -= 20;
	}

	/**
	 * The track ID identifies the track; this shall be the track ID of a track
	 * in the Movie Box.
	 *
	 * @return the track ID
	 */
	public long getTrackID() {
		return trackID;
	}

	/**
	 * The default sample description index used in the track fragments.
	 *
	 * @return the default sample description index
	 */
	public long getDefaultSampleDescriptionIndex() {
		return defaultSampleDescriptionIndex;
	}

	/**
	 * The default sample duration used in the track fragments.
	 *
	 * @return the default sample duration
	 */
	public long getDefaultSampleDuration() {
		return defaultSampleDuration;
	}

	/**
	 * The default sample size used in the track fragments.
	 *
	 * @return the default sample size
	 */
	public long getDefaultSampleSize() {
		return defaultSampleSize;
	}

	/**
	 * The default 'sample depends on' value as defined in the
	 * SampleDependencyTypeBox.
	 *
	 * @see SampleDependencyTypeBox#getSampleDependsOn()
	 * @return the default 'sample depends on' value
	 */
	public int getSampleDependsOn() {
		return sampleDependsOn;
	}

	/**
	 * The default 'sample is depended on' value as defined in the
	 * SampleDependencyTypeBox.
	 *
	 * @see SampleDependencyTypeBox#getSampleIsDependedOn()
	 * @return the default 'sample is depended on' value
	 */
	public int getSampleIsDependedOn() {
		return sampleIsDependedOn;
	}

	/**
	 * The default 'sample has redundancy' value as defined in the
	 * SampleDependencyBox.
	 *
	 * @see SampleDependencyTypeBox#getSampleHasRedundancy()
	 * @return the default 'sample has redundancy' value
	 */
	public int getSampleHasRedundancy() {
		return sampleHasRedundancy;
	}

	/**
	 * The default padding value as defined in the PaddingBitBox.
	 *
	 * @see PaddingBitBox#getPad1()
	 * @return the default padding value
	 */
	public int getSamplePaddingValue() {
		return samplePaddingValue;
	}

	public boolean isSampleDifferenceSample() {
		return differenceSample;
	}

	/**
	 * The default degradation priority for the samples.
	 * @return
	 */
	public int getSampleDegradationPriority() {
		return sampleDegradationPriority;
	}
}