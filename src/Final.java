package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Final {
    static Scanner in = new Scanner(System.in);
    
    // Game statistics and settings
    static int totalGamesPlayed = 0;
    static int totalGamesWon = 0;
    static int totalScore = 0;
    static int currentGameScore = 0;
    static String difficulty = "Normal";
    static boolean hintsEnabled = true;
    static boolean colorEnabled = true;
    
    // Difficulty settings
    static int maxHealth = 8;
    static int hintsAvailable = 3;
    
    // Color codes for console output
    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String BLUE = "\u001B[34m";
    static final String PURPLE = "\u001B[35m";
    static final String CYAN = "\u001B[36m";
    static final String WHITE = "\u001B[37m";
    static final String BOLD = "\u001B[1m";

    public static void main(String[] args) {
        infoDisplay();
        
        boolean playAgain = true;
        
        while (playAgain) {
            // Show main menu
            int menuChoice = showMainMenu();
            
            switch (menuChoice) {
                case 1:
                    playGame();
                    break;
                case 2:
                    showSettings();
                    break;
                case 3:
                    showStatistics();
                    break;
                case 4:
                    showInstructions();
                    break;
                case 5:
                    printColored("Thanks for playing! Goodbye! 👋", CYAN);
                    playAgain = false;
                    break;
                default:
                    printColored("Invalid choice. Please try again.", RED);
            }
            
            if (playAgain && menuChoice == 1) {
                System.out.println("\n" + YELLOW + "Would you like to play another round? (yes/no)" + RESET);
                String response = extraSpaceFilter(in.nextLine()).toLowerCase();
                playAgain = response.equals("yes") || response.equals("y");
            }
        }
        
        endDisplay();
    }
    
    public static int showMainMenu() {
        System.out.println("\n" + BOLD + CYAN + "═══════════════ MAIN MENU ═══════════════" + RESET);
        System.out.println(BLUE + "1. 🎮 Play Hangman" + RESET);
        System.out.println(PURPLE + "2. ⚙️  Settings" + RESET);
        System.out.println(GREEN + "3. 📊 Statistics" + RESET);
        System.out.println(YELLOW + "4. 📖 Instructions" + RESET);
        System.out.println(RED + "5. 🚪 Exit Game" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════" + RESET);
        System.out.print("Enter your choice (1-5): ");
        
        try {
            return Integer.parseInt(in.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public static void playGame() {
        currentGameScore = 0;
        setDifficultySettings();
        
        // PART ZERO : come up with the magic word, whether by exterior file or default categories.
        String magic_word = null;
        String wordCategory = "";
        int key = 0;

        do {
            printColored("Do you want to use an external file? (yes/no)", CYAN);
            String q = extraSpaceFilter(in.nextLine()).toLowerCase();

            // if the user entered "yes"
            if (q.equals("yes")) {
                do {
                    printColored("Enter the location of the file, or type \"back\" to return", YELLOW);
                    String location = extraSpaceFilter(in.nextLine());

                    if (location.toLowerCase().equals("back")) {
                        break;
                    }
                    
                    File file = new File(location);
                    if (file.exists() && file.isFile()) {
                        ArrayList<String> name = new ArrayList<>();
                        findFile(location, name);
                        if (!name.isEmpty()) {
                            magic_word = extraSpaceFilter(getRandomName(name)).toLowerCase();
                            wordCategory = "External File";
                            key = 1;
                        } else {
                            printColored("The file is empty or contains no valid words", RED);
                        }
                    } else {
                        printColored("The location is not found", RED);
                    }
                } while (key == 0);
            }

            // if the user enter "no"
            else if (q.equals("no")) {
                String[] result = inGameWordSelector();
                magic_word = extraSpaceFilter(result[0]).toLowerCase();
                wordCategory = result[1];
                key = 1;
            }

            // if the user entered gibberish.
            else {
                printColored("Invalid input, try again.", RED);
            }

        } while (key == 0);
        // PART ZERO DONE.

        openingSequence();
        displayGameInfo(wordCategory);
        String blank = hideStringAndDisplay(magic_word);
        System.out.println("\n" + BOLD + GREEN + "**************************** BEGIN **************************************" + RESET);

        int health = maxHealth;
        int hintsUsed = 0;
        ArrayList<String> inputs = new ArrayList<>();
        ArrayList<String> correctGuesses = new ArrayList<>();
        ArrayList<String> wrongGuesses = new ArrayList<>();

        do {
            // Show current game status
            showGameStatus(health, hintsUsed, correctGuesses, wrongGuesses, blank);
            
            String user_input = getUserInput(inputs, magic_word, wordCategory, hintsUsed);
            
            if (user_input.startsWith("HINT_")) {
                hintsUsed++;
                continue;
            }
            
            if (user_input.startsWith("SOLVE_")) {
                String guess = user_input.substring(6);
                if (guess.equals(magic_word)) {
                    printColored("🎉 AMAZING! You solved it: " + capitalize(magic_word), GREEN);
                    currentGameScore += (health * 50) + 200; // Bonus for solving
                    break;
                } else {
                    printColored("❌ Wrong guess! The word is not: " + capitalize(guess), RED);
                    health -= 2; // Penalty for wrong solve attempt
                    drawPic(health);
                    wrongGuesses.add(guess);
                }
                continue;
            }

            inputs.add(user_input);

            if (magic_word.indexOf(user_input) == -1) {
                printColored("❌ WRONG: Letter '" + user_input + "' is not in the word!", RED);
                wrongGuesses.add(user_input);
                health -= 1;
                drawPic(health);
            } else {
                printColored("✅ CORRECT: Letter '" + user_input + "' found!", GREEN);
                correctGuesses.add(user_input);
                
                // Update blank string with correct letters
                blank = updateBlankString(blank, magic_word, user_input);
                
                // Calculate score for correct guess
                int letterCount = countOccurrences(magic_word, user_input);
                currentGameScore += letterCount * 10;
                
                printColored("Current word: " + spaceOut(capitalize(blank)), CYAN);
            }

        } while (health > 0 && blank.indexOf("_") != -1);
        
        // Game finished - show results
        totalGamesPlayed++;
        if (health > 0) {
            currentGameScore += health * 20; // Bonus for remaining health
            totalGamesWon++;
            winDisplay();
            printColored("🏆 Final Score: " + currentGameScore + " points!", YELLOW);
        } else {
            loseDisplay(magic_word);
        }
        
        totalScore += currentGameScore;
    }

    // *************************************************************************************************************************************************************************
    // Utility method for colored output
    public static void printColored(String message, String color) {
        if (colorEnabled) {
            System.out.println(color + message + RESET);
        } else {
            System.out.println(message);
        }
    }
    
    public static void setDifficultySettings() {
        switch (difficulty) {
            case "Easy":
                maxHealth = 12;
                hintsAvailable = 5;
                break;
            case "Normal":
                maxHealth = 8;
                hintsAvailable = 3;
                break;
            case "Hard":
                maxHealth = 6;
                hintsAvailable = 2;
                break;
            case "Expert":
                maxHealth = 4;
                hintsAvailable = 1;
                break;
        }
    }
    
    public static void showSettings() {
        boolean inSettings = true;
        
        while (inSettings) {
            System.out.println("\n" + BOLD + YELLOW + "═══════════════ SETTINGS ═══════════════" + RESET);
            System.out.println(BLUE + "1. Difficulty: " + difficulty + RESET);
            System.out.println(GREEN + "2. Hints: " + (hintsEnabled ? "Enabled" : "Disabled") + RESET);
            System.out.println(PURPLE + "3. Colors: " + (colorEnabled ? "Enabled" : "Disabled") + RESET);
            System.out.println(RED + "4. Back to Main Menu" + RESET);
            System.out.println(YELLOW + "═══════════════════════════════════════" + RESET);
            System.out.print("Enter your choice (1-4): ");
            
            try {
                int choice = Integer.parseInt(in.nextLine().trim());
                switch (choice) {
                    case 1:
                        changeDifficulty();
                        break;
                    case 2:
                        hintsEnabled = !hintsEnabled;
                        printColored("Hints " + (hintsEnabled ? "enabled" : "disabled"), GREEN);
                        break;
                    case 3:
                        colorEnabled = !colorEnabled;
                        System.out.println("Colors " + (colorEnabled ? "enabled" : "disabled"));
                        break;
                    case 4:
                        inSettings = false;
                        break;
                    default:
                        printColored("Invalid choice. Please try again.", RED);
                }
            } catch (NumberFormatException e) {
                printColored("Invalid input. Please enter a number.", RED);
            }
        }
    }
    
    public static void changeDifficulty() {
        System.out.println("\n" + CYAN + "Select difficulty level:" + RESET);
        System.out.println("1. Easy (12 lives, 5 hints)");
        System.out.println("2. Normal (8 lives, 3 hints)");
        System.out.println("3. Hard (6 lives, 2 hints)");
        System.out.println("4. Expert (4 lives, 1 hint)");
        System.out.print("Enter choice (1-4): ");
        
        try {
            int choice = Integer.parseInt(in.nextLine().trim());
            switch (choice) {
                case 1: difficulty = "Easy"; break;
                case 2: difficulty = "Normal"; break;
                case 3: difficulty = "Hard"; break;
                case 4: difficulty = "Expert"; break;
                default: 
                    printColored("Invalid choice. Keeping current difficulty.", RED);
                    return;
            }
            printColored("Difficulty set to: " + difficulty, GREEN);
        } catch (NumberFormatException e) {
            printColored("Invalid input. Keeping current difficulty.", RED);
        }
    }
    
    public static void showStatistics() {
        System.out.println("\n" + BOLD + GREEN + "═══════════════ STATISTICS ═══════════════" + RESET);
        System.out.println(CYAN + "Games Played: " + totalGamesPlayed + RESET);
        System.out.println(GREEN + "Games Won: " + totalGamesWon + RESET);
        System.out.println(RED + "Games Lost: " + (totalGamesPlayed - totalGamesWon) + RESET);
        
        if (totalGamesPlayed > 0) {
            double winRate = (double) totalGamesWon / totalGamesPlayed * 100;
            System.out.println(YELLOW + "Win Rate: " + String.format("%.1f", winRate) + "%" + RESET);
        }
        
        System.out.println(PURPLE + "Total Score: " + totalScore + " points" + RESET);
        
        if (totalGamesPlayed > 0) {
            int avgScore = totalScore / totalGamesPlayed;
            System.out.println(BLUE + "Average Score: " + avgScore + " points" + RESET);
        }
        
        System.out.println(CYAN + "Current Difficulty: " + difficulty + RESET);
        System.out.println(GREEN + "═══════════════════════════════════════" + RESET);
        
        System.out.println("\nPress Enter to continue...");
        in.nextLine();
    }
    
    public static void showInstructions() {
        System.out.println("\n" + BOLD + BLUE + "═══════════════ HOW TO PLAY ═══════════════" + RESET);
        System.out.println(CYAN + "🎯 OBJECTIVE:" + RESET);
        System.out.println("   Guess the hidden word before running out of lives!");
        
        System.out.println("\n" + YELLOW + "🎮 CONTROLS:" + RESET);
        System.out.println("   • Type a letter and press Enter to guess");
        System.out.println("   • Type 'hint' for a helpful clue (limited uses)");
        System.out.println("   • Type 'solve [word]' to guess the entire word");
        
        System.out.println("\n" + GREEN + "📊 SCORING:" + RESET);
        System.out.println("   • +10 points per correct letter");
        System.out.println("   • +20 points per remaining life at the end");
        System.out.println("   • +200 bonus for solving the word directly");
        
        System.out.println("\n" + PURPLE + "⚙️ DIFFICULTY LEVELS:" + RESET);
        System.out.println("   • Easy: 12 lives, 5 hints");
        System.out.println("   • Normal: 8 lives, 3 hints");
        System.out.println("   • Hard: 6 lives, 2 hints");
        System.out.println("   • Expert: 4 lives, 1 hint");
        
        System.out.println("\n" + RED + "⚠️ TIPS:" + RESET);
        System.out.println("   • Wrong solve attempts cost 2 lives");
        System.out.println("   • Common letters like E, A, R, S, T are good starting guesses");
        System.out.println("   • Use hints wisely - they're limited!");
        
        System.out.println(BLUE + "═══════════════════════════════════════" + RESET);
        System.out.println("\nPress Enter to continue...");
        in.nextLine();
    }
    
    public static void showGameStatus(int health, int hintsUsed, ArrayList<String> correct, ArrayList<String> wrong, String blank) {
        System.out.println("\n" + BOLD + CYAN + "═══════════ GAME STATUS ═══════════" + RESET);
        System.out.println(health > 3 ? GREEN + "❤️ Lives: " + health : RED + "💀 Lives: " + health + RESET);
        System.out.println(YELLOW + "🏆 Score: " + currentGameScore + RESET);
        System.out.println(BLUE + "💡 Hints remaining: " + (hintsAvailable - hintsUsed) + RESET);
        
        if (!correct.isEmpty()) {
            System.out.println(GREEN + "✅ Correct: " + String.join(", ", correct) + RESET);
        }
        if (!wrong.isEmpty()) {
            System.out.println(RED + "❌ Wrong: " + String.join(", ", wrong) + RESET);
        }
        System.out.println(CYAN + "═══════════════════════════════════" + RESET);
    }
    
    public static void displayGameInfo(String category) {
        System.out.println("\n" + BOLD + PURPLE + "🎮 GAME INFO 🎮" + RESET);
        System.out.println(CYAN + "Category: " + category + RESET);
        System.out.println(YELLOW + "Difficulty: " + difficulty + RESET);
        System.out.println(GREEN + "Lives: " + maxHealth + RESET);
        System.out.println(BLUE + "Hints available: " + hintsAvailable + RESET);
    }
    
    public static String getUserInput(ArrayList<String> inputs, String magicWord, String category, int hintsUsed) {
        String userInput;
        
        do {
            System.out.print("\n" + CYAN + "Your guess (letter/hint/solve [word]): " + RESET);
            userInput = extraSpaceFilter(in.nextLine()).toLowerCase();
            
            if (userInput.isEmpty()) {
                printColored("Invalid input, try again.", RED);
                continue;
            }
            
            // Handle hint request
            if (userInput.equals("hint")) {
                if (!hintsEnabled) {
                    printColored("Hints are disabled in settings.", RED);
                    continue;
                }
                if (hintsUsed >= hintsAvailable) {
                    printColored("No hints remaining!", RED);
                    continue;
                }
                
                String hint = getHint(magicWord, category, inputs);
                printColored("💡 HINT: " + hint, YELLOW);
                return "HINT_USED";
            }
            
            // Handle solve attempt
            if (userInput.startsWith("solve ")) {
                String guess = userInput.substring(6).trim();
                if (guess.isEmpty()) {
                    printColored("Please provide a word to solve (e.g., 'solve elephant')", RED);
                    continue;
                }
                return "SOLVE_" + guess;
            }
            
            // Validate single letter input
            if (userInput.length() != 1 || !Character.isLetter(userInput.charAt(0))) {
                printColored("Please enter a single letter, 'hint', or 'solve [word]'", RED);
                continue;
            }
            
            if (inputs.contains(userInput)) {
                printColored("You already guessed '" + userInput + "'", RED);
            }
            
        } while (inputs.contains(userInput) || userInput.isEmpty());
        
        return userInput;
    }
    
    public static String getHint(String magicWord, String category, ArrayList<String> guessed) {
        String[] hints = {
            "Category: " + category,
            "Word length: " + magicWord.length() + " characters",
            "First letter: " + Character.toUpperCase(magicWord.charAt(0)),
            "Last letter: " + Character.toUpperCase(magicWord.charAt(magicWord.length() - 1)),
        };
        
        // Try to reveal a random unguessed letter
        ArrayList<Character> unguessedLetters = new ArrayList<>();
        for (char c : magicWord.toCharArray()) {
            if (Character.isLetter(c) && !guessed.contains(String.valueOf(c))) {
                unguessedLetters.add(c);
            }
        }
        
        if (!unguessedLetters.isEmpty()) {
            char randomLetter = unguessedLetters.get((int)(Math.random() * unguessedLetters.size()));
            return "The word contains the letter: " + Character.toUpperCase(randomLetter);
        }
        
        // Fallback to basic hints
        return hints[(int)(Math.random() * hints.length)];
    }
    
    public static String updateBlankString(String blank, String magicWord, String letter) {
        StringBuilder newBlank = new StringBuilder(blank);
        
        for (int i = 0; i < magicWord.length(); i++) {
            if (magicWord.charAt(i) == letter.charAt(0)) {
                newBlank.setCharAt(i, letter.charAt(0));
            }
        }
        
        return newBlank.toString();
    }
    
    public static int countOccurrences(String word, String letter) {
        int count = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter.charAt(0)) {
                count++;
            }
        }
        return count;
    }
    // *************************************************************************************************************************************************************************
    // this method takes whatever string and space it out by adding " " after each
    // letter.
    public static String spaceOut(String whatever) {
        StringBuilder spacedOut = new StringBuilder();
        for (int i = 0; i < whatever.length(); i++) {
            spacedOut.append(whatever.charAt(i));
            spacedOut.append(" ");
        }
        return spacedOut.toString();
    }

    // *************************************************************************************************************************************************************************
    // this methods takes a stirng and clears exterior space. It also clean extra
    // spaces within the word.
    // noting, this method isnt perfect, but it works the way it is. probelm has to
    // do with functionality with errors.
    // if the string is made all of spaces, then it will leave one space bar.
    // public static String extraSpaceFilter(String user_input) {
    // while (user_input.indexOf(" ") == 0 && user_input.length() > 1) {
    // user_input = user_input.substring(1);
    // }
    // while (user_input.lastIndexOf(" ") == user_input.length() - 1 &&
    // user_input.length() > 1) {
    // user_input = user_input.substring(0, user_input.length() - 1);
    // }

    // int hook = 0;
    // while (user_input.indexOf(" ", hook + 1) != -1) {
    // hook = user_input.indexOf(" ", hook + 1);
    // while (user_input.substring(hook + 1, hook + 2).equals(" ")) {
    // String part1 = user_input.substring(0, hook);
    // String part2 = user_input.substring(hook + 1);
    // user_input = part1 + part2;
    // }
    // }
    // return user_input;
    // }
    public static String extraSpaceFilter(String userInput) {
        userInput = userInput.trim(); // remove leading/trailing whitespace
        userInput = userInput.replaceAll("\\s+", " "); // replace multiple spaces with single space
        return userInput;
    }

    // *************************************************************************************************************************************************************************
    // this method draw the hanged men steps based on health, except it doesmt draw
    // health 0.
    // public static void drawPic(int health) {
    // if (health == 0) {
    // return;
    // } // will draw Game-Over screen seperatly.so when zero, then exit.
    // String face = "";
    // switch (health) {
    // case 7:
    // face = "(^0^ )";
    // break;
    // case 6:
    // face = "(^-^ )";
    // break;
    // case 5:
    // face = "(^_^;)";
    // break;
    // case 4:
    // face = "(._.;)";
    // break;
    // case 3:
    // face = "(o_O )";
    // break;
    // case 2:
    // face = "(-_- )";
    // break;
    // case 1:
    // face = "(~<_~)";
    // break;
    // }

    // System.out.println(" __________________________");
    // System.out.println("|The Nuke Will Explode At..|");
    // System.out.println("| ___ _ {(_(_^ ) |");
    // System.out.println("| [___] _|_|_ (_ { } |");
    // System.out.println("| /*\\ " + face + " ()^{ |");
    // System.out.println("| [===] /|_|\\ | {|");
    // System.out.println("|~~|(" + health + ")|~~~~~~|~|~~~~~~|(0)|");
    // System.out.println("|~ (___) ~~ ~ ~~~ ~ | |");
    // System.out.println("|______________________|___|");
    // }
    public static void drawPic(int health) {
        if (health <= 0) {
            return;
        }

        String[] faces = {
                "(^0^ )",
                "(^-^ )",
                "(^_^;)",
                "(._.;)",
                "(o_O )",
                "(-_- )",
                "(~<_~)"
        };
        String face = faces[Math.min(health - 1, faces.length - 1)];

        System.out.println(" __________________________");
        System.out.println("|The Nuke Will Explode At..|");
        System.out.println("|   ___        _  {(_(_^ ) |");
        System.out.println("|  [___]     _|_|_  (_ { } |");
        System.out.println("|   /*\\      " + face + "   ()^{ |");
        System.out.println("|  [===]     /|_|\\     |  {|");
        System.out.printf("|~~|(%d)|~~~~~~|~|~~~~~~|(0)|%n", health);
        System.out.println("|~ (___) ~~  ~  ~~~  ~ |   |");
        System.out.println("|______________________|___|");
    }

    // *************************************************************************************************************************************************************************
    public static void loseDisplay(String magicWord) {
        System.out.println(RED + " ___________________  __________________  _______________________");
        System.out.println("|                   ||       _--_       ||         _____         |");
        System.out.println("|                   ||     _(    )_     ||        |     |\\       |");
        System.out.println("|      00:00        ||    (________)    ||     ___|     ||__     |");
        System.out.println("|                   ||  _____))((_____  ||~~~~|    R.I.P    |\\~~~|");
        System.out.println("| Bomb Initiated... || (______________) ||    |___  lol  ___||   |");
        System.out.println("|                   ||_______))((_______||        |     |\\__\\|   |");
        System.out.println("|___________________||__________________||________|_____||_______|" + RESET);
        System.out.println();
        printColored("💥 GAME OVER 💥", BOLD + RED);
        printColored("The word was: " + capitalize(magicWord), YELLOW);
        printColored("Score this round: " + currentGameScore + " points", CYAN);
        printColored("Better luck next time! 💪", BLUE);
    }

    // *************************************************************************************************************************************************************************
    public static void winDisplay() {
        System.out.println(GREEN + " ____________________________________________");
        System.out.println("| ____ _____---===___--___-   _|             |");
        System.out.println("| _______-----____===_       |_|             |");
        System.out.println("|  ____----======______-- -_   |  (^0^)/     |");
        System.out.println("| __---___---__==    --_-__ ___   (|_|       |");
        System.out.println("| ____--====    __---  __--     ___(_)____   |");
        System.out.println("|   __--____-- __----__     ~~~ ]_] (8) | \\  |");
        System.out.println("| ________------==__    ~~~~~~~~]_]__ __|_/  |");
        System.out.println("|____________________________________________|" + RESET);
        System.out.println();
        printColored("🎉 CONGRATULATIONS! YOU WIN! 🎉", BOLD + GREEN);
        printColored("🏆 You're a Hangman Master! 🏆", YELLOW);
    }

    // ************************************************************************************************************************************************************************
    // this method takes a method and capitalize each first letter in every
    // word.This may not be a perfect proof for errors and such.
    // public static String capitalize(String user_input) {
    // // Capitlize very first Letter:
    // String first_part = (user_input.substring(0, 1)).toUpperCase();// first part
    // that is first letter capitalized.
    // String second_part = user_input.substring(1);// the rest of string without
    // the first letter.
    // user_input = first_part + second_part;
    // // Donw with that.

    // // Capialize first letter subsequent to space:
    // int hook = 0;
    // while (user_input.indexOf(" ", hook + 1) != -1) {
    // hook = user_input.indexOf(" ", hook + 1); // toss the hook to the next space
    // String part1 = user_input.substring(0, hook + 1);// from begining to the
    // space.
    // String part2 = (user_input.substring(hook + 1, hook + 2)).toUpperCase();//
    // the actually letter/index to be
    // // capitalized
    // String part3 = user_input.substring(hook + 2);// the reaming string after the
    // capitalized index
    // user_input = part1 + part2 + part3;
    // }
    // return user_input;
    // }
    public static String capitalize(String user_input) {
        String[] words = user_input.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.length() > 0) {
                String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1);
                capitalized.append(capitalizedWord);
                if (i < words.length - 1) {
                    capitalized.append(" ");
                }
            }
        }

        return capitalized.toString();
    }

    // ************************************************************************************************************************************************************************
    // Rijan's methods
    // public static String RandomName(ArrayList<String> name) {
    // String word;
    // word = (String) name.get(getRandomNumber(name));// getting the random word; I
    // had to typecast here because you
    // // cant convert an object into string.
    // return word;

    // }
    public static String getRandomName(ArrayList<String> names) {
        int index = getRandomNumber(names);
        return names.get(index);
    }

    // ************************************************************************************************************************************************************************
    // Rijan's method
    public static int getRandomNumber(ArrayList<String> name) {
        return (int) (Math.random() * name.size());
    }

    // ************************************************************************************************************************************************************************
    // this method takesa string and turn in into blank version bt switching all
    // letters to underlines, and also it desplay word&letter counts for the user as
    // a hint.
    // public static String hideStringAndDisplay(String magic_word) {
    // int word_counter = 1;
    // int letter_counter = 0;
    // String blank = "";
    // for (int i = 0; i < magic_word.length(); i++) {
    // if (magic_word.indexOf(" ", i) - i == 0) {
    // blank += " ";
    // word_counter++;
    // } else {
    // blank += "_";
    // letter_counter++;
    // }
    // }
    // System.out.println("*The Text consists of " + word_counter + " word(s) and "
    // + letter_counter + " letters: "
    // + spaceOut(blank) + ".");

    // return blank;
    // }
    public static String hideStringAndDisplay(String magicWord) {
        int wordCount = 0;
        int letterCount = 0;
        StringBuilder hiddenWord = new StringBuilder();
        boolean inWord = false;
        
        for (int i = 0; i < magicWord.length(); i++) {
            char c = magicWord.charAt(i);
            if (Character.isWhitespace(c)) {
                hiddenWord.append(' ');
                inWord = false;
            } else {
                hiddenWord.append('_');
                letterCount++;
                if (!inWord) {
                    wordCount++;
                    inWord = true;
                }
            }
        }
        String formattedWord = spaceOut(hiddenWord.toString());
        System.out.printf("*The text consists of %d word(s) and %d letters: %s.%n",
                wordCount, letterCount, formattedWord);
        return hiddenWord.toString();
    }

    // *************************************************************************************************************************************************************************
    public static void infoDisplay() {
        System.out.println(" {0==0}**)=========\\*/=========(**{0==0}");
        System.out.println(" _]__[_<>0<>0<>0==**Y**==0<>0<>0<>_]__[_");
        System.out.println("(6)==(9)                         (9)==(6)");
        System.out.println("  ]||[                             ]||[");
        System.out.println("  ||||        THE HANGING MAN      ||||");
        System.out.println("  ||||                             ||||");
        System.out.println("  ||||       By Rijan Timsina      ||||");
        System.out.println("  ||||               &             ||||");
        System.out.println("  ||||        Ibrahim Skouti       ||||");
        System.out.println("  ||||                             ||||");
        System.out.println("  ||||          06/19/2017         ||||");
        System.out.println(" _]||[_                           _]||[_");
        System.out.println("(9)==(6)*************************(6)==(9)");
        System.out.println("\n");
    }

    // **************************************************************************************************************************************************************************
    // display rules, instructions, how to play etc.
    public static void instructionsDisplay() {
        printColored("INSTRUCTIONS:", BOLD + CYAN);
        printColored("• Guess the blank text letter by letter", YELLOW);
        printColored("• No numbers or symbols allowed", YELLOW);
        printColored("• Lives depend on difficulty level", YELLOW);
        printColored("• Type 'hint' for clues (limited uses)", GREEN);
        printColored("• Type 'solve [word]' to guess the entire word", BLUE);
        printColored("• Wrong solve attempts cost 2 lives!", RED);
    }

    // *************************************************************************************************************************************************************************
    // display the opening for the 'story'
    public static void openingSequence() {
        System.out.println("");
        System.out.println("*************************** PROLOGUE ************************************\n");
        System.out.println("    Once Upon A time....            A Nuke Feel From The Sky.");
        System.out.println(" _________________________            ____________________");
        System.out.println("|    (  )  ^^ ^^          |  | | |   |    _        |     ||      ");
        System.out.println("|{ }         ^^        {{ |   | |    |  {V_}     __|_   _||        ");
        System.out.println("|{{ }           ___   {{ }|  | | |   |   /\\     [_________|");
        System.out.println("|~)(~~~~~~~~~~~/||_\\__~~)(|  {_V_}   |~~|==|~~~~~(_ ((__ _|      ");
        System.out.println("|~~~  _,_,_\\__'=O===O='  ~|   /*\\    |~ |8)| ~~ ~(0_)(0_) |");
        System.out.println("|~   '-O--=O--'   ~~~~    |  |=+=|   |~~   ~~ ~   \\ <.  / |");
        System.out.println("|_________________________|  |(8)|   |~ wHaT Da..? \\(-`)  |");
        System.out.println("                             \\===/   |______________\\_____|");
        System.out.println("                              \\_/");
        System.out.println("\n************************** WIN or DIE! **********************************");
        System.out.println("");
    }

    // *************************************************************************************************************************************************************************
    // those two methods makes it easier to sop statements
    public static void sop(String x) {
        System.out.print(x);
    }

    public static void sopln(String x) {
        System.out.println(x);
    }

    // *************************************************************************************************************************************************************************
    public static String[] inGameWordSelector() {
        printColored("Enter the number of your preferred category:", CYAN);
        System.out.println(
                "1. 🐘 Animals \n2. 👑 Historical figures \n3. 🎬 Movies \n4. ⚽ Sports \n5. 🍎 Fruit & vegetables \n6. 🚗 Transportation \n7. 🏛️ Places \n8. 🎲 Random");
        int category = getCategoryNumber();
        
        // Expanded word arrays with more words
        String animal[] = { 
            "elephant", "lion", "tiger", "rabbit", "snake", "giraffe", "wolf", "owl", "bear", "deer",
            "eagle", "dolphin", "vulture", "whale", "mouse", "kangaroo", "penguin", "octopus", "butterfly", "rhinoceros",
            "cheetah", "zebra", "hippo", "crocodile", "gorilla", "chimpanzee", "orangutan", "koala", "panda", "sloth"
        };
        
        String historical[] = { 
            "abraham lincoln", "john f kennedy", "adolf hitler", "benito mussolini",
            "joseph stalin", "mao zedong", "winston churchill", "genghis khan", "albert einstein",
            "alexander the great", "charles darwin", "king louis", "muhammad ali", "hammurabi",
            "cleopatra", "napoleon bonaparte", "leonardo da vinci", "galileo galilei", "isaac newton", "shakespeare"
        };
        
        String movies[] = { 
            "home alone", "finding nemo", "titanic", "toy story", "the incredibles", "shrek",
            "mission impossible", "kung fu panda", "godzilla", "avatar", "star wars", "lion king", "interstellar",
            "frozen", "the matrix", "jurassic park", "the avengers", "harry potter", "lord of the rings", "pirates of the caribbean"
        };
        
        String sport[] = { 
            "soccer", "football", "volleyball", "lacrosse", "cricket", "hockey", "baseball", "badminton",
            "nascar", "formula one", "rally", "fencing", "archery", "rugby", "basketball", "tennis", 
            "golf", "swimming", "cycling", "skiing", "snowboarding", "surfing", "skateboarding", "boxing"
        };
        
        String fruit_veg[] = { 
            "banana", "watermelon", "mango", "guava", "apple", "pineapple", "kiwifruit", "carrot",
            "eggplant", "strawberry", "cherry", "orange", "grape", "tomato", "potato", "onion",
            "broccoli", "spinach", "lettuce", "cucumber", "pepper", "avocado", "blueberry", "raspberry"
        };
        
        String transportation[] = { 
            "car", "plane", "bus", "boat", "tram", "train", "air balloon", "submarine",
            "bicycle", "motorcycle", "helicopter", "rocket", "spaceship", "yacht", "scooter", "truck",
            "ambulance", "fire truck", "police car", "taxi", "limousine", "jet", "cruise ship", "ferry"
        };
        
        String places[] = { 
            "great pyramid of giza", "statue of liberty", "eiffel tower", "taj mahal", "niagara falls",
            "great wall of china", "tower of pisa", "mount everest", "big ben", "burj khalifa", "hagia sophia",
            "sistine chapel", "vatican city", "mount rushmore", "machu picchu", "stonehenge", "colosseum",
            "brooklyn bridge", "golden gate bridge", "sydney opera house", "christ the redeemer", "petra"
        };

        String[] categories = {"Animals", "Historical Figures", "Movies", "Sports", "Fruits & Vegetables", "Transportation", "Places"};
        
        int random;
        String magic_word = "";
        String categoryName = "";
        
        switch (category) {
            case 1:
                random = (int) (Math.random() * animal.length);
                magic_word = animal[random];
                categoryName = categories[0];
                break;
            case 2:
                random = (int) (Math.random() * historical.length);
                magic_word = historical[random];
                categoryName = categories[1];
                break;
            case 3:
                random = (int) (Math.random() * movies.length);
                magic_word = movies[random];
                categoryName = categories[2];
                break;
            case 4:
                random = (int) (Math.random() * sport.length);
                magic_word = sport[random];
                categoryName = categories[3];
                break;
            case 5:
                random = (int) (Math.random() * fruit_veg.length);
                magic_word = fruit_veg[random];
                categoryName = categories[4];
                break;
            case 6:
                random = (int) (Math.random() * transportation.length);
                magic_word = transportation[random];
                categoryName = categories[5];
                break;
            case 7:
                random = (int) (Math.random() * places.length);
                magic_word = places[random];
                categoryName = categories[6];
                break;
        }
        return new String[]{magic_word, categoryName};
    }

    // *************************************************************************************************************************************************************************
    // this method is made speicifically made to get user input for catigory number.
    // it takes it as a string becuase we cant error check intgers. then it
    // converted to an int.
    // public static int getCatigoryNumber() {
    // String something;
    // int number = -1;
    // while (number == -1) {
    // something = (in.nextLine()).substring(0, 1);
    // switch (something) {
    // case "1":
    // number = 1;
    // break;
    // case "2":
    // number = 2;
    // break;
    // case "3":
    // number = 3;
    // break;
    // case "4":
    // number = 4;
    // break;
    // case "5":
    // number = 5;
    // break;
    // case "6":
    // number = 6;
    // break;
    // case "7":
    // number = 7;
    // break;
    // case "8":
    // number = (int) (Math.random() * 7) + 1;
    // break;// if the user chose random, then we will choose a random for him.
    // default:
    // System.out.println("This input is invalid, try again:");
    // }
    // }
    // return number;
    // }
    public static int getCategoryNumber() {
        String input;
        int category = -1;
        while (category == -1) {
            input = in.nextLine();
            if (input.length() == 1 && Character.isDigit(input.charAt(0))) {
                int num = Integer.parseInt(input);
                if (num >= 1 && num <= 8) {
                    category = num;
                }
            }
            if (category == -1) {
                printColored("Invalid input. Please enter a number between 1 and 8:", RED);
            }
        }
        if (category == 8) {
            category = (int) (Math.random() * 7) + 1;
            printColored("🎲 Random category selected!", PURPLE);
        }
        return category;
    }

    // ***********************************************************************************************************************************************************************

    public static ArrayList<String> findFile(String FILENAME, ArrayList<String> name) {
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(FILENAME);
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                String cleanLine = extraSpaceFilter(sCurrentLine);
                if (!cleanLine.isEmpty()) {
                    name.add(cleanLine);
                }
            }
        }

        catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                System.out.println("Error closing file: " + ex.getMessage());
            }
        }
        return name;
    }

    // *************************************************************************************************************************************************************************
    public static void endDisplay() {
        System.out.println("\n" + BOLD + CYAN + "*************************************************************************" + RESET);
        printColored("🎮 THANKS FOR PLAYING THE ENHANCED HANGMAN GAME! 🎮", BOLD + GREEN);
        printColored("📊 Final Statistics:", YELLOW);
        printColored("   Games Played: " + totalGamesPlayed, CYAN);
        printColored("   Games Won: " + totalGamesWon, GREEN);
        printColored("   Total Score: " + totalScore + " points", PURPLE);
        
        if (totalGamesPlayed > 0) {
            double winRate = (double) totalGamesWon / totalGamesPlayed * 100;
            printColored("   Win Rate: " + String.format("%.1f", winRate) + "%", BLUE);
        }
        
        printColored("Hope you enjoyed the new features! 🌟", YELLOW);
        System.out.println(CYAN + "*************************************************************************" + RESET);
    }

    // ************************************************************************************************************************************************************************
    // this method takes a string and seperate all letters as individual strings in
    // an array ignoring spaces & repeated letters.
    public static ArrayList<String> wordSeperator(String word) {
        ArrayList<String> backage = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            String current_holder = word.substring(0 + i, 1 + i);
            if (!(current_holder.equals(" ") || backage.contains(current_holder))) {
                backage.add(current_holder);
            }
        }
        return backage;
    }
    // ************************************************************************************************************************************************************************
}

// NOTES :

// genertae way more built in documents and rename the files. ex:
// Video_Game_Titles, Historic_Figures, Movies.