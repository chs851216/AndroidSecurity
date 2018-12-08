package csie.mcu.edu.tw.group5.ad;

import java.util.HashMap;
import java.util.Map;

public class AdList {

	private static final String path = "AdLibraryList/";
	
	private Map<String, String> adMap;
	
	public AdList() {
		this.adMap = new HashMap<String, String>();
		this.adMap.put("com/adcolony", path + "Adcolony");
		this.adMap.put("com/adjust", path + "Adjust");
		this.adMap.put("com/google/ads", path + "Admob");
		this.adMap.put("com/airpush", path + "AirPush");
		this.adMap.put("com/applovin", path + "AppLovin");
		this.adMap.put("com/appnext", path + "Appnext");
		this.adMap.put("com/appsflyer", path + "AppsFlyer");
		this.adMap.put("com/chartboost", path + "Chartboost");
		this.adMap.put("com/facebook/ads", path + "Facebook Audience Network");
		this.adMap.put("com/flurry", path + "Flurry");
		this.adMap.put("com/inmobi", path + "InMobi");
		this.adMap.put("com/ironsource", path + "IronSource");
		this.adMap.put("com/millennialmedia", path + "Millennial Media");
		this.adMap.put("com/mobfox", path + "MobFox");
		this.adMap.put("com/mopub", path + "Mopub");
		this.adMap.put("com/nativex", path + "NativeX");
		this.adMap.put("com/smaato", path + "Smaato");
		this.adMap.put("com/tapjoy", path + "Tapjoy");
		this.adMap.put("com/trialpay", path + "Trialpay");
		this.adMap.put("com/unity3d/ads", path + "Unity Ads");
		this.adMap.put("com/vungle", path + "Vungle");
		this.adMap.put("com/amazon", path + "Amazon Mobile Ads");
		this.adMap.put("com/mobvista/msdk", path + "Mobvista");
		this.adMap.put("com/heyzap", path + "HeyZap");
		this.adMap.put("com/startapp", path + "Startapp");
	}
	
	public String getAdvertisers(String key) {
		return this.adMap.get(key);
	}
	
	public String[] getAllKey() {
		String[] result = new String[this.adMap.size()];
		
		return this.adMap.keySet().toArray(result);
	}
}