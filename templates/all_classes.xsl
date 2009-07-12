<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template match="/">
		<html>
		<head>
			<title>Class</title>
			<link href="css/general.css" rel="stylesheet" type="text/css" />
		</head>
		<body>
			<h3>All classes</h3>

			<ul>
				<xsl:for-each select="//class">
					<li>
						<a target="overview">
							<xsl:attribute name="href">class_<xsl:value-of select="@fullname" />.html</xsl:attribute>
							<xsl:value-of select="@name" />
						</a>
					</li>
				</xsl:for-each>
			</ul>
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
