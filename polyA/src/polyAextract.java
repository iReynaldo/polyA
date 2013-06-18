//Reynaldo Morillo
//Samir Dahmani

/**To make this program work, you need to create a folder that contains the following files:
 * 
 * 1. A reference genome file, in the format .fa
 * 2. A copy of the reference genome with the following format .fai
 * 3. A file in the format .bed that conatins information about Poly-A sites.
 * 
 * This program takes in 5 inputs.
 * 1.The first parameter is the location of the reference Genome on the hard drive.
 * 		Example below
 * 		"/home/reynaldo/Documents/hg19_ref_genome_nonrandom_sorted.fa"
 * 
 * 2.The second parameter is the location of the Poly-A sites file on the hard drive.
 * 		Example below
 * 		 "/home/reynaldo/Documents/GSM747470_human_brain.sites.clustered.hg19.bed" */

import java.io.*;
import java.util.Scanner;

import net.sf.samtools.*;
import net.sf.samtools.util.AsciiWriter;
import net.sf.picard.*;
import net.sf.picard.reference.*;
import net.sf.picard.sam.CreateSequenceDictionary;
import net.sf.picard.util.*;

public class polyAextract {

	public static void main(String[] args) throws IOException {
		// This creates a variable of type file.
		File genome = new File("/");
		// This creates a variable of type file.
		File polyAfile = new File("/");
		Integer numberToExtract = 0;
		Integer tailLength = 0;
		File file = new File("OutputFile");
		int i = 0;
		boolean printHelp = false;
		while (i < args.length && args[i].charAt(0) == '-') {
			if ("-f".equalsIgnoreCase(args[i])) {
				i++;
				genome = new File(args[i]);
			} else if ("-b".equalsIgnoreCase(args[i])) {
				i++;
				polyAfile = new File(args[i]); // age.parseInt(args[i]);
			} else if ("-l".equalsIgnoreCase(args[i])) {
				i++;
				numberToExtract = numberToExtract.parseInt(args[i]);
			} else if ("-a".equalsIgnoreCase(args[i])) {
				i++;
				tailLength = tailLength.parseInt(args[i]);
			} else if ("-o".equalsIgnoreCase(args[i])) {
				i++;
				file = new File(args[i]);
			} else {
				printHelp = true;
			}
			i++;
		}
		if (args.length < 8 || printHelp) {
			System.out
					.println("USAGE: extract -f <fasta-file-name> -b <bed-file-name> -l <sequence-length> -a <poly-A-tail-length> -o <output-file-name>");
			return;
		}

		String command = "polyAextract" + "-f" + genome + "-b" + polyAfile
				+ "-l" + numberToExtract + "-a" + tailLength + "-o" + file;
		// polyAextract app = new
		// polyAextract("/home/reynaldo/Documents/hg19_ref_genome_nonrandom_sorted.fa"
		// ,"/home/reynaldo/Documents/GSM747470_human_brain.sites.clustered.hg19.bed",
		// 400 , 100, "/home/reynaldo/testFile4.fa");

		// This retrieves the .fai file, given the .fa file.
		IndexedFastaSequenceFile seqFile = new IndexedFastaSequenceFile(genome);
		// This was left like this just for instantiation
		ReferenceSequence refSeq = seqFile.getSubsequenceAt("chr1", 10001,
				10005); // Notice, this takes the first 5 bases (after all the
						// Ns)
		// Reading from the .bed file; which contains the Poly-A sites.

		BasicInputParser polyAsites = new BasicInputParser(true, polyAfile);
		// These 3 variables will serve as inputs in the getSubsequenceAt method
		// above.
		String chromosome = "";
		int start = 0;
		int end = 1;
		int extract = numberToExtract - 1;
		//String strand;
		String header = "";
		String tail = tailMaker(tailLength);
		// These next two lines are responsible for making the file.
		Writer writer = new BufferedWriter(new FileWriter(file));
		// This is used to make the file start off from the first line, rather
		// than starting off on the second line.
		int indicator = 0;
		try {
			// while (polyAsites.hasNext()) {
			for (int j = 0; j < 4; j++) { // This is just for testing purposes
				// This moves onto the next line in the poly-A site file.
				polyAsites.next();
				// This Scanner reads the line.
				Scanner myScanner = new Scanner(polyAsites.getCurrentLine());
				chromosome = myScanner.next();
				end = myScanner.nextInt();
				start = end - extract;
				/*strand = myScanner.next();
				if (strand == "+") {
					System.out.println("hekjrhkajer");
				}*/
				refSeq = seqFile.getSubsequenceAt(chromosome, start, end);
				String bases = new String(refSeq.getBases());

				// This part makes the .fa file
				if (chromosome.equals(header)) {
					writer.write("\n" + ">" + header + ":" + start + "-" + end
							+ "+" + tailLength + "A");
					writer.write("\n" + bases + tail);
					// System.out.println(">"+header + ":" + start + "-" + end +
					// "+" + tailLength + "A"); //Testing purposes
					// System.out.println(bases + tail); //Testing purposes
				} else {
					if (indicator == 1) {
						header = chromosome;
						writer.write("\n" + ">" + header + ":" + start + "-"
								+ end + "+" + tailLength + "A");
						writer.write("\n" + bases + tail);
						// System.out.println(">"+header + ":" + start + "-" +
						// end + "+" + tailLength + "A"); //Testing purposes
						// System.out.println(bases + tail); //Testing purposes
					} else {
						header = chromosome;
						writer.write(">" + header + ":" + start + "-" + end
								+ "+" + tailLength + "A");
						writer.write("\n" + bases + tail);
						// System.out.println(">"+header + ":"+ start + "-" +
						// end + "+" + tailLength + "A"); //Testing purposes
						// System.out.println(bases + tail); //Testing purposes
						indicator = 1;
					}
				}
			}
			writer.close();
			polyAsites.close();
		}

		catch (net.sf.picard.PicardException e) {
			System.out.println(e.getMessage());
			// System.out.println(e.toString()); //For error detection
		}

	}

	public static String tailMaker(int tailLength) {
		String tail = "";
		for (int i = 0; i < tailLength; i++) {
			tail = tail + "A";
		}
		return tail;
	}

}
