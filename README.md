## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## Game Rules:

Goal: Have the fewest number of points in the end
- number cards have point values equivalent to their number
- Ace = 1, Jack = 11, Queen = 12, Black King = 13, Red King = 0

During a turn you can:
1. Permanently discard one of your cards if it is the same value of the card at the top of the discard pile
- ex. Ace in hand, Ace on top of discard, can permanently discard the Ace
- beware that if you accidentally try to discard the wrong card, you will get a penalty card
2. Draw a card and choose to swap or discard it
3. Swap the card at the top of the discard pile with one of your own
4. Click the end game button, calculating the points and ending the game

Special Cards:
- Jack - you can click on one of your own cards and look at it
- Queen - you can swap one of your cards with the other player's 
- (first click on the other player's card and then your own)
