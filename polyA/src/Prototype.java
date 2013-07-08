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


public class Prototype {
//-------------------Samir------------------
String bases;	
String feed;
//-----------------Samir--------------------

	public Prototype(String refGenomeFileLocation,
			String PolyAsitesFileLocation, int numberToExtract, int tailLength,
			String newFileLocationAndNewFileName) throws IOException {
		
		
		
		// This creates a variable of type file.
		File genome = new File(refGenomeFileLocation);
		// This creates a variable of type file.
		File polyAfile = new File(PolyAsitesFileLocation);
		// This retrieves the .fai file, given the .fa file.
		IndexedFastaSequenceFile seqFile = new IndexedFastaSequenceFile(genome);
		// This was left like this just for instantiation
		ReferenceSequence refSeq; // = seqFile.getSubsequenceAt("chr1", 10,10005); // Notice, this takes the first 5 bases (after all the Ns).
		// Reading from the .bed file; which contains the Poly-A sites.
		BasicInputParser polyAsites = new BasicInputParser(true, polyAfile);
		// These 3 variables will serve as inputs in the getSubsequenceAt method
		// above.
		String chromosome = "";
		int start = 0;
		int end = 1;
		int extract = numberToExtract - 1;
		String header = "";
		String tail = tailMaker(tailLength);
		//-------------------------Reynaldo--------------------------------
		String minus = "-";
		boolean isMinus = false;
		//-------------------------Reynaldo--------------------------------
		// These next two lines are responsible for making the file.
		File file = new File(newFileLocationAndNewFileName);
		Writer writer = new BufferedWriter(new FileWriter(file));
		// This is used to make the file start off from the first line, rather
		// than starting off on the second line.
		int indicator = 0;
		try {
			 //while (polyAsites.hasNext()) {
			 for (int i = 0; i < 9; i++) { // This is just for testing purposes
				// This moves onto the next line in the poly-A site file.
				polyAsites.next();
				
				// This Scanner reads the line.
				Scanner myScanner = new Scanner(polyAsites.getCurrentLine());
				chromosome = myScanner.next();
				int chromosomeSize = seqFile.getSequence(chromosome).length();
				//-------------------------Reynaldo--------------------------------
				//This small loop puts the scanner in position to detect the if it refers to the positive or negative strand.
				for (int k=0; k<4 ;k++){
					myScanner.next();
				}
				
				if ( myScanner.next().equalsIgnoreCase("-")/*minus.equalsIgnoreCase(myScanner.findInLine("-"))*/) { //If the last column contains a "-".
					myScanner= new Scanner(polyAsites.getCurrentLine()); //To reset the scanner to the beginning of the line.
					myScanner.next(); //To skip first column in bed file.
					start = myScanner.nextInt() + 1;
					end = start + extract;
					isMinus = true;
					//Truncates if exceeds bounds.
					if (start < 0) {
						start = 1;
					}
					if (end > chromosomeSize){
						end = chromosomeSize;
					}
				}
				else { //If the last column didn't contain a minus, then it must have a "+".
					myScanner= new Scanner(polyAsites.getCurrentLine()); //To reset the scanner to the beginnig of the line.
					myScanner.next(); //To skip first column in bed file.
					end = myScanner.nextInt() + 1;
					start = end - extract;
					isMinus = false;
					//Truncates if exceeds bounds.
					if (start < 0) {
						start = 1;
					}
					if (end > chromosomeSize){
						end = chromosomeSize;
					}
				}
				//-------------------------Reynaldo--------------------------------
				refSeq = seqFile.getSubsequenceAt(chromosome, start, end);
				bases = new String(refSeq.getBases());
				// -------------------------Samir---------------------------------
				String strand;
				String fwd;
				String nuBases;
				for (int j = 0; j < 4; j++) {
					strand = myScanner.next();
					fwd = "-";
					if (j >= 2 && strand.equals(fwd)) {
						//System.out.println(strand + "original strand:	");
						seq();
						//nuBases = bases.replace(bases, feed );
					}
					
				}
				
				// -----------------------Samir-------------------------------
				//-------------------------Reynaldo--------------------------------
				// This part makes the .fa file if the stand is '-'
				if (isMinus){
					if (chromosome.equals(header)) {
						writer.write("\n" + ">" + header + ":" + start + "-" + end+ "+" + tailLength + "A");
						writer.write("\n" + feed + tail);
						System.out.println(">" + header + ":" + start + "-" + end+ "+" + tailLength + "A"); // Testing purposes
						System.out.println(feed + tail); // Testing purposes
					} else {
						if (indicator == 1) {
							header = chromosome;
							writer.write("\n" + ">" + header + ":" + start + "-"+ end + "+" + tailLength + "A");
							writer.write("\n" + feed + tail);
							System.out.println(">" + header + ":" + start + "-"+ end + "+" + tailLength + "A"); // Testing purposes
							System.out.println(feed + tail); // Testing purposes
						} else {
							header = chromosome;
							writer.write(">" + header + ":" + start + "-" + end+ "+" + tailLength + "A");
							writer.write("\n" + feed + tail);
							System.out.println(">" + header + ":" + start + "-"+ end + "+" + tailLength + "A"); // Testing
							System.out.println(feed + tail); // Testing purposes
							indicator = 1;
						}
					}
				}
				//-------------------------Reynaldo--------------------------------
				//-------------------------Reynaldo--------------------------------
				// This part makes the .fa file if the strand is '+'
				else {
					if (chromosome.equals(header)) {
						writer.write("\n" + ">" + header + ":" + start + "-" + end+ "+" + tailLength + "A");
						writer.write("\n" + bases + tail);
						System.out.println(">" + header + ":" + start + "-" + end+ "+" + tailLength + "A"); // Testing purposes
						System.out.println(bases + tail); // Testing purposes
					} else {
						if (indicator == 1) {
							header = chromosome;
							writer.write("\n" + ">" + header + ":" + start + "-"+ end + "+" + tailLength + "A");
							writer.write("\n" + bases + tail);
							System.out.println(">" + header + ":" + start + "-"+ end + "+" + tailLength + "A"); // Testing purposes
							System.out.println(bases + tail); // Testing purposes
						} else {
							header = chromosome;
							writer.write(">" + header + ":" + start + "-" + end+ "+" + tailLength + "A");
							writer.write("\n" + bases + tail);
							System.out.println(">" + header + ":" + start + "-"+ end + "+" + tailLength + "A"); // Testing
							System.out.println(bases + tail); // Testing purposes
							indicator = 1;
						}
					}
				}
				//-------------------------Reynaldo--------------------------------
			}
			writer.close();
			polyAsites.close();
		}

		catch (net.sf.picard.PicardException e) {
			System.out.println(e.getMessage());
			writer.close();
			// System.out.println(e.toString()); //For error detection
		}

	

	}

