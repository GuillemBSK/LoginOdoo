package com.example.loginodoo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class CustomerListActivity extends AppCompatActivity {

    private OdooUtility odoo;
    private String uid;
    private String password;
    private String serverAddress;
    private String database;
    private long searchTaskId;
    ListView listViewPartner;
    List arrayListPartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        uid = SharedData.getKey(CustomerListActivity.this, "uid");
        password = SharedData.getKey(CustomerListActivity.this, "password");
        serverAddress = SharedData.getKey(CustomerListActivity.this,
                "serverAddress");
        database = SharedData.getKey(CustomerListActivity.this, "database");
        odoo = new OdooUtility(serverAddress, "object");
        arrayListPartner = new ArrayList();
        listViewPartner = (ListView) findViewById(R.id.listPartner);
    }

    public void onClickSearchPartner(View v){
        EditText editKeyword = (EditText) findViewById(R.id.editKeyword);
        String keyword = editKeyword.getText().toString();
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("name", "ilike", keyword)));
        //pyhthon equivalent ==> [ [ ["name", "ilike", keyword] ] ]
        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "display_name"
            ));
        }};
        //python equivalent ==> { "fields" : ["id","display_name"] }
        searchTaskId = odoo.search_read(listener, database, uid, password,
                "res.partner", conditions, fields);
    }

    XMLRPCCallback listener = new XMLRPCCallback() {
        public void onResponse(long id, Object result) {
            Looper.prepare();
            if (id==searchTaskId)
            {
                Object[] classObjs=(Object[])result;
                int length=classObjs.length;
                if(length>0){
                    arrayListPartner.clear();
                    for (int i=0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> classObj =
                                (Map<String,Object>)classObjs[i];
                        arrayListPartner.add(classObj.get("display_name"));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillListPartner();
                        }
                    });
                }
                else
                {
                    odoo.MessageDialog(CustomerListActivity.this,
                            "Partner not found");
                }
            }
            Looper.loop();
        }

        public void onError(long id, XMLRPCException error) {
            Log.e("SEARCH", error.getMessage());
            odoo.MessageDialog(CustomerListActivity.this, error.getMessage());
        }
        public void onServerError(long id, XMLRPCServerException error) {
            Log.e("SEARCH", error.getMessage());
            odoo.MessageDialog(CustomerListActivity.this, error.getMessage());
        }
    };

    private void fillListPartner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, arrayListPartner);
        listViewPartner.setAdapter(adapter);
        listViewPartner.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id)
                    {
                        // ListView Clicked item index
                        int itemPosition = position;
                        // ListView Clicked item value
                        String itemValue = (String) listViewPartner.getItemAtPosition(position);
                        // Show Alert
                        Toast.makeText(getApplicationContext(), "Position :" + itemPosition + " ListItem : " + itemValue, Toast.LENGTH_LONG).show();

                        Intent myIntent = new Intent(CustomerListActivity.this,
                                CustomerFormActivity.class);
                        myIntent.putExtra("name", itemValue);
                        CustomerListActivity.this.startActivity(myIntent);

                    }
                });
    }
    public void onAddPartnerClick(View v){
        Intent myIntent = new Intent(CustomerListActivity.this, CustomerFormActivity.class);
        CustomerListActivity.this.startActivity(myIntent);
    }
}