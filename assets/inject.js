function enterIDPASS(id, pass) {
	$("#KID").val(id);
	$("#pass").val(pass);
}

function removeCard() {
	console.log($("table.table_type6 a"));
	window.location.href= $("table.table_type6 a")[1].href;
}

function addCard(cardname, cardpass, paseli) {
	$("#CRcontents_table input[name='ucd']").val(cardname);
	$("#CRcontents_table input[name='pass']").val(cardpass);
	$("#CRcontents_table input[name='ecprop'][value='" + paseli + "']").prop('checked', true);
	//$("#CRcontents_table input[name='ecprop']:checked")
	$("form").submit();
}

function useCard(cardid) {
	$("td.eapass_detach a").each(function (i, obj) {
		if (obj.href.indexOf(cardid) >= 0) {
			window.location.href=obj.href;
			window.location.replace(obj.href);	// for mobile
			return;
		}
	});
}

function iidxme(id, pw) {
	// iidxme script
	if(location.protocol==="https:"){alert("Can't load over https");return;}var s=document.createElement("script");s.id="iidxme";s.type="text/javascript";s.src="http://iidx.me/loader.js";document.head.appendChild(s);

	var timerID = setInterval(function () {
		if ($("input#id").length) {
			$("input#id").val(id);
			$("input#pass").val(pw);
			clearInterval(timerID);
		}
	}, 1000);
}

$(function() {
	logined = false;
	// check login status
	if ($("a[href='/gate/p/logout.html']").length
 || $("a[href='/gate/p/logout.html?___REDIRECT=1']").length
 || $("#head_info_box").length) {
		logined = true;
	}

	// page loaded 
	chrome.runtime.sendMessage({method: "onload", logined: logined}, function(response) {
		console.log(response.status);
	});

});