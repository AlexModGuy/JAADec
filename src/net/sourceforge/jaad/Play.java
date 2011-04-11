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
package net.sourceforge.jaad;

import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.*;
import java.io.FileInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * Command line example, that can decode an AAC file and play it.
 * @author in-somnia
 */
public class Play {

	private static final String USAGE = "usage:\njaad.Play [-mp4] <infile>\n\n\t-mp4\tinput file is in MP4 container format";

	public static void main(String[] args) {
		try {
			if(args.length<1) printUsage();
			if(args[0].equals("-mp4")) {
				if(args.length<2) printUsage();
				else decodeMP4(args[1]);
			}
			else decodeAAC(args[0]);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("error while decoding: "+e.toString());
		}
	}

	private static void printUsage() {
		System.out.println(USAGE);
		System.exit(1);
	}

	private static void decodeMP4(String in) throws Exception {
		SourceDataLine line = null;
		byte[] b;
		try {
			final MP4Container cont = new MP4Container(new FileInputStream(in));
			final Movie movie = cont.getMovie();
			final AudioTrack track = (AudioTrack) movie.getTracks(Track.Type.AUDIO).get(0);
			final AudioFormat aufmt = new AudioFormat(track.getSampleRate(), track.getSampleSize(), track.getChannelCount(), true, true);
			line = AudioSystem.getSourceDataLine(aufmt);
			line.open();
			line.start();

			final Decoder dec = new Decoder(track.getDecoderSpecificInfo());

			Frame frame;
			final SampleBuffer buf = new SampleBuffer();
			while(track.hasMoreFrames()) {
				frame = track.readNextFrame();
				dec.decodeFrame(frame.getData(), buf);
				b = buf.getData();
				line.write(b, 0, b.length);
			}
		}
		finally {
			if(line!=null) {
				line.stop();
				line.close();
			}
		}
	}

	private static void decodeAAC(String in) throws Exception {
		SourceDataLine line = null;
		byte[] b;
		try {
			final Decoder dec = new Decoder(new FileInputStream(in));
			final SampleBuffer buf = new SampleBuffer();
			while(true) {
				if(!dec.decodeFrame(buf)) break;

				if(line==null) {
					final AudioFormat aufmt = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
					line = AudioSystem.getSourceDataLine(aufmt);
					line.open();
					line.start();
				}
				b = buf.getData();
				line.write(b, 0, b.length);
			}
		}
		finally {
			if(line!=null) {
				line.stop();
				line.close();
			}
		}
	}
}
