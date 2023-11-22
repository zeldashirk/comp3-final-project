package com.example;

import java.util.ArrayList;

public class UnitTest {

    MidiFileToNotes midiNotes;

    UnitTest(MidiFileToNotes midiNotes)
    {
        this.midiNotes = midiNotes;
    }

    void test()
    {
        //first generation of pitches and rhythms
        ProbabilityGenerator<Integer> pitchgen = new ProbabilityGenerator<Integer>();
		ProbabilityGenerator<Double> rhythmgen = new ProbabilityGenerator<Double>();

        //training the pitches and rhythms from train() method logic, using the midi notes
        pitchgen.train(midiNotes.getPitchArray());
		rhythmgen.train(midiNotes.getRhythmArray());

        //this is restating the unit test 1, here instead of in main/app.java
        pitchgen.printProbabilityDistribution(false);
		rhythmgen.printProbabilityDistribution(false);

        //second creation of generation of pitches and rhythms
        ProbabilityGenerator<Integer> pitchgen2 = new ProbabilityGenerator<Integer>();
		ProbabilityGenerator<Double> rhythmgen2 = new ProbabilityGenerator<Double>();

        //generate 10,000 times, for all pitch and rhythms generations created above
        for(int i=0; i<100000; i++)
        {
            ArrayList<Integer> pitches = pitchgen.generate(20);
            ArrayList<Double> rhythms = rhythmgen.generate(20);

            pitchgen2.train(pitches);
            rhythmgen2.train(rhythms);
        }
        //printing the unit test 2 entirely
        pitchgen2.printProbabilityDistribution(true);
        rhythmgen2.printProbabilityDistribution(true);

    }
}
