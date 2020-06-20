# HoneyImHome
Android project for POST PC COMPUTING course in the Hebrew university in Jerusalem.

I pledge the highest level of ethical principles in support of academic excellence. I ensure that all of my work reflects my own abilities and not those of someone else.

Question:
What should we add in our code-base so that when the SMS will get delivered, this notification's text will be changed to "sms sent: ..."?

Answer:
We can provide a PendingIntent that broadcasts a message that will trigger
a callback function that updates the contents of our notification, using our notification channel name.
