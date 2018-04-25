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
// Partner Name:    NA
// Partner Email:   NA
// Lecturer's Name: NA
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
// Persons:         NA
// Online Sources:  NA
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This class contains all the methods to run the WaTor simulations. These methods 
 * are public and can be accessed anywhere, this is important because the TestWaTor 
 * file calls some of the methods in order to test the functionality of them.
 * 
 * @author Jim Williams
 * @author Aksel Torgerson
 *
 */
public class WaTor { 
    
    /**
     * This is the main method for WaTor simulation.
     * Based on: http://home.cc.gatech.edu/biocs1/uploads/2/wator_dewdney.pdf
     * This method contains the main simulation loop. In main the Scanner
     * for System.in is allocated and used to interact with the user.
     *  
     * @param args (unused)
     */
    public static void main(String[] args) {
        
        //scanner and random number generator for use throughout
        Scanner input = new Scanner(System.in);
        Random randGen = new Random();  

        //values at the same index in these parallel arrays correspond to the same creature
        
        //a value equal or greater than 0 at a location indicates a fish of that age at that location.
        int[][] fish = null;
        
        //true at a location indicates that the fish moved during the current chronon
        boolean[][] fishMoved = null; 

        //a value equal or greater than 0 at a location indicates a shark of that age at that location
        int[][] sharks = null; 
        
        //true at a location indicates that the shark moved during the current chronon
        boolean[][] sharksMoved = null;
        
        //a value equal or greater than 0 indicates the number of chronon since the shark last ate.
        int[][] starve = null;

        //an array for simulation parameters to be used when saving or loading parameters in Milestone 3
        int[] simulationParameters = null;
        
        //welcome message
        System.out.println("Welcome to Wa-Tor");

        //Ask user if they would like to load simulation parameters from a file. 
        //If the user enters a y or Y as the only non-whitespace characters
        //then prompt for filename and call loadSimulationParameters
        System.out.print("Do you want to load simulation parameters from a file (y/n): ");
        if (Character.toLowerCase(input.nextLine().charAt(0)) == 'y') {
        	System.out.print("Enter filename to load: ");
        	simulationParameters = loadSimulationParameters(input.nextLine());
        }
       
        //prompts the user to enter the simulation parameters
        if ( simulationParameters == null) {
        	System.out.println("SimParam null");
            simulationParameters = new int[Config.SIM_PARAMS.length];
            for ( int i = 0; i < Config.SIM_PARAMS.length; i++) {
                System.out.print("Enter " + Config.SIM_PARAMS[i] + ": ");
                simulationParameters[i] = input.nextInt();
            }
            input.nextLine(); //read and ignore remaining newline
        }
        
        //if seed is > 0 then set the random number generator to seed
        if (simulationParameters[indexForParam("seed")] > 0) {
            randGen.setSeed(simulationParameters[indexForParam("seed")]);
        }
        
        //save simulation parameters in local variables to help make code 
        //more readable.
        int oceanWidth = simulationParameters[indexForParam("ocean_width")];
        int oceanHeight = simulationParameters[indexForParam("ocean_height")];
        int startingFish = simulationParameters[indexForParam("starting_fish")];
        int startingSharks = simulationParameters[indexForParam("starting_sharks")];
        int fishBreed = simulationParameters[indexForParam("fish_breed")];
        int sharksBreed = simulationParameters[indexForParam("sharks_breed")];
        int sharksStarve = simulationParameters[indexForParam("sharks_starve")];

        //create parallel arrays to track fish and sharks
        fish = new int[oceanHeight][oceanWidth];
        fishMoved = new boolean[oceanHeight][oceanWidth];
        sharks = new int[oceanHeight][oceanWidth];
        sharksMoved = new boolean[oceanHeight][oceanWidth];
        starve = new int[oceanHeight][oceanWidth];

        //make sure fish, sharks and starve arrays are empty (call emptyArray)
        emptyArray(fish);
        emptyArray(sharks);
        emptyArray(starve);

        //place the initial fish and sharks and print out the number placed
        int numFish = placeFish(fish, startingFish, fishBreed, randGen);
        System.out.println("Placed " + numFish + " fish.");
        
        int numSharks = placeSharks(fish, sharks, startingSharks, sharksBreed, randGen);
        System.out.println("Placed " + numSharks + " sharks.");

        int currentChronon = 1;
        
        //init the history array list
        ArrayList<int[]> history = new ArrayList<int[]>();

        //simulation ends when no more sharks or fish remain
        boolean simulationEnd = numFish <= 0 || numSharks <= 0;
        while (!simulationEnd){

            //print out the locations of the fish and sharks
            showFishAndSharks(currentChronon, fish, sharks);

            //prompt user for Enter, # of chronon, or 'end'
            //Enter advances to next chronon, a number entered means run that many chronon, 'end' will end the simulation
            System.out.print("Press Enter, # of chronon, or 'end': ");
            String response = input.nextLine().trim();
            int x = 1;
            if (response.length() > 0) {
            	try {
            		x = Integer.parseInt(response);
            	} catch (NumberFormatException e) {
            		
            	}
            	if (response.trim().equalsIgnoreCase("end")) {
            		break;
            	}
            }
            // run until
            for (int k = 0; k < x; k++) {
            	
            	//save our current info to the history array list to be used later if the user wants to save the population chart
                int[] currentInfo = new int[3];
                currentInfo[Config.HISTORY_CHRONON_INDEX] = currentChronon;
                currentInfo[Config.HISTORY_NUM_FISH_INDEX] = numFish;
                currentInfo[Config.HISTORY_NUM_SHARKS_INDEX] = numSharks;
                history.add(currentInfo);
   
            	//clear fishMoved and sharksMoved from previous chronon
            	clearMoves(fishMoved);
            	clearMoves(sharksMoved);
                 
            	//call fishSwimAndBreed
            	fishSwimAndBreed(fish, sharks, fishMoved, fishBreed, randGen);
                
            	//call sharksHuntAndBreed
            	sharksHuntAndBreed(fish, sharks, fishMoved, sharksMoved, sharksBreed, starve, sharksStarve, randGen);
            	
            	//get num fish and sharks
            	currentChronon++;
            	numFish = countCreatures(fish);
            	numSharks = countCreatures(sharks);
                
            	//if all the fish or sharks are gone then end simulation
            	simulationEnd = numFish <= 0 || numSharks <= 0;
            	if (simulationEnd) {
            		break;
            	}           
            }
        }
        
        //print the final ocean contents
        showFishAndSharks(currentChronon, fish, sharks);
        
        //add the final fish and shark data to our history list
        int[] currentInfo = new int[3];
        currentInfo[Config.HISTORY_CHRONON_INDEX] = currentChronon;
        currentInfo[Config.HISTORY_NUM_FISH_INDEX] = numFish;
        currentInfo[Config.HISTORY_NUM_SHARKS_INDEX] = numSharks;
        history.add(currentInfo);
        
        //Print out why the simulation ended.
        if ( numSharks <= 0 ) {
            System.out.println("Wa-Tor simulation ended since no sharks remain.");
        } else if ( numFish <= 0 ){
            System.out.println("Wa-Tor simulation ended since no fish remain.");
        } else {
            System.out.println("Wa-Tor simulation ended at user request.");
        }

        //If the user was prompted to enter simulation parameters
        //then prompt the user to see if they would like to save them.
        //If the user enters a y or Y as the only non-whitespace characters
        //then prompt for filename and save, otherwise don't save parameters.
        //call saveSimulationParameters to actually save the parameters to the file.
        //If saveSimulationParameters throws an IOException then catch it and
        //repeat the code to prompt asking the user if they want to save
        //the population chart.
        //TODO Milestone 3
        try {
        	//saveSimulationParameters(simulationParameters, "simParams.txt");
            savePopulationChart(simulationParameters, history, oceanWidth, oceanHeight, "savepopchart.txt");
        } catch (IOException e) {
        	
        } catch (NullPointerException e) {
        	
        }
        
        
        //Always prompt the user to see if they would like to save a 
        //population chart of the simulation.
        //If the user enters a y or Y as the only non-whitespace characters
        //then prompt for filename and save, otherwise don't save chart.
        //call savePopulationChart to save the parameters to the file.
        //If savePopulationChart throws an IOException then catch it and
        //repeat the code to prompt asking the user if they want to save
        //the population chart.
        //TODO Milestone 3
        

        input.close();
    }
    
