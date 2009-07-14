<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output
		method="html"
		indent="yes"
		omit-xml-declaration="yes"
		doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
		doctype-public="-//W3C//DTD XHTML 1.1//EN"
	/>

	<xsl:param name="target" />

	<xsl:template name="namespace">
		<li class="closed"><span><a href="#"><xsl:value-of select="@name" /></a></span>
			<ul>
				<xsl:for-each select="namespace">
					<xsl:call-template name="namespace" />
				</xsl:for-each>

				<xsl:for-each select="class">
					<xsl:call-template name="class" />
				</xsl:for-each>
			</ul>
		</li>
	</xsl:template>

	<xsl:template name="class">
		<xsl:choose>
			<xsl:when test="@alias-for">
				<li>
					<span class="alias">
						<a class="aliasLink">
							<xsl:attribute name="href">class_<xsl:value-of select="@alias-for" />.html</xsl:attribute>
							<xsl:text><xsl:value-of select="@name" /></xsl:text>
						</a>
					</span>
				</li>
			</xsl:when>

			<xsl:when test="@static">
				<li>
					<span class="singleton">
						<a>
							<xsl:attribute name="href">class_<xsl:value-of select="@fullname" />.html</xsl:attribute>
							<xsl:text><xsl:value-of select="@name" /></xsl:text>
						</a>
					</span>
				</li>
			</xsl:when>

			<xsl:otherwise>
				<li>
					<span class="class">
						<a>
							<xsl:attribute name="href">class_<xsl:value-of select="@fullname" />.html</xsl:attribute>
							<xsl:text><xsl:value-of select="@name" /></xsl:text>
						</a>
					</span>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
			<title>TinyMCE API</title>
			<meta name="generator" content="MoxieDoc" />

			<link rel="stylesheet" type="text/css" href="css/reset.css" />
			<link rel="stylesheet" type="text/css" href="css/grids.css" />
			<link rel="stylesheet" type="text/css" href="css/general.css" />
			<link rel="stylesheet" type="text/css" href="css/jquery.treeview.css" />

			<script type="text/javascript" src="http://www.google.com/jsapi"></script>
			<script type="text/javascript">
				google.load("jquery", "1.3");
			</script>
			<script type="text/javascript" src="js/jquery.treeview.min.js"></script>
			<script type="text/javascript" src="js/general.js"></script>
		</head>
		<body>
			<div id="doc3" class="yui-t1" style="height:500px">
				<div id="hd"><h1>TinyMCE API</h1></div>

				<div id="bd">
					<div id="yui-main">
						<div id="detailsView" class="yui-b">
							<xsl:comment>Gets filled using Ajax</xsl:comment>
						</div>
					</div>

					<div id="classView" class="yui-b">
						<ul id="browser" class="classtree treeview-famfamfam">
							<li><span class="root">API Documentation</span>
								<ul>
									<xsl:for-each select="model/namespace">
										<xsl:call-template name="namespace" />
									</xsl:for-each>

									<xsl:for-each select="model/class">
										<xsl:call-template name="class" />
									</xsl:for-each>
								</ul>
							</li>
						</ul>
					</div>
				</div>
			</div> 
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
