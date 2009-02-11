(function() {
	var someLongVar;

	function someLongFunctionName() {
		someLongVar = 1;
	};

	someLongFunctionName();

	// #ifdef value1

		alert('Some logic 1.');

		// #ifdef value3

		alert('Some logic 2.1.');

		// #endif

		// #ifdef value2

		alert('Some logic 3.1');

		// #endif

		// #ifdef value3

		alert('Some logic 2.2.');

		// #endif

		// #ifndef value2

		alert('Some logic 3.2');

		// #endif

	// #endif
})();