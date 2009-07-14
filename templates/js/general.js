(function($){
	var history = [], pos = 0, currentPage;

	function resizeUI() {
		$('#doc3').css('height', (window.innerHeight || document.documentElement.clientHeight) - $('#hd').height() - 12);
	}

	function scrollToHash(hash) {
		if (hash) {
			$(hash).each(function() {
				var offset = $(this).offset();

				$('#detailsView')[0].scrollTop = offset.top - $('#detailsView').offset().top;
			});
		}
	}

	function loadURL(url) {
		var parts = /^([^#]+)(#.+)?$/.exec(url), hash = parts[2];

		// In page link, no need to load anything
		if (parts[1] == currentPage) {
			scrollToHash(hash);
			return;
		}

		currentPage = parts[1];

		$.get(parts[1], "", function(data) {
			data = /<body[^>]*>([\s\S]+)<\/body>/.exec(data);

			if (data) {
				$('#detailsView').html(data[1])[0].scrollTop = 0;
				scrollToHash(hash);
			}
		});
	}

	$().ready(function(){
		$("#browser").treeview();
		$(window).resize(resizeUI).trigger('resize');

	/*	window.setInterval(function() {
			var hash = parseInt(document.location.hash.replace(/^#/, ''));

			loadURL(history[hash - 1]);
			history.length = hash;
		}, 100); */

		$("a").live("click", function(e) {
			var url = e.target.href;

			if (url.indexOf('class_') != -1)
				loadURL(url);

			e.preventDefault();
		});
	});
})(jQuery);
