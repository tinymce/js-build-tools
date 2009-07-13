<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" indent="yes"/>

	<xsl:template match="/">
		<html>
		<head>
			<title>Class</title>
			<link href="css/general.css" rel="stylesheet" type="text/css" />
		</head>
		<body>
			<h3>Namespaces</h3>

			<ul>
				<xsl:if test="model/class">
					<li><a href="namespace_top_level.html" target="classes">Top Level</a></li>
				</xsl:if>

				<xsl:for-each select="//namespace">
					<xsl:if test="class">
						<li>
							<a target="classes">
								<xsl:attribute name="href">namespace_<xsl:value-of select="@fullname" />.html</xsl:attribute>
								<xsl:value-of select="@fullname" />
							</a>
						</li>
					</xsl:if>
				</xsl:for-each>
			</ul>
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
