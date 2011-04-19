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

import net.sourceforge.jaad.util.wav.WaveFileWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.AudioTrack;
import net.sourceforge.jaad.mp4.api.Frame;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;

/**
 * Command line example, that can decode an AAC file to a WAVE file.
 * @author in-somnia
 */
public class Main {

	private static final String USAGE = "usage:\njaad.Main [-mp4] <infile> <outfile>\n\n\t-mp4\tinput file is in MP4 container format";

	public static void main(String[] args) {
		try {
			if(args.length<2) printUsage();
			if(args[0].equals("-mp4")) {
				if(args.length<3) printUsage();
				else decodeMP4(args[1], args[2]);
			}
			else decodeAAC(args[0], args[1]);
		}
		catch(Exception e) {
			System.err.println("error while decoding: "+e.toString());
		}
	}

	private static void printUsage() {
		System.out.println(USAGE);
		System.exit(1);
	}

	private static void decodeMP4(String in, String out) throws Exception {
		WaveFileWriter wav = null;
		try {
			final MP4Container cont = new MP4Container(new FileInputStream(in));
			final Movie movie = cont.getMovie();
			final List<Track> tracks = movie.getTracks(AudioTrack.AudioCodec.AAC);
			if(tracks.isEmpty()) throw new Exception("movie does not contain any AAC track");
			final AudioTrack track = (AudioTrack) tracks.get(0);
			
			wav = new WaveFileWriter(new File(out), track.getSampleRate(), track.getChannelCount(), track.getSampleSize());

			final Decoder dec = new Decoder(track.getDecoderSpecificInfo());

			Frame frame;
			final SampleBuffer buf = new SampleBuffer();
			while(track.hasMoreFrames()) {
				frame = track.readNextFrame();
				dec.decodeFrame(frame.getData(), buf);
				wav.write(buf.getData());
			}
		}
		finally {
			if(wav!=null) wav.close();
		}
	}

	private static void decodeAAC(String in, String out) throws IOException {
		WaveFileWriter wav = null;
		try {
			final Decoder dec = new Decoder(new FileInputStream(in));

			final SampleBuffer buf = new SampleBuffer();
			while(true) {
				if(!dec.decodeFrame(buf)) break;

				if(wav==null) wav = new WaveFileWriter(new File(out), buf.getSampleRate(), buf.getChannels(), buf.getBitsPerSample());
				wav.write(buf.getData());
			}
		}
		finally {
			if(wav!=null) wav.close();
		}
	}
}
