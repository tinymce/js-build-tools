js-build-tools - A set of tools for building JS projects
==========================================================

What is js-build-tools
-----------------
This project contains a collection of Ant tasks useful for building JavaScript projects. 

What you need to build js-build-tools
--------------------------------------
* Install the Java JDK or JRE packages you can find it at: [http://java.sun.com/javase/downloads/index.jsp](http://java.sun.com/javase/downloads/index.jsp)
* Install Apache Ant you can find it at: [http://ant.apache.org/](http://ant.apache.org/)
* Add Apache Ant to your systems path environment variable, this is not required but makes it easier to issue commands to Ant without having to type the full path for it.

How to build js-build-tools
----------------------------

In the root directory of js-build-tools where the build.xml file is you can run ant against different targets.

`ant`

Will create a build directory containing the js_build_tools.jar file.

`ant release`

Will produce release packages. The release packages will be placed in the tmp directory.

Cache Fingerprint Task
----------------------

What this task does:
 This ant task goes through a fileset, through each line of each file, looking for things like:
 1) Script references
	`<script type ="text/javascript" src= "/scripts/script.js"/>`
 2) Image references
	`<img alt="whatever" src="/images/img.gif"></img>`
 3)	Link references
	`<link href="style.css"/>`
 4)	CSS background images 
	`background-image:url(/images/test.jpg);`
	
 The task get a checksum value for the file and use it to create a new reference:
 
 By default it create a reference with a the checksum value as a query string:
	`/images/test.jpg?5454385464
	/javascript/script.js?4654684864`
 
 This solves the caching problem by letting the browser know when a file has changed by changing the url.
 
 Paramenters:
 1) processjs (default value: true)
	It will search for `<script type ="text/javascript" src= "/scripts/script.js"/>` like references
	
 2) processimg (default value: true)
	It will search for `<img alt="whatever" src="/images/img.gif"></img>` like references
	
 3) processcss (default value: true)
	It will search for `<link href="style.css"/>` like references
	
 4) processcss (default value: true)
	It will search for `background-image:url(/images/test.jpg);` like references
	
 5) uri (default value: false)
	for better caching throght proxies you can use the uri="true" paramenter this way the fingerprint is going to be added
	to the begining of the uri. Something like this:
		`/a/5454385464/images/test.jpg
		/a/4654684864/javascript/script.js`
		
	Note: this will work only if yo create a rewrite rule on your server. 
		
 6) staticservers
	It prepends to the url a host. It receives a comma separated list of hosts to prepend and it choose one randomly for each referenced file.
	Example:    staticservers="static1.yourhost.com,static2.yourhost.com"
	Return:
		`http://static1.yourhost.com/images/test.jpg?5454385464
		http://static2.yourhost.com/javascript/script.js?4654684864`