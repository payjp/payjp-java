package jp.pay.model;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;

import jp.pay.BasePayjpTest;
import jp.pay.model.Event;
import jp.pay.net.APIResource;
import static org.junit.Assert.assertEquals;

public class DeserializerTest extends BasePayjpTest {

	private static Gson gson = APIResource.GSON;
	
	@Test
	public void deserializeEventDataAccountEvent() throws IOException {
		String json = resource("account_event.json");
		Event e = gson.fromJson(json, Event.class);

		assertEquals(Subscription.class, e.getData().getClass());
		
		Subscription sub = (Subscription)e.getData();
		assertEquals("sub_dca9de79e5240009adb994d52974", sub.getId());
	}
}
