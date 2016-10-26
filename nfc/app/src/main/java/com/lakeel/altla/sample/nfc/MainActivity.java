package com.lakeel.altla.sample.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter mAdapter;

    private PendingIntent mPendingIntent;

    private TextView mTextViewIdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mTextViewIdm = (TextView) findViewById(R.id.text_view_idm);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            // フォアグラウンドディスパッチ
            // 第3、4引数のインテントフィルタ、テクノロジーリストは使わずnullとする。
            // これにより、すべてのタグテクノロジーをスキャン対象となる。
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            // onPause() で disableForegroundDispatch() をコールすること。
            mAdapter.disableForegroundDispatch(this);
        }
    }

    // NFCタグスキャン後に呼ばれる。
    // 引数のintentには、スキャンしたタグ情報が詰まっている。
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Tag から情報を取得（API Level 10なら確実に取り出せる）
        Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        String tagString = tag.toString();

        // IDm
        String idm = getIdm(tag);

        // Tech List
//		String techList = getTechList(tag);

        dumpNfcF(tag);

//		IsoDep isoDep = IsoDep.get(tag);
//		NfcA nfcA = NfcA.get(tag);
//		NfcB nfcB = NfcB.get(tag);
//		NfcF nfcF = NfcF.get(tag);
//		NfcV nfcV = NfcV.get(tag);
//		Ndef ndef = Ndef.get(tag);
//		NdefFormatable ndefFormatable = NdefFormatable.get(tag);
//		NfcBarcode nfcBarcode = NfcBarcode.get(tag);
//		MifareClassic mifareClassic = MifareClassic.get(tag);
//		MifareUltralight mifareUltralight = MifareUltralight.get(tag);

//		// NDEF 対応か否か
//		String action = intent.getAction();
//		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//			// Ndefメッセージの取得
//			Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//			NdefMessage[] ndefMessages = new NdefMessage[parcelables.length];
//
//			for (int i = 0; i < parcelables.length; i++) {
//				ndefMessages[i] = (NdefMessage) parcelables[i];
//				for (NdefRecord record : ndefMessages[i].getRecords()) {
////					builder.append("Type : ");
////					builder.append("Type : " + new String(record.getType()) + "\n");
////					builder.append("TNF : " + record.getTnf() + "\n");
////					byte[] payload = record.getPayload();
////					if (payload == null)
////						break;
////					int idx = 0;
////					for (byte data : payload) {
////						builder.append(String.format("Payload[%2d] : 0x%02x / %c\n", idx, data, data));
////						idx++;
////					}
//				}
//			}
//		}
    }

    private String getIdm(Tag tag) {
        StringBuilder builder = new StringBuilder();

        byte[] idm = tag.getId();

        // 書式 XX:XX:XX:XX:XX:XX:XX:XX
        for (int i = 0; i < idm.length; i++) {
            builder.append(String.format("%02X", idm[i] & 0xff)).append(':');
        }

        if (0 < builder.length()) {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }

    private String getTechList(Tag tag) {
        StringBuilder builder = new StringBuilder();

        String[] techList = tag.getTechList();
        for (String tech : techList) {
            builder.append(tech).append('\n');
        }

        if (0 < builder.length()) {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }

    private void dumpNfcF(Tag tag) {
        NfcF nfcF = NfcF.get(tag);
        if (nfcF == null) return;

        String systemCode = toHexString(nfcF.getSystemCode());
        String manufacturer = toHexString(nfcF.getManufacturer());

        System.out.println();
    }

    private String toHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        builder.append("0x");

        for (int i = 0; i < bytes.length; i++) {
            builder.append(String.format("%02X", bytes[i] & 0xff));
        }

        return builder.toString();
    }
}