	// ------------------------------Samir------------------------------------

	// String line = null;

	public char replace(char in) {
			switch (in) {
			case 'A':
				return 'T';
			case 'T':
				return 'A';
			case 'C':
				return 'G';
			case 'G':
				return 'C';
			//-------------------------Reynaldo--------------------------------
			case 'a':
				return 't';
			case 't':
				return 'a';
			case 'c':
				return 'g';
			case 'g':
				return 'c';
			case 'R':
				return 'Y';
			case 'Y':
				return 'R';
			case 'K':
				return 'M';
			case 'M':
				return 'K';
			case 'S':
				return 'W';
			case 'W':
				return 'S';
			case 'B': case 'D': case 'H': case 'V': case 'N': case 'X': case '-':
				return in;
			//-------------------------Reynaldo--------------------------------
			default:
				System.out.println("This is the problem: " + in);
				throw new IllegalArgumentException();
			}
	}

	public void seq() {
		String in= bases;
		final StringBuilder b = new StringBuilder(in.length());
		for (int i = in.length(); i > 0; i--)
			b.append(replace(in.charAt(i - 1)));
		feed = b.toString();
		
		//System.out.println(feed); //(Reynaldo)We can comment this out, but leave it like this for testing.
		
		
	}

	// --------------------------------Samir----------------------------------

	public String tailMaker(int tailLength) {
		String tail = "";
		for (int i = 0; i < tailLength; i++) {
			tail = tail + "A";
		}
		return tail;
	}

	public static void main(String[] args) throws IOException {
		Prototype app = new Prototype(
				"/home/reynaldo/Documents/RegressionTest/RegressionTestGenome.fa",
				"/home/reynaldo/Documents/RegressionTest/RegressionBedFile.bed",
				40, 0, "/home/reynaldo/Desktop/testFile7.fa");
	}

}
