<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" indent="yes"/>
	<xsl:param name="target" />

	<xsl:template match="/">
		<html>
			<head>
				<title>Class</title>
				<link href="css/general.css" rel="stylesheet" type="text/css" />
			</head>
			<frameset cols="10%,90%" title="">
				<frameset rows="30%,70%" title="">
					<frame src="namespaces.html" name="namespaces" />
					<frame src="classes.html" name="classes" />
				</frameset>
				<frame src="overview.html" name="overview" scrolling="yes" />
			<noframes>
			</noframes>
			</frameset>
		</html>
	</xsl:template>
</xsl:stylesheet>
