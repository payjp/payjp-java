#Payjp-java Request Example


##支払い(Charges)

###post支払いを作成

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Map<String, Object> chargeParams = new HashMap<String, Object>();

	chargeParams.put("card", "your_token");
	chargeParams.put("amount", 3500);
	chargeParams.put("currency", "jpy");
	
	Charge.create(chargeParams);

###get支払い情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Charge.retrieve("ch_fa990a4c10672a93053a774730b0a");

###post支払い情報を更新

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Charge ch = Charge.retrieve("ch_fa990a4c10672a93053a774730b0a");
	Map<String, Object> updateParams = new HashMap<String, Object>();
	updateParams.put("description", "Updated");
	
	ch.update(updateParams);

###post返金する

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Charge ch = Charge.retrieve("ch_fa990a4c10672a93053a774730b0a");
	ch.refund();

###post支払い処理を確定する

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Charge ch = Charge.retrieve("ch_fa990a4c10672a93053a774730b0a");
	ch.capture();

###get支払いリストを取得
	
	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Map<String, Object> chargeParams = new HashMap<String, Object>();
	chargeParams.put("limit", 3);
	chargeParams.put("offset", 10);
	
	Charge.all(chargeParams);

##顧客 (CUSTOMERS)

###post顧客を作成

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Map<String, Object> customerParams = new HashMap<String, Object>();
	customerParams.put("description", "test");

	Customer.create(customerParams);

###get顧客情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Customer.retrieve("cus_121673955bd7aa144de5a8f6c262");

###post顧客情報を更新

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Customer cu = Customer.retrieve("cus_121673955bd7aa144de5a8f6c262");
	Map<String, Object> updateParams = new HashMap<String, Object>();
	updateParams.put("email", "added@email.com");
	
###delete顧客を削除

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Customer cu = Customer.retrieve("cus_121673955bd7aa144de5a8f6c262");
	cu.delete();

###get顧客リストを取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Map<String, Object> customerParams = new HashMap<String, Object>();
	customerParams.put("limit", 3);
	customerParams.put("offset", 10);
	
	Customer.all(customerParams);

###get顧客のカード情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Customer cu = Customer.retrieve("cus_4df4b5ed720933f4fb9e28857517");
	cu.getCards().retrieve("car_f7d9fa98594dc7c2e42bfcd641ff");

###post顧客のカードを更新

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Customer cu = Customer.retrieve("cus_4df4b5ed720933f4fb9e28857517");
	Card ca = cu.getCards().retrieve("car_f7d9fa98594dc7c2e42bfcd641ff");
	
	Map<String, Object> updateParams = new HashMap<String, Object>();
	
	updateParams.put("exp_year", "2026");
	updateParams.put("exp_month", "05");
	
	ca.update(updateParams);
	
###delete顧客のカードを削除

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Customer cu = Customer.retrieve("cus_4df4b5ed720933f4fb9e28857517");
	Card ca = cu.getCards().retrieve("car_f7d9fa98594dc7c2e42bfcd641ff");

	ca.delete();

###get顧客のカードリストを取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Map<String, Object> listParams = new HashMap<String, Object>();
	listParams.put("limit", 3);
	listParams.put("offset", 1);
	
	Customer.retrieve("cus_4df4b5ed720933f4fb9e28857517").getCards().all(listParams);

###get顧客の定期購入情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Customer cu = Customer.retrieve("cus_4df4b5ed720933f4fb9e28857517");
	cu.getSubscriptions().retrieve("sub_567a1e44562932ec1a7682d746e0");

###get顧客の定期購入リストを取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Customer cu = Customer.retrieve("cus_4df4b5ed720933f4fb9e28857517");
	
	Map<String, Object> listParams = new HashMap<String, Object>();
	listParams.put("limit", 3);
	
	cu.getSubscriptions().all(listParams);

##プラン (PLANS)

###postプランを作成

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Map<String, Object> planParams = new HashMap<String, Object>();
	planParams.put("amount", 500);
	planParams.put("currency", "jpy");
	planParams.put("interval", "month");
	planParams.put("trial_days", 30);
	
	Plan.create(planParams);

###getプラン情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Plan.retrieve("pln_45dd3268a18b2837d52861716260");

###postプランを更新

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Plan p = Plan.retrieve("pln_45dd3268a18b2837d52861716260");
	
	Map<String, Object> updateParams = new HashMap<String, Object>();
	updateParams.put("name", "NewPlan");

	p.update(updateParams);

###deleteプランを削除

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Plan p = Plan.retrieve("pln_45dd3268a18b2837d52861716260");
	p.delete();

###getプランリストを取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Map<String, Object> listParams = new HashMap<String, Object>();
	listParams.put("limit", 3);

	Plan.all(listParams);

##定期購入 (SUBSCRIPTIONS)

###post定期購入を作成

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Map<String, Object> subscriptionParams = new HashMap<String, Object>();
	subscriptionParams.put("plan", "pln_9589006d14aad86aafeceac06b60");
	subscriptionParams.put("customer", "cus_4df4b5ed720933f4fb9e28857517);
		
	Subscription.create(subscriptionParams);

###get定期購入情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Subscription.retrieve("sub_567a1e44562932ec1a7682d746e0");

###post定期購入を更新

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Subscription su = Subscription.retrieve("sub_567a1e44562932ec1a7682d746e0");

	Map<String, Object> updateParams = new HashMap<String, Object>();
	updateParams.put("plan", "pln_68e6a67f582462c223ca693bc549");
	updateParams.put("next_cycle_plan", null); // null -> empty string
	
	su = su.update(updateParams);

###post定期購入を停止

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Subscription su = Subscription.retrieve("sub_567a1e44562932ec1a7682d746e0");
	su.pause();

###post定期購入を再開

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Subscription su = Subscription.retrieve("sub_567a1e44562932ec1a7682d746e0");
	su.resume();

###post定期購入をキャンセル

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Subscription su = Subscription.retrieve("sub_567a1e44562932ec1a7682d746e0");
	su.cancel();

###delete定期購入を削除

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Subscription su = Subscription.retrieve("sub_567a1e44562932ec1a7682d746e0");
	su.delete();

###get定期購入のリストを取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Map<String, Object> listParams = new HashMap<String, Object>();
	listParams.put("limit", 3);

	Subscription.all(listParams);

##トークン (TOKENS)
###getトークン情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Token.retrieve("tok_eff34b780cbebd61e87f09ecc9c6");

##入金 (TRANSFERS)

###get入金情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Transfer.retrieve("tr_8f0c0fe2c9f8a47f9d18f03959ba1");

###get入金リストを取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Map<String, Object> listParams = new HashMap<String, Object>();
	listParams.put("limit", 3);

	Transfer.all(listParams);

###get入金の内訳を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Transfer tr = Transfer.retrieve("tr_8f0c0fe2c9f8a47f9d18f03959ba1");
	
	Map<String, Object> listParams = new HashMap<String, Object>();
	listParams.put("limit", 3);
	
	tr.getCharges.all(listParams);

##イベント (EVENTS)

###getイベント情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Event.retrieve("evnt_2f7436fe0017098bc8d22221d1e");

###getイベントリストを取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
	
	Map<String, Object> listParams = new HashMap<String, Object>();
	listParams.put("limit", 3);
	listParams.put("offset", 10);

	Event.all(listParams);

##アカウント (ACCOUNTS)

###getアカウント情報を取得

	Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";

	Account.retrieve();
