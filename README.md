- Taggat is a simple cross-platform file organizer application that allows you to associate files with tags and then filter by tags
- For backups, the **taggat.db** file contains all the tagging data but make sure your filepaths are the same when restoring it

Running the application:
- Requires latest JVM/JDK 
- Create your own build: `mvn clean install` (Requires Apache Maven, will create jar in `target` directory) or download latest version from releases
- Run by double clicking `Taggat-1.0.0.jar` or run `java -jar Taggat-1.0.0.jar` from terminal

![Taggat Screenshot](https://github.com/ashtonhogan/Taggat/blob/main/screenshot.png?raw=true)

TODO:

General
- Window should be full screen on start
- Increase thumbnail sizes
- Use icons for buttons instead of text to avoid multilingual support requirement

Locations
- Load locations in DB on start
- Improve styling of locations:
    - More height
    - Single uniform component which contains a checkbox, input field and floppy icon to save the location
- Persist location on floppy icon click

Tags
- Add new column to persist tag color to db table
- Load tags in DB on start
- Improve styling of tags:
    - More Padding and margins around tags
    - Less height in new tag input field
    - Single uniform component which contains a checkbox, input field and floppy icon to save the tag
- Move apply button to float right of tabs so it's clear that it's purpose is to save tag to photo relationships

Search
- Load tags in DB on start
- Allow user to select/deselect tags and then filter thumbnails on right panel asynchrnously in accordance with selection
