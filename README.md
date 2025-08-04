# 🎮 Enhanced Java Hangman Game

A feature-rich, console-based hangman game written in Java with Docker containerization support. Now with multiple difficulty levels, hint systems, scoring, statistics, and much more!

## 🚀 **NEW FEATURES**

### 🎯 **Complete Game Overhaul**

- **Main Menu System**: Navigate through game options, settings, statistics, and help
- **Multiple Difficulty Levels**: Easy (12 lives), Normal (8 lives), Hard (6 lives), Expert (4 lives)
- **Advanced Hint System**: Smart hints with limited usage based on difficulty
- **Comprehensive Scoring**: Point-based system with bonuses and penalties
- **Player Statistics**: Track your progress with detailed analytics
- **Enhanced Visual Experience**: Color-coded output with emojis and better UI

### 🎲 **Enhanced Gameplay**

- **Word Solving**: Guess entire words with `solve [word]` command
- **Smart Hints**: Type `hint` for strategic clues (category, length, letters)
- **200+ Words**: Expanded database across 8 categories
- **Real-time Status**: Live game state with health, score, and progress tracking
- **Multiple Rounds**: Play consecutive games without restart

## Game Features

- **🎮 Interactive Console Interface**: Text-based user interface with ASCII art and colors
- **📚 Multiple Word Categories**: Built-in categories including Car Brands, Video Game Titles, Animals, Movies, Sports, and more
- **📁 External File Support**: Load custom word lists from external text files
- **🎨 Visual Hangman Display**: ASCII art showing the hangman's progression with different facial expressions
- **💪 Flexible Health System**: 4-12 step health progression based on difficulty level
- **💡 Smart Hint System**: Strategic hints including word statistics, category info, and letter reveals
- **🏆 Advanced Scoring**: Point-based scoring with bonuses for performance
- **📊 Player Statistics**: Comprehensive tracking of games, wins, scores, and performance
- **⚙️ Customizable Settings**: Adjust difficulty, hints, and visual options

## Project Structure

```
Java-Hangman/
├── src/
│   └── Final.java          # Enhanced main game logic with all new features
├── data/
│   ├── Car_Brands.txt      # Car brand word list
│   └── Video_Game_Titles.txt # Video game titles word list
├── run.bat                 # Windows batch script to compile and run
├── Dockerfile              # Docker container configuration
├── compose.yaml            # Docker Compose configuration
├── .dockerignore           # Docker ignore file
├── README.Docker.md        # Docker-specific documentation
├── FEATURES.md             # Detailed new features documentation
└── README.md               # This file
```

## Running the Game

### Option 1: Native Java (Windows)

1. Ensure Java JDK is installed and in your PATH
2. Run the batch script:

   ```cmd
   run.bat
   ```

### Option 2: Docker (Recommended)

#### Quick Start with Docker Compose

```bash
docker compose up --build
```

#### Manual Docker Commands

```bash
# Build the image
docker build -t java-hangman .

# Run interactively
docker run -it java-hangman
```

### Option 3: Manual Compilation

```bash
# Compile
javac -d . src/Final.java

# Run
java src.Final
```

## How to Play

1. **Choose Word Source**:
   - Select "no" to use built-in categories
   - Select "yes" to load from an external file

2. **Select Category** (if using built-in):
   - 1: Car Brands
   - 2: Video Game Titles

3. **Guess Letters**:
   - Enter one letter at a time
   - The game will show your progress with underscores
   - Wrong guesses reduce your health

4. **Win/Lose Conditions**:
   - Win: Guess all letters before health reaches 0
   - Lose: Health reaches 0 before completing the word

## Game Mechanics

- **Health System**: Start with 7 health points
- **Visual Feedback**: Hangman drawing changes based on remaining health
- **Case Insensitive**: All input is converted to lowercase
- **Space Handling**: Extra spaces are automatically filtered
- **Word Formatting**: Words are properly capitalized and spaced

## Docker Features

- **Lightweight**: Uses OpenJDK 11 slim image
- **Security**: Runs with non-privileged user
- **Interactive**: Supports TTY for console input
- **Volume Support**: Optional volume mounting for custom word files
- **Multi-platform**: Compatible with Linux, macOS, and Windows

## Development

### Authors

- Rijan Timsina
- Ibrahim Skouti

### Date

06/19/2017

### Requirements

- Java 11 or higher
- Docker (for containerized deployment)

## File Formats

### Word List Files

Word list files should contain one word or phrase per line:

```
Toyota
Honda
Mercedes-Benz
Volkswagen
```

## Troubleshooting

### Docker Issues

- Ensure Docker is running
- Use `docker compose up --build` to rebuild if needed
- For interactive mode, always use `-it` flags

### Java Issues

- Verify Java is installed: `java -version`
- Ensure JAVA_HOME is set correctly
- Check that javac is in your PATH

### Game Issues

- External files must exist and be readable
- Word files should not be empty
- Use absolute paths for external files when running natively

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
