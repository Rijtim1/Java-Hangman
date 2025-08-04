# Java Hangman Game - Docker Setup

## Building and Running the Application

This is a console-based Java Hangman game that requires interactive input from the user.

### Quick Start with Docker Compose

To build and run the application:

```bash
docker compose up --build
```

This will build the Docker image and start the game in interactive mode, allowing you to play the hangman game directly in your terminal.

### Alternative: Using Docker directly

1. Build the image:

   ```bash
   docker build -t java-hangman .
   ```

2. Run the container interactively:

   ```bash
   docker run -it java-hangman
   ```

### Game Features

- Interactive console-based hangman game
- Built-in word categories (Car Brands, Video Game Titles)
- Support for external word files
- Visual hangman drawings
- Multiple difficulty levels

### File Structure

The containerized application includes:

- Compiled Java classes
- Data files with word lists (`data/Car_Brands.txt`, `data/Video_Game_Titles.txt`)
- All necessary runtime dependencies

### Notes

- The application requires interactive input, so make sure to use the `-it` flags when running with Docker
- Word data files are included in the container but can also be mounted as volumes for easy updates
- The game runs with a non-privileged user for security