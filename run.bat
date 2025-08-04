@echo off
echo Compiling Java Hangman Game...
javac -d . src/Final.java
if %errorlevel% equ 0 (
    echo Compilation successful! Running the game...
    echo.
    java src.Final
) else (
    echo Compilation failed! Please check that Java is installed and in your PATH.
    echo You can download Java from: https://www.oracle.com/java/technologies/downloads/
)
pause
