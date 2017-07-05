# StopSignal
This is an android app designed as a stop-signal response test for the Transportation Research Group in the 
UCF Applied Experimental and Human Factors Program. This Android application is being used in doctoral student Jennifer Louie's dissertation project on the psychology of driving while distracted.

The test has multiple sets of trials. Participants are allowed practice trials where data is not recorded. If a participant does not score well on the practice trials, they will be unable to start the recorded trials to prevent results from being skewed by miscommunication or other factors. Once a participant passes the practice trials, they move onto the recorded trials. If a participant has already run through the test once, they may opt out of the practice trials at the beginning of the test.

There are two batteries of trials in the pattern of practice-recorded. The first battery contains no vibrations. The second battery contains vibrations. Each recorded set of trials should take around 5-7 minutes.

In each trial, participants have a set amount of time to respond to an auditory tone by pressing the left button if a low tone is played,
the right button if a high tone is played, or not responding to whatever tone is played if the phone vibrates.

There are two conditions this test can be run on. Condition 1 is easier and gives more time in between signals for participants to react. Condition 2 is more challenging and has less time between signals for participants to react.

Data from each trial is recorded and saved to the device. Data saved includes the trial number, the block number, the trial type(i.e. stop or non-stop), the correct response, the response time, and the participant's response for each trial. The mean response time of each recorded battery, the mean response time for each practice battery, the mean response time in stop(vibration) trials, the mean response time in non-stop trials, the percentage of stop trials correctly ignored, the percentage of non-stop trials correctly answered, and the overall percentage of trials that timed out are also recorded. All data is saved in a format that can easily be pasted into Microsoft Excel. The resulting file is saved to the StopTrials folder in the devices documents directory with the name formatted as "month.day hour:minute.txt"

App built by Jacob Sage
