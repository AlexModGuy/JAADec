/*
 * Copyright (C) 2010 in-somnia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jaad.util.mp4.boxes.impl;

import net.sourceforge.jaad.util.mp4.MP4InputStream;
import java.io.IOException;
import net.sourceforge.jaad.util.mp4.boxes.FullBox;

public class TrackHeaderBox extends FullBox {

	private long creationTime, modificationTime, duration;
	private int trackID, layer, alternateGroup;
	private double volume, width, height;
	private double[] matrix;

	public TrackHeaderBox() {
		super("Track Header Box", "tkhd");
		matrix = new double[9];
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		if(version==1) {
			creationTime = in.readBytes(8);
			modificationTime = in.readBytes(8);
			trackID = (int) in.readBytes(4);
			in.skipBytes(4); //reserved
			duration = in.readBytes(8);
			left -= 32;
		}
		else {
			creationTime = in.readBytes(4);
			modificationTime = in.readBytes(4);
			trackID = (int) in.readBytes(4);
			in.skipBytes(4); //reserved
			duration = in.readBytes(4);
			left -= 20;
		}

		in.skipBytes(4); //reserved
		in.skipBytes(4); //reserved

		layer = (int) in.readBytes(2);
		alternateGroup = (int) in.readBytes(2);
		volume = getFloatingPoint(in.readBytes(2), MASK8);

		in.skipBytes(2);

		for(int i = 0; i<9; i++) {
			matrix[i] = getFloatingPoint(in.readBytes(4), MASK16);
		}

		width = getFloatingPoint(in.readBytes(4), MASK16);
		height = getFloatingPoint(in.readBytes(4), MASK16);

		left = 0;
	}

	/**
	 * A flag indicating that the track is enabled. A disabled track is treated
	 * as if it were not present.
	 * @return true if the track is enabled
	 */
	public boolean isTrackEnabled() {
		return (flags&1)==1;
	}

	/**
	 * A flag indicating that the track is used in the presentation.
	 * @return true if the track is used
	 */
	public boolean isTrackInMovie() {
		return (flags&2)==2;
	}

	/**
	 * A flag indicating that the track is used when previewing the
	 * presentation.
	 * @return true if the track is used in previews
	 */
	public boolean isTrackInPreview() {
		return (flags&4)==4;
	}

	/**
	 * The creation time is an integer that declares the creation time of the
	 * presentation in seconds since midnight, Jan. 1, 1904, in UTC time.
	 * @return the creation time
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * The modification time is an integer that declares the most recent time
	 * the presentation was modified in seconds since midnight, Jan. 1, 1904,
	 * in UTC time.
	 */
	public long getModificationTime() {
		return modificationTime;
	}

	/**
	 * The track ID is an integer that uniquely identifies this track over the
	 * entire life-time of this presentation. Track IDs are never re-used and
	 * cannot be zero.
	 * @return the track's ID
	 */
	public int getTrackID() {
		return trackID;
	}

	/**
	 * The duration is an integer that declares length of the presentation (in
	 * the indicated timescale). This property is derived from the
	 * presentation's tracks: the value of this field corresponds to the
	 * duration of the longest track in the presentation.
	 * @return the duration of the longest track
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * The layer specifies the front-to-back ordering of video tracks; tracks
	 * with lower numbers are closer to the viewer. 0 is the normal value, and
	 * -1 would be in front of track 0, and so on.
	 * @return the layer
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * The alternate group is an integer that specifies a group or collection
	 * of tracks. If this field is 0 there is no information on possible
	 * relations to other tracks. If this field is not 0, it should be the same
	 * for tracks that contain alternate data for one another and different for
	 * tracks belonging to different such groups. Only one track within an
	 * alternate group should be played or streamed at any one time, and must be
	 * distinguishable from other tracks in the group via attributes such as
	 * bitrate, codec, language, packet size etc. A group may have only one
	 * member.
	 * @return the alternate group
	 */
	public int getAlternateGroup() {
		return alternateGroup;
	}

	/**
	 * The volume is a floating point number that indicates the preferred
	 * playback volume: 0.0 is mute, 1.0 is normal volume.
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}

	/**
	 * The width specifies the track's visual presentation width as a floating
	 * point values. This needs not be the same as the pixel width of the
	 * images, which is documented in the sample description(s); all images in
	 * the sequence are scaled to this width, before any overall transformation
	 * of the track represented by the matrix. The pixel dimensions of the
	 * images are the default values. 
	 * @return the image width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * The height specifies the track's visual presentation height as a floating
	 * point value. This needs not be the same as the pixel height of the
	 * images, which is documented in the sample description(s); all images in
	 * the sequence are scaled to this height, before any overall transformation
	 * of the track represented by the matrix. The pixel dimensions of the
	 * images are the default values.
	 * @return the image height
	 */
	public double getHeight() {
		return height;
	}
}
