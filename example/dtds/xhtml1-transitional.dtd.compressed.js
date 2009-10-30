unpack({
	Z : 'H|K|N|O|P',
	Y : 'X|form|R|Q',
	ZG : 'E|span|width|align|char|charoff|valign',
	X : 'p|T|div|U|W|isindex|fieldset|table',
	ZF : 'E|align|char|charoff|valign',
	W : 'pre|hr|blockquote|address|center|noframes',
	ZE : 'abbr|axis|headers|scope|rowspan|colspan|align|char|charoff|valign|nowrap|bgcolor|width|height',
	ZD : '[E][S]',
	U : 'ul|ol|dl|menu|dir',
	ZC : 'p|Y|div|U|W|table|br|span|bdo|object|applet|img|map|K|N|Q',
	T : 'h1|h2|h3|h4|h5|h6',
	ZB : 'X|S|Q',
	S : 'R|P',
	ZA : 'a|G|J|M|O|P',
	R : 'a|H|K|N|O',
	Q : 'noscript|P',
	P : 'ins|del|script',
	O : 'input|select|textarea|label|button',
	N : 'M|L',
	M : 'em|strong|dfn|code|q|samp|kbd|var|cite|abbr|acronym',
	L : 'sub|sup',
	K : 'J|I',
	J : 'tt|i|b|u|s|strike',
	I : 'big|small|font|basefont',
	H : 'G|F',
	G : 'br|span|bdo',
	F : 'object|applet|img|map|iframe',
	E : 'A|B|C',
	D : 'accesskey|tabindex|onfocus|onblur',
	C : 'onclick|ondblclick|onmousedown|onmouseup|onmouseover|onmousemove|onmouseout|onkeypress|onkeydown|onkeyup',
	B : 'lang|xml:lang|dir',
	A : 'id|class|style|title'
}, 'script[id|charset|type|language|src|defer|xml:space]' + 
	'style[B|id|type|media|title|xml:space]' + 
	'object[E|declare|classid|codebase|data|type|codetype|archive|standby|height|width|usemap|name|tabindex|align|border|hspace|vspace][param|Y]' + 
	'param[id|name|value|valuetype|type]' + 
	'p[E|align][S]' + 
	'a[E|D|charset|type|name|href|hreflang|rel|rev|shape|coords|target][Z]' + 
	'br[A|clear]' + 
	'spanZD' + 
	'bdo[A|C|B][S]' + 
	'applet[A|codebase|archive|code|object|alt|name|width|height|align|hspace|vspace][param|Y]' + 
	'h1[E|align][S]' + 
	'img[E|src|alt|name|longdesc|height|width|usemap|ismap|align|border|hspace|vspace]' + 
	'map[B|C|A|name][X|form|Q|area]' + 
	'h2[E|align][S]' + 
	'iframe[A|longdesc|name|src|frameborder|marginwidth|marginheight|scrolling|align|height|width][Y]' + 
	'h3[E|align][S]' + 
	'ttZD' + 
	'iZD' + 
	'bZD' + 
	'uZD' + 
	'sZD' + 
	'strikeZD' + 
	'bigZD' + 
	'smallZD' + 
	'font[A|B|size|color|face][S]' + 
	'basefont[id|size|color|face]' + 
	'emZD' + 
	'strongZD' + 
	'dfnZD' + 
	'codeZD' + 
	'q[E|cite][S]' + 
	'sampZD' + 
	'kbdZD' + 
	'varZD' + 
	'citeZD' + 
	'abbrZD' + 
	'acronymZD' + 
	'subZD' + 
	'supZD' + 
	'input[E|D|type|name|value|checked|disabled|readonly|size|maxlength|src|alt|usemap|onselect|onchange|accept|align]' + 
	'select[E|name|size|multiple|disabled|tabindex|onfocus|onblur|onchange][optgroup|option]' + 
	'optgroup[E|disabled|label][option]' + 
	'option[E|selected|disabled|label|value]' + 
	'textarea[E|D|name|rows|cols|disabled|readonly|onselect|onchange]' + 
	'label[E|for|accesskey|onfocus|onblur][S]' + 
	'button[E|D|name|value|type|disabled][p|T|div|U|W|table|G|object|applet|img|map|K|N|Q]' + 
	'h4[E|align][S]' + 
	'ins[E|cite|datetime][Y]' + 
	'h5[E|align][S]' + 
	'del[E|cite|datetime][Y]' + 
	'h6[E|align][S]' + 
	'div[E|align][Y]' + 
	'ul[E|type|compact][li]' + 
	'li[E|type|value][Y]' + 
	'ol[E|type|compact|start][li]' + 
	'dl[E|compact][dt|dd]' + 
	'dtZD' + 
	'dd[E][Y]' + 
	'menu[E|compact][li]' + 
	'dir[E|compact][li]' + 
	'pre[E|width|xml:space][ZA]' + 
	'hr[E|align|noshade|size|width]' + 
	'blockquote[E|cite][Y]' + 
	'address[E][S|p]' + 
	'center[E][Y]' + 
	'noframes[E][Y]' + 
	'isindex[A|B|prompt]' + 
	'fieldset[E][legend|Y]' + 
	'legend[E|accesskey|align][S]' + 
	'table[E|summary|width|border|frame|rules|cellspacing|cellpadding|align|bgcolor][caption|col|colgroup|thead|tfoot|tbody|tr]' + 
	'caption[E|align][S]' + 
	'col[ZG]' + 
	'colgroup[ZG][col]' + 
	'thead[ZF][tr]' + 
	'tr[ZF|bgcolor][th|td]' + 
	'th[E|ZE][Y]' + 
	'form[E|action|method|name|enctype|onsubmit|onreset|accept|accept-charset|target][X|R|Q]' + 
	'noscript[E][Y]' + 
	'td[E|ZE][Y]' + 
	'tfoot[ZF][tr]' + 
	'tbody[ZF][tr]' + 
	'area[E|D|shape|coords|href|nohref|alt|target]' + 
	'base[id|href|target]'
);