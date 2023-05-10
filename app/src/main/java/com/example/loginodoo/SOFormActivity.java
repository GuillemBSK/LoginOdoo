package com.example.loginodoo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class SOFormActivity extends AppCompatActivity {

    private OdooUtility odoo;
    private String uid;
    private String password;
    private String serverAddress;
    private String database;
    private long searchTaskId;
    TextView textName;
    EditText editPartner;
    EditText editOrderDate;
    EditText editClientOrderRef;
    EditText editWarehouse;
    EditText editAmountTotal;
    EditText editState;
    SO saleOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soform);

        initFields();
        uid = SharedData.getKey(SOFormActivity.this, "uid");
        password = SharedData.getKey(SOFormActivity.this, "password");
        serverAddress = SharedData.getKey(SOFormActivity.this,
                "serverAddress");
        database = SharedData.getKey(SOFormActivity.this, "database");
        odoo = new OdooUtility(serverAddress, "object");
        saleOrder = new SO();
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        searchSaleOrderByName(name);

    }
    private void initFields()
    {
        //textName = (TextView) findViewById(R.id.textname);
        editPartner = (EditText) findViewById(R.id.editPartner);
        editOrderDate = (EditText) findViewById(R.id.editOrderDate);
        editClientOrderRef = (EditText)
                findViewById(R.id.editClientOrderRef);
        editWarehouse = (EditText) findViewById(R.id.editWarehouse);
        editAmountTotal = (EditText) findViewById(R.id.editAmountTotal);
        editState = (EditText) findViewById(R.id.editState);
    }

    private void searchSaleOrderByName(String name) {
        List conditions = Arrays.asList(Arrays.asList(
                Arrays.asList("name", "=", name)));
        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "name",
                    "partner_id",
                    "date_order",
                    "client_order_ref",
                    "state",
                    "medium_id",
                    "amount_total"
            ));
        }};
        searchTaskId = odoo.search_read(listener, database, uid, password,
                "sale.order", conditions, fields);
    }

    XMLRPCCallback listener = new XMLRPCCallback() {
        public void onResponse(long id, Object result) {
            Looper.prepare();
            if (id == searchTaskId) {
                Object[] classObjs = (Object[]) result;
                int length = classObjs.length;
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> classObj =
                                (Map<String, Object>) classObjs[i];
                        saleOrder.setData(classObj);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillSaleOrderForm();
                        }
                    });
                } else {
                    odoo.MessageDialog(SOFormActivity.this,
                            "Sale Order not found");
                }
            }
            Looper.loop();
        }
        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            Log.e("SEARCH", error.getMessage());
            odoo.MessageDialog(SOFormActivity.this, error.getMessage());
            Looper.loop();
        }
        public void onServerError(long id, XMLRPCServerException error) {
            Looper.prepare();
            Log.e("SEARCH", error.getMessage());
            odoo.MessageDialog(SOFormActivity.this, error.getMessage());
            Looper.loop();
        }
    };
    private void fillSaleOrderForm() {
        //textName.setText(saleOrder.getName());
        editPartner.setText(saleOrder.getPartner());
        editOrderDate.setText(saleOrder.getDateOrder());
        editClientOrderRef.setText(saleOrder.getClientOrderRef());
        editState.setText(saleOrder.getState());
        editWarehouse.setText(saleOrder.getWarehouse());
        editAmountTotal.setText(saleOrder.getAmountTotal().toString());
    }
}