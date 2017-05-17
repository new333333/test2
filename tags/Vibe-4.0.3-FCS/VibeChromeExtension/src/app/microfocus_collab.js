chrome.downloads.onCreated.addListener(function(download){
	var expr=/application\/x-java-jnlp-file/;
	console.log("Current Mime ",download.mime);
	if(expr.test(download.mime)){
		if(download.state == "in_progress"){
			console.log("JNLP Downloading URL: ",download.url);
			chrome.downloads.cancel(download.id,function(){
				console.log("Download ",download.url," cancelled.");
			});
			
			chrome.runtime.sendNativeMessage('com.microfocus.jnlplauncher',{url:download.url},function(response){
			console.log(chrome.runtime.lastError);
			console.log(response);
		});
		}
	}
})