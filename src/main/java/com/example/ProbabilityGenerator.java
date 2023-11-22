/*
 * c2017-2023 Courtney Brown (NOTE: you'll have to change the name and give me a bit of credit!)
 * 
 * Class: ProbabliityGenerator
 * 
 * Used by Zelda Shirk, Sept. 2023
 */

package com.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProbabilityGenerator<E> {
	// i am creating these arraylists -zelda
	ArrayList<E> alphabet = new ArrayList<E>();
	ArrayList<Float> tokenCounts = new ArrayList<Float>();
	double tokenCount = 0;

	void train(ArrayList<E> data) {
		// write code for train
		for (int i = 0; i <= data.size() - 1; i++) {
			// adding index to alphabet array
			int index = alphabet.indexOf(data.get(i));
			// if its not there...
			if (index == -1) {
				// add index and count
				index = alphabet.size();
				alphabet.add(data.get(i));
				tokenCounts.add((float) 0);
			}
			// increasing the counter when if statement executes
			tokenCounts.set(index, tokenCounts.get(index) + 1);
			tokenCount++;

		}
	}

	// the actual generation method of assigning and finding random numbers -> to
	// make notes or tokens
	public ArrayList<E> generate(int x) {
		double value;
		ArrayList<E> tokens = new ArrayList<>();
		float rIndex; // the random number generation

		// for x times, loop how many times i want to generate:
		for (int j = 0; j < x; j++) {
			rIndex = (float) Math.random();
			double target = 0;
			// loop for alphabet's size
			for (int i = 0; i < tokenCounts.size(); i++) {
				//System.out.println("here line 54");

				//System.out.println("rIndex: " + rIndex);
				//System.out.println("target: " + target);

				value = (tokenCounts.get(i) / tokenCount);
				target += value; // cumulative of values
				if (rIndex < target) // if the random number is less than the count
				// probability, then...
				{
					//System.out.println("here line 59");

					tokens.add(alphabet.get(i)); // ...the token will have the value of alphabet array at index i
					break;
				}
				// this might not be necessary
				// else {
				// rIndex -= tokenCounts.get(i) / tokenCount; // ..and if not, then the random
				// // number is subtracted from the probability above and gone through again
				// }
			}
			//System.out.println(alphabet);
			//System.out.println(tokenCounts);
			//System.out.println(tokens);

			//System.out.println(j);
		}
		return tokens; // returning the result of the generation above^^
	}

	// logic of generate above, but tweaking it for markov project.
	E generate(ArrayList<E> alphabet, ArrayList<Float> tokenCounts, double tokenCount) {
		E token = null;
		double value;
		float rIndex; // the random number generation

		for (int i = 0; i < alphabet.size(); i++) {
			rIndex = (float) Math.random(); // initializng the RNG logic
			value = (tokenCounts.get(i) / tokenCount);
			if (rIndex < value) // if the random number is less than the count probability, then...
			{
				token = alphabet.get(i); // ...the token will have the value of alphabet array at index i
				break;
			} else {
				rIndex -= value;
			}
		}
		return token;
	}

	public E selectRandomSymbol() {
		double rIndex = Math.random();
		double target = 0;
		E output = null;
		// loop for alphabet's size
		for (int i = 0; i < tokenCounts.size(); i++) {
			double value = (tokenCounts.get(i) / tokenCount);
			target += value; // cumulative of values
			if (rIndex < target) // if the random number is less than the count probability, then...
			{
				output = alphabet.get(i); // ...the token will have the value of alphabet array at index i
				break;
			}
		}
		if (output == null) {
			output = alphabet.get(alphabet.size() - 1);
		}

		return output;
	}

	// this is probably incorrect, going to fix og generate method
	// overriding generate^^
	ArrayList<E> generate2(int x) // this is how many times the loop will occur (see for loop parameters below)
	{
		ArrayList<E> gen = new ArrayList<E>(); // new arraylist to use for overriding
		// loop for x amount of times,
		for (int i = 0; i < x; i++) {
			gen.add(generate(alphabet, tokenCounts, tokenCount)); // and add the generate method info to the gen array
																	// list override
		}
		return gen; // then return the overwritten arraylist of tokens
	}

	// nested convenience class to return two arrays from sortArrays() method
	// students do not need to use this class
	protected class SortArraysOutput {
		public ArrayList<E> symbolsListSorted;
		public ArrayList<Float> symbolsCountSorted;
	}

	// sort the symbols list and the counts list, so that we can easily print the
	// probability distribution for testing
	// symbols -- your alphabet or list of symbols (input)
	// counts -- the number of times each symbol occurs (input)
	// symbolsListSorted -- your SORTED alphabet or list of symbols (output)
	// symbolsCountSorted -- list of the number of times each symbol occurs inorder
	// of symbolsListSorted (output)
	public SortArraysOutput sortArrays(ArrayList<E> symbols, ArrayList<Float> counts) {

		SortArraysOutput sortArraysOutput = new SortArraysOutput();

		sortArraysOutput.symbolsListSorted = new ArrayList<E>(symbols);
		sortArraysOutput.symbolsCountSorted = new ArrayList<Float>();

		// sort the symbols list
		Collections.sort(sortArraysOutput.symbolsListSorted, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		// use the current sorted list to reference the counts and get the sorted counts
		for (int i = 0; i < sortArraysOutput.symbolsListSorted.size(); i++) {
			int index = symbols.indexOf(sortArraysOutput.symbolsListSorted.get(i));
			sortArraysOutput.symbolsCountSorted.add(counts.get(index));
		}

		return sortArraysOutput;

	}

	// Students should USE this method in your unit tests to print the probability
	// distribution
	// HINT: you can overload this function so that it uses your class variables
	// instead of taking in parameters
	// boolean is FALSE to test train() method & TRUE to test generate() method
	// symbols -- your alphabet or list of symbols (input)
	// counts -- the number of times each symbol occurs (input)
	// sumSymbols -- the count of how many tokens we have encountered (input)
	public void printProbabilityDistribution(boolean round, ArrayList<E> symbols, ArrayList<Float> counts,
			double sumSymbols) {
		// sort the arrays so that elements appear in the same order every time and it
		// is easy to test.
		SortArraysOutput sortResult = sortArrays(symbols, counts);
		ArrayList<E> symbolsListSorted = sortResult.symbolsListSorted;
		ArrayList<Float> symbolsCountSorted = sortResult.symbolsCountSorted;

		System.out.println("-----Probability Distribution-----");

		for (int i = 0; i < symbols.size(); i++) {
			if (round) {
				DecimalFormat df = new DecimalFormat("#.##");
				System.out.println("Data: " + symbolsListSorted.get(i) + " | Probability: "
						+ df.format((double) symbolsCountSorted.get(i) / sumSymbols));
			} else {
				System.out.println("Data: " + symbolsListSorted.get(i) + " | Probability: "
						+ (double) symbolsCountSorted.get(i) / sumSymbols);
			}
		}

		System.out.println("------------");
	}

	// overloading previous print method ^^, will call in main
	public void printProbabilityDistribution(boolean round) {
		printProbabilityDistribution(round, alphabet, tokenCounts, tokenCount);
	}
}