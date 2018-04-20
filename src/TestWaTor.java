//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title:           WaTor
// Files:           WaTor, TestWaTor, Config
// Course:          Cs200, Semester 2, freshman
//
// Author:          Aksel Torgerson
// Email:           atorgerson@wisc.edu
// Lecturer's Name: Marc Renault
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ///////////////////
//
// Partner Name:    None
// Partner Email:   None
// Lecturer's Name: None
// 
// VERIFY THE FOLLOWING BY PLACING AN X NEXT TO EACH TRUE STATEMENT:
//   ___ Write-up states that pair programming is allowed for this assignment.
//   ___ We have both read and understand the course Pair Programming Policy.
//   ___ We have registered our team prior to the team registration deadline.
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully 
// acknowledge and credit those sources of help here.  Instructors and TAs do 
// not need to be credited here, but tutors, friends, relatives, room mates 
// strangers, etc do.  If you received no outside help from either type of 
// source, then please explicitly indicate NONE.
//
// Persons:         None
// Online Sources:  None
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////



/**
 * This file contains testing methods for the WaTor project.
 * These methods are intended to serve several objectives:
 * 1) provide an example of a way to incrementally test your code
 * 2) provide example method calls for the WaTor methods
 * 3) provide examples of creating, accessing and modifying arrays
 *    
 * Toward these objectives, the expectation is that part of the 
 * grade for the WaTor project is to write some tests and
 * write header comments summarizing the tests that have been written. 
 * Specific places are noted with TODO but add any other comments 
 * you feel would be useful.
 * 
 * Some of the provided comments within this file explain
 * Java code as they are intended to help you learn Java.  However,
 * your comments and comments in professional code, should
 * summarize the purpose of the code, not explain the meaning
 * of the specific Java constructs.
 *    
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This class contains a few methods for testing methods in the WaTor
 * class as they are developed. These methods are all private as they are only
 * intended for use within this class.
 * 
 * @author Jim Williams
 * @author Aksel Torgerson
 *
 */
public class TestWaTor {
    
    /**
     * This is the main method that runs the various tests. Uncomment the tests
     * when you are ready for them to run.
     * 
     * @param args  (unused)
     */
    public static void main(String []args) {

        //milestone 1
        testClearMoves();
        testEmptyArray();
        testCountCreatures();
  
        //milestone 2
        testUnoccupiedPositions();
        testChooseMove();
        testFishPositions();
        
        //milestone 3
        //comparing results of either inputting from or outputting to files
        //may be the easiest way to test.
    }
    
    /**
     * Compares the lists to see if they are the same size and contain the same elements.
     * @param list1 One list of coordinates.
     * @param list2 Another list of coordinates
     * @return Whether the lists contain the same coordinates or not.
     */
    private static boolean matchingArrayLists(ArrayList<int[]>list1, ArrayList<int[]>list2) {
        boolean result = true;
        if ( list1.size() != list2.size()) {
            System.err.println("list1 size: " + list1.size() + " list2 size:" + list2.size() + " should be the same");
             result = false;
             return result;
        } 
        for ( int i = 0; i < list1.size(); i++) {
            int[]move1 = list1.get(i);
            int[]move2 = list2.get(i);
            if ( move1[0] == move2[0] && move1[1] == move2[1]) {
                //ok
            } else {
                result = false;
                System.err.println( "list1("+ i+"):" + Arrays.toString(move1) + " doesn't match in list2: " + Arrays.toString( move2));
               
                break;
            }
        }
        return result;
    }
    
