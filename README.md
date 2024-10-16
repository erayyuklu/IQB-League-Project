﻿# IQB-League-Project

 The requirements for this Java project were as follows:
•	PostgreSQL would be used as the database.
•	It was assumed that the database would contain an even number of teams.
•	A fixture would be generated for the first and second halves of the season, using the teams from the database.
•	In each half of the season, teams would alternate between playing home and away each week (my supervisor noted that this may not always be fully achievable, but he asked me to design an algorithm as close to this as possible).
•	The second half of the season would follow the same match order as the first half, but the home and away teams would be reversed.
•	During matches, the home team would have a slight advantage compared to the away team.
•	Teams’ past statistics would also influence the winning probabilities of matches.
•	After the matches, both the matches and the detailed standings of the teams would be saved to the database.
•	The recorded data would be queryable through various requests using the REST-API.
•	Lastly, the authenticator application written in C would generate a key valid for 30 minutes, which would be required for every API request. If authentication failed, the response to the request would return an authentication error.

