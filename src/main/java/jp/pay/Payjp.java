package jp.pay;

public abstract class Payjp {
	public static final String LIVE_API_BASE = "https://api.pay.jp";
	public static final String VERSION = "0.1.0";
	public static volatile String apiKey;
	public static volatile String apiVersion;

	private static volatile String apiBase = LIVE_API_BASE;

	static {
		final String _apiBase = System.getProperty("jp.pay.api.baseUrl");
		if (_apiBase != null)
			apiBase = _apiBase;
	}

	/**
	 * (FOR TESTING ONLY) If you'd like your API requests to hit your own
	 * (mocked) server, you can set this up here by overriding the base api URL.
	 */
	public static void overrideApiBase(final String overriddenApiBase) {
		apiBase = overriddenApiBase;
	}

	public static String getApiBase() {
		return apiBase;
	}
}
