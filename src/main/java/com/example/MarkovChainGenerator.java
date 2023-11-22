/*
 * Used by Zelda Shirk, Oct. 2023
 */


package com.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MarkovChainGenerator<E> extends ProbabilityGenerator<E> {

	//class variables
	//ArrayList<E> alphabet = new ArrayList<E>();
	ArrayList<ArrayList<Float>> transitionTable = new ArrayList<ArrayList<Float>>(); //transition table, tt for short
	//int tokenCount = 0;
	ProbabilityGenerator<E> probgen;

	//begin coding zelda
	void train(ArrayList<E> data)
	{
		probgen = new ProbabilityGenerator<E>();
		probgen.train(data);
		int lastIndex = -1;

		for(int i=0; i<data.size(); i++)
		{
			E token = data.get(i);
			int tokenIndex = alphabet.indexOf(token); //current token
			if(!(alphabet.contains(token))) //if the current token is not found in alphabet
			{
				//expanding transition table vertically
				ArrayList<Float> newRow = new ArrayList<Float>();
				for(int j=0; j<alphabet.size(); j++)
				{
					newRow.add(0.0f); //for each array in tt, add 0 (float)
				}
				transitionTable.add(newRow); //adding the row i just created in the for loop
				//now expanding transition table horizontally
				for(int j=0; j<transitionTable.size(); j++)
				{
					transitionTable.get(j).add(0.0f); //for each array in tt, add 0 (float)
				}
				alphabet.add(token); //adding the token to alphabet
				tokenIndex = alphabet.indexOf(token); //updating current token
				tokenCounts.add((float) 0);
			}

			if(lastIndex > -1) //if not the first time through the loop (aka we have a previous token)
			{
				// ArrayList<Float> row = transitionTable.get(lastIndex); //vertical placement in table
				// float column = row.get(tokenIndex); //horizontal placement in table of current row
				// column += 1.0f; //adding 1 to the value found above
				// row.set(tokenIndex, column); //setting index of row at tokenIndex to value of column
				float capture = transitionTable.get(lastIndex).get(tokenIndex); //current token
				transitionTable.get(lastIndex).set(tokenIndex, capture + 1.0f); //just adding 1 to the current
			}
			tokenCounts.set(tokenIndex, tokenCounts.get(tokenIndex) + 1.0f); //incrementing token count by 1
			lastIndex = tokenIndex; //setting current to previous for next loop
		}
	}

	//generate goes here

  	//nested convenience class to return two arrays from sortTransitionTable() method
	//students do not need to use this class
	protected class SortTTOutput
	{
		public ArrayList<E> symbolsListSorted;
		ArrayList<ArrayList<Float>> ttSorted;
	}

	//sort the symbols list and the counts list, so that we can easily print the probability distribution for testing
	//symbols -- your alphabet or list of symbols (input)
	//tt -- the unsorted transition table (input)
	//symbolsListSorted -- your SORTED alphabet or list of symbols (output)
	//ttSorted -- the transition table that changes reflecting the symbols sorting to remain accurate  (output)
	public SortTTOutput sortTT(ArrayList<E> symbols, ArrayList<ArrayList<Float>> tt)	{

		SortTTOutput sortArraysOutput = new SortTTOutput(); 
		
		sortArraysOutput.symbolsListSorted = new ArrayList<E>(symbols);
		sortArraysOutput.ttSorted = new ArrayList<ArrayList<Float>>();
	
		//sort the symbols list
		Collections.sort(sortArraysOutput.symbolsListSorted, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		//use the current sorted list to reference the counts and get the sorted counts
		for(int i=0; i<sortArraysOutput.symbolsListSorted.size(); i++)
		{
			int index = symbols.indexOf(sortArraysOutput.symbolsListSorted.get(i));
			sortArraysOutput.ttSorted.add(new ArrayList<Float>());
			for( int j=0; j<tt.get(index).size(); j++)
			{
				int index2 = symbols.indexOf(sortArraysOutput.symbolsListSorted.get(j));
				sortArraysOutput.ttSorted.get(i).add(tt.get(index).get(index2));
			}
		}

		return sortArraysOutput;

	}
	
	//this prints the transition table
	//symbols - the alphabet or list of symbols found in the data
	//tt -- the transition table of probabilities (not COUNTS!) for each symbol coming after another
	public void printProbabilityDistribution(boolean round, ArrayList<E> symbols, ArrayList<ArrayList<Float>> tt)
	{
		//sort the transition table
		SortTTOutput sorted = sortTT(symbols, tt);
		symbols = sorted.symbolsListSorted;
		tt = sorted.ttSorted;

		System.out.println("-----Transition Table -----");
		
		System.out.println(symbols);
		
		for (int i=0; i<tt.size(); i++)
		{
			System.out.print("["+symbols.get(i) + "] ");
			for(int j=0; j<tt.get(i).size(); j++)
			{
				//figuring out position in table, dividing by total in row to get probability
				double position = tt.get(i).get(j);
				double sum = addRows(tt.get(i));
				double dividend = 0;

				//logic for the actual probability (dividend)
				if(sum != 0)
				{
					dividend = position / sum;
				}

				if(round)
				{
					DecimalFormat df = new DecimalFormat("#.##");
					System.out.print(df.format(dividend)+ " ");
				}
				else
				{
					System.out.print(dividend + " ");
				}
			
			}
			System.out.println();


		}
		System.out.println();
		
		System.out.println("------------");
	}

	public float addRows(ArrayList<Float> rowToAdd)
	{
		//row by row
		//add up all values in one row
		//divide an index by the sum
		float total = 0;

		for(int i=0; i<rowToAdd.size(); i++)
		{
			total += rowToAdd.get(i);
		}
		return total;

	}

	//overloading previous print method ^^, will call in main
	public void printProbabilityDistribution(boolean round)
	{
		printProbabilityDistribution(round, alphabet, transitionTable);
	}

	public E generate(E initToken)
	{
		// E initToken = probgen.selectRandomSymbol();
		// ArrayList<E> output = new ArrayList<>();

		float total = 0;
		int initTokenIndex = alphabet.indexOf(initToken);
		//System.out.println("initToken"+initToken);
		ArrayList<Float> rowTotal = transitionTable.get(initTokenIndex);

		for(int i=0; i<rowTotal.size(); i++)
		{
			total += rowTotal.get(i);
		}
		if(total == 0)
		{


			E token = probgen.generate(1).get(0);
			//System.out.println("line 200 " + token);
			return token;
		}

		else {
			tokenCount = (int) total;
			tokenCounts = rowTotal;
			ArrayList<E> tokens = super.generate(5);
			//System.out.println("tokens size " + tokens.size());

			// System.out.println("line 206 " + token + " total " + total);
			// System.out.println("alphabet " + alphabet);
			// System.out.println("rowTotal " + rowTotal);
			return tokens.get(0);
		}
	}

	public ArrayList<E> generate(E initToken, int x) //x is how many times i want to generate, aka how many tokens or loops
	{
		E token = generate(initToken);
		ArrayList<E> listOfTokens = new ArrayList<E>();
		for(int i=0; i<x; i++)
		{
			listOfTokens.add(token);
			token = generate(token);
		}
		return listOfTokens;
	}

	//potential idea
	// public E markovSymbolSelect(int inputToken)
	// {
	// 	int tokenIndex = alphabet.indexOf(inputToken);
	// 	ArrayList<Float> row = transitionTable.get(tokenIndex);

	// }
}
