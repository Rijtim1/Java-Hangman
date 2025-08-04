# Java Hangman Game

A classic hangman game implemented in Java by Rijan Timsina & Ibrahim Skouti.

## How to Run

### Option 1: Using the batch file (Windows)
1. Double-click `run.bat` to compile and run the game

### Option 2: Manual compilation
1. Open command prompt in the project directory
2. Compile: `javac -d . src/Final.java`
3. Run: `java src.Final`

## How to Play

1. The game will display the title screen with author information
2. Choose whether to use an external file or built-in categories:
   - **External file**: Enter the full path to a text file with words (one per line)
   - **Built-in categories**: Choose from 8 different categories

## External Files

The game comes with two sample external files in the `data/` folder:
- `Car_Brands.txt` - Contains various car brand names
- `Video_Game_Titles.txt` - Contains popular video game titles

To use these files, when prompted for a file location, enter:
- `data/Car_Brands.txt`
- `data/Video_Game_Titles.txt`

Or you can create your own text file with words (one word per line).

## Game Rules

- Guess the hidden word letter by letter
- You have 8 attempts before the game ends
- Letters and words are case-insensitive
- You cannot guess the same letter twice
- Spaces in multi-word answers are preserved

## Built-in Categories

1. Animals
2. Historical figures
3. Movies
4. Sports
5. Fruit & vegetables
6. Transportation
7. Places
8. Random (selects a random category)

## Requirements

- Java 8 or higher
- Windows, macOS, or Linux

## Bug Fixes Applied

- Fixed random number generation bug that could cause crashes
- Improved word count calculation
- Enhanced file reading with better error handling
- Improved input validation
- Fixed external file path handling
