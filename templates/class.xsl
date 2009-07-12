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
		<body>
			<h1><xsl:value-of select="//class[@fullname=$target]/@name" /></h1>

			<table class="classDetails">
				<tr>
					<td class="first">Namespace</td>
					<td class="last"><xsl:value-of select="//class[@fullname=$target]/parent::*/@fullname" /></td>
				</tr>

				<tr>
					<td class="first">Class</td>
					<td class="last"><xsl:value-of select="//class[@fullname=$target]/@name" /></td>
				</tr>

				<tr>
					<td class="first">Inheritance</td>
					<td class="last inheritageList">
						<xsl:for-each select="//class[@fullname=$target]/super-classes/class-ref">
							<a>
								<xsl:if test="position() = last()">
									<xsl:attribute name="class">last</xsl:attribute>
								</xsl:if>

								<xsl:attribute name="href">class_<xsl:value-of select="@class" />.html</xsl:attribute>

								<xsl:variable name="class" select="@class" />
								<xsl:value-of select="//class[@fullname=$class]/@name" />
							</a>
						</xsl:for-each>
					</td>
				</tr>
			</table>

			<div class="classSummary">
				<xsl:value-of select="//class[@fullname=$target]/summary/text()" />
			</div>

			<xsl:if test="//class[@fullname=$target]/members/property">
				<h2>Public Properties</h2>
				<ul>
					<xsl:for-each select="//class[@fullname=$target]/members/property">
						<li>
							<a>
								<xsl:attribute name="href">#<xsl:value-of select="@name" /></xsl:attribute>
								<xsl:value-of select="@name" />
							</a>
						</li>
					</xsl:for-each>
				</ul>
			</xsl:if>

			<xsl:if test="//class[@fullname=$target]/members/method">
				<h2>Public Methods</h2>

				<a class="toggleinherit" href="#">
					<span class="show">Show Inherited Public Methods</span>
					<span class="hide">Hide Inherited Public Methods</span>
				</a>

				<table class="methods summary">
					<thead>
						<tr>
							<th>Method</th>
							<th>Defined By</th>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each select="//class[@fullname=$target]/members/constructor">
							<tr>
								<xsl:if test="position() mod 2 = 0">
									<xsl:attribute name="class">even</xsl:attribute>
								</xsl:if>

								<td class="first">
									<div>
										<a>
											<xsl:attribute name="href">#<xsl:value-of select="@name" /></xsl:attribute>
											<xsl:value-of select="@name" />
										</a>
									</div>

									<div class="shortSummary">
										<xsl:value-of select="substring-before(summary/text(), '.')" />.
									</div>
								</td>

								<td><xsl:value-of select="//class[@fullname=$target]/@name" /></td>
							</tr>
						</xsl:for-each>

						<xsl:for-each select="//class[@fullname=$target]/members/method">
							<xsl:sort select="@name" />

							<xsl:choose>
								<xsl:when test="@inherited-from">
									<xsl:variable name="inheritedfrom" select="@inherited-from" />
									<xsl:variable name="name" select="@name" />

									<tr class="inherited">
										<xsl:if test="position() mod 2 = 0">
											<xsl:attribute name="class">inherited even</xsl:attribute>
										</xsl:if>

										<td class="first">
											<div>
												<a>
													<xsl:attribute name="href">class_<xsl:value-of select="$inheritedfrom" />.html#<xsl:value-of select="@name" /></xsl:attribute>
													<xsl:value-of select="@name" />
												</a>
											</div>

											<div class="shortSummary">
												<xsl:value-of select="substring-before(//class[@fullname=$inheritedfrom]/members/method[@name=$name]/summary/text(), '.')" />.
											</div>
										</td>

										<td>
											<a>
												<xsl:attribute name="href">class_<xsl:value-of select="$inheritedfrom" />.html#<xsl:value-of select="@name" /></xsl:attribute>
												<xsl:value-of select="//class[@fullname=$inheritedfrom]/@name" />
											</a>
										</td>
									</tr>
								</xsl:when>

								<xsl:otherwise>
									<tr>
										<xsl:if test="position() mod 2 = 0">
											<xsl:attribute name="class">even</xsl:attribute>
										</xsl:if>

										<td class="first">
											<div>
												<a>
													<xsl:attribute name="href">#<xsl:value-of select="@name" /></xsl:attribute>
													<xsl:value-of select="@name" />
												</a>
											</div>


											<div class="shortSummary">
												<xsl:value-of select="substring-before(summary/text(), '.')" />.
											</div>
										</td>

										<td><xsl:value-of select="//class[@fullname=$target]/@name" /></td>
									</tr>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</tbody>
				</table>
			</xsl:if>
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
