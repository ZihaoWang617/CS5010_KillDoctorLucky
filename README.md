# Kill Doctor Lucky - Milestone 1

## Author
Zihao Wang - Northeastern University Vancouver

## Project Description
This project implements the model for a Kill Doctor Lucky board game. The world consists of interconnected rooms arranged in a 2D grid, items with damage values, players, and Doctor Lucky who moves through the mansion automatically.

## How to Run

### Using Command Line:
```bash
# Compile
javac -d bin src/killdoctorlucky/*.java

# Run with my custom world
java -cp bin killdoctorlucky.GameDriver res/mansion.txt
