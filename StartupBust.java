package sinkship;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class StartupBust {
    private GameHelper helper = new GameHelper();
    private ArrayList<Startup> startups = new ArrayList<>();
    private int numOfGuesses = 0;

    private void setUpGame() {
        Startup one = new Startup();
        one.setName("poniez");
        Startup two = new Startup();
        two.setName("hacqi");
        Startup three = new Startup();
        three.setName("cabista");

        startups.add(one);
        startups.add(two);
        startups.add(three);

        System.out.println("Welcome to Startup Bust!");
        System.out.println("Your mission is to sink three startups hidden on a 7x7 grid.");
        System.out.println("The startups are: poniez, hacqi, and cabista.");
        System.out.println("Each startup occupies 3 consecutive cells, placed either horizontally or vertically.");
        System.out.println("Enter your guesses in the form of a coordinate like 'a3', 'b6', etc.");
        System.out.println("Try to sink all startups in the fewest number of guesses!");
        System.out.println("------------------------------------------------------------");

        printGridLegend();

        for (Startup startup : startups) {
            ArrayList<String> newLocation = helper.placeStartup(3);
            startup.setLocationCells(newLocation);
        }
    }

    private void printGridLegend() {
        System.out.println("\nGRID REFERENCE (Rows 0-6, Columns a-g):");
        System.out.println("   a b c d e f g");
        for (int row = 0; row < 7; row++) {
            System.out.print(row + "  ");
            for (int col = 0; col < 7; col++) {
                System.out.print("~ ");
            }
            System.out.println();
        }
        System.out.println("------------------------------------------------------------");
    }

    private void startPlaying() {
        while (!startups.isEmpty()) {
            System.out.println("\nStartups remaining: " + startups.size());
            String userGuess = helper.getUserInput("Enter a guess");
            checkUserGuess(userGuess);
        }
        finishGame();
    }

    private void checkUserGuess(String userGuess) {
        numOfGuesses++;
        String result = "miss";

        for (Startup startupToTest : startups) {
            result = startupToTest.checkYourself(userGuess);
            if (result.equals("hit")) {
                System.out.println("You hit a part of " + startupToTest.getName() + "!");
                break;
            }
            if (result.equals("kill")) {
                System.out.println("You sunk the startup: " + startupToTest.getName() + "!");
                startups.remove(startupToTest);
                break;
            }
        }

        if (result.equals("miss")) {
            System.out.println("You missed. Try again!");
        }
    }

    private void finishGame() {
        System.out.println("\nAll Startups are dead! Your stock is now worthless.");
        System.out.println("You took " + numOfGuesses + " guesses to sink all the startups.");

        if (numOfGuesses <= 18) {
            System.out.println("Great job! You got out before your options sank.");
        } else {
            System.out.println("Took you long enough... Fish are dancing with your options!");
        }
    }

    public static void main(String[] args) {
        StartupBust game = new StartupBust();
        game.setUpGame();
        game.startPlaying();
    }
}



class Startup {
    private ArrayList<String> locationCells;
    private String name;

    public void setLocationCells(ArrayList<String> loc) {
        locationCells = loc;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public String checkYourself(String userInput) {
        String result = "miss";
        int index = locationCells.indexOf(userInput);

        if (index >= 0) {
            locationCells.remove(index);

            if (locationCells.isEmpty()) {
                result = "kill";
            } else {
                result = "hit";
            }
        }

        return result;
    }
}



class GameHelper {
   private static final String ALPHABET = "abcdefg";
   private static final int GRID_LENGTH = 7;
   private static final int GRID_SIZE = 49;
   private static final int MAX_ATTEMPTS = 200;
   private static final int HORIZONTAL_INCREMENT = 1;
   private static final int VERTICAL_INCREMENT = GRID_LENGTH;

   private final int[] grid = new int[GRID_SIZE];
   private final Random random = new Random();
   private int startupCount = 0;

   public String getUserInput(String prompt) {
       System.out.print(prompt + ": ");
       Scanner scanner = new Scanner(System.in);
       return scanner.nextLine().toLowerCase();
   }

   public ArrayList<String> placeStartup(int startupSize) {
       int[] startupCoords = new int[startupSize];
       int attempts = 0;
       boolean success = false;
       startupCount++;

       int increment = getIncrement();

       while (!success && attempts++ < MAX_ATTEMPTS) {
           int location = random.nextInt(GRID_SIZE);
           for (int i = 0; i < startupCoords.length; i++) {
               startupCoords[i] = location;
               location += increment;
           }

           if (startupFits(startupCoords, increment) && coordsAvailable(startupCoords)) {
               success = true;
           }
       }

       savePositionToGrid(startupCoords);
       return convertCoordsToAlphaFormat(startupCoords);
   }

   private boolean startupFits(int[] startupCoords, int increment) {
       int finalLocation = startupCoords[startupCoords.length - 1];
       if (increment == HORIZONTAL_INCREMENT) {
           return calcRowFromIndex(startupCoords[0]) == calcRowFromIndex(finalLocation);
       } else {
           return finalLocation < GRID_SIZE;
       }
   }

   private boolean coordsAvailable(int[] startupCoords) {
       for (int coord : startupCoords) {
           if (grid[coord] != 0) {
               return false;
           }
       }
       return true;
   }

   private void savePositionToGrid(int[] startupCoords) {
       for (int index : startupCoords) {
           grid[index] = 1;
       }
   }

   private ArrayList<String> convertCoordsToAlphaFormat(int[] startupCoords) {
       ArrayList<String> alphaCells = new ArrayList<>();
       for (int index : startupCoords) {
           String alphaCoords = getAlphaCoordsFromIndex(index);
           alphaCells.add(alphaCoords);
       }
       return alphaCells;
   }

   private String getAlphaCoordsFromIndex(int index) {
       int row = calcRowFromIndex(index);
       int column = index % GRID_LENGTH;
       String letter = ALPHABET.substring(column, column + 1);
       return letter + row;
   }

   private int calcRowFromIndex(int index) {
       return index / GRID_LENGTH;
   }

   private int getIncrement() {
       return (startupCount % 2 == 0) ? HORIZONTAL_INCREMENT : VERTICAL_INCREMENT;
   }
}