    /**
     * This runs some tests on the unoccupiedPositions method. 
     */
    private static void testUnoccupiedPositions() {
        boolean error = false;
        
        int [][]fish = new int[][]{ {-1,-1,-1},
                                    {-1, 0,-1},
                                    {-1,-1,-1}}; // create a new array of fish and sharks
        
        int [][]sharks = new int[][]{   {-1,-1,-1},
                                        {-1,-1,-1},
                                        {-1,-1,-1}}; // fill them each with -1 or 0
        
        ArrayList<int[]> positions = WaTor.unoccupiedPositions( fish, sharks, 1, 1); // create an array list of positions from our method
        ArrayList<int[]>expected = new ArrayList<>(); // create an array of expected positions
        expected.add( new int[]{0,1}); // fill the array with the expected outputs
        expected.add( new int[]{2,1});
        expected.add( new int[]{1,0});
        expected.add( new int[]{1,2});
        if ( !matchingArrayLists( expected, positions)) { // if our array lists dont match then output an error
            error = true;
            System.err.println("testUnoccupiedPositions 1 :" );
        }
        
        positions = WaTor.unoccupiedPositions( fish, sharks, 0, 1); // repeat above code for the rest of the tests
        expected = new ArrayList<>();
        expected.add( new int[]{2,1});
        expected.add( new int[]{0,0});
        expected.add( new int[]{0,2});
        if ( !matchingArrayLists( expected, positions)) {
            error = true;
            System.err.println("testUnoccupiedPositions 2 :" );
        }
        
        positions = WaTor.unoccupiedPositions( fish, sharks, 0, 0);
        expected = new ArrayList<>();
        expected.add( new int[]{2,0});
        expected.add( new int[]{1,0});
        expected.add( new int[]{0,2});
        expected.add( new int[]{0,1});
        if ( !matchingArrayLists( expected, positions)) {
            error = true;
            System.err.println("testUnoccupiedPositions 3 :" );
        }        
        
        fish = new int[][]{{0,0,0},{0,0,0},{0,0,0}}; // create a new starting array for fish and sharks
        sharks = new int[][]{{-1,-1,-1},{-1,-1,-1},{-1,-1,-1}};
        
        positions = WaTor.unoccupiedPositions( fish, sharks, 1, 1);
        expected = new ArrayList<>();

        if ( !matchingArrayLists( expected, positions)) { // again repeat the code for 3 more tests
            error = true;
            System.err.println("testUnoccupiedPositions 4 :" );
        }
        
        positions = WaTor.unoccupiedPositions( fish, sharks, 0, 1);
        expected = new ArrayList<>();

        if ( !matchingArrayLists( expected, positions)) {
            error = true;
            System.err.println("testUnoccupiedPositions 5 :" );
        }
        
        positions = WaTor.unoccupiedPositions( fish, sharks, 0, 0);
        expected = new ArrayList<>();

        if ( !matchingArrayLists( expected, positions)) {
            error = true;
            System.err.println("testUnoccupiedPositions 6 :" );
        }        
        fish = new int[][]{{0,0,0},{-1,0,0},{0,0,0}}; // make a new fish and shark array again
        sharks = new int[][]{{-1,-1,-1},{-1,-1,-1},{-1,-1,-1}};
        
        positions = WaTor.unoccupiedPositions( fish, sharks, 1, 1); // repeat these tests again for 3
        expected = new ArrayList<>();
        expected.add( new int[]{1,0});

        if ( !matchingArrayLists( expected, positions)) {
            error = true;
            System.err.println("testUnoccupiedPositions 7 :" );
        }
        
        positions = WaTor.unoccupiedPositions( fish, sharks, 0, 1);
        expected = new ArrayList<>();
        if ( !matchingArrayLists( expected, positions)) {
            error = true;
            System.err.println("testUnoccupiedPositions 8 :" );
        }
        
        positions = WaTor.unoccupiedPositions( fish, sharks, 0, 0);
        expected = new ArrayList<>();
        expected.add( new int[]{1,0});
        if ( !matchingArrayLists( expected, positions)) {
            error = true;
            System.err.println("testUnoccupiedPositions 9 :" );
        }
        
        if ( error) { // if any errors are true then print failed else print passed
            System.err.println("testUnoccupiedPositions failed");
        } else {
            System.out.println("testUnoccupiedPositions passed");            
        }
    }
    
