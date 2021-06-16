
/**********************************
**	Author: Brent Dudley (btd27)
**	Class: CS1653/Khattab/MW-12:30
**	Assignment 1
**********************************/

import java.util.*;
import java.io.*;
import java.util.Scanner;

/*
**	Importing some file printing packages to check lists
*/
import java.io.FileWriter;
import java.io.IOException;


public class Kasiski {
	
	public static void main(String[] args) throws IOException {
		// Read in the ciphertext
		File file = new File("ciphertext.txt");
		Scanner inputFile = new Scanner(file);
		StringBuilder CTextBuilder = new StringBuilder();
		String word;
		int CTextCounter = 0;
		while (inputFile.hasNext()) {
			word = inputFile.nextLine();
			CTextBuilder.append(word);
			CTextCounter++;
		}

		// Some test prints
		String CText = CTextBuilder.toString();
		CText = CText.toUpperCase();
		System.out.println("Printing the ciphertext in all caps:");
		System.out.println(CText);

		// Test print: Ciphertext length
		System.out.println("");
		System.out.println("Ciphertext string length is: " + CText.length());

		/*	Going to attempt to preprocess ciphertext into all consecutive substrings
		**	of 3 <= length <= 10 characters.  Assume key won't be less than 3 char because
		**	it wouldn't be effective, and assume key will be less than 10 char because this
		**	is a practice exercise, and well, part of cracking a secret code is using what
		**	you know about your defender against them, right? (Read: I'm thinking Dr. Khattab
		**	will take pity on us in this regard, haha)
		*/	

		/*
		**	Creating file writer to check list of substrings
		*/
		FileWriter myWriter = new FileWriter("list.txt");

		// Create arraylist to store all of the substrings and string to turn into substrings
		ArrayList<String> subList = new ArrayList<String>();
		StringBuilder subToAdd = new StringBuilder();
		// FILL IT!
		for (int i = 0; i < CText.length() - 9; i++) {
			int counter = 0;
			while (counter < 3) {
				subToAdd.append(CText.charAt(i + counter));
				counter++;
			}
			subList.add(subToAdd.toString());
			// Delete this line after checking list
			myWriter.write(subToAdd.toString() + "\n");
			for (int j = 3; j < 10; j++) {
				subToAdd.append(CText.charAt(i + j));
				subList.add(subToAdd.toString());
				// Delete this line after checking list
				myWriter.write(subToAdd.toString() + "\n");
			}
			subToAdd.delete(0, subToAdd.length());
		}
		// Close the FileWriter
		myWriter.close();
		// List tested successfully, moving on to trying frequency count

		/*	Frequency Count:  Make a new list of all substrings who have a frequency >= 2
		**	Use Collections.frequency(<input list>, <string to check for>)
		**
		**	int occurrence: an int to be used later to make 2D array for checking distances
		**	int[][] distanceArray: Row Dominant; Each new row is a different substring, each new column is the distance between each
		**	occurrence of that substring.
		*/
		int occurrence = 0;
		//int[][] distanceArray = new int[repeatSubs.size()][occurrence - 1];

		ArrayList<String> repeatSubs = new ArrayList<String>();
		for (int i = 0; i < subList.size(); i++) {
			int checkNum = Collections.frequency(subList, subList.get(i));
			if (checkNum > 1) {
				if (checkNum > occurrence) {
				occurrence = checkNum;
				}

				if (!repeatSubs.contains(subList.get(i))) {
					repeatSubs.add(subList.get(i));
				}

				//distanceArray[i][] = repeatSubs.indexOf(subList.get(i));
			}
		}

		FileWriter outputFreq = new FileWriter("substringList.txt");
		for (int i = 0; i < repeatSubs.size(); i++) {
			String substringToAdd = repeatSubs.get(i);
			int substringFreqToAdd = Collections.frequency(subList, repeatSubs.get(i));
			outputFreq.write(substringToAdd + " " + substringFreqToAdd + "\n");
		}
		outputFreq.close();
		/*
		** This section just used for testing
		for (int i = 0; i < repeatSubs.size(); i++) {
			System.out.println(repeatSubs.get(i));
		}
		*/

		/*
		**	Use indexOf method from StringBuilder to get indeces of substring matches
		*/



	}
}