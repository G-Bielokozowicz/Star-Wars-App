# Star Wars App
 
Simple application for finding information about Star Wars films, characters, planets, species, starships and vehicles written in JavaFX.
Uses https://swapi.dev/api/ as the source for information

User has to have a local MySQL server running on their machine, for the app to work correctly and save things to favourites.

# Main menu

![obraz](https://user-images.githubusercontent.com/95439533/226929540-a0825e2c-ba90-4a7f-a925-31e96309c4ca.png)

From the main menu, user can select one of the few categories, or go to their favourites.

# Categories

![obraz](https://user-images.githubusercontent.com/95439533/226930857-977004f1-27b3-4538-8184-99bc54670fcb.png)

Every category has its own page, and the look similar.

User can search the name of the thing they are looking for, and the app will show all the info it could find about it.

For example, above, user has searched for "anakin". App has found character that matches search term, which is "Anakin Skywalker" and shows basic info about him, and a list of films he's in, his starthips and vehicles he piloted.

User can also add the result to their favourites.

If nothing is found, app will show an error message:

![obraz](https://user-images.githubusercontent.com/95439533/226931232-e49c496c-1bdc-4b8b-9a80-32083038bde7.png)


# Favourites

![obraz](https://user-images.githubusercontent.com/95439533/226930556-5f62f85e-7b07-46b4-acd9-9d6da3e265a1.png)

In favourites page, user can see list of everything they have added to favourites. Double clicking on something, will bring the user to page with detailed information about it, and double right clicking will delete position from favourites.