    /**
     * This runs some tests on the fishPositions method. 
     */
    private static void testFishPositions() {
            
        //------------------------ TEST 1 -------------------------------------
        boolean error = false;
        int[][] testBoard = {{Config.EMPTY, 1, Config.EMPTY},       // -1, 1 ,-1
                            {3, Config.SHARK_MARK, Config.EMPTY},   //  3, O ,-1
                            {2, 5, 2}};                             //  2, 5 , 2
        
       
        ArrayList<int[]> expected = new ArrayList<>(); // create array objects for expected and returned
        ArrayList<int[]> returned = new ArrayList<>();
    
        int[] fish1 = {0,1}; // create our expected array locations using our test board
        int[] fish2 = {2,1};
        int[] fish3 = {1,0};

        expected.add(fish1); // add them to our array list
        expected.add(fish2);
        expected.add(fish3);
        
        returned = WaTor.fishPositions(testBoard, 1, 1); // actually run our method 

        if (!matchingArrayLists(expected, returned)) { // if they arent equal flag an error
            error = true;
            System.err.println("fishPositions(testBoard, 1,1) has failed because the array did not match the expected");
        }
 
        //------------------------ TEST 2 ------------------------------------- // repeat for two more tests where our shark in on the edge
        
        error = false;
        int[][] testBoard2 =   {{Config.EMPTY, 1, Config.EMPTY},     // -1, 1 ,-1
                                {Config.SHARK_MARK, -1, 7},          //  O, -1, 7
                                {2, 5, 2}};                          //  2, 5 , 2
        
        expected = new ArrayList<>();
        returned = new ArrayList<>();
    
        int[] fish4 = {2,0};
        int[] fish5 = {1,2};

        expected.add(fish4);
        expected.add(fish5);
        
        returned = WaTor.fishPositions(testBoard2, 1, 0);

        if (!matchingArrayLists(expected, returned)) {
            error = true;
            System.err.println("fishPositions(testBoard2, 1, 0) has failed because the array did not match the expected");
        }
        
        //------------------------ TEST 3 -------------------------------------
        
        error = false;
        int[][] testBoard3 =   {{Config.SHARK_MARK, 1, 1},       //  O,  1,  1
                                {3, -1, 7},                                 //  3, -1, -1
                                {2, 5, 2}};                                 //  1, -1, -1
        
        expected = new ArrayList<>();
        returned = new ArrayList<>();
    
        int[] fish6 = {2,0};
        int[] fish7 = {1,0};
        int[] fish8 = {0,2};
        int[] fish9 = {0,1};

        expected.add(fish6);
        expected.add(fish7);
        expected.add(fish8);
        expected.add(fish9);
        
        returned = WaTor.fishPositions(testBoard3, 0, 0);

        if (!matchingArrayLists(expected, returned)) {
            error = true;
            System.err.println("fishPositions(testBoard3, 0, 0) has failed because the array did not match the expected");
        }

        //----------------------- Final Printout -----------------------------------
        
        if (error) { // if any errors are true then print failed else print passed
            System.err.println("testFishPositions failed");
        } else {
            System.out.println("testFishPositions passed");            
        }
    }

    /**
     * This runs some tests on the chooseMove method. 
     */    
    private static void testChooseMove() {
        boolean error = false;
        Random randGen = new Random();
        randGen.setSeed(456); // make a new random object with a seed of 456
        
        ArrayList<int[]> input = new ArrayList<>(); // make a new array list object
        int [] expected = null;
        int [] result = WaTor.chooseMove(input, randGen); // put results of chooseMove into result array
        if ( result != expected) {
            error = true;
            System.err.println("testChooseMove 0: result not null");
        }
        
        input.clear(); // clear the input array list
        int [] oneMove = new int[] {0,1}; 
        input.add(oneMove); // add one move to the input list
        expected = oneMove; // set it equal to the expected array
        result = WaTor.chooseMove( input, randGen); // get the result of the input array with the rand gen
        if ( result != expected) { // if the result is not equal to what we want flag an error and output
            error = true;
            System.err.println("testChooseMove 1: result not " + Arrays.toString(oneMove));
        }
        
        input.clear(); // clear the input array
        int [] move1 = new int[] {0,1}; // make two move arrays
        int [] move2 = new int[] {1,0};
        input.add( move1); // add each array to the input array list
        input.add( move2);
        int move1Count = 0; // set the move count to 0 
        int move2Count = 0;
        int numTrials = 1000; // set the number of trials to 1000
        for ( int i = 0; i < numTrials; i++) { // loop through 0-1000 with variable i
            result = WaTor.chooseMove( input, randGen); // get a result and put into result array
            if ( result == move1) move1Count++; // if the result equals our move increase move count
            else if ( result == move2) move2Count++; // if the result equals move 2 then increase move count 2
        }
        if ( move1Count != 513 || move2Count != 487 ) { // if move1count does not equal expected or move2count flag an error
            error = true; // print off error message
            System.err.println("testChooseMove 2: expected 513,487 move1Count=" + move1Count + " move2Count=" + move2Count);
        }
        
        input.clear();
        move1 = new int[] {0,1}; // init 3 moves each into respecting arrays
        move2 = new int[] {1,0};
        int[] move3 = new int[] {2,1};
        input.add( move1); // add all the moves to the input array list
        input.add( move2);
        input.add( move3);
        move1Count = 0; // init all move counts to 0
        move2Count = 0;
        int move3Count = 0;
        numTrials = 1000; 
        for ( int i = 0; i < numTrials; i++) { // loop through 1000 times
            result = WaTor.chooseMove( input, randGen); // put the result into the result array
            if ( result == move1) move1Count++; // again if the result equals move 1, 2, or 3 then inc respective move count
            else if ( result == move2) move2Count++;
            else if ( result == move3) move3Count++;
        }
        if ( move1Count != 325 || move2Count != 341 || move3Count != 334 ) { // if they dont equal expected values then flag error
            error = true; // print out error message
            System.err.println("testChooseMove 3: expected 325,341,334 move1Count=" + move1Count + " move2Count=" + move2Count + " move3Count=" + move3Count);
        }
        
        input.clear(); // repeat same exact tests as above but repeat for 4 moves instead of 3 like before
        move1 = new int[] {0,1};
        move2 = new int[] {1,0};
        move3 = new int[] {2,1};
        int []move4 = new int[] {1,2};
        input.add( move1);
        input.add( move2);
        input.add( move3);
        input.add( move4);
        move1Count = 0;
        move2Count = 0;
        move3Count = 0;
        int move4Count = 0;
        numTrials = 1000;
        for ( int i = 0; i < numTrials; i++) {
            result = WaTor.chooseMove( input, randGen);
            if ( result == move1) move1Count++; 
            else if ( result == move2) move2Count++;
            else if ( result == move3) move3Count++;
            else if ( result == move4) move4Count++;
        }
        if ( move1Count != 273 || move2Count != 231 || move3Count != 234 || move4Count != 262 ) {
            error = true;
            System.err.println("testChooseMove 4: expected 325,341,334,262 move1Count=" + move1Count + " move2Count=" + move2Count + " move3Count=" + move3Count + " move4Count=" + move4Count);
        }

        if ( error) {
            System.err.println("testChooseMove failed");
        } else {
            System.out.println("testChooseMove passed");            
        }
    }
    
