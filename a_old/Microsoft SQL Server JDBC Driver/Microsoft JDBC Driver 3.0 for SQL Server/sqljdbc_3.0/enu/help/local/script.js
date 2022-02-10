// updated from DevDiv
var jsPath = scriptPath();

writeCSS(jsPath);

function scriptPath() {
    //Determine path to JS-the CSS is in the same directory as the script
    var scripts = document.getElementsByTagName("SCRIPT");
    var lastScript = scripts.item(scripts.length - 1);
    var srcAttr = lastScript.attributes.getNamedItem("SRC");
    var spath = srcAttr.nodeValue.toLowerCase();

    return spath.replace("script.js", "");
}

function writeCSS(spath) {
	document.writeln('<SCRIPT SRC="' + spath + '\script_loc.js"></SCRIPT>');
	document.writeln('<SCRIPT SRC="' + spath + '\script_main.js"></SCRIPT>');
	document.writeln('<SCRIPT SRC="' + spath + '\script_fb.js"></SCRIPT>');
}
