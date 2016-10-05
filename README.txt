UniSuper Code Challenge.
Completed by Matthew Leviny.
Time spent on task: 4 hours.

All tasks succeed in chrome.
1 issue with editing a task in firefox.
Results outputted in console.
Asserts were used after each task to ensure the correct action was taken.

A little summary of what I was able to do in this time and what held me back.

This was the first time for me using Selenium and testing within a web browser. all previous testing was either a unit test or integration test 
	for java projects. I found the challenge of completing a task like this enjoyable, as after each task was complete a result was visible when
	 the test was run. In the test Thread.sleep is used purely to make each step visible when viewing the browser and is not required in a
	 real world scenario. Tasks that I was unable to complete within a reasonable amount of time, these were not included so that the test
	 would succeed included:
	  	- separating each task into its own test so that the tests that failed would be easily visible. The issue I encountered was that at the
			start of each test a new web driver would be created and the old one destroyed causing each test to fail due to the previous step of
			creating an item missing.
		- Had to place the edit test in a try catch block due to on firefox the double click not being recognised and causing that test to crash. 

I was able it implement Parallel testing using juinit parameterised testing but was unable to get the delivered product to the level I desired.
	Unable to complete tasks included:
		- Naming threads. The tests simply show a number either 0 or 1. I would have like to have named these to ensure ease of readability.
		- 100% working in both browsers. While testing I had the issue of different browsers not recognising different commands. I was able
			 to make firefox ignore the one problem command but Edge browser would not recognise even the simplest of things like a key press.
I could have circumvented these issues by creating browser specific tests and using either marven or a program like testNG to run the tests in
	parallel but this didn't seem like the solution that would be looked for.
	
If you wish to know any additional information please let me know.