    /**
     * This runs some tests on the clearMoves method. 
     */        
    private static void testClearMoves() {
        boolean error = false; // initiate error variable
        boolean [][] moves = new boolean[4][9]; // create an array called moves with 5 elements that each contain 10 booleans
        for (int row = 0; row < moves.length; row++) {
            for (int col = 0; col < moves[row].length; col++) {
                moves[row][col] = true; // fill every element in the array with true
            }
        }        
        WaTor.clearMoves(moves); // call the clearMoves function
        for (int row = 0; row < moves.length; row++) {
            for (int col = 0; col < moves[row].length; col++) {
                if ( moves[row][col]) { // if any of the elements in the moves array are still true (i.e. clear move did not work)
                    System.err.println("testClearMoves 0: move " + row + "," + col + " not false"); // print out an error message
                    error = true; // set error equal to true and break the loops
                    break;
                }
            }
        }    
        if (error) {
            System.err.println("testClearMoves failed"); // print failed if failed
        } else {
            System.out.println("testClearMoves passed"); // print passed if passed          
        }        
    }
    
    /**
     * This runs some tests on the emptyArray method. 
     */        
    private static void testEmptyArray() {
        boolean error = false;
        int [][] moves = new int[100][99]; // create a new array of integers with 101 arrays that each hold 100 ints
        for (int row = 0; row < moves.length; row++) {
            for (int col = 0; col < moves[row].length; col++) {
                moves[row][col] = Config.EMPTY + 2; //make sure not EMPTY by filling array with the number 1 (-1+2) 
            }
        }        
        WaTor.emptyArray(moves); // call the method
        for (int row = 0; row < moves.length; row++) {
            for (int col = 0; col < moves[row].length; col++) {
                if ( moves[row][col] != Config.EMPTY) { // if any of the elements dont equal the empty int then print an error
                    System.err.println("testEmptyArray 0: move " + row + "," + col + " not EMPTY");
                    error = true;
                    break;
                }
            }
        }    
        if (error) {
            System.err.println("testEmptyArray failed");
        } else {
            System.out.println("testEmptyArray passed");            
        }            
    }
    
    /**
     * This runs some tests on the countFish method. 
     */        
    private static void testCountCreatures() {
        boolean error = false;

        int[][] fish = new int[7][3]; // create a new array that has 8 arrays of 4 ints
        WaTor.emptyArray(fish); // empty the fish array
        fish[0][0] = 1;
        fish[6][2] = 2; // fill different locations of the array with fish numbered 1-5
        fish[0][2] = 3;
        fish[6][0] = 4;
        fish[3][1] = 5;
        int result = WaTor.countCreatures(fish); // count the creates in the fish array using the method
        if ( result != 5) {
            System.err.println("testCountCreatures 0: expected 5 found " + result ); // if it doesn't get 5 fish then print an error
            error = true;
        }

        if ( error) {
            System.err.println("testCountCreatures failed");
        } else {
            System.out.println("testCountCreatures passed");            
        }              
    }

}