/**
 * This is a namespace jada jada.
 *
 * @namespace namespace
 */
(function() {
	/**
	 * Here is some class.
	 *
	 * @version 1.0
	 * @example
	 * window.SomeClass.someFunc();
	 * window.SomeClass.someOtherFunc();
	 * @class namespace.SomeClass
	 */
	window.SomeClass = function() {
		/**
		 * Some function.
		 *
		 * @version 1.1
		 * @method someFunc
		 * @param {Object} settings First arg.
		 *   @option {String/Object} some_setting1 some value 1.
		 *   @option {Number} some_setting2 some value 2.
		 * @param {String} arg2 Second arg.
		 * @return {String} Some string.
		 */
		this.someFunc = function(arg1, arg2) {
			return "";
		};

		/**
		 * Some other function.
		 *
		 * @deprecated Removed in version 1.0
		 * @method someOtherFunc
		 * @param {String} arg1 First arg.
		 * @param {Object} arg2 Second arg.
		 * @option {String} some_setting1 some value 1.
		 * @option {Number} some_setting2 some value 2.
		 * @return {String} Some string.
		 */
		this.someOtherFunc = function(arg1, arg2) {
			return "";
		};
	};

	/**
	 * Here is some static class.
	 *
	 * @class namespace.SomeOtherClass
	 * @static
	 */
	window.SomeOtherClass = {
		/**
		 * Some function.
		 *
		 * @method someFunc
		 * @param {String} arg1 First arg.
		 * @param {String} arg2 Second arg.
		 * @return {String} Some string.
		 */
		someStaticFunc : function(arg1, arg2) {
			return "";
		}
	};

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