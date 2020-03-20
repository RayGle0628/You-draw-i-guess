# You-draw-i-

Product introduction:

After a number theory discussion, our group always felt that it was a small game of "You Draw Me Guess". In fact, the game has not yet determined the specific name, but it should have multiple clients, a server, and then carry out multi-threaded concurrent communication. Allow different members to join a game room and play "you draw me guess" games in that room. By connecting to the database, the user can create an account and log in. When the account is successfully verified, the user can freely enter the main page and start the game.
The game also saves, updates, and resets the game's data through the database. And you can view the scores the user has obtained and the score ranking. You can also change passwords, reset passwords, etc. through the database background.


Product use process:

1.Players will start the game and log on. If a player does not have an account, they will be able to create one.
2.Once logged on, players will be shows a list of available game rooms.
3.Players will be able to join game rooms with many other players.
4.When enough players are in a room, the game will start.
5.One player will be given a random word to draw. As they draw, the progress will be 6.displayed to the other players.
7.Other players will type into the chat their guesses of what the drawing is.
8.If a player guesses correctly, they gain a number of points and the round ends. A new drawer is selected and the process is repeated for a number of rounds.
9.After a number of rounds have finished, the points are used to decide a winner.
10.Afterwards a new game can be started.
11.Players can join and leave at any point.
12.If there are not enough players due to people leaving, the game will end declaring the last player in the room the winner.


Technology used:

Product backend: Java
Product Beautification: CSS
Gui: JavaFX
Database: postgresql


Long term planning：

If possible, add some dynamic effects to the user interface and make the product more beautiful;
Make the functions more complete, such as adding a hotel system, to be determined.
Add rich background sound effects to this product.
There are not many tables in the potential database at present, can plan more data tables in future.
