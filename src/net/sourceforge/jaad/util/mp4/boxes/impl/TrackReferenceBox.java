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
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.jaad.util.mp4.boxes.BoxImpl;

public class TrackReferenceBox extends BoxImpl {

	private String referenceType;
	private List<Long> trackIDs;

	public TrackReferenceBox() {
		super("Track Reference Box", "tref");
		trackIDs = new ArrayList<Long>();
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		referenceType = in.readString(4);

		while(left>3) {
			trackIDs.add(in.readBytes(4));
			left -= 4;
		}
	}

	/**
	 * The reference type shall be set to one of the following values: 
	 * <ul>
	 * <li>'hint': the referenced track(s) contain the original media for this 
	 * hint track.</li>
	 * <li>'cdsc': this track describes the referenced track.</li>
	 * <li>'hind': this track depends on the referenced hint track, i.e., it 
	 * should only be used if the referenced hint track is used.</li>
	 * @return the reference type
	 */
	public String getReferenceType() {
		return referenceType;
	}

	/**
	 * The track IDs are integer that provide a reference from the containing
	 * track to other tracks in the presentation. Track IDs are never re-used
	 * and cannot be equal to zero.
	 * @return the track IDs this box refers to
	 */
	public List<Long> getTrackIDs() {
		return trackIDs;
	}
}