    /**
     * This is called when a fish cannot move.  This increments the fish's age and notes in
     * the fishMove array that it has been updated this chronon.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param row The row of the fish that is staying.
     * @param col The col of the fish that is staying.
     */
    public static void aFishStays(int[][] fish, boolean[][] fishMove, int row, int col) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG fish %d,%d stays\n", row, col);
        }
        fish[row][col]++; // increment age of fish
        fishMove[row][col] = true;  
    }
    
    /**
     * The fish moves from fromRow,fromCol to toRow,toCol.  The age of the fish is incremented. The
     * fishMove array records that this fish has moved this chronon.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param fromRow  The row the fish is moving from.
     * @param fromCol The column the fish is moving from.
     * @param toRow  The row the fish is moving to.
     * @param toCol  The column the fish is moving to.
     */
    public static void aFishMoves(int[][] fish, boolean[][] fishMove, int fromRow, int fromCol,
        int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG fish moved from %d,%d to %d,%d\n", fromRow, fromCol, toRow, toCol);
        }
        // just move fish
        fish[toRow][toCol] = fish[fromRow][fromCol] + 1; // increment age
        fishMove[toRow][toCol] = true;

        // clear previous location
        fish[fromRow][fromCol] = Config.EMPTY;
        fishMove[fromRow][fromCol] = false;
    }
    
    /**
     * The fish moves from fromRow,fromCol to toRow,toCol. This fish breeds so its
     * age is reset to 0.  The new fish is put in the fromRow,fromCol with an age of 0.   The
     * fishMove array records that both fish moved this chronon.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param fromRow  The row the fish is moving from and where the new fish is located.
     * @param fromCol The column the fish is moving from and where the new fish is located.
     * @param toRow  The row the fish is moving to.
     * @param toCol  The column the fish is moving to.
     */
    public static void aFishMovesAndBreeds(int[][] fish, boolean[][] fishMove, int fromRow, int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG fish moved from %d,%d to %d,%d and breed\n", fromRow, fromCol, toRow,
                toCol);
        }
        // move fish, resetting age in new location
        fish[toRow][toCol] = 0;
        fishMove[toRow][toCol] = true;

        // breed
        fish[fromRow][fromCol] = 0; // new fish in previous location
        fishMove[fromRow][fromCol] = true;
    }

    /**
     * This removes the shark from the sharks, sharksMove and starve arrays.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param row The row the shark is in.
     * @param col The column the shark is in.
     */
    public static void sharkStarves(int[][] sharks, boolean[][] sharksMove, int[][] starve, int row, int col) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark %d,%d starves\n", row, col);
        }
        sharks[row][col] = Config.EMPTY;
        starve[row][col] = Config.EMPTY;
        sharksMove[row][col] = false;
    }
    
    /**
     * This is called when a shark cannot move.  This increments the shark's age and time since
     * the shark last ate and notes in the sharkMove array that it has been updated this chronon.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param row The row the shark is in.
     * @param col The column the shark is in.
     */
    public static void sharkStays(int[][] sharks, boolean[][] sharksMove, int[][] starve, int row, int col) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark %d,%d can't move\n", row, col);
        }
        sharks[row][col]++; // increment age of shark
        starve[row][col]++; // increment time since last ate
        sharksMove[row][col] = true;
    }
    
    /**
     * This moves a shark from fromRow,fromCol to toRow,toCol.  This increments the age and time
     * since the shark last ate and notes that this shark has moved this chronon.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fromRow  The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow  The row the shark is moving to.
     * @param toCol  The column the shark is moving to.
     */
    public static void sharkMoves(int[][] sharks, boolean[][] sharksMove, int[][] starve, int fromRow, int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d to %d,%d\n", fromRow, fromCol, toRow, toCol);
        }
        // just move shark
        sharks[toRow][toCol] = sharks[fromRow][fromCol] + 1; // move age
        sharksMove[toRow][toCol] = true;
        starve[toRow][toCol] = starve[fromRow][fromCol] + 1;

        sharks[fromRow][fromCol] = Config.EMPTY;
        sharksMove[fromRow][fromCol] = false;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * The shark moves from fromRow,fromCol to toRow,toCol. This shark breeds so its
     * age is reset to 0 but its time since last ate is incremented.  
     * The new shark is put in the fromRow,fromCol with an age of 0 and 0 time since last ate. The
     * fishMove array records that both fish moved this chronon. 
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fromRow  The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow  The row the shark is moving to.
     * @param toCol  The column the shark is moving to.
     */
    public static void sharkMovesAndBreeds(int[][] sharks, boolean[][] sharksMove, int[][] starve, int fromRow, int fromCol, int toRow, int toCol) {

        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d to %d,%d and breeds\n", fromRow, fromCol,
                toRow, toCol);
        }
        sharks[toRow][toCol] = 0; // reset age in new location
        sharks[fromRow][fromCol] = 0; // new fish in previous location

        sharksMove[toRow][toCol] = true;
        sharksMove[fromRow][fromCol] = true;

        starve[toRow][toCol] = starve[fromRow][fromCol] + 1;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * The shark in fromRow,fromCol moves to toRow,toCol and eats the fish. The sharks age is 
     * incremented, time since it last ate and that this shark moved this chronon are noted. 
     * The fish is now gone.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param fromRow  The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow  The row the shark is moving to.
     * @param toCol  The column the shark is moving to.  
     */
    public static void sharkEatsFish(int[][] sharks, boolean[][] sharksMove, int[][] starve, int[][] fish, boolean[][] fishMove, int fromRow, int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d and ate fish %d,%d\n", fromRow, fromCol,
                toRow, toCol);
        }
        // eat fish
        fish[toRow][toCol] = Config.EMPTY;
        fishMove[toRow][toCol] = false;

        // move shark
        sharks[toRow][toCol] = sharks[fromRow][fromCol] + 1; // move age
        sharksMove[toRow][toCol] = true;
        starve[toRow][toCol] = starve[fromRow][fromCol] = 0;

        // clear old location
        sharks[fromRow][fromCol] = Config.EMPTY;
        sharksMove[fromRow][fromCol] = true;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * The shark in fromRow,fromCol moves to toRow,toCol and eats the fish. The fish is now gone.
     * This shark breeds so its age is reset to 0 and its time since last ate is incremented.
     * The new shark is put in the fromRow,fromCol with an age of 0 and 0 time since last ate. 
     * That these sharks moved this chronon is noted.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param fromRow  The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow  The row the shark is moving to.
     * @param toCol  The column the shark is moving to.  
     */
    public static void sharkEatsFishAndBreeds(int[][] sharks, boolean[][] sharksMove, int[][] starve, int[][] fish, boolean[][] fishMove, int fromRow, int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d and ate fish %d,%d and breed\n", fromRow,
                fromCol, toRow, toCol);
        }
        // shark eats fish and may breed
        // eat fish
        fish[toRow][toCol] = Config.EMPTY;
        fishMove[toRow][toCol] = false;

        // move to new location
        sharks[toRow][toCol] = 0; // reset age in new location
        sharksMove[toRow][toCol] = true;
        starve[toRow][toCol] = 0;

        // breed
        sharks[fromRow][fromCol] = 0; // new shark in previous location
        sharksMove[fromRow][fromCol] = true;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * This sets all elements within the array to Config.EMPTY.
     * This does not assume any array size but uses the .length attribute of the array.
     * If arr is null the method prints an error message and returns.
     * 
     * @param arr The array that only has EMPTY elements when method has executed.
     */
    public static void emptyArray(int[][] arr) {
        if ( arr == null) {
            System.out.println("emptyArray arr is null"); // if array is null just return
            return;
        }
        for (int row = 0; row < arr.length; row++) { // otherwise set each element of the array with Config.EMPTY
            for (int col = 0; col < arr[row].length; col++) {
                arr[row][col] = Config.EMPTY;
            }
        }
    }

    /**
     * This sets all elements within the array to false, indicating not moved this chronon.
     * This does not assume any array size but uses the .length attribute of the array.
     * If arr is null the method prints a message and returns.
     * 
     * @param arr The array will have only false elements when method completes.
     */
    public static void clearMoves(boolean[][] arr) {
        if ( arr == null) {
            System.out.println("clearMoves arr is null"); // almost same as empty array, return if null
            return;
        }        
        for (int row = 0; row < arr.length; row++) { // otherwise set each element of the array to false
            for (int col = 0; col < arr[row].length; col++) {
                arr[row][col] = false;
            }
        }
    }
    
    /**
     * Shows the locations of all the fish and sharks noting a fish with Config.FISH_MARK,
     * a shark with Config.SHARK_MARK and empty water with Config.WATER_MARK.
     * At the top is a title "Chronon: " with the current chronon and at the bottom is a count
     * of the number of fish and sharks.
     * Example of a 3 row, 5 column ocean. Note every mark is also followed by a space.
     * Chronon: 1
     *     O . . 
     * O   . . . 
     *   . .   O 
     * fish:7 sharks:3
     * 
     * @param chronon  The current chronon.
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     */
    public static void showFishAndSharks(int chronon, int[][] fish, int[][] sharks) {
        System.out.println("Chronon: " + chronon); // print chronon at top
        for (int i = 0; i < fish.length; i++) { // loop through every element of the array
        		for (int t = 0; t < fish[0].length; t++) { 
        			if (fish[i][t] != Config.EMPTY) { // if the fish array is not empty at the spot put a fish char on board
        				System.out.print(Config.FISH_MARK + " ");
        			} else if (sharks[i][t] != Config.EMPTY) { // if the fish array is empty but the shark is not put shark on board
        				System.out.print(Config.SHARK_MARK + " ");
        			} else { // otherwise put the water mark on the board if there isnt a fish or shark there
        			    System.out.print(Config.WATER_MARK + " ");
        			}
        		}
        		System.out.println(); 
        }
        System.out.println("fish:"+ countCreatures(fish) + " sharks:" + countCreatures(sharks)); // print actual count of both
    }

    /**
     * This places up to startingFish fish in the fish array. This randomly chooses a location and
     * age for each fish.
     * Algorithm:
     * For each fish this tries to place
     *     reset the attempts to place the particular fish to 0.
     *     Try to place a single fish up to Config.MAX_PLACE_ATTEMPTS times
     *          Randomly choose a row, then column using randGen.nextInt( ) with the appropriate fish 
     *          array dimension as the parameter. 
     *          Increment the number of attempts to place the fish.
     *          If the location is empty in the fish array then 
     *              place the fish in that location, randomly choosing its age from 0 up to 
     *              and including fishBreed.
     *          If the location is already occupied, generate another location and try again.
     *     If a single fish is not placed after Config.MAX_PLACE_ATTEMPTS times, then stop trying
     *     to place the rest of the fish.
     * Return the number of fish actually placed.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param startingFish The number of fish to attempt to place in the fish array.
     * @param fishBreed The age at which fish breed.
     * @param randGen The random number generator.
     * @return the number of fish actually placed.
     */
    public static int placeFish(int[][] fish, int startingFish, int fishBreed, Random randGen) {
        int numFishPlaced = 0; // init variables
        int placeAttempts = 0;
        for (int i = 0; i < startingFish; i++) { // create a for loop for each fish in starting fish
            placeAttempts = 0;
            while (placeAttempts < Config.MAX_PLACE_ATTEMPTS) { // loop through the max places attempts
                int row = randGen.nextInt(fish.length);
                int column = randGen.nextInt(fish[0].length); // generate a random row and column
                placeAttempts++;
                if (fish[row][column] == Config.EMPTY) { // if the fish board is empty at that spot
                    int age = randGen.nextInt(fishBreed+1); // create a random age and 
                    fish[row][column] = age; // set that fish spot equal to the age
                    numFishPlaced++;
                    break; // break through the while loop to move to the next fish
                }
            } 
            if (placeAttempts >= Config.MAX_PLACE_ATTEMPTS) { // if however it does take you MAX_PLACE_ATTEMPTS return the fish placed
                return numFishPlaced;
            }
        }
        return numFishPlaced; // return the int of num fish placed
    }

    /**
     * This places up to startingSharks sharks in the sharks array. This randomly chooses a 
     * location and age for each shark.
     * Algorithm:
     * For each shark this tries to place
     *     reset the attempts to place the particular shark to 0.
     *     Try to place a single shark up to Config.MAX_PLACE_ATTEMPTS times
     *          Randomly choose a row, then column using randGen.nextInt( ) with the appropriate shark 
     *          array dimension as the parameter. 
     *          Increment the number of attempts to place the shark.
     *          If the location is empty in both the fish array and sharks array then 
     *              place the shark in that location, randomly choosing its age from 0 up to 
     *              and including sharkBreed.
     *          If the location is already occupied, generate another location and try again.
     *     If a single shark is not placed after Config.MAX_PLACE_ATTEMPTS times, then stop trying
     *     to place the rest of the sharks.
     * Return the number of sharks actually placed.     *     
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     * @param startingSharks The number of sharks to attempt to place in the sharks array.
     * @param sharksBreed The age at which sharks breed.
     * @param randGen The random number generator.
     * @return the number of sharks actually placed.
     */
    public static int placeSharks(int[][] fish, int[][] sharks, int startingSharks, int sharksBreed, Random randGen) {
        int numSharksPlaced = 0;
        int placeAttempts = 0; // this is the same exact code as place fish except for the if statement
        for (int i = 0; i < startingSharks; i++) {
            placeAttempts = 0;
            while (placeAttempts < Config.MAX_PLACE_ATTEMPTS) { 
                int row = randGen.nextInt(sharks.length);
                int column = randGen.nextInt(sharks[0].length);
                placeAttempts++;
                if (sharks[row][column] == Config.EMPTY && fish[row][column] == Config.EMPTY) { // must check to make sure there 
                    int age = randGen.nextInt(sharksBreed+1);                                   // isn't a fish there too already
                    sharks[row][column] = age;
                    numSharksPlaced++;
                    break;
                }
            }   
            if (placeAttempts >= Config.MAX_PLACE_ATTEMPTS) {
                return numSharksPlaced;
            }
        } 
        return numSharksPlaced;
    }
    
    /**
     * This counts the number of fish or the number of sharks depending on the array passed in.
     * 
     * @param fishOrSharks Either an array containing the ages of all the fish or an array containing the ages of all the sharks.
     * @return The number of fish or number of sharks, depending on the array passed in.
     */
    public static int countCreatures(int[][] fishOrSharks) {
        int numCreatures = 0;
        for (int i = 0; i < fishOrSharks.length; i++) { // loop through every element of the array 
            for (int t = 0; t < fishOrSharks[0].length; t++) {
                if (fishOrSharks[i][t] != Config.EMPTY) { // if its not empty then increase number of creatures
                    numCreatures++;
                }
            }
        }    
        return numCreatures;
    }

    /**
     * This returns a list of the coordinates (row,col) of positions around the row, col
     * parameters that do not contain a fish or shark. The positions that are considered
     * are directly above, below, left and right of row, col and IN THAT ORDER. Where 0,0 is 
     * the upper left corner when fish and sharks arrays are printed out.  Remember that creatures
     * moving off one side of the array appear on the opposite side. For example, those moving left
     * off the array appear on the right side and those moving down off the array appear at the top.
     * 
     * @param fish A non-Config.EMPTY value indicates the age of the fish occupying the location.
     * @param sharks A non-Config.EMPTY value indicates the age of the shark occupying the location.
     * @param row The row of a creature trying to move.
     * @param col The column of a creature trying to move.
     * @return An ArrayList containing 0 to 4, 2-element arrays with row,col coordinates of 
     *      unoccupied locations. In each coordinate array the 0 index is the row, the 1 index 
     *      is the column.
     */
    public static ArrayList<int[]> unoccupiedPositions(int[][] fish, int[][] sharks, int row, int col) {
        ArrayList<int[]> unoccupied = new ArrayList<>();
        int checkRow = 0;
        int checkCol = 0;
        
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                checkRow = row - 1; // check coordinates for directly above 
                checkCol = col;
            } else if (i == 1) {
                checkRow = row + 1; // check coordinates for directly below 
                checkCol = col;
            } else if (i == 2) {
                checkRow = row; // check coordinates for directly left
                checkCol = col - 1;
            } else if (i == 3) {
                checkRow = row; // check coordinates for directly right
                checkCol = col + 1;
            } 
            if (checkRow > fish.length-1) { // if the checkRow is off the map on the bottom set it to 0(top row)
                checkRow = 0; 
            } else if (checkRow < 0) { // if the checkRow is off the map on the top then set it to max-1(bottom row)
                checkRow = fish.length-1;
            }
        
            if (checkCol > fish[0].length-1) { // if the checkCol is off the map on the right then set it to 0(left side)
                checkCol = 0;
            } else if (checkCol < 0) { // if the checkCol is off the map on the left then set it to max-1(right side)
                checkCol = fish[0].length-1;
            }
        
            if ((fish[checkRow][checkCol] == Config.EMPTY) && (sharks[checkRow][checkCol]) == Config.EMPTY) { // if the spot we are checking is not filled with a fish or shark
                int[] currentSpace = new int[2];
                currentSpace[0] = checkRow;                  // i.e. it is open
                currentSpace[1] = checkCol;  
                unoccupied.add(currentSpace); // then fill the array with the coords of the open space
            }
        }
        
        return unoccupied;
    }

    /**
     * This randomly selects, with the Random number generator passed as a parameter, 
     * one of elements (array of int) in the neighbors list.  If the size of neighbors is 0 (empty)
     * then null is returned. If neighbors contains 1 element then that element is returned. The 
     * randGen parameter is only used to select 1 element from a neighbors list containing 
     * more than 1 element. If neighbors or randGen is null then an error message is 
     * printed to System.err and null is returned.
     * 
     * @param neighbors A list of potential neighbors to choose from.
     * @param randGen The random number generator used throughout the simulation.
     * @return A int[] containing the coordinates of a creatures move or null as specified above.
     */
    public static int[] chooseMove(ArrayList<int[]> neighbors, Random randGen) {
    	if (neighbors == null) {
    		System.err.println("Null neighbors array.");
    		return null;
    	} else if (randGen == null) {
    		System.err.println("Null randGen object.");
    	} else if (neighbors.size() == 0) {
    		return null;
    	} else if (neighbors.size() == 1) {
    		return neighbors.get(0);
    	}
    	
    	int randElement = randGen.nextInt(neighbors.size());
    	
        return neighbors.get(randElement);
    }

    /**
     * This attempts to move each fish each chronon.  
     * 
     * This is a key method with a number of parameters. Check that the parameters are valid 
     * prior to writing the code to move a fish. The parameters are checked in the order they
     * appear in the parameter list.  
     * If any of the array parameters are null or not at least 1 element in size then 
     * a helpful error message is printed out and -1 is returned. An example message for 
     * an invalid fish array is "fishSwimAndBreed Invalid fish array: Null or not at least 1 in 
     * each dimension.". Testing will not check the content of the message but will check whether
     * the correct number is returned for the situation.  Passing this test means we know fish[0]
     * exists and so won't cause a runtime error and also that fish[0].length is the width. For this
     * project it is safe to assume rectangular arrays (arrays where all the rows are the same 
     * length).
     * If fishBreed is less than zero a helpful error message is printed out
     * and -2 is returned. 
     * If randGen is null then a helpful error message is printed out and -3 is returned.
     *  
     *
     * Algorithm:
     * for each fish that has not moved this chronon
     *     get the available unoccupied positions for the fish to move (call unoccupiedPositions)
     *     choose a move from those positions (call chooseMove)
     *     Based on the move chosen, either the
     *         fish stays (call aFishStays)
     *         fish moves (call aFishMoves)
     *         or fish moves and breeds (call aFishMovesAndBreeds)
     *         
     *         
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     * @param fishMove The array containing the indicator of whether each fish moved this 
     *        chronon.
     * @param fishBreed The age in chronon that a fish must be to breed.
     * @param randGen The instance of the Random number generator.
     * @return -1, -2, -3 for invalid parameters as specified above. After attempting to move all
     *          fish 0 is returned indicating success. 
     */
    public static int fishSwimAndBreed(int[][] fish, int[][] sharks, boolean[][] fishMove, int fishBreed, Random randGen) {
        if (fish == null || fish[0].length < 1 || fish.length < 1){ // check for null or less than 1 in each dimension
            System.err.println("fishSwimAndBreed Invalid fish array: Null or not at least 1 in each dimension.");
            return -1; // return -1 meaning array(s) are bad
        } else if (sharks == null || sharks[0].length < 1 || sharks.length < 1) { // repeat for sharks and fishMove
            System.err.println("fishSwimAndBreed Invalid sharks array: Null or not at least 1 in each dimension.");
            return -1;
        } else if (fishMove == null || fishMove[0].length < 1 || fishMove.length < 1) {
            System.err.println("fishSwimAndBreed Invalid fishMove array: Null or not at least 1 in each dimension.");
            return -1;
        } else if (fishBreed < 0) { // if fishBreed is negative
            System.err.println("FishBreed is less than 0"); 
            return -2; // return -2 meaning fishBreed is negative
        } else if (randGen == null) {
            System.err.println("randGen object is null"); // if randGen is null
            return -3; // return -3 meaning randGen is null
        }
        
        // Swim and Breed Fish
        for (int i = 0; i < fishMove.length; ++i) {
            for (int t = 0; t < fishMove[0].length; ++t) {              
                if (!fishMove[i][t] && fish[i][t] != Config.EMPTY) {    
                    int[] chosenMove = chooseMove(unoccupiedPositions(fish, sharks, i, t), randGen);
                    if (chosenMove == null) {
                        aFishStays(fish, fishMove, i, t);
                    } else if (fish[i][t] < fishBreed) {
                        aFishMoves(fish, fishMove, i, t, chosenMove[0], chosenMove[1]);
                    } else {
                        aFishMovesAndBreeds(fish, fishMove, i, t, chosenMove[0], chosenMove[1]);
                    }
                }
            }
        }
        return 0;
    }

    /**
     * This returns a list of the coordinates (row,col) of positions around the row, col
     * parameters that contain a fish. The positions that are considered are directly above, 
     * below, left and right of row, col and IN THAT ORDER. Where 0,0 is the upper left corner 
     * when fish array is printed out.  Remember that sharks moving off one side of the array appear
     * on the opposite side. For example, those moving left off the array appear on the right side 
     * and those moving down off the array appear at the top.
     * 
     * @param fish A non-Config.EMPTY value indicates the age of the fish occupying a location.
     * @param row The row of a hungry shark.
     * @param col The column of a hungry shark.
     * @return An ArrayList containing 0 to 4, 2-element arrays with row,col coordinates of 
     *      fish locations. In each coordinate array the 0 index is the row, the 1 index 
     *      is the column.
     */
    public static ArrayList<int[]> fishPositions(int[][] fish, int row, int col) {
        ArrayList<int[]> fishPositions = new ArrayList<>();
        int checkRow = 0;
        int checkCol = 0;
        
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                checkRow = row - 1; // check coordinates for directly above 
                checkCol = col;
            } else if (i == 1) {
                checkRow = row + 1; // check coordinates for directly below 
                checkCol = col;
            } else if (i == 2) {
                checkRow = row; // check coordinates for directly left
                checkCol = col - 1;
            } else if (i == 3) {
                checkRow = row; // check coordinates for directly right
                checkCol = col + 1;
            } 
            if (checkRow > fish.length-1) { // if the checkRow is off the map on the bottom set it to 0(top row)
                checkRow = 0; 
            } else if (checkRow < 0) { // if the checkRow is off the map on the top then set it to max-1(bottom row)
                checkRow = fish.length-1;
            }
        
            if (checkCol > fish[0].length-1) { // if the checkCol is off the map on the right then set it to 0(left side)
                checkCol = 0;
            } else if (checkCol < 0) { // if the checkCol is off the map on the left then set it to max-1(right side)
                checkCol = fish[0].length-1;
            }
        
            if (fish[checkRow][checkCol] != Config.EMPTY) { // if the spot we are checking is not filled with an empty char
                int[] currentFish = new int[2];
                currentFish[0] = checkRow;                  // i.e. it has a fish
                currentFish[1] = checkCol;  
                fishPositions.add(currentFish); // then fill the fish positions array with the coord of a fish
            }
        }
        
        return fishPositions;
    }

    /**
     * This attempts to move each shark each chronon.  
     *
     * This is a key method with a number of parameters. Check that the parameters are valid 
     * prior to writing the code to move a shark. The parameters are checked in the order they
     * appear in the parameter list.  
     * If any of the array parameters are null or not at least 1 element in size then 
     * a helpful error message is printed out and -1 is returned. An example message for 
     * an invalid fish array is "sharksHuntAndBreed Invalid fish array: Null or not at least 1 in 
     * each dimension.". Testing will not check the content of the message but will check whether
     * the correct number is returned for the situation.  Passing this test means we know fish[0]
     * exists and so won't cause a runtime error and also that fish[0].length is the width. For this
     * project it is safe to assume rectangular arrays (arrays where all the rows are the same 
     * length).
     * If sharksBreed or sharksStarve are less than zero a helpful error message is printed out
     * and -2 is returned. 
     * If randGen is null then a helpful error message is printed out and -3 is returned.
     * 
     * Algorithm to move a shark:
     * for each shark that has not moved this chronon
     *     check to see if the shark has starved, if so call sharkStarves
     *     otherwise get the available positions of neighboring fish (call fishPositions)
     *     if there are no neighboring fish to eat then determine available positions 
     *              (call unoccupiedPositions)
     *          choose a move (call chooseMove) and based on the move chosen
     *          call sharkStays, sharkMoves or sharkMovesAndBreeds appropriately, using
     *          the sharkBreed parameter to see if a shark breeds.
     *     else if there are neighboring fish then choose the move (call chooseMove),
     *          eat the fish (call sharkEatsFish or sharkEatsFishAndBreeds) appropriately.
     * return 0, meaning success.
     *         
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param sharksMove The array containing the indicator of whether each shark moved this chronon.
     * @param sharksBreed The age the sharks must be in order to breed.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param sharksStarve The time in chronon since the sharks last ate that results in them
     *          starving to death.
     * @param randGen The instance of the Random number generator.
     * @return -1, -2, -3 for invalid parameters as specified above. After attempting to move all
     * sharks 0 is returned indicating success.
     */
    public static int sharksHuntAndBreed(int[][] fish, int[][] sharks, boolean[][] fishMove, boolean[][] sharksMove, int sharksBreed, int[][] starve, int sharksStarve, Random randGen) {
        if (fish == null || fish[0].length < 1 || fish.length < 1){ // check for null or less than 1 in each dimension
            System.err.println("sharksHuntAndBreed Invalid fish array: Null or not at least 1 in each dimension.");
            return -1; // return -1 meaning array(s) are bad
        } else if (sharks == null || sharks[0].length < 1 || sharks.length < 1) { // repeat for all arrays in the parameters
            System.err.println("sharksHuntAndBreed Invalid sharks array: Null or not at least 1 in each dimension.");
            return -1;
        } else if (fishMove == null || fishMove[0].length < 1 || fishMove.length < 1) {
            System.err.println("sharksHuntAndBreed Invalid fishMove array: Null or not at least 1 in each dimension.");
            return -1;
        } else if (sharksMove == null || sharksMove[0].length < 1 || sharksMove.length < 1) {
            System.err.println("sharksHuntAndBreed Invalid sharksMove array: Null or not at least 1 in each dimension.");
            return -1;
        } else if (starve == null || starve[0].length < 1 || starve.length < 1) {
            System.err.println("sharksHuntAndBreed Invalid starve array: Null or not at least 1 in each dimension.");
            return -1;
        } else if (sharksBreed < 0) { // if the sharksBreed is negative print an error
            System.err.println("sharksHuntAndBreed sharksBreed value is less than 0");
            return -2;
        } else if (sharksStarve < 0) { // same with sharks starve
            System.err.println("SharksHuntAndBreed sharksStarve value is less than 0");
            return -2;
        } else if (randGen == null) {
            System.err.println("randGen object is null"); // if randGen is null
            return -3; // return -3 meaning randGen is null
        }
        
        // Sharks Hunt and Breed
        for (int i = 0; i < sharksMove.length; i++) {
            for (int j = 0; j < sharksMove[0].length; ++j) {
                if (!sharksMove[i][j] && sharks[i][j] != Config.EMPTY) {
                    if (starve[i][j] >= sharksStarve) {
                        sharkStarves(sharks, sharksMove, starve, i, j);
                    } else {
                        if (fishPositions(fish, i, j).size() == 0) {
                            int[] chosenMove = chooseMove(unoccupiedPositions(fish, sharks, i, j), randGen);
                            if (chosenMove == null) {
                                sharkStays(sharks, sharksMove, starve, i, j);
                            } else if (sharks[i][j] < sharksBreed) {
                                sharkMoves(sharks, sharksMove, starve, i, j, chosenMove[0], chosenMove[1]);
                            } else {
                                sharkMovesAndBreeds(sharks, sharksMove, starve, i, j, chosenMove[0], chosenMove[1]);
                            }
                        } else {
                            int[] chosenMove = chooseMove(fishPositions(fish, i, j),randGen);
                            if(chosenMove != null && sharks[i][j] < sharksBreed){
                                sharkEatsFish(sharks, sharksMove, starve, fish, fishMove, i, j, chosenMove[0], chosenMove[1]);
                            } else {
                                sharkEatsFishAndBreeds(sharks, sharksMove, starve, fish, fishMove, i, j, chosenMove[0], chosenMove[1]);
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }
    
    /**
     * This looks up the specified paramName in this Config.SIM_PARAMS array,
     * ignoring case.  If found then the array index is returned.
     * @param paramName The parameter name to look for, ignoring case.
     * @return The index of the parameter name if found, otherwise returns -1.
     */
    public static int indexForParam(String paramName) {
        for ( int i = 0; i < Config.SIM_PARAMS.length; i++) {
            if ( paramName.equalsIgnoreCase( Config.SIM_PARAMS[i])) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Writes the simulationParameters to the file named filename.
     * The format of the file is the name of the parameter and value
     * on one line separated by =. The order of the lines does not matter.
     * Algorithm:
     * Open the file named filename for writing. Any IOExceptions should be handled with a throws
     *     clause and not a try-catch block.
     * For each of the simulation parameters whose names are found in Config.SIM_PARAMS
     *     Write out the name of the parameter, =, the parameter value and then newline.
     * Close the file.
     * 
     * Example contents of file:
     * seed=233
     * ocean_width=20
     * ocean_height=10
     * starting_fish=100
     * starting_sharks=10
     * fish_breed=3
     * sharks_breed=10
     * sharks_starve=4
     *  
     * @param simulationParameters The values of the parameters to write out.
     * @param filename The name of the file to write the parameters to.
     */
    public static void saveSimulationParameters(int[] simulationParameters, String filename) throws IOException {	
    	File file = new File(filename);
    	FileWriter fw = new FileWriter(file);
    	PrintWriter pw = new PrintWriter(fw);
   		for (int i = 0; i < simulationParameters.length; i++) {
       		pw.println(Config.SIM_PARAMS[i] + "=" + simulationParameters[i]);
       	}    		
   		pw.close();
    }    
    
    /**
     * This loads the simulation parameters from the file named filename.
     * The names of the parameters are in the Config.SIM_PARAMS array and the array returned from
     * this method is a parallel array containing the parameter values. The name corresponds to 
     * the value with the same index.
     * Algorithm:
     *      Try to open filename for reading. If the FileNotFoundException is thrown print the
     *      message printing out the filename without < > and return null;
     *      
     * File not found: <filename>
     * 
     *      Read lines from the file as long as each line contains "=".  As soon as a line does not
     *      contain "=" then stop reading from the file. The order of the lines in the
     *      file is not significant.
     *      In a line the part before "=" is the name and the part after is the value.
     *      The separate method you wrote in P7 is helpful here.
     *      Find the index of the name within Config.SIM_PARAMS (call indexForParam).
     *      If the index is found then convert the value into an int and store in the corresponding 
     *          index in the array of int that will be returned from this method.
     *      If the index is not found then print out the message followed by the entire line
     *      without the < >.
     *      
     * Unrecognized: <line>
     * 
     * @param filename The name of the from which to read simulation parameters.
     * @return The array of parameters.
     */
    public static int[] loadSimulationParameters(String filename) {
        int[] params = null;
        Scanner sc = null;
        String nextLine = "";
        try {
        	File file = new File(filename);
        	sc = new Scanner(file);
        	params = new int[Config.SIM_PARAMS.length];
        	while (sc.hasNextLine()) {
        	    nextLine = sc.nextLine();
        		if (!nextLine.contains("=")) { break; }
        		int index = indexForParam(nextLine.substring(0, nextLine.indexOf("=")));
        		params[index] = Integer.parseInt(nextLine.substring(nextLine.indexOf("=")+1));
        	}
        } catch (FileNotFoundException e) {
        	System.err.println("File not found: " + filename);
        } catch (Exception e) {
        	System.err.println("Unrecognized: " + nextLine);
        } finally {
        	sc.close();
        }
        return params;
    }  
    
    /**
     * This writes the simulation parameters and the chart of the simulation to a file.
     * If simulationParameters is null or history is null then print an error message
     * and leave the method before any output.
     * If filename cannot be written to then this method should throw an IOException.     * 
     * 
     * Parameters are written first, 1 per line in the file. Use an = to separate the
     * name from the value. Then write a blank line and then the Population Chart.
     * Example file contents are:
     * seed=111
     * ocean_width=5
     * ocean_height=2
     * starting_fish=6
     * starting_sharks=2
     * fish_breed=3
     * sharks_breed=3
     * sharks_starve=3
     * 
     * Population Chart
     * Numbers of fish(.) and sharks(O) in units of 1.
     * F  6,S  2    1)OO....                                            
     * F  4,S  2    2)OO..                                              
     * F  2,S  4    3)..OO                                              
     * F  1,S  4    4).OOO                                              
     * F  0,S  4    5)OOOO       
     * 
     * Looking at one line in detail
     * F  6,S  2    1)OO.... 
     *                ^^^^^^ 6 fish (the larger of sharks or fish is in the background)
     *                ^^ 2 sharks  
     *          ^^^^^ chronon 1
     *      ^^^^ the number of sharks        
     * ^^^^ the number of fish
     *                                      
     * The unit size is determined by dividing the maximum possible number of a creature
     * (oceanWidth * oceanHeight) by Config.POPULATION_CHART_WIDTH.
     * Then iterate through the history printing out the number of fish and sharks.
     * PrintWriter has a printf method that is helpful for formatting.
     * printf("F%3d", 5) 
     * prints "F  5", a 5 right justified in a field of size 3.
     * 
     * @param simulationParameters The array of simulation parameter values.
     * @param history The ArrayList containing the number of fish and number of sharks at each chronon.
     * @param oceanWidth The width of the ocean.
     * @param oceanHeight The height of the ocean.
     * @param filename The name of the file to write the parameters and chart to.
     */
    public static void savePopulationChart(int[]simulationParameters, ArrayList<int[]> history, int oceanWidth, int oceanHeight, String filename) throws IOException {
        //save all the parameters
        File file = new File(filename);
        FileWriter fw = new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);
        for (int i = 0; i < simulationParameters.length; i++) {
            pw.println(Config.SIM_PARAMS[i] + "=" + simulationParameters[i]);
        }           
        
        pw.print("\nPopulation Chart\nNumbers of fish(.) and sharks(O) in units of " + (oceanWidth * oceanHeight) / Config.POPULATION_CHART_WIDTH + ".\n");
        
        for (int i = 0; i<history.size(); i++) {
        	pw.printf("F%3d", history.get(i)[Config.HISTORY_NUM_FISH_INDEX]);
        	pw.printf(",S%3d", history.get(i)[Config.HISTORY_NUM_SHARKS_INDEX]);
            pw.printf("%4d)", history.get(i)[Config.HISTORY_CHRONON_INDEX]);
            
            if (history.get(i)[Config.HISTORY_NUM_FISH_INDEX] > history.get(i)[Config.HISTORY_NUM_SHARKS_INDEX]) {
            	for(int t = 0; t < history.get(i)[Config.HISTORY_NUM_SHARKS_INDEX] / ((oceanWidth * oceanHeight) / Config.POPULATION_CHART_WIDTH); t++) {
            		pw.print(Config.SHARK_MARK);
            	}
            	for (int t = 0; t < history.get(i)[Config.HISTORY_NUM_FISH_INDEX] / ((oceanWidth * oceanHeight) / Config.POPULATION_CHART_WIDTH) - (history.get(i)[Config.HISTORY_NUM_SHARKS_INDEX] / ((oceanWidth * oceanHeight) / Config.POPULATION_CHART_WIDTH)); t++) {
            		pw.print(Config.FISH_MARK);
            	}
            } else {
            	for (int t = 0; t < history.get(i)[Config.HISTORY_NUM_FISH_INDEX] / ((oceanWidth * oceanHeight) / Config.POPULATION_CHART_WIDTH); t++) {
            		pw.print(Config.FISH_MARK);
            	}
            	for(int t = 0; t < history.get(i)[Config.HISTORY_NUM_SHARKS_INDEX] / ((oceanWidth * oceanHeight) / Config.POPULATION_CHART_WIDTH) - (history.get(i)[Config.HISTORY_NUM_FISH_INDEX] / ((oceanWidth * oceanHeight) / Config.POPULATION_CHART_WIDTH)); t++) {
            		pw.print(Config.SHARK_MARK);
            	}
            } 
            pw.println();  
        }
        pw.close();
    }